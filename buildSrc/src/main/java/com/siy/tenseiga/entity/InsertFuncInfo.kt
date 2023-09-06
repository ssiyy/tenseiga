package com.siy.tenseiga.entity

import com.siy.tenseiga.base.tools.join
import com.siy.tenseiga.ext.cloneSelf
import org.objectweb.asm.tree.MethodNode


/**
 *
 * @author  Siy
 * @since  2022/7/22
 */
class InsertFuncInfo(

    /**
     * 替换的方法
     */
    val hookMethodNode: MethodNode,

    /**
     * 替换的类,internalName
     */
    val hookClass: String,

    /**
     * 需要替换哪些用了targetClass 的 replace的包
     */
    filters: List<String> = listOf(),

    /**
     * 需要排除的文件
     */
    excludes:List<String> = listOf()
) : Filter(filters,excludes) {

    fun cloneHookMethodNode(): MethodNode {
        return hookMethodNode.cloneSelf()
    }


    override fun toString(): String {
        return "InsertFuncInfo{ " +
                "hookMethod=${hookMethodNode.name}, " +
                "excludes=${excludes.join(",")}, " +
                "includes=${includes.join(",")}}"
    }
}