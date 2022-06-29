package com.siy.tenseiga.base.annotations

/**
 * 用来替换需要hook的方法
 */
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
annotation class Replace(
    val value: String
)
