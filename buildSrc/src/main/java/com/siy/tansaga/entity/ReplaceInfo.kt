package com.siy.tansaga.entity

import com.siy.tansaga.ext.AsmUtil
import org.objectweb.asm.tree.MethodNode


/**
 *
 * @author  Siy
 * @since  2022/5/26
 */
data class ReplaceInfo(
    /**
     * 被替换的类
     */
    val targetClass: String,

    /**
     * 被替换的方法名
     */
    val replace: String,

    /**
     * 替换的类
     */
    val sourceClass: String,
    /**
     * 替换的方法
     */
    val sourceMethod: MethodNode,
    /**
     * 需要替换哪些用了targetClass 的 replace的包
     */
    var filter: String? = null
) {

    /**
     * 替换目标方法的desc
     */
    val targetDesc: String
        get() {
            return sourceMethod.desc
        }

    private val local = object : ThreadLocal<MethodNode>() {
        @Synchronized
        override fun initialValue(): MethodNode {
            return AsmUtil.clone(sourceMethod)
        }
    }

    fun threadLocalNode(): MethodNode? {
        return local.get()
    }


    override fun toString(): String {
        return "ReplaceInfo{ targetClass=$targetClass, " +
                "replace=$replace, " +
                "sourceClass=$sourceClass, " +
                "sourceMethod=$sourceMethod, " +
                "targetDesc=$targetDesc, " +
                "filter=$filter}"
    }
}