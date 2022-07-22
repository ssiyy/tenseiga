package com.siy.tenseiga.entity

import org.objectweb.asm.tree.MethodNode


/**
 *
 * @author  Siy
 * @since  2022/7/22
 */
class TryCatchHandlerInfo(
    /**
     * 替换的类,internalName
     */
    val hookClass: String,
    /**
     * 替换的方法
     */
    val hookMethodNode: MethodNode,
    /**
     * 需要替换哪些用了targetClass 的 replace的包
     */
    filters: List<String> = listOf()
):Filter() {
    init {
        this.filters = filters
    }
}