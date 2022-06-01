package com.siy.tansaga.ext

import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
import org.objectweb.asm.tree.*


/**
 *
 * @author  Siy
 * @since  2022/5/31
 */


/**
 * 创建一个方法
 */
fun createMethod(
    access: Int,
    name: String,
    desc: String,
    exceptions: List<String>?,
    action: ((InsnList) -> Unit)
): MethodNode {
    val methodNode = MethodNode(access, name, desc, null, exceptions?.toTypedArray())
    val insnList = InsnList()

    //加载参数
    val params = Type.getArgumentTypes(desc)
    var index = 0;
    if (!TypeUtil.isStatic(access)) {
        index++
        insnList.add(VarInsnNode(Opcodes.ALOAD, 0))
    }

    for (t in params) {
        insnList.add(VarInsnNode(t.getOpcode(Opcodes.ILOAD), index))
        index += t.size
    }

    //操作
    action(insnList)

    //返回值
    val ret = Type.getReturnType(desc)
    insnList.add(InsnNode(ret.getOpcode(Opcodes.IRETURN)))

    methodNode.instructions.add(insnList)
    return methodNode
}

/**
 * 替换方法体
 * @param method 需要替换的方法
 *
 * @param action 新的方法提
 */
fun replaceMethodBody(method: MethodNode, action: ((InsnList) -> Unit)) {
    method.instructions.clear()
    val insnList = InsnList();
    //加载参数
    val params = Type.getArgumentTypes(method.desc)
    var index = 0;
    if (!TypeUtil.isStatic(method.access)) {
        index++
        insnList.add(VarInsnNode(Opcodes.ALOAD, 0))
    }

    for (t in params) {
        insnList.add(VarInsnNode(t.getOpcode(Opcodes.ILOAD), index))
        index += t.size
    }

    //操作
    action(insnList)

    //返回值
    val ret = Type.getReturnType(method.desc)
    insnList.add(InsnNode(ret.getOpcode(Opcodes.IRETURN)))
    method.instructions.add(insnList)
}