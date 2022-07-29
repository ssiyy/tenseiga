package com.siy.tenseiga.entity

import org.objectweb.asm.tree.MethodNode


/**
 *
 * 序列化消息体
 *
 * @author  Siy
 * @since  2022/7/29
 */
class SerializableInfo(

    /**
     * 被替换的类名
     */
    val targetClass:List<String> = listOf(),

    /**
     * hook方法
     */
     var hookMethodNode: MethodNode,

    /**
     * hook所在的类
     */
     var hookClass: String,

){
    override fun toString(): String {
        return "SerializableInfo{ " +
                "hookClass=$hookClass, " +
                "hookMethod=${hookMethodNode.name}, " +
                "targetClass=${targetClass}}"
    }
}