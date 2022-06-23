package com.siy.tansaga

import com.siy.tansaga.entity.*
import com.siy.tansaga.ext.TypeUtil
import com.siy.tansaga.ext.TypeUtil.isNormalMethod
import com.siy.tansaga.interfaces.TransformParser
import org.objectweb.asm.ClassReader
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.MethodNode
import java.io.File


/**
 *解析的类
 * @author  Siy
 * @since  2022/5/31
 */
class TansagaParser : TransformParser {

    override fun parse(extension: TExtension, dir: Iterator<File>): TransformInfo {
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
                        TypeUtil.isNormalMethod(it)
                    }.forEach { mn ->
                        if (mn.name == rp.hookMethod) {
                            transformPlaceholder(cn.name, mn)
                            infos.replaceInfo.add(
                                ReplaceInfo(
                                    rp.targetClass!!.replace(".", "/"),
                                    rp.name,
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
                        isNormalMethod(it)
                    }.forEach { mn ->
                        if (mn.name == pp.hookMethod) {
                            transformPlaceholder(cn.name, mn)
                            infos.proxyInfo.add(
                                ProxyInfo(
                                    pp.targetClass!!.replace(".", "/"),
                                    pp.name,
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


    /**
     * 转换一下占位符
     */
    private fun transformPlaceholder(sourceClass: String, methodNode: MethodNode) {
        AopMethodAdjuster(sourceClass, methodNode).adjust()
    }
}