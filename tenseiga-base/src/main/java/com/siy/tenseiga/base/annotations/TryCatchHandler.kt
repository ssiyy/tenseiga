package com.siy.tenseiga.base.annotations

/**
 * 需要异常处理的类
 *
 *
 * 如果是单个方法，不需要用这个注解，用
 *  try{
 *  Self.invoker
 *  }catch(e:Exception){
 *      ...
 *  }
 */
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
annotation class TryCatchHandler(
    val value: String
)
