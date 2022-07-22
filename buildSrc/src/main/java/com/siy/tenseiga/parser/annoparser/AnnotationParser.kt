package com.siy.tenseiga.parser.annoparser

import com.siy.tenseiga.entity.TransformInfo
import com.siy.tenseiga.ext.*
import com.siy.tenseiga.interfaces.PARSER_NONE
import com.siy.tenseiga.interfaces.TransformParser
import org.objectweb.asm.ClassReader
import org.objectweb.asm.Type
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
                    //遍历每个方法
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
    private fun checkAnnoParse(annos: List<String>?) {
        val haveRepalce = existAnno(annos, REPLACE_TYPE)
        val haveProxy = existAnno(annos, PROXY_TYPE)
        val haveTryCatch = existAnno(annos, TRYCATCHHANDLER_TYPE)

        if (haveRepalce && haveProxy && haveTryCatch) {
            illegalState("$REPLACE_TYPE 、 $PROXY_TYPE 和 $haveTryCatch 不能用在同一个方法上")
        }
    }

    private fun infoParse(classNode: ClassNode, methodNode: MethodNode, transformInfo: TransformInfo) {
        val annoDesc = methodNode.visibleAnnotations?.map {
            it.desc
        }

        checkAnnoParse(annoDesc)

        val placeholderParser = when {
            existAnno(annoDesc, REPLACE_TYPE) -> {
                ReplaceAnnoParser
            }
            existAnno(annoDesc, PROXY_TYPE) -> {
                ProxyAnnoParser
            }
            existAnno(annoDesc, TRYCATCHHANDLER_TYPE)->{
                TryCatchHandlerParser
            }

            else -> PARSER_NONE
        }

        placeholderParser.phParser(classNode, methodNode, transformInfo)
    }

    /**
     * 是否存在某Type
     */
    private fun existAnno(annos: List<String>?, type: Type) = annos?.contains(type.descriptor) == true


}