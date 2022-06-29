package com.siy.tenseiga.base.annotations

/**
 * 用来代理需要hook的方法
 */
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
annotation class Proxy(
    val value: String
)
