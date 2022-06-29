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
     *转换信息是否为空 true 空，false不为空
     */
    fun isEmpty() = replaceInfo.isEmpty() && proxyInfo.isEmpty()

    override fun toString(): String {
        val replaceInfoStr = replaceInfo.map {
            it.toString()
        }.join("\n")
        val proxyInfoStr = proxyInfo.map {
            it.toString()
        }.join("\n")

        return "replaceInfoStr:\n${replaceInfoStr} \n\nproxyInfoStr:\n${proxyInfoStr}"
    }
}