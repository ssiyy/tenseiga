package com.siy.tenseiga.parser.annoparser

import com.siy.tenseiga.entity.TransformInfo
import com.siy.tenseiga.entity.TryCatchHandlerInfo
import com.siy.tenseiga.ext.TRYCATCHHANDLER_TYPE
import com.siy.tenseiga.ext.value
import com.siy.tenseiga.interfaces.PlaceholderParser
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.MethodNode


/**
 *
 * @author  Siy
 * @since  2022/7/22
 */
object TryCatchHandlerParser : PlaceholderParser {
    override fun phParser(classNode: ClassNode, methodNode: MethodNode, transformInfo: TransformInfo) {
        val annotations = methodNode.visibleAnnotations
        var filter = listOf<String>()
        annotations.forEach {
            when (it.desc) {
                TRYCATCHHANDLER_TYPE.descriptor -> {
                    filter = (it.value as? Array<String>)?.let { arr ->
                        listOf(*arr)
                    } ?: listOf()
                }
            }
        }

        transformInfo.tryCatchHandlerInfo.add(
            TryCatchHandlerInfo(
                classNode.name,
                methodNode,
                filter
            )
        )
    }
}