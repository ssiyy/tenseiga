package com.siy.tenseiga.entity

import java.util.regex.Pattern


/**
 *
 * @author  Siy
 * @since  2022/7/21
 */
open class Filter(var includes: List<String> = listOf(), var excludes:List<String> = listOf()) {

    /**
     * 是否包含内部类
     */
    private val withInnerClass = true

    /**
     * 配置内部类的后缀
     */
    private val REGEX_SUFFIX = "(|\\$.*)"

    /**
     * 包含文件的正则表达式
     */
    val includePattern: List<Pattern>
        get() {
            return includes.map {
                if (withInnerClass) {
                    "${it}$REGEX_SUFFIX"
                } else {
                    it
                }
            }.map {
                Pattern.compile(it)
            }
        }

    /**
     * 排除的文件的正则表达式
     */
    val excludePattern:List<Pattern>
        get() {
            return excludes.map {
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