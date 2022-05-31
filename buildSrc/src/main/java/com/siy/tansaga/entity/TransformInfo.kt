package com.siy.tansaga.entity

import com.siy.tansaga.ext.join


/**
 *
 * @author  Siy
 * @since  2022/5/31
 */
class TransformInfo {

    val replaceInfo = mutableListOf<ReplaceInfo>()

    val proxyInfo = mutableListOf<ProxyInfo>()

    fun isEmpty() = replaceInfo.isEmpty() && proxyInfo.isEmpty()

    override fun toString(): String {
        val replaceInfoStr = replaceInfo.map {
            it.toString()
        }.join("\n")
        val proxyInfoStr = proxyInfo.map {
            it.toString()
        }.join("\n")

        return "replaceInfoStr:\n\n${replaceInfoStr} \n\n proxyInfoStr\n\n${proxyInfoStr}"
    }
}