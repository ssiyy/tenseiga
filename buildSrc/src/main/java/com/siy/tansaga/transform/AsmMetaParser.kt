package com.siy.tansaga.transform

import com.didiglobal.booster.kotlinx.touch
import com.didiglobal.booster.transform.TransformContext
import com.didiglobal.booster.transform.asm.ClassTransformer
import com.google.wireless.android.sdk.stats.ApplyChangesAgentError
import com.siy.tansaga.base.annotations.Filter
import com.siy.tansaga.base.annotations.Replace
import com.siy.tansaga.base.annotations.TargetClass
import com.siy.tansaga.entity.ReplaceInfo
import com.siy.tansaga.ext.TypeUtil
import com.siy.tansaga.ext.asIterable
import com.siy.tansaga.ext.errOut
import com.siy.tansaga.ext.value
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.MethodInsnNode
import java.io.PrintWriter


/**
 *
 * @author  Siy
 * @since  2022/5/26
 */
class AsmMetaParserTransform : ClassTransformer {

    /**
     * 替代的结构体
     */
    private val replaceInfos = mutableListOf<ReplaceInfo>()


    override fun transform(context: TransformContext, klass: ClassNode): ClassNode {
        filterReplaceInfo(klass)
        replaceMethod(context, klass)
        return klass
    }

    private fun filterReplaceInfo(klass: ClassNode) {
        klass.methods.forEach { methodNode ->
            //这个方法的所有注解
            var targetMethod = ""
            var targetClass = ""
            var filter = ""
            methodNode.visibleAnnotations?.forEach { annotationNode ->
                if (annotationNode.desc == Type.getDescriptor(Replace::class.java)) {
                    targetMethod = annotationNode.value as String
                } else if (annotationNode.desc == Type.getDescriptor(TargetClass::class.java)) {
                    targetClass = (annotationNode.value as String).replace(".","/")
                } else if (annotationNode.desc == Type.getDescriptor(Filter::class.java)) {
                    filter = annotationNode.value as String
                }
            }
            if (targetMethod.isNotEmpty() && targetClass.isNotEmpty()) {
                replaceInfos.add(ReplaceInfo(targetClass, targetMethod, klass.name, methodNode, filter))
            }
        }
    }

    private fun replaceMethod(context: TransformContext, klass: ClassNode) {
        if (replaceInfos.isNotEmpty()) {
            klass.methods.forEach { methodNode ->
                methodNode.instructions
                    ?.iterator()
                    ?.asIterable()
                    ?.filterIsInstance(MethodInsnNode::class.java)
                    ?.forEach { methodInsnNode ->
                        for (info in replaceInfos) {
                            val sameDesc = methodInsnNode.desc == info.targetDesc
                            val sameOwner = methodInsnNode.owner == info.targetClass
                            val sameName = methodInsnNode.name == info.replace
                            if (sameDesc && sameOwner && sameName) {
                                methodInsnNode.run {
                                    logger.println("命中了before:--${owner}---------${name}---${desc}---${opcode}----${itf}-----")
                                    owner = info.sourceClass
                                    name = info.sourceMethod.name
                                    desc = info.sourceMethod.desc
                                    opcode = Opcodes.INVOKESTATIC
                                    itf = false

                                    logger.println("命中了after:--${owner}---------${name}---${desc}---${opcode}----${itf}-----")
                                }
                            }
                        }
                    }

            }
        }
    }

    private lateinit var logger: PrintWriter



    override fun onPreTransform(context: TransformContext) {
        this.logger = getReport(context, "report.txt").touch().printWriter()
    }



    override fun onPostTransform(context: TransformContext) {
        super.onPostTransform(context)
        this.logger.close()
        replaceInfos.forEach {
            errOut(it.toString())
        }
    }

}