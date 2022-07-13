package com.siy.tenseiga.base


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
    const val METHOD_PREFIX = "invoke"

    /**
     * 方法不带返回值的名字
     */
    const val METHOD_RETURN_VOID = "invokeVoid"

    @JvmStatic
    fun invoke(vararg params: Any) = Any()

    @JvmStatic
    fun invokeVoid(vararg params: Any) = Unit
}