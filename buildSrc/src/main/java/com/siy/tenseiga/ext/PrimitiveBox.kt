package com.siy.tenseiga.ext

import org.objectweb.asm.Type


/**
 *
 * @author  Siy
 * @since  2022/7/13
 */
object PrimitiveBox {
    /**
     * 拆箱的方法
     */
    val unboxMethod = mapOf(
        CHARACTER_TYPE to "charValue",
        BOOLEAN_TYPE to "booleanValue",

        BYTE_TYPE to "byteValue",
        SHORT_TYPE to "shortValue",
        INTEGER_TYPE to "intValue",
        FLOAT_TYPE to "floatValue",
        LONG_TYPE to "longValue",
        DOUBLE_TYPE to "doubleValue"
    )

    /**
     * 其它类型type转换成NumberType
     *
     * 为什么要这么做？主要是为了应付byte ,short  ,Float 等跨类型转换。
     * 因为基本数据类型之间可以强制转换，包装类型之间不可以
     *
     * @param type
     */
    fun typeToNumberType(type: Type): Type {
        return when (type) {
            BYTE_TYPE,
            SHORT_TYPE,
            INTEGER_TYPE,
            FLOAT_TYPE,
            LONG_TYPE,
            DOUBLE_TYPE,
            Type.BYTE_TYPE,
            Type.SHORT_TYPE,
            Type.INT_TYPE,
            Type.FLOAT_TYPE,
            Type.LONG_TYPE,
            Type.DOUBLE_TYPE -> NUMBER_TYPE
            else -> type
        }
    }
}