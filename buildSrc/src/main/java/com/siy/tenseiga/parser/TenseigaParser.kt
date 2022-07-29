package com.siy.tenseiga.parser

import com.siy.tenseiga.adjuster.PlaceHolderNodeJuster
import com.siy.tenseiga.entity.TExtension
import com.siy.tenseiga.entity.TransformInfo
import com.siy.tenseiga.ext.PROXY_TYPE
import com.siy.tenseiga.ext.REPLACE_TYPE
import com.siy.tenseiga.ext.SAFETRYCATCHHANDLER_TYPE
import com.siy.tenseiga.ext.SERIALIZABLE_TYPE
import com.siy.tenseiga.interfaces.TransformParser
import com.siy.tenseiga.parser.annoparser.AnnotationParser
import java.io.File


/**
 *
 * @author  Siy
 * @since  2022/6/27
 */
class TenseigaParser(extension: TExtension) : TransformParser {

    /**
     * 配置解析器
     */
    private val parsers = listOf(
        //gradle配置解析器
        TExtensionParser(extension),
        //注解解析器
        AnnotationParser()
    )

    override fun parse(dir: Sequence<File>): TransformInfo {
        return parsers.map {
            it.parse(dir)
        }.foldIndexed(TransformInfo()) { index, acc, item ->
            if (index == 0) {
                item
            } else {
                (acc.proxyInfo).addAll(item.proxyInfo)
                (acc.replaceInfo).addAll(item.replaceInfo)
                (acc.safeTryCatchHandlerInfo).addAll(item.safeTryCatchHandlerInfo)
                acc
            }
        }.apply {
            proxyInfo.forEach {
                PlaceHolderNodeJuster(it.hookMethodNode, PROXY_TYPE).adjust()
            }

            replaceInfo.forEach {
                PlaceHolderNodeJuster(it.hookMethodNode, REPLACE_TYPE).adjust()
            }

            safeTryCatchHandlerInfo.forEach {
                PlaceHolderNodeJuster(it.hookMethodNode, SAFETRYCATCHHANDLER_TYPE).adjust()
            }

            serializableParserInfo.forEach {
                PlaceHolderNodeJuster(it.hookMethodNode, SERIALIZABLE_TYPE).adjust()
            }
        }
    }
}