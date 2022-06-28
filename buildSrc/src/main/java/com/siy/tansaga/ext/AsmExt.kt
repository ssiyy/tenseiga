package com.siy.tansaga.ext

import org.objectweb.asm.tree.AnnotationNode


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