package com.siy.tenseiga.entity

import com.siy.tenseiga.ext.cloneSelf
import org.objectweb.asm.tree.MethodNode


/**
 *
 * hook替换需要的信息
 *
 * @author  Siy
 * @since  2022/5/26
 */
 class ReplaceInfo(
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

    ) :ReplaceParam(targetMethod){

    init {
        this.targetClass = targetClass
        this.hookClass = hookClass
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
        return "ReplaceInfo{ targetClass=$targetClass, " +
                "targetMethod=$targetMethod, " +
                "hookClass=$hookClass, " +
                "hookMethod=${hookMethodNode.name}, " +
                "targetDesc=$targetDesc}"
    }
}