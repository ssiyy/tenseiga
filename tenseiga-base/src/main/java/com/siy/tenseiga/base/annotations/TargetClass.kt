package com.siy.tenseiga.base.annotations

/**
 * 需要被Hook的目标类的名字
 */
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
annotation class TargetClass(
    val value: String
)
