package com.siy.tenseiga.parser

import org.objectweb.asm.tree.AbstractInsnNode
import org.objectweb.asm.tree.InsnList
import org.objectweb.asm.tree.MethodNode


/**
 *
 * placeHolder的Inflater
 *
 * @author  Siy
 * @since  2022/7/14
 */
interface Inflater {

    /**
     * @param methodNode 需要膨胀的方法
     *
     * @param inflaterNode 膨胀的那个指令
     *
     * @param replaceInsn 会用这个指令替换[inflaterNode]
     */
    fun inflate(methodNode: MethodNode, inflaterNode: AbstractInsnNode, replaceInsn: AbstractInsnNode?): InsnList
}