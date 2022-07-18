package com.siy.tenseiga.ext

import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
import org.objectweb.asm.tree.InsnList
import org.objectweb.asm.tree.InsnNode
import org.objectweb.asm.tree.MethodNode
import org.objectweb.asm.tree.VarInsnNode


/**
 *
 * @author  Siy
 * @since  2022/5/31
 */

/**
 * @param access 访问权
 * @param desc 方法描述
 * @param className 类
 */
fun descToStaticMethod(access: Int, desc: String, className: String): String {
    return if (isStaticMethod(access)) {
        desc
    } else {
        "(L" + className.replace('.', '/') + ";" + desc.substring(1)
    }
}

/**
 *
 * 根据[access]获取opcodes
 * @param access
 */
fun getOpcodesByAccess(access: Int): Int {
    return if (isStaticMethod(access)) {
        Opcodes.INVOKESTATIC
    } else {
        Opcodes.INVOKEVIRTUAL
    }
}

/**
 * 是否是构造方法
 */
fun isInitMethod(methodNode: MethodNode) = "<init>" == methodNode.name

/**
 * 是否是静态初始化块
 */
fun isCInitMethod(methodNode: MethodNode) = "<clinit>" == methodNode.name

/**
 * 是否是抽象方法
 */
fun isAbstractMethod(access: Int) = access and Opcodes.ACC_ABSTRACT != 0

/**
 * 是否是本地方法
 */
fun isNativeMethod(access: Int) = access and Opcodes.ACC_NATIVE != 0

/**
 * 是否是静态方法
 *
 * access & Opcodes.ACC_STATIC != 0
 *
 */
fun isStaticMethod(access: Int) = access and Opcodes.ACC_STATIC != 0

/**
 * 是否是静态方法调用
 */
fun isStaticMethodInsn(opcode: Int) = opcode == Opcodes.INVOKESTATIC

/**
 * @param access 方法的访问权限
 *
 * @param name 方法的名字
 *
 * @param desc 方法的描述符
 *
 * @param exceptions 方法抛出的异常
 *
 * @param action 方法体
 *
 *
 * 创建一个方法
 */
fun createMethod(
    access: Int,
    name: String,
    desc: String,
    exceptions: List<String>?,
    action: ((MethodNode) -> Unit)
): MethodNode {
    val methodNode = MethodNode(access, name, desc, null, exceptions?.toTypedArray())
    //操作
    action(methodNode)
    return methodNode
}

/**
 * 替换[method]的方法体
 * @param method 需要替换的方法
 *
 * @param action 新的方法体
 */
fun replaceMethodBody(method: MethodNode, action: ((InsnList) -> Unit)) {
    method.instructions.clear()
    val insnList = InsnList();
    //加载参数
    val params = Type.getArgumentTypes(method.desc)
    var index = 0;
    if (!isStaticMethod(method.access)) {
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