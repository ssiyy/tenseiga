package com.siy.tansaga.transform

import com.android.build.api.transform.DirectoryInput
import com.android.build.api.transform.Format
import com.android.build.api.transform.TransformInvocation
import com.didiglobal.booster.kotlinx.asIterable
import com.didiglobal.booster.kotlinx.redirect
import com.didiglobal.booster.kotlinx.search
import com.didiglobal.booster.kotlinx.touch
import com.didiglobal.booster.transform.TransformContext
import com.didiglobal.booster.transform.asm.ClassTransformer
import com.didiglobal.booster.transform.util.transform
import com.google.wireless.android.sdk.stats.ApplyChangesAgentError
import com.siy.tansaga.base.annotations.Filter
import com.siy.tansaga.base.annotations.Replace
import com.siy.tansaga.base.annotations.TargetClass
import com.siy.tansaga.entity.ReplaceInfo
import com.siy.tansaga.ext.*
import org.objectweb.asm.ClassReader
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.MethodInsnNode
import java.io.File
import java.io.PrintWriter
import kotlin.streams.toList


/**
 *
 * @author  Siy
 * @since  2022/5/26
 */
class AsmMetaParserTransform(val extension: TExtension) : ClassTransformer {

    /**
     * 替代的结构体
     */
    private val replaceInfos = mutableListOf<ReplaceInfo>()


    override fun onPreTransform(context: TransformContext) {
        if (extension.replaces.isNullOrEmpty()) {
            return
        }

        (context as TransformInvocation).inputs.asSequence().map {
            it.jarInputs + it.directoryInputs
        }.flatten().map { input ->
            input.file
        }.filter {
            it.isDirectory
        }.forEach {
            extension.replaces.all { rp ->
                val hookClass = File(it, rp.hookClass?.trim()?.replace(".", "\\").plus(".class"))
                if (hookClass.exists()) {
                    errOut("hookcass:${hookClass.absolutePath}")
                    hookClass.inputStream().use { fs ->
                        val cn = ClassNode()
                        ClassReader(fs).accept(cn, 0)

                        cn.methods.forEach { mn ->
                            errOut("methodNOde:${mn.name}")
                            if (mn.name == rp.replace) {
                                replaceInfos.add(ReplaceInfo(rp.targetClass!!, rp.replace!!, cn.name, mn, null))
                            }
                        }
                    }
                }
            }
        }
    }




    override fun transform(context: TransformContext, klass: ClassNode): ClassNode {
        replaceMethod(context, klass)
      /*  klass.methods.forEach { method ->
            method.instructions?.iterator()?.asIterable()?.filterIsInstance(MethodInsnNode::class.java)?.filter {
                it.opcode == Opcodes.INVOKEVIRTUAL && it.name == "show" && it.desc == "()V" && (it.owner == TOAST || context.klassPool.get(TOAST).isAssignableFrom(it.owner))
            }?.forEach {
                it.optimize(klass, method)
            }
        }
        return klass*/


        return klass
    }


    private fun replaceMethod(context: TransformContext, klass: ClassNode) {
        if (replaceInfos.isNotEmpty()) {
            klass.methods.forEach { methodNode ->
                methodNode.instructions
                    ?.iterator()
                    ?.asIterable()
                    ?.filterIsInstance(MethodInsnNode::class.java)
                    ?.forEach { methodInsnNode ->
                        for (info in replaceInfos) {
                            val sameDesc = methodInsnNode.desc == info.targetDesc
                            val sameOwner = methodInsnNode.owner == info.targetClass
                            val sameName = methodInsnNode.name == info.replace
                            if (sameDesc && sameOwner && sameName) {
                                methodInsnNode.run {
                                    errOut("命中了before:--${owner}---------${name}---${desc}---${opcode}----${itf}-----")
                                    owner = info.sourceClass
                                    name = info.sourceMethod.name
                                    desc = info.sourceMethod.desc
                                    opcode = Opcodes.INVOKESTATIC
                                    itf = false

                                    errOut("命中了after:--${owner}---------${name}---${desc}---${opcode}----${itf}-----")
                                }
                            }
                        }
                    }

            }
        }
    }


    override fun onPostTransform(context: TransformContext) {
        super.onPostTransform(context)

        replaceInfos.forEach {
            errOut(it.toString())
        }
    }

}


private const val TOAST = "android/widget/Toast"

private const val SHADOW_TOAST = "com/didiglobal/booster/instrument/ShadowToast"