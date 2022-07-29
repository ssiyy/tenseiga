package com.siy.tenseiga.parser.annoparser

import com.siy.tenseiga.entity.SerializableInfo
import com.siy.tenseiga.entity.TransformInfo
import com.siy.tenseiga.ext.SERIALIZABLE_TYPE
import com.siy.tenseiga.ext.value
import com.siy.tenseiga.interfaces.AnnotationParser
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.MethodNode


/**
 *
 * @author  Siy
 * @since  2022/7/29
 */
object SerializableParser : AnnotationParser{
    override fun parser(classNode: ClassNode, methodNode: MethodNode, transformInfo: TransformInfo) {
        val annotations = methodNode.visibleAnnotations
        var targetClass:String? = null
        annotations.forEach {
            when(it.desc){
                SERIALIZABLE_TYPE.descriptor->{
                    targetClass = it.value as? String
                }
            }
        }
        if (targetClass.orEmpty().isNotEmpty()){
            transformInfo.serializableParserInfo.add(
                SerializableInfo(
                    targetClass!!.replace('.', '/'),
                    methodNode,
                    classNode.name
                )
            )
        }
    }
}