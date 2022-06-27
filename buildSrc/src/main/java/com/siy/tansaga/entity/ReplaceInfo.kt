package com.siy.tansaga.entity

import org.objectweb.asm.tree.MethodNode


/**
 *
 * hook替换需要的信息
 *
 * @author  Siy
 * @since  2022/5/26
 */
data class ReplaceInfo(
    /**
     * 被替换的类,internalName
     */
    val targetClass: String,

    /**
     * 被替换的方法名
     */
    val targetMethod: String,

    /**
     * 替换的类,internalName
     */
    val hookClass: String,
    /**
     * 替换的方法
     */
    val hookMethod: MethodNode,

) {

    /**
     * 替换目标方法的desc
     */
    val targetDesc: String
        get() {
            return hookMethod.desc
        }

    override fun toString(): String {
        return "ReplaceInfo{ targetClass=$targetClass, " +
                "targetMethod=$targetMethod, " +
                "hookClass=$hookClass, " +
                "hookMethod=${hookMethod.name}, " +
                "targetDesc=$targetDesc}"
    }
}