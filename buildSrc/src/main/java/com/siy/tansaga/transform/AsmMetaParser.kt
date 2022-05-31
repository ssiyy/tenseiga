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
import org.objectweb.asm.Type
import org.objectweb.asm.tree.*
import java.io.File
import java.lang.reflect.Method


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

    private fun isNormalMethod(method: MethodNode): Boolean {
        val isAbstract = TypeUtil.isAbstract(method.access)
        val isNative = TypeUtil.isNative(method.access)
        val isInitMethod = TypeUtil.isInitMethod(method.name)
        val isclinitMethod = TypeUtil.isCInitMethod(method.name)

        return !(isAbstract || isNative || isInitMethod || isclinitMethod)
    }

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
                        cn.methods.filter {
                            isNormalMethod(it)
                        }.forEach { mn ->
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
                        cn.methods.filter {
                            isNormalMethod(it)
                        }.forEach { mn ->
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

        replaceInfos.forEach { info ->
            if (info.targetClass == klass.name) {
                var targetMethod: MethodNode? = null
                klass.methods.forEach {
                    if (it.name == info.targetMethod) {
                        targetMethod = it
                    }
                }

                targetMethod?.let {
                    //创建一个新的方法去掉用hook的方法
                    val methodInsn = MethodInsnNode(
                        if (TypeUtil.isStatic(info.hookMethod.access)) Opcodes.INVOKESTATIC else Opcodes
                            .INVOKEVIRTUAL, info.hookClass, info.hookMethod.name, info.hookMethod.desc
                    )
                    val newMethodNode = createMethod(Opcodes.ACC_PRIVATE, "xxxfdhdffdfswr_aafd", it.desc, null, methodInsn)
                    klass.methods.add(newMethodNode)


                    //替换到原来方法的方法体，去调用新创建的方法
                    val methodInsn1 = MethodInsnNode(
                        if (TypeUtil.isStatic(it.access)) Opcodes.INVOKESTATIC else Opcodes
                            .INVOKEVIRTUAL, klass.name, newMethodNode.name, newMethodNode.desc
                    )
                    replaceMethodBody(it, methodInsn1)
                }

            }
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

    private fun replaceMethodBody(method: MethodNode, action: AbstractInsnNode) {
        method.instructions.clear()
        val insnList = InsnList();
        //加载参数
        val params = Type.getArgumentTypes(method.desc)
        var index = 0;
        if (!TypeUtil.isStatic(method.access)) {
            index++
            insnList.add(VarInsnNode(Opcodes.ALOAD, 0))
        }

        for (t in params) {
            insnList.add(VarInsnNode(t.getOpcode(Opcodes.ILOAD), index))
            index += t.size
        }

        //操作
        insnList.add(action)

        //返回值
        val ret = Type.getReturnType(method.desc)
        insnList.add(InsnNode(ret.getOpcode(Opcodes.IRETURN)))
        method.instructions.add(insnList)
    }

    /**
     * 创建一个方法
     */
    private fun createMethod(access: Int, name: String, desc: String, exceptions: Array<String>?, action: AbstractInsnNode):
            MethodNode {
        val methodNode = MethodNode(access, name, desc, null, exceptions)
        val insnList = InsnList()

        //加载参数
        val params = Type.getArgumentTypes(desc)
        var index = 0;
        if (!TypeUtil.isStatic(access)) {
            index++
            insnList.add(VarInsnNode(Opcodes.ALOAD, 0))
        }

        for (t in params) {
            insnList.add(VarInsnNode(t.getOpcode(Opcodes.ILOAD), index))
            index += t.size
        }

        //操作
        insnList.add(action)

        //返回值
        val ret = Type.getReturnType(desc)
        insnList.add(InsnNode(ret.getOpcode(Opcodes.IRETURN)))

        methodNode.instructions.add(insnList)
        return methodNode
    }
}