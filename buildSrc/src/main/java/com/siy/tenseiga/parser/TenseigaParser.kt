package com.siy.tenseiga.parser

import com.siy.tenseiga.adjuster.PlaceHolderNodeJuster
import com.siy.tenseiga.entity.TransformInfo
import com.siy.tenseiga.ext.PROXY_TYPE
import com.siy.tenseiga.ext.REPLACE_TYPE
import com.siy.tenseiga.ext.SAFETRYCATCHHANDLER_TYPE
import com.siy.tenseiga.ext.SERIALIZABLE_TYPE
import com.siy.tenseiga.parser.annoparser.AnnotationParser
import java.io.File


/**
 *
 * @author  Siy
 * @since  2022/6/27
 */
class TenseigaParser {
    fun parse(dir: Sequence<File>): TransformInfo {
        return AnnotationParser().parse(dir)
            .apply {
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