package com.siy.tenseiga.interfaces

import com.siy.tenseiga.entity.TransformInfo
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.MethodNode


/**
 *
 * @author  Siy
 * @since  2022/7/21
 */
interface PlaceholderParser {
    fun phParser(classNode: ClassNode, methodNode: MethodNode, transformInfo: TransformInfo)
}

val PARSER_NONE = object:PlaceholderParser{
    override fun phParser(classNode: ClassNode, methodNode: MethodNode, transformInfo: TransformInfo)  = Unit

}