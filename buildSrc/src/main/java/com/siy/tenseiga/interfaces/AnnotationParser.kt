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
interface AnnotationParser {

    /**
     *
     * @param classNode
     * @param methodNode
     * @param transformInfo
     *
     */
    fun parser(classNode: ClassNode, methodNode: MethodNode, transformInfo: TransformInfo)
}

val PARSER_NONE = object : AnnotationParser {
    override fun parser(classNode: ClassNode, methodNode: MethodNode, transformInfo: TransformInfo) = Unit
}