package com.siy.tansaga.ext

import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project


/**
 *
 * @author  Siy
 * @since  2022/5/30
 */


open class ReplaceParam
    (val name: String) {
    var replace: String? = null
    var targetClass: String? = null
    var hookClass: String? = null


    override fun toString(): String {
        return "ReplaceInfo{ targetClass=$targetClass, " +
                "replace=$replace, " +
                "hookClass=$hookClass, " +
                "sourceMethod=$name, "
    }
}


open class TExtension @JvmOverloads constructor(
    project: Project
) {
    val replaces = project.container(ReplaceParam::class.java)

    fun replaces(action: Action<NamedDomainObjectContainer<ReplaceParam>>) {
        action.execute(replaces)
    }
}