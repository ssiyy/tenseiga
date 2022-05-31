package com.siy.tansaga.transform

import com.android.build.api.transform.TransformInvocation
import com.didiglobal.booster.kotlinx.asIterable
import com.didiglobal.booster.transform.TransformContext
import com.didiglobal.booster.transform.asm.ClassTransformer
import com.siy.tansaga.BaseClassNodeTransform
import com.siy.tansaga.ProxyClassNodeTransform
import com.siy.tansaga.ReplaceClassNodeTransform
import com.siy.tansaga.TansagaParser
import com.siy.tansaga.entity.ProxyInfo
import com.siy.tansaga.entity.ReplaceInfo
import com.siy.tansaga.entity.TExtension
import com.siy.tansaga.entity.TransformInfo
import com.siy.tansaga.ext.*
import com.siy.tansaga.interfaces.ClassNodeTransform
import com.siy.tansaga.interfaces.TransformParser
import org.objectweb.asm.ClassReader
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
import org.objectweb.asm.tree.*
import org.stringtemplate.v4.compiler.Bytecode.instructions
import java.io.File
import java.lang.reflect.Method


/**
 *
 * @author  Siy
 * @since  2022/5/26
 */
class AsmMetaParserTransform(private val extension: TExtension) : ClassTransformer {

    private var transformInfo: TransformInfo? = null

    override fun onPreTransform(context: TransformContext) {
        (context as? TransformInvocation)?.inputs?.asSequence()?.map {
            it.jarInputs + it.directoryInputs
        }?.flatten()?.map { input ->
            input.file
        }?.filter {
            it.isDirectory
        }?.let {
            transformInfo = TansagaParser().parse(extension, it.iterator())
        }
    }

    private fun registerTransform(): ClassNodeTransform? {
        if (transformInfo?.isEmpty() != false) {
            return null
        }

        var classNodeTransform: ClassNodeTransform? = null

        if (transformInfo?.replaceInfo?.isNotEmpty() == true) {
            classNodeTransform = ReplaceClassNodeTransform(transformInfo?.replaceInfo ?: listOf(), classNodeTransform)
        }

        if (transformInfo?.proxyInfo?.isNotEmpty() == true) {
            classNodeTransform = ProxyClassNodeTransform(transformInfo?.proxyInfo ?: listOf(), classNodeTransform)
        }

        return classNodeTransform
    }


    override fun transform(context: TransformContext, klass: ClassNode): ClassNode {
        val classNodeTransform = registerTransform() ?: return klass

        klass.let {
            classNodeTransform.visitorClassNode(it)
            it
        }.methods?.filter {
            TypeUtil.isNormalMethod(it)
        }?.flatMap {
            classNodeTransform.visitorMethod(it)
            it?.instructions?.iterator()?.asIterable()?.filterIsInstance(MethodInsnNode::class.java) ?: arrayListOf()
        }?.forEach {
            classNodeTransform.visitorInsnMethod(it)
        }
        return klass
    }


    /*private fun proxyMethod(klass: ClassNode): ClassNode {
        if (proxyInfos.map {
                it.hookClass
            }.contains(klass.name)) {
            return klass
        }

        if (proxyInfos.isNotEmpty()) {
            klass.methods.forEach { methodNode ->
                methodNode.instructions
                    ?.iterator()
                    ?.asIterable()
                    ?.filterIsInstance(MethodInsnNode::class.java)
                    ?.forEach { methodInsnNode ->
                        for (info in proxyInfos) {
                            //    val sameDesc = methodInsnNode.desc == info.targetDesc
                            val sameOwner = methodInsnNode.owner == info.targetClass
                            val sameName = methodInsnNode.name == info.targetMethod
                            if (sameName) {
                                errOut("${methodInsnNode.owner} --- ${methodInsnNode.desc} --- ${methodInsnNode.name}")
                            }
                            if (*//*sameDesc && *//*sameOwner && sameName) {
                                methodInsnNode.run {
                                    errOut("命中了before:--${owner}---------${name}---${desc}---${opcode}----${itf}-----")
                                    owner = info.hookClass
                                    name = info.hookMethod.name
                                    desc = info.hookMethod.desc
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
    }*/


    override fun onPostTransform(context: TransformContext) {
        super.onPostTransform(context)
    }


}