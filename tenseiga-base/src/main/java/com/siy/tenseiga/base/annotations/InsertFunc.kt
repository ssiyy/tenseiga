package com.siy.tenseiga.base.annotations

/**
 * 会在类中插入随机的属性字段和调用空方法
 */
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
annotation class InsertFunc
