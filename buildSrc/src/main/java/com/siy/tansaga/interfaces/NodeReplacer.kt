package com.siy.tansaga.interfaces

import jdk.internal.org.objectweb.asm.tree.MethodNode
import org.objectweb.asm.tree.AbstractInsnNode
import org.objectweb.asm.tree.MethodInsnNode

interface NodeReplacer {
    fun replace(node: MethodInsnNode): AbstractInsnNode
}