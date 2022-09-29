package com.siy.tenseiga.parser.annoparser

import com.siy.tenseiga.entity.ProxyInfo
import com.siy.tenseiga.entity.TransformInfo
import com.siy.tenseiga.ext.*
import com.siy.tenseiga.interfaces.AnnotationParser
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.MethodNode


/**
 *
 * @author  Siy
 * @since  2022/7/21
 */
object ProxyAnnoParser : AnnotationParser {
    override fun parser(classNode: ClassNode, methodNode: MethodNode, transformInfo: TransformInfo) {
        val annotations = methodNode.visibleAnnotations
        var targetMethod: String? = null
        var targetClass: String? = null

        var includes = listOf<String>()
        var excludes = listOf<String>()

        annotations.forEach {
            when (it.desc) {
                PROXY_TYPE.descriptor -> {
                    targetMethod = it.value as? String
                }
                TARGETCLASS_TYPE.descriptor -> {
                    targetClass = (it.value as? String)?.replace('.', '/')
                }

                FILTER_TYPE.descriptor -> {
                    includes = (it.valueMaps["include"] as? ArrayList<*>)?.map { item ->
                        item as String
                    } ?: listOf()

                    excludes = (it.valueMaps["exclude"] as? ArrayList<*>)?.map { item ->
                        item as String
                    } ?: listOf()
                }
            }
        }

        if (targetMethod.orEmpty().isNotEmpty() && targetClass.orEmpty().isNotEmpty()) {
            transformInfo.proxyInfo.add(
                ProxyInfo(
                    targetClass!!.replace('.', '/'),
                    targetMethod!!,
                    classNode.name,
                    methodNode,
                    includes,
                    excludes
                )
            )
        } else {
            illegalState("方法(${methodNode.name})使用$PROXY_TYPE 需要 $TARGETCLASS_TYPE")
        }
    }

}