package com.siy.tansaga.parser

import com.siy.tansaga.entity.TransformInfo
import com.siy.tansaga.ext.TypeUtil
import com.siy.tansaga.interfaces.TransformParser
import org.objectweb.asm.ClassReader
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.MethodNode
import java.io.File


/**
 * 用来解析注解配置的类
 * @author  Siy
 * @since  2022/6/27
 */
class AnnotationParser : TransformParser {

    override fun parse(dir: Sequence<File>): TransformInfo {
        val transformInfo = TransformInfo()

        dir.flatMap {
            it.walk()
        }.filter {
            it.extension == "class"
        }.forEach { file ->
            file.inputStream().use { fs ->
                val cn = ClassNode()
                ClassReader(fs).accept(cn, 0)

                cn.methods.filter {
                    TypeUtil.isNormalMethod(it)
                }.forEach {
                    infoParse(it)
                }

            }
        }
        return transformInfo
    }


    private fun infoParse(methodNode: MethodNode) {
        methodNode.visibleAnnotations?.forEach {

        }
    }
}