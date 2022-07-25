package com.siy.tenseiga.interfaces

import com.siy.tenseiga.entity.TransformInfo
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.MethodNode


/**
 *
 * 占位符解析器
 *
 * @author  Siy
 * @since  2022/7/21
 */
interface PlaceholderParser {

    /**
     *
     * @param classNode
     * @param methodNode
     * @param transformInfo
     *
     */
    fun phParser(classNode: ClassNode, methodNode: MethodNode, transformInfo: TransformInfo)
}

val PARSER_NONE = object : PlaceholderParser {
    override fun phParser(classNode: ClassNode, methodNode: MethodNode, transformInfo: TransformInfo) = Unit
}