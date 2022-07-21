package com.siy.tenseiga.parser

import com.siy.tenseiga.entity.*
import com.siy.tenseiga.ext.isAbstractMethod
import com.siy.tenseiga.ext.isCInitMethod
import com.siy.tenseiga.ext.isInitMethod
import com.siy.tenseiga.ext.isNativeMethod
import com.siy.tenseiga.interfaces.TransformParser
import org.objectweb.asm.ClassReader
import org.objectweb.asm.tree.ClassNode
import java.io.File


/**
 * 用来解析gradle文件配置的类
 * @author  Siy
 * @since  2022/5/31
 */
class TExtensionParser(private val extension: TExtension) : TransformParser {

    override fun parse(dir: Sequence<File>): TransformInfo {
        val transformInfo = TransformInfo()
        if (extension.isEmpty()) {
            return transformInfo
        }
        dir.forEach {
            parseReplaceInfo(it, extension.replaces, transformInfo)
            parseProxyInfo(it, extension.proxys, transformInfo)
        }
        return transformInfo
    }

    /**
     * 解析出替换的info
     */
    private fun parseReplaceInfo(file: File, replaceParams: Collection<ReplaceParam>, infos: TransformInfo) {
        replaceParams.forEach { rp ->
            val hookClass = File(file, rp.hookClass?.trim()?.replace(".", "\\").plus(".class"))
            if (hookClass.exists()) {
                hookClass.inputStream().use { fs ->
                    val cn = ClassNode()
                    ClassReader(fs).accept(cn, 0)
                    cn.methods.filter {
                        //过滤掉 静态代码块 构造方法 抽象方法 本地方法
                        !(isCInitMethod(it) || isInitMethod(it) || isAbstractMethod(it.access) || isNativeMethod(it.access))
                    }.forEach { mn ->
                        if (mn.name == rp.hookMethod) {
                            infos.replaceInfo.add(
                                ReplaceInfo(
                                    rp.targetClass!!.replace(".", "/"),
                                    rp.targetMethod!!,
                                    cn.name,
                                    mn
                                )
                            )
                        }
                    }
                }
            }
        }
    }

    /**
     * 解析出代理的info
     */
    private fun parseProxyInfo(file: File, proxysParams: Collection<ProxyParam>, infos: TransformInfo) {
        proxysParams.forEach { pp ->
            val hookClass = File(file, pp.hookClass?.trim()?.replace(".", "\\").plus(".class"))
            if (hookClass.exists()) {
                hookClass.inputStream().use { fs ->
                    val cn = ClassNode()
                    ClassReader(fs).accept(cn, 0)
                    cn.methods.filter {
                        //过滤掉 静态代码块 构造方法 抽象方法 本地方法
                        !(isCInitMethod(it) || isInitMethod(it) || isAbstractMethod(it.access) || isNativeMethod(it.access))
                    }.forEach { mn ->
                        if (mn.name == pp.hookMethod) {
                            infos.proxyInfo.add(
                                ProxyInfo(
                                    pp.targetClass!!.replace(".", "/"),
                                    pp.targetMethod!!,
                                    cn.name,
                                    mn,
                                    pp.filters
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}