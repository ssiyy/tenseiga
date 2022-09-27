package com.siy.tenseiga.entity

import java.util.regex.Pattern


/**
 *
 * @author  Siy
 * @since  2022/7/21
 */
open class Filter(var filters: List<String> = listOf()) {

    /**
     * 是否包含内部类
     */
    private val withInnerClass = true

    /**
     * 配置内部类的后缀
     */
    private val REGEX_SUFFIX = "(|\\$.*)"

    /**
     * 过滤的正则表达式
     */
    val filterPattern: List<Pattern>
        get() {
            return filters.map {
                if (withInnerClass) {
                    "${it}$REGEX_SUFFIX"
                } else {
                    it
                }
            }.map {
                Pattern.compile(it)
            }
        }
}