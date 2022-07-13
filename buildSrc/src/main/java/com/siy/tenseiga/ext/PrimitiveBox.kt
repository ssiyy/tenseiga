package com.siy.tenseiga.ext


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
        BOOLEAN_TYPE to "booleanValue",
        CHARACTER_TYPE to "charValue",
        BYTE_TYPE to "byteValue",
        SHORT_TYPE to "shortValue",
        INTEGER_TYPE to "intValue",
        FLOAT_TYPE to "floatValue",
        LONG_TYPE to "longValue",
        DOUBLE_TYPE to "doubleValue"
    )
}