package com.siy.tenseiga.entity

import com.siy.tenseiga.base.tools.join
import com.siy.tenseiga.ext.cloneSelf
import org.objectweb.asm.tree.MethodNode


/**
 *
 * @author  Siy
 * @since  2022/7/22
 */
class SafeTryCatchHandlerInfo(
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
    /**
     * 需要替换哪些用了targetClass 的 replace的包
     */
    includes: List<String> = listOf(),
    /**
     * 需要排除的类
     */
    excludes:List<String> = listOf()
) : Filter(includes) {

    fun cloneHookMethodNode(): MethodNode {
        return hookMethodNode.cloneSelf()
    }


    override fun toString(): String {
        return "SafeTryCatchHandlerInfo{ " +
                "hookClass=$hookClass, " +
                "hookMethod=${hookMethodNode.name}, " +
                "excludes=${excludes.join(",")}, " +
                "filter=${includes.join(",")}}"
    }
}