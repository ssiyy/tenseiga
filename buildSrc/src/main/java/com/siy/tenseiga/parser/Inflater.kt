package com.siy.tenseiga.parser

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
     */
    fun inflate(methodNode: MethodNode): InsnList
}