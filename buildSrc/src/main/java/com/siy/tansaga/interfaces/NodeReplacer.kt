package com.siy.tansaga.interfaces

import org.objectweb.asm.tree.AbstractInsnNode
import org.objectweb.asm.tree.MethodInsnNode


interface NodeReplacer {

    fun replace(node: MethodInsnNode): AbstractInsnNode
}