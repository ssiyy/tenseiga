package com.siy.tenseiga.parser.annoparser

import com.siy.tenseiga.entity.SerializableInfo
import com.siy.tenseiga.entity.TransformInfo
import com.siy.tenseiga.ext.SERIALIZABLE_TYPE
import com.siy.tenseiga.ext.illegalState
import com.siy.tenseiga.ext.value
import com.siy.tenseiga.interfaces.AnnotationParser
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.MethodNode


/**
 *
 * @author  Siy
 * @since  2022/7/29
 */
object SerializableParser : AnnotationParser {
    override fun parser(classNode: ClassNode, methodNode: MethodNode, transformInfo: TransformInfo) {
        val annotations = methodNode.visibleAnnotations
        var targetClass: List<String> = listOf()
        annotations.forEach {
            when (it.desc) {
                SERIALIZABLE_TYPE.descriptor -> {
                    targetClass = (it.value as? ArrayList<*>)?.map { item ->
                        item as String
                    } ?: listOf()
                }
            }
        }
        if (targetClass.isNotEmpty()) {
            transformInfo.serializableParserInfo.add(
                SerializableInfo(
                    targetClass,
                    methodNode,
                    classNode.name
                )
            )
        } else {
            illegalState("方法(${methodNode.name})使用$SERIALIZABLE_TYPE 需要 values")
        }
    }
}