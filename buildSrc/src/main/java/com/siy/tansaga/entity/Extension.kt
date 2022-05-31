package com.siy.tansaga.entity

import com.siy.tansaga.ext.join
import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project


/**
 *
 * @author  Siy
 * @since  2022/5/30
 */


open class ReplaceParam
/**
 *被替换的方法名字
 */
    (val name: String) {
    /**
     * 被替换方法所在的类
     */
    var targetClass: String? = null

    /**
     * hook方法
     */
    var hookMethod: String? = null

    /**
     * hook所在的类
     */
    var hookClass: String? = null
    internal var filters: List<String> = listOf()

    /**
     * 过滤的包名
     */
    fun filter(vararg filters: String) {
        this.filters = filters.toList()
    }


    override fun toString(): String {
        return "ReplaceParam{ targetClass=$targetClass, " +
                "targetMethod=$name, " +
                "hookMethod=$hookMethod, " +
                "hookClass=$hookClass, " +
                "filter=${filters.join(",")}}"
    }
}


open class TExtension constructor(
    project: Project
) {
    val replaces = project.container(ReplaceParam::class.java)

    val proxys = project.container(ReplaceParam::class.java)

    fun replaces(action: Action<NamedDomainObjectContainer<ReplaceParam>>) {
        action.execute(replaces)
    }

    fun proxys(action: Action<NamedDomainObjectContainer<ReplaceParam>>) {
        action.execute(proxys)
    }

    fun isEmpty() = replaces.isNullOrEmpty() && proxys.isNullOrEmpty()

}