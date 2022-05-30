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
                    hookClass.inputStream().use { fs ->
                        val cn = ClassNode()
                        ClassReader(fs).accept(cn, 0)
                        cn.methods.forEach { mn ->
                            if (mn.name == rp.replace) {
                                replaceInfos.add(ReplaceInfo(rp.targetClass!!.replace(".","/"), rp.replace!!, cn.name, mn, null))
                            }
                        }
                    }
                }
            }
        }
    }


    override fun transform(context: TransformContext, klass: ClassNode): ClassNode {
        if(replaceInfos.map {
            it.sourceClass
        }.contains(klass.name)){
            return klass
        }

        return replaceMethod(context, klass)
    }


    private fun replaceMethod(context: TransformContext, klass: ClassNode): ClassNode {
        if (replaceInfos.isNotEmpty()) {
            klass.methods.forEach { methodNode ->
                methodNode.instructions
                    ?.iterator()
                    ?.asIterable()
                    ?.filterIsInstance(MethodInsnNode::class.java)
                    ?.forEach { methodInsnNode ->
                        for (info in replaceInfos) {
                            //    val sameDesc = methodInsnNode.desc == info.targetDesc
                            val sameOwner = methodInsnNode.owner == info.targetClass
                            val sameName = methodInsnNode.name == info.replace
                            if(sameName){
                                errOut("${methodInsnNode.owner} --- ${methodInsnNode.desc} --- ${methodInsnNode.name}")
                            }
                            if (/*sameDesc && */sameOwner && sameName) {
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
            return klass
        } else {
            return klass
        }
    }



    override fun onPostTransform(context: TransformContext) {
        super.onPostTransform(context)

        replaceInfos.forEach {
            errOut(it.toString())
        }
    }
}