package com.siy.tansaga.parser

import com.siy.tansaga.entity.TExtension
import com.siy.tansaga.entity.TransformInfo
import com.siy.tansaga.interfaces.TransformParser
import java.io.File


/**
 *
 * @author  Siy
 * @since  2022/6/27
 */
class TansagaParser(extension: TExtension) : TransformParser {

    /**
     * 配置解析器
     */
    private val parsers = listOf(
        TExtensionParser(extension),
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
                acc
            }
        }
    }
}