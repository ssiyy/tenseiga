package com.siy.tansaga.base


/**
 *
 * @author  Siy
 * @since  2022/5/26
 */
object Origin {

    val CLASS_NAME = Origin.javaClass.name.replace('.', '/')

    @JvmStatic
    fun callVoid() = Unit

    @JvmStatic
    fun call() = Any()
}