package com.siy.tenseiga.interfaces

import org.objectweb.asm.tree.AbstractInsnNode
import org.objectweb.asm.tree.MethodInsnNode
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
     * @param inflaterNodes 膨胀的那个指令
     *
     * @param replaceInsn 会用这个指令替换[inflaterNodes],可以为null
     */
    fun inflate(methodNode: MethodNode, inflaterNodes:  List<AbstractInsnNode>, replaceInsn: MethodInsnNode?)
}