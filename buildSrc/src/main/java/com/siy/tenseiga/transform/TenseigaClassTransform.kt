package com.siy.tenseiga.transform

import com.android.build.api.transform.TransformInvocation
import com.didiglobal.booster.kotlinx.asIterable
import com.didiglobal.booster.kotlinx.touch
import com.didiglobal.booster.transform.TransformContext
import com.didiglobal.booster.transform.asm.ClassTransformer
import com.siy.tenseiga.asmtools.forDebug
import com.siy.tenseiga.entity.TExtension
import com.siy.tenseiga.entity.TransformInfo
import com.siy.tenseiga.ext.TypeUtil
import com.siy.tenseiga.parser.TenseigaParser
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.MethodInsnNode
import java.io.PrintWriter


/**
 *
 * 具体的Class转换
 *
 * @author  Siy
 * @since  2022/5/26
 */
class TenseigaClassTransform(private val extension: TExtension) : ClassTransformer {

    /**
     *日志输出流
     */
    private lateinit var logger: PrintWriter

    /**
     * hook转换的相关信息
     */
    private var transformInfo: TransformInfo? = null

    override fun onPreTransform(context: TransformContext) {
        this.logger = getReport(context, "report.txt").touch().printWriter()

        //获取class输入路径
        (context as? TransformInvocation)?.inputs?.asSequence()?.map {
            it.jarInputs + it.directoryInputs
        }?.flatten()?.map { input ->
            input.file
        }?.filter {
            it.isDirectory
        }?.let {
            transformInfo = TenseigaParser(extension).parse(it)

            logger.println(transformInfo.toString())
        }
    }

    /**
     * 注册转换相关的类
     */
    private fun registerTransform(): ClassNodeTransform? {
        if (transformInfo?.isEmpty() == true) {
            return null
        }

        var classNodeTransform: ClassNodeTransform? = null

        //注册一个ReplaceClassNodeTransform
        if (transformInfo?.replaceInfo?.isNotEmpty() == true) {
            classNodeTransform = ReplaceClassNodeTransform(
                transformInfo?.replaceInfo ?: listOf(),
                classNodeTransform
            )
        }

        //注册一个ProxyClassNodeTransform
        if (transformInfo?.proxyInfo?.isNotEmpty() == true) {
            classNodeTransform = ProxyClassNodeTransform(
                transformInfo?.proxyInfo ?: listOf(),
                classNodeTransform
            )
        }

        return classNodeTransform
    }


    override fun transform(context: TransformContext, klass: ClassNode): ClassNode {
        //如果没有注册任何转换器就直接返回
        val classNodeTransform = registerTransform() ?: return klass

        klass.let {
            classNodeTransform.visitorClassNode(context, it)
            it
        }.methods?.filter {
            TypeUtil.isNormalMethod(it)
        }?.flatMap {
            classNodeTransform.visitorMethod(context, it)
            it?.instructions?.iterator()?.asIterable()?.filterIsInstance(MethodInsnNode::class.java)
                ?: arrayListOf()
        }?.forEach {
            classNodeTransform.visitorInsnMethod(context, it)
        }

        return klass
    }


    override fun onPostTransform(context: TransformContext) {
        super.onPostTransform(context)
        forDebug(context, transformInfo, logger)
        this.logger.close()
    }
}