package com.siy.tenseiga.interfaces

import org.objectweb.asm.tree.AbstractInsnNode
import org.objectweb.asm.tree.MethodInsnNode


/**
 * 用来处理定义的Placeholder
 */
interface NodeAdjuster {
    /**
     * placeholder指令替换
     */
    fun replace(insnNode: MethodInsnNode): AbstractInsnNode
}

/**
 * 指令原样返回
 */
val ADJUSTER_NONE = object : NodeAdjuster {
    override fun replace(insnNode: MethodInsnNode) = insnNode
}