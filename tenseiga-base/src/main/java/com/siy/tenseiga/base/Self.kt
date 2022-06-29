package com.siy.tenseiga.base


/**
 *
 * @author  Siy
 * @since  2022/5/31
 */
object Self {

    val CLASS_NAME  = Self.javaClass.name.replace('.', '/')

    /**
     * 获取实例方法的对象
     */
    @JvmStatic
    fun get() = Any()
}