package com.siy.tenseiga.base.annotations

@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.CLASS)
annotation class Serializable(
    /**
     * 类名
     */
    vararg val value:String,
    val withUUid:Boolean = false
)
