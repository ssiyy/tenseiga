@file:JvmName("Tools")
package com.siy.tenseiga


/**
 *
 * @author  Siy
 * @since  2022/7/18
 */

fun <T> Class<T>.loadField(obj: T, fieldName: String): Any {
    val filed = this.getDeclaredField(fieldName)
    filed.isAccessible = true
    val fieldValue = filed.get(obj)
    filed.isAccessible = false
    return fieldValue
}