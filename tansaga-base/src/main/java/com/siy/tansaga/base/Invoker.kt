package com.siy.tansaga.base


/**
 *
 * @author  Siy
 * @since  2022/5/26
 */
object Invoker {

    /**
     * 类的internalName名字
     */
    val CLASS_NAME = Invoker.javaClass.name.replace('.', '/')

    /**
     * 方法名字的前缀
     */
    const val FUN_PREFIX = "invoke"

    @JvmStatic
    fun invoke(vararg params: Any) = Any()

    @JvmStatic
    fun invokeVoid(vararg params: Any) = Unit
}