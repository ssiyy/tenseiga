package com.siy.tenseiga.entity

import com.siy.tenseiga.base.tools.join
import com.siy.tenseiga.ext.cloneSelf
import org.objectweb.asm.tree.MethodNode


/**
 *
 * classJar语法
 *
 * @author  Siy
 * @since  2022/5/26
 */
class ProxyInfo(
    /**
     * 被替换的类,internalName
     */
    targetClass: String,

    /**
     * 被替换的方法名
     */
    targetMethod: String,

    /**
     * 替换的类,internalName
     */
    hookClass: String,
    /**
     * 替换的方法
     */
    val hookMethodNode: MethodNode,
    /**
     * 需要替换哪些用了targetClass 的 replace的包
     */
    filters: List<String> = listOf()
) : ProxyParam(targetMethod) {

    init {
        this.targetClass = targetClass
        this.hookClass = hookClass
        this.filters = filters
        this.hookMethod = hookMethodNode.name
    }

    /**
     * 替换目标方法的desc
     */
    val targetDesc: String
        get() {
            return hookMethodNode.desc
        }

    fun cloneHookMethodNode(): MethodNode {
        return hookMethodNode.cloneSelf()
    }

    override fun toString(): String {
        return "ProxyInfo{ targetClass=$targetClass, " +
                "targetMethod=$targetMethod, " +
                "hookClass=$hookClass, " +
                "hookMethod=${hookMethodNode.name}, " +
                "targetDesc=$targetDesc, " +
                "filter=${filters.join(",")}}"
    }
}