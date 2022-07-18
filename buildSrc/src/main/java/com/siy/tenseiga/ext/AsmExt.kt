package com.siy.tenseiga.ext

import com.didiglobal.booster.transform.asm.asIterable
import org.objectweb.asm.Type
import org.objectweb.asm.tree.AbstractInsnNode
import org.objectweb.asm.tree.AnnotationNode
import org.objectweb.asm.tree.InsnList


/**
 *
 * @author  Siy
 * @since  2022/5/26
 */

/**
 * 将注解的值转换成Map，
 *
 *
 *
 */
val AnnotationNode.valueMaps: Map<String, Any>
    get() {
        val map = mutableMapOf<String, Any>()
        for (i in 0 until values.size / 2) {
            val key = values[i * 2]
            val value = values[i * 2 + 1]
            map[key as String] = value
        }
        return map
    }

/**
 * 获取value的值
 */
val AnnotationNode.value: Any?
    get() {
        return valueMaps["value"]
    }

/**
 * 是否是基本数据类型
 */
val Type.isPrimitive: Boolean
    get() {
        return when (sort) {
            Type.BYTE, Type.SHORT, Type.INT, Type.LONG, Type.FLOAT, Type.DOUBLE, Type.CHAR, Type.BOOLEAN -> true
            else -> false
        }
    }

/**
 * 如果是基本数据类型就装箱
 */
val Type.boxedType: Type
    get() {
        return when (sort) {
            Type.BYTE -> BYTE_TYPE
            Type.SHORT -> SHORT_TYPE
            Type.INT -> INTEGER_TYPE
            Type.LONG -> LONG_TYPE
            Type.FLOAT -> FLOAT_TYPE
            Type.DOUBLE -> DOUBLE_TYPE
            Type.CHAR -> CHARACTER_TYPE
            Type.BOOLEAN -> BOOLEAN_TYPE
            else -> this
        }
    }

fun <K> InsnList.groupBy(keySelector: (AbstractInsnNode) -> K): Map<K, List<AbstractInsnNode>> {
    return this.asIterable().groupBy(keySelector)
}