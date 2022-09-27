package com.siy.tenseiga.entity

import com.siy.tenseiga.base.tools.join


/**
 *
 * 转换的信息
 *
 * @author  Siy
 * @since  2022/5/31
 */
class TransformInfo {

    /**
     *替换的相关信息
     */
    val replaceInfo = mutableListOf<ReplaceInfo>()

    /**
     * 代理的相关信息
     */
    val proxyInfo = mutableListOf<ProxyInfo>()

    /**
     * 异常捕捉相关
     */
    val safeTryCatchHandlerInfo = mutableListOf<SafeTryCatchHandlerInfo>()

    /**
     * 插入方法的info
     */
    val insertFuncInfo = mutableListOf<InsertFuncInfo>()

    /**
     * 序列化相关
     */
    val serializableParserInfo = mutableListOf<SerializableInfo>()

    override fun toString(): String {
        val replaceInfoStr = replaceInfo.map {
            it.toString()
        }.join("\n")

        val proxyInfoStr = proxyInfo.map {
            it.toString()
        }.join("\n")

        val safeTryCatchHandlerInfoStr = safeTryCatchHandlerInfo.map {
            it.toString()
        }.join("\n")

        val serializableParserInfoStr = serializableParserInfo.map{
            it.toString()
        }.join("\n")

        return "replaceInfoStr:\n${replaceInfoStr} " +
                "\nproxyInfoStr:\n${proxyInfoStr} " +
                "\nsafeTryCatchHandlerInfoStr:\n${safeTryCatchHandlerInfoStr} " +
                "\nserializableParserInfo:${serializableParserInfoStr}"
    }
}