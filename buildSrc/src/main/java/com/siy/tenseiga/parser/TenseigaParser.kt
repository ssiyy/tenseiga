package com.siy.tenseiga.parser

import com.siy.tenseiga.adjuster.AopMethodAdjuster
import com.siy.tenseiga.entity.TExtension
import com.siy.tenseiga.entity.TransformInfo
import com.siy.tenseiga.interfaces.TransformParser
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
                acc
            }
        }.apply {
            proxyInfo.forEach {
                AopMethodAdjuster(it.hookMethod).adjust()
            }

            replaceInfo.forEach {
                AopMethodAdjuster(it.hookMethod).adjust()
            }
        }
    }
}