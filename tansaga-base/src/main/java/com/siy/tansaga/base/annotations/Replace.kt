package com.siy.tansaga.base.annotations

@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class Replace(
    val value:String
)
