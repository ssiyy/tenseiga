package com.siy.tenseiga.entity

import com.siy.tenseiga.base.tools.join
import org.objectweb.asm.tree.MethodNode
import java.util.regex.Pattern


/**
 *
 * classJar语法
 *
 * @author  Siy
 * @since  2022/5/26
 */
class ProxyInfo(
    /**
     * 被替换的类,internalName
     */
    targetClass: String,

    /**
     * 被替换的方法名
     */
    targetMethod: String,

    /**
     * 替换的类,internalName
     */
    hookClass: String,
    /**
     * 替换的方法
     */
    val hookMethodNode: MethodNode,
    /**
     * 需要替换哪些用了targetClass 的 replace的包
     */
    var filter: List<String> = listOf()
) : ProxyParam(targetMethod) {

    init {
        this.targetClass = targetClass
        this.hookClass = hookClass
    }

    /**
     * 是否包含内部类
     */
    private val withInnerClass = true

    /**
     * 配置内部类的后缀
     */
    private val REGEX_SUFFIX = "(|\\$.*)"

    /**
     * 过滤的正则表达式
     */
    val filterPattern = filter
        .map {
            if (withInnerClass) {
                "${it}$REGEX_SUFFIX"
            } else {
                it
            }
        }
        .map {
            Pattern.compile(it)
        }

    /**
     * 替换目标方法的desc
     */
    val targetDesc: String
        get() {
            return hookMethodNode.desc
        }


    override fun toString(): String {
        return "ProxyInfo{ targetClass=$targetClass, " +
                "targetMethod=$targetMethod, " +
                "hookClass=$hookClass, " +
                "hookMethod=${hookMethodNode.name}, " +
                "targetDesc=$targetDesc, " +
                "filter=${filter.join(",")}}"
    }
}