package com.siy.tenseiga.parser

import com.siy.tenseiga.base.annotations.Filter
import com.siy.tenseiga.base.annotations.Proxy
import com.siy.tenseiga.base.annotations.Replace
import com.siy.tenseiga.base.annotations.TargetClass
import com.siy.tenseiga.entity.ProxyInfo
import com.siy.tenseiga.entity.ReplaceInfo
import com.siy.tenseiga.entity.TransformInfo
import com.siy.tenseiga.ext.TypeUtil
import com.siy.tenseiga.ext.value
import com.siy.tenseiga.interfaces.TransformParser
import org.objectweb.asm.ClassReader
import org.objectweb.asm.Type
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.MethodNode
import java.io.File

val ReplaceType = Type.getType(Replace::class.java)

val ProxyType = Type.getType(Proxy::class.java)

val TargetClassType = Type.getType(TargetClass::class.java)

val FilterType = Type.getType(Filter::class.java)

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
                    infoParse(cn, it, transformInfo)
                }

            }
        }
        return transformInfo
    }


    private fun infoParse(classNode: ClassNode, methodNode: MethodNode, transformInfo: TransformInfo) {
        val annotations = methodNode.visibleAnnotations

        val annotationDescs = annotations?.map {
            it.desc
        }

        val isReplace = annotationDescs?.contains(ReplaceType.descriptor) == true

        val isProxy = annotationDescs?.contains(ProxyType.descriptor) == true

        if (isReplace) {
            var targetMethod: String? = null
            var targetClass: String? = null
            annotations.forEach {
                when (it.desc) {
                    ReplaceType.descriptor -> {
                        targetMethod = it.value as? String
                    }
                    TargetClassType.descriptor -> {
                        targetClass = (it.value as? String)?.replace(".", "/")
                    }
                }
            }
            if (targetMethod.orEmpty().isNotEmpty() && targetClass.orEmpty().isNotEmpty()) {
                transformInfo.replaceInfo.add(
                    ReplaceInfo(
                        targetClass!!.replace(".", "/"),
                        targetMethod!!,
                        classNode.name,
                        methodNode
                    )
                )
            }
        } else if (isProxy) {
            var targetMethod: String? = null
            var targetClass: String? = null
            var filters = listOf<String>()
            annotations.forEach {
                when (it.desc) {
                    ProxyType.descriptor -> {
                        targetMethod = it.value as? String
                    }
                    TargetClassType.descriptor -> {
                        targetClass = (it.value as? String)?.replace(".", "/")
                    }
                    FilterType.descriptor -> {
                        filters = (it.value as? Array<String>)?.let { arr ->
                            listOf(*arr)
                        } ?: listOf()
                    }
                }
            }

            if (targetMethod.orEmpty().isNotEmpty() && targetClass.orEmpty().isNotEmpty()) {
                transformInfo.proxyInfo.add(
                    ProxyInfo(
                        targetClass!!.replace(".", "/"),
                        targetMethod!!,
                        classNode.name,
                        methodNode,
                        filters
                    )
                )
            }
        }
    }

}