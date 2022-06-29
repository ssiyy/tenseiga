package com.siy.tenseiga.base.annotations

/**
 * 用来过滤包名，哪些包需要被hook
 */
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
annotation class Filter(
    vararg val value:String
)