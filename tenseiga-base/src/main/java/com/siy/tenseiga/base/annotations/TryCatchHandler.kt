package com.siy.tenseiga.base.annotations

/**
 * 需要异常处理的方法
 */
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
annotation class TryCatchHandler(
    val value: String
)
