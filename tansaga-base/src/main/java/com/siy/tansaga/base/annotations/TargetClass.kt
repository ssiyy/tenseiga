package com.siy.tansaga.base.annotations

@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class TargetClass(
    val value: String
)
