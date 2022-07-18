package com.siy.tenseiga.inflater

import com.siy.tenseiga.parser.Inflater
import org.objectweb.asm.tree.AbstractInsnNode
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.MethodInsnNode
import org.objectweb.asm.tree.MethodNode


/**
 *
 * @author  Siy
 * @since  2022/7/15
 */
class SelfInflater(private val classNode: ClassNode) : Inflater {


    override fun inflate(methodNode: MethodNode, inflaterNodes: List<AbstractInsnNode>, replaceInsn: MethodInsnNode?) {
        if (inflaterNodes.isEmpty()) {
            return
        }

        val methodNodeInsn = methodNode.instructions



        inflaterNodes.forEach {
            methodNodeInsn.remove(it)
        }
    }

}