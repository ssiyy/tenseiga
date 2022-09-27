package com.siy.tenseiga.base


/**
 * 用来表示调用被hook方法原逻辑的占位符
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

    /**
     * @param params 如果原来方法有参数，params就是传入的参数
     */
    @JvmStatic
    fun invoke(vararg params: Any?) = Any()

    @JvmStatic
    fun invokeVoid(vararg params: Any?) = Unit
}