package com.siy.tenseiga.adjuster

import com.siy.tenseiga.ext.isStaticMethod
import com.siy.tenseiga.interfaces.NodeAdjuster
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.AbstractInsnNode
import org.objectweb.asm.tree.MethodInsnNode
import org.objectweb.asm.tree.MethodNode
import org.objectweb.asm.tree.VarInsnNode


/**
 *
 * @author  Siy
 * @since  2022/7/12
 */
 class SelfAdjuster(private val methodNode: MethodNode) : NodeAdjuster {

    override fun replace(insnNode:  MethodInsnNode): AbstractInsnNode {
        when (insnNode.name) {
            "get" -> {
                if(isStaticMethod(methodNode.access)){
                    illegalState("静态方法不应该调用这个函数")
                }
                val varInsnNode = VarInsnNode(Opcodes.ALOAD, 0)
                //就把那个指令替换成变量
                methodNode.instructions.set(insnNode, varInsnNode)
                return varInsnNode
            }
        }
        return insnNode
    }

}