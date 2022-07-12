package com.siy.tenseiga.adjuster

import com.siy.tenseiga.ext.TypeUtil
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

    override fun replace(node: MethodInsnNode): AbstractInsnNode {
        when (node.name) {
            "get" -> {
                if (TypeUtil.isStatic(methodNode.access)) {
                    illegalState("static method shouldn't call This's function")
                }
                val varInsnNode = VarInsnNode(Opcodes.ALOAD, 0)
                methodNode.instructions.set(node, varInsnNode)
                return varInsnNode
            }
        }
        return node
    }

}