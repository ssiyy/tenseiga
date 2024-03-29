package com.siy.tenseiga.ext

import com.didiglobal.booster.transform.TransformContext
import com.didiglobal.booster.transform.asm.asIterable
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
import org.objectweb.asm.tree.AbstractInsnNode
import org.objectweb.asm.tree.AnnotationNode
import org.objectweb.asm.tree.InsnList
import org.objectweb.asm.tree.MethodNode
import java.io.File

/**
 * 复制一份MethodNode
 */
fun MethodNode.cloneSelf(): MethodNode {
    val clone = MethodNode(Opcodes.ASM7, access, name, desc, signature, exceptions.toTypedArray())
    accept(clone)
    return clone
}

/**
 * 将注解的值转换成Map，
 */
val AnnotationNode.valueMaps: Map<String, Any>
    get() {
        val map = mutableMapOf<String, Any>()
        for (i in 0 until values.size / 2) {
            val key = values[i * 2]
            val value = values[i * 2 + 1]
            map[key as String] = value
        }
        return map
    }

/**
 * 获取value的值
 */
val AnnotationNode.value: Any?
    get() {
        return valueMaps["value"]
    }

/**
 * 是否是基本数据类型
 */
val Type.isPrimitive: Boolean
    get() {
        return when (sort) {
            Type.BYTE, Type.SHORT, Type.INT, Type.LONG, Type.FLOAT, Type.DOUBLE, Type.CHAR, Type.BOOLEAN -> true
            else -> false
        }
    }

/**
 * 如果是基本数据类型就装箱
 */
val Type.boxedType: Type
    get() {
        return when (sort) {
            Type.BYTE -> BYTE_TYPE
            Type.SHORT -> SHORT_TYPE
            Type.INT -> INTEGER_TYPE
            Type.LONG -> LONG_TYPE
            Type.FLOAT -> FLOAT_TYPE
            Type.DOUBLE -> DOUBLE_TYPE
            Type.CHAR -> CHARACTER_TYPE
            Type.BOOLEAN -> BOOLEAN_TYPE
            else -> this
        }
    }

/**
 * 如果是基本数据类型就拆箱
 */
val Type.unBoxedType: Type
    get() {
        return when (this) {
            BYTE_TYPE -> Type.BYTE_TYPE
            SHORT_TYPE -> Type.SHORT_TYPE
            INTEGER_TYPE -> Type.INT_TYPE
            LONG_TYPE -> Type.LONG_TYPE
            FLOAT_TYPE -> Type.FLOAT_TYPE
            DOUBLE_TYPE -> Type.DOUBLE_TYPE
            CHARACTER_TYPE -> Type.CHAR_TYPE
            BOOLEAN_TYPE -> Type.BOOLEAN_TYPE
            else -> this
        }
    }

fun <K> InsnList.groupBy(keySelector: (AbstractInsnNode) -> K): Map<K, List<AbstractInsnNode>> {
    return this.asIterable().groupBy(keySelector)
}

/**
 * 获取报告文件
 */
fun TransformContext.getReport(name: String): File {
    val report: File by lazy {
        val dir = File(File(reportsDir, name), name)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        val file = File(dir, name)
        if (!file.exists()) {
            file.createNewFile()
        }
        file
    }
    return report
}