package com.siy.tansaga

import com.siy.tansaga.entity.ReplaceInfo
import com.siy.tansaga.ext.TypeUtil
import com.siy.tansaga.ext.createMethod
import com.siy.tansaga.ext.errOut
import com.siy.tansaga.ext.replaceMethodBody
import com.siy.tansaga.interfaces.ClassNodeTransform
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.MethodInsnNode
import org.objectweb.asm.tree.MethodNode


/**
 *
 * @author  Siy
 * @since  2022/5/31
 */
class ReplaceClassNodeTransform(private val replaceInfos: List<ReplaceInfo>, cnt: ClassNodeTransform?) :
    BaseClassNodeTransform(cnt) {

    /**
     * 如果不为空就是需要hook的类
     */
    private var klass: ClassNode? = null

    override fun visitorClassNode(klass: ClassNode) {
        super.visitorClassNode(klass)
        if (replaceInfos.map {
                it.targetClass
            }.contains(klass.name)) {
            this.klass = klass
        }
    }

    override fun visitorMethod(method: MethodNode) {
        super.visitorMethod(method)
        klass?.let { _ ->
            replaceInfos.forEach {
                errOut("${it.targetMethod} --- ${method.name}-----${klass?.name}")
                if (it.targetMethod == method.name) {
                    updateMethod(method, it)
                }
            }
        }
    }

    private fun updateMethod(target: MethodNode, info: ReplaceInfo) {
        //创建一个新的方法去调用hook的方法
        val newMethodNode = createMethod(
            Opcodes.ACC_PRIVATE,
            "ffffdfdfdsfd",
            target.desc,
            null
        ) {
            it.add(
                MethodInsnNode(
                    if (TypeUtil.isStatic(info.hookMethod.access)) Opcodes.INVOKESTATIC else Opcodes
                        .INVOKEVIRTUAL, info.hookClass, info.hookMethod.name, info.hookMethod.desc
                )
            )
        }
        klass?.methods?.add(newMethodNode)

        replaceMethodBody(target) {
            it.add(
                MethodInsnNode(
                    if (TypeUtil.isStatic(target.access)) Opcodes.INVOKESTATIC else Opcodes
                        .INVOKEVIRTUAL, klass?.name, newMethodNode.name, newMethodNode.desc
                )
            )
        }
    }


}