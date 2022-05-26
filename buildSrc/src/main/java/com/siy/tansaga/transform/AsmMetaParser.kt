package com.siy.tansaga.transform

import com.didiglobal.booster.transform.TransformContext
import com.didiglobal.booster.transform.asm.ClassTransformer
import com.google.wireless.android.sdk.stats.ApplyChangesAgentError
import com.siy.tansaga.base.annotations.Replace
import com.siy.tansaga.base.annotations.TargetClass
import com.siy.tansaga.entity.ReplaceInfo
import com.siy.tansaga.ext.TypeUtil
import com.siy.tansaga.ext.asIterable
import com.siy.tansaga.ext.value
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.MethodInsnNode


/**
 *
 * @author  Siy
 * @since  2022/5/26
 */
class AsmMetaParserTransform : ClassTransformer {

    private val replaceInfos = mutableListOf<ReplaceInfo>()

    override fun transform(context: TransformContext, klass: ClassNode): ClassNode {
        filterReplaceInfo(klass)
        replaceMethod(context, klass)
        return klass
    }

    private fun filterReplaceInfo(klass: ClassNode) {
        klass.methods.forEach { methodNode ->
            methodNode.invisibleAnnotations?.forEach { annotationNode ->
                var targetMethod = ""
                var targetClass = ""
                if (annotationNode.desc == Type.getDescriptor(Replace::class.java)) {
                    targetMethod = annotationNode.value as String
                } else if (annotationNode.desc == Type.getDescriptor(TargetClass::class.java)) {
                    targetMethod = annotationNode.value as String
                }
                replaceInfos.add(ReplaceInfo(targetClass, targetMethod, klass.name, methodNode))
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
                         replaceInfos.find { replaceInfo ->
                            val sameDesc = methodInsnNode.desc == replaceInfo.targetDesc
                            val sameOwner = (methodInsnNode.owner == replaceInfo.targetClass
                                    || context.klassPool[replaceInfo.targetClass].isAssignableFrom(
                                methodInsnNode.owner
                            ))

                            val sameAccess =
                                TypeUtil.isStatic(replaceInfo.sourceMethod.access) == (methodInsnNode.opcode == Opcodes
                                    .INVOKESTATIC)

                            sameDesc && sameOwner && sameAccess
                        }?.let { info->
                            /* methodInsnNode.run {
                                 owner = info.sourceClass
                                 name = info.sourceMethod.name
                                 desc = info.sourceMethod.desc
                                 opcode = info.sourceMethod.access
                             }*/
                         }
                    }

            }
        }
    }

}