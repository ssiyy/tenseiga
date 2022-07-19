package com.siy.tenseiga.parser

import com.siy.tenseiga.entity.ProxyInfo
import com.siy.tenseiga.entity.ReplaceInfo
import com.siy.tenseiga.entity.TransformInfo
import com.siy.tenseiga.ext.*
import com.siy.tenseiga.interfaces.TransformParser
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
                    //过滤掉 静态代码块 构造方法 抽象方法 本地方法
                    !(isCInitMethod(it) || isInitMethod(it) || isAbstractMethod(it.access) || isNativeMethod(it.access))
                }.forEach {
                    infoParse(cn, it, transformInfo)
                }
            }
        }
        return transformInfo
    }

    /**
     * 检查注解是否规范
     *
     */
    private fun checkAnnoParse(annos:List<String>?){
        val haveRepalce = annos?.contains(REPLACE_TYPE.descriptor) == true
        val haveProxy = annos?.contains(PROXY_TYPE.descriptor) == true

      if (haveRepalce && haveProxy){
          illegalState("$REPLACE_TYPE 和 $PROXY_TYPE 不能用在同一个方法上")
      }
    }


    private fun infoParse(classNode: ClassNode, methodNode: MethodNode, transformInfo: TransformInfo) {
        val annotations = methodNode.visibleAnnotations

        val annotationDescs = annotations?.map {
            it.desc
        }

        val isReplace = annotationDescs?.contains(REPLACE_TYPE.descriptor) == true

        val isProxy = annotationDescs?.contains(PROXY_TYPE.descriptor) == true

        checkAnnoParse(annotationDescs)

        if (isReplace) {
            var targetMethod: String? = null
            var targetClass: String? = null
            annotations.forEach {
                when (it.desc) {
                    REPLACE_TYPE.descriptor -> {
                        targetMethod = it.value as? String
                    }
                    TARGETCLASS_TYPE.descriptor -> {
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
                    PROXY_TYPE.descriptor -> {
                        targetMethod = it.value as? String
                    }
                    TARGETCLASS_TYPE.descriptor -> {
                        targetClass = (it.value as? String)?.replace(".", "/")
                    }
                    FILTER_TYPE.descriptor -> {
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