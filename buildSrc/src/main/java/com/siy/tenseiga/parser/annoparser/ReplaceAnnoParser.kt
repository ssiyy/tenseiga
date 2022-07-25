package com.siy.tenseiga.parser.annoparser

import com.siy.tenseiga.entity.ReplaceInfo
import com.siy.tenseiga.entity.TransformInfo
import com.siy.tenseiga.ext.REPLACE_TYPE
import com.siy.tenseiga.ext.TARGETCLASS_TYPE
import com.siy.tenseiga.ext.illegalState
import com.siy.tenseiga.ext.value
import com.siy.tenseiga.interfaces.PlaceholderParser
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.MethodNode


/**
 *
 * @author  Siy
 * @since  2022/7/21
 */
object ReplaceAnnoParser : PlaceholderParser {

    override fun phParser(classNode: ClassNode, methodNode: MethodNode, transformInfo: TransformInfo) {
        val annotations = methodNode.visibleAnnotations
        var targetMethod: String? = null
        var targetClass: String? = null
        annotations.forEach {
            when (it.desc) {
                REPLACE_TYPE.descriptor -> {
                    targetMethod = it.value as? String
                }
                TARGETCLASS_TYPE.descriptor -> {
                    targetClass = (it.value as? String)?.replace(".", "/")
                }
            }
        }
        if (targetMethod.orEmpty().isNotEmpty() && targetClass.orEmpty().isNotEmpty()) {
            transformInfo.replaceInfo.add(
                ReplaceInfo(
                    targetClass!!.replace(".", "/"),
                    targetMethod!!,
                    classNode.name,
                    methodNode
                )
            )
        } else {
            illegalState("方法(${methodNode.name})使用$REPLACE_TYPE 需要 $TARGETCLASS_TYPE")
        }
    }
}