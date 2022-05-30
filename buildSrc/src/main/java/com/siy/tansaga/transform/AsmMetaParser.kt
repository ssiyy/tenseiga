package com.siy.tansaga.transform

import com.android.build.api.transform.TransformInvocation
import com.didiglobal.booster.kotlinx.asIterable
import com.didiglobal.booster.transform.TransformContext
import com.didiglobal.booster.transform.asm.ClassTransformer
import com.siy.tansaga.entity.ProxyInfo
import com.siy.tansaga.entity.ReplaceInfo
import com.siy.tansaga.entity.TExtension
import com.siy.tansaga.ext.*
import org.objectweb.asm.ClassReader
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.MethodInsnNode
import java.io.File


/**
 *
 * @author  Siy
 * @since  2022/5/26
 */
class AsmMetaParserTransform(val extension: TExtension) : ClassTransformer {

    /**
     * 替换方法
     */
    private val replaceInfos = mutableListOf<ReplaceInfo>()

    /**
     * 代理的类
     */
    private val proxyInfos = mutableListOf<ProxyInfo>()

    override fun onPreTransform(context: TransformContext) {
        if (extension.replaces.isNullOrEmpty() || extension.proxys.isNullOrEmpty()) {
            return
        }

        (context as TransformInvocation).inputs.asSequence().map {
            it.jarInputs + it.directoryInputs
        }.flatten().map { input ->
            input.file
        }.filter {
            it.isDirectory
        }.forEach {
            //处理替换
            extension.replaces.all { rp ->
                errOut("replace:$rp")
                val hookClass = File(it, rp.hookClass?.trim()?.replace(".", "\\").plus(".class"))
                errOut("replace:${hookClass.absoluteFile}-${hookClass.exists()}")
                if (hookClass.exists()) {
                    hookClass.inputStream().use { fs ->
                        val cn = ClassNode()
                        ClassReader(fs).accept(cn, 0)
                        cn.methods.forEach { mn ->
                            if (mn.name == rp.hookMethod) {
                                replaceInfos.add(
                                    ReplaceInfo(
                                        rp.targetClass!!.replace(".", "/"),
                                        rp.name,
                                        cn.name,
                                        mn,
                                        rp.filters
                                    )
                                )
                            }
                        }
                    }
                }
            }

            //处理代理
            extension.proxys.all { rp ->
                errOut("proxy:$rp")
                val hookClass = File(it, rp.hookClass?.trim()?.replace(".", "\\").plus(".class"))
                errOut("proxy:${hookClass.absoluteFile}-${hookClass.exists()}")
                if (hookClass.exists()) {
                    hookClass.inputStream().use { fs ->
                        val cn = ClassNode()
                        ClassReader(fs).accept(cn, 0)
                        cn.methods.forEach { mn ->
                            errOut("${mn.name}------${rp.hookMethod}----------------")
                            if (mn.name == rp.hookMethod) {

                                proxyInfos.add(
                                    ProxyInfo(
                                        rp.targetClass!!.replace(".", "/"),
                                        rp.name,
                                        cn.name,
                                        mn,
                                        rp.filters
                                    )
                                )
                            }
                        }
                    }
                }
            }

        }
    }


    override fun transform(context: TransformContext, klass: ClassNode): ClassNode {


        val proxyCn = proxyMethod(klass)

        val replaceCn = repacleMethod(proxyCn)

        return replaceCn
    }


    private fun proxyMethod(klass: ClassNode): ClassNode {
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
                            if (/*sameDesc && */sameOwner && sameName) {
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
    }

    private fun repacleMethod(klass: ClassNode): ClassNode {
        if (replaceInfos.map {
                it.hookClass
            }.contains(klass.name)) {
            return klass
        }

        return klass
    }


    override fun onPostTransform(context: TransformContext) {
        super.onPostTransform(context)

        errOut("代理：\n")
        proxyInfos.forEach {
            errOut(it.toString())
        }

        errOut("替换：\n")
        proxyInfos.forEach {
            errOut(it.toString())
        }
    }
}