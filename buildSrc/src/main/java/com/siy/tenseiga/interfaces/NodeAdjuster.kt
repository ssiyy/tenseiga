package com.siy.tenseiga.interfaces

import org.objectweb.asm.tree.AbstractInsnNode
import org.objectweb.asm.tree.MethodInsnNode


interface NodeAdjuster {

    fun replace(node: MethodInsnNode): AbstractInsnNode
}

val NONE = object : NodeAdjuster {
    override fun replace(node: MethodInsnNode) = node
}