package com.siy.tenseiga.entity

import com.siy.tenseiga.base.tools.join
import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project


/**
 *
 * @author  Siy
 * @since  2022/5/30
 */


open class ProxyParam constructor(
    /**
     * 不用管它给gradle TExtionsin用的
     *
     *   必须定义一个 name 属性，并且这个属性值初始化以后不要修改
     */
    private val name: String
) : Filter() {

    /**
     * 被替换的方法名
     */
    var targetMethod: String = name

    /**
     * 被替换方法所在的类,ClassFile类表示法
     */
    lateinit var targetClass: String

    /**
     * hook方法
     */
    lateinit var hookMethod: String

    /**
     * hook所在的类
     */
    lateinit var hookClass: String

    /**
     * 过滤的包名
     */
    fun filter(vararg filters: String) {
        this.filters = filters.toList()
    }


    override fun toString(): String {
        return "ProxyParam{ targetClass=$targetClass, " +
                "targetMethod=$targetMethod, " +
                "hookMethod=$hookMethod, " +
                "hookClass=$hookClass, " +
                "filter=${filters.join(",")}}"
    }
}


open class ReplaceParam constructor(
    /**
     * 不用管它给gradle TExtionsin用的
     *
     *   必须定义一个 name 属性，并且这个属性值初始化以后不要修改
     */
    private val name: String
) {
    /**
     * 被替换的方法名
     */
    var targetMethod: String = name

    /**
     * 被替换方法所在的类
     */
    lateinit var targetClass: String

    /**
     * hook方法
     */
    lateinit var hookMethod: String

    /**
     * hook所在的类
     */
    lateinit var hookClass: String


    override fun toString(): String {
        return "ReplaceParam{ targetClass=$targetClass, " +
                "targetMethod=$targetMethod, " +
                "hookMethod=$hookMethod, " +
                "hookClass=$hookClass}"
    }
}

open class SafeTryCatchParam constructor(
    /**
     * 不用管它给gradle TExtionsin用的
     *
     *   必须定义一个 name 属性，并且这个属性值初始化以后不要修改
     */
    private val name: String
) : Filter() {
    /**
     * hook方法
     */
    lateinit var hookMethod: String

    /**
     * hook所在的类
     */
    lateinit var hookClass: String

    /**
     * 过滤的包名
     */
    fun filter(vararg filters: String) {
        this.filters = filters.toList()
    }

    override fun toString(): String {
        return "SafeTryCatchParam{ " +
                "hookClass=$hookClass, " +
                "hookMethod=${hookMethod}, " +
                "filter=${filters.join(",")}}"
    }
}


open class TExtension constructor(
    project: Project
) {
    val replaces = project.container(ReplaceParam::class.java)

    val proxys = project.container(ProxyParam::class.java)

    val safeTryCatchs = project.container(SafeTryCatchParam::class.java)

    fun replaces(action: Action<NamedDomainObjectContainer<ReplaceParam>>) {
        action.execute(replaces)
    }

    fun proxys(action: Action<NamedDomainObjectContainer<ProxyParam>>) {
        action.execute(proxys)
    }

    fun safeTryCatchs(action: Action<NamedDomainObjectContainer<SafeTryCatchParam>>) {
        action.execute(safeTryCatchs)
    }
}