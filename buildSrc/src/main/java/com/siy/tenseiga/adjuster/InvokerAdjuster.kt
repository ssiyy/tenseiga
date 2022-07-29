package com.siy.tenseiga.adjuster

import com.siy.tenseiga.base.Invoker
import com.siy.tenseiga.ext.*
import com.siy.tenseiga.interfaces.NodeAdjuster
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
import org.objectweb.asm.tree.AbstractInsnNode
import org.objectweb.asm.tree.MethodInsnNode
import org.objectweb.asm.tree.MethodNode
import org.objectweb.asm.tree.TypeInsnNode


/**
 *
 * @author  Siy
 * @since  2022/7/12
 */
class InvokerAdjuster constructor(private val methodNode: MethodNode, private val transformType: Type) : NodeAdjuster {

    private val methodType = Type.getMethodType(methodNode.desc)

    /**
     * hook方法的返回值类型
     */
    private val hookMethodReturnType = methodType.returnType


    /**
     * hook方法的返回值类型如果是基本数据类型就将其转换成对应的包装类型
     */
    private val boxHookMethodReturnType = hookMethodReturnType.boxedType

    /**
     * @param  insnNode 占位符所在的方法指令 ，如 Invoker.invoke(args)
     */
    override fun replace(insnNode: MethodInsnNode): AbstractInsnNode {
        //检查一下是否Invoker是否可用
        checkPlaceHolderAllow(insnNode.name)
        //检查一下返回值类型
        checkReturnType(insnNode)
        //替换成自己的opcode
        insnNode.opcode = OPCODES_INVOKER
        if (boxHookMethodReturnType != Type.VOID_TYPE && boxHookMethodReturnType != OBJECT_TYPE) {
            checkCast(insnNode.next)
//            methodVisitor.visitMethodInsn(INVOKESTATIC, "com/siy/tenseiga/base/Invoker", "invoke", "([Ljava/lang/Object;)Ljava/lang/Object;", false);
//            methodVisitor.visitTypeInsn(CHECKCAST, "java/lang/Integer");    //把这个指令移除了   这里不需要强转，因为这里会替换成原来的调用，返回的本来就是原本的数据类型
//            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I", false);
//            methodVisitor.visitVarInsn(ISTORE, 9);

            methodNode.instructions.remove(insnNode.next)
        }

        if (hookMethodReturnType.isPrimitive) {
//            methodVisitor.visitMethodInsn(INVOKESTATIC, "com/siy/tenseiga/base/Invoker", "invoke", "([Ljava/lang/Object;)Ljava/lang/Object;", false);
//            methodVisitor.visitTypeInsn(CHECKCAST, "java/lang/Integer");    //把这个指令移除了   这里不需要强转，因为这里会替换成原来的调用，返回的本来就是原本的数据类型
//            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I", false);   //把这个指令移除了
//            methodVisitor.visitVarInsn(ISTORE, 9);
            checkUnbox(insnNode.next)
            methodNode.instructions.remove(insnNode.next)
        }
        return insnNode
    }

    /**
     * 检查当前placeHolder是否可用
     */
    private fun checkPlaceHolderAllow(placeHolder: String) {
        if (transformType == SAFETRYCATCHHANDLER_TYPE || transformType == SERIALIZABLE_TYPE) {
            illegalState("Invoker.$placeHolder 不允许在${transformType.internalName}中使用")
        }
    }

    /**
     * 如果是基本数据类型,就检查一下拆箱
     *
     * @param insnNode 占位符所在的方法指令 ，如 Invoker.invoke(args)
     */
    private fun checkUnbox(insnNode: AbstractInsnNode) {
        if (insnNode !is MethodInsnNode) {
            //如果不是方法调用
            illegalState("请不要自行拆箱1.")
        }
        val methodInsnNode = insnNode as MethodInsnNode
        if (methodInsnNode.owner != boxHookMethodReturnType.internalName) {
            illegalState("请不要自行拆箱2.")
        }
        if (methodInsnNode.name != PrimitiveBox.unboxMethod[boxHookMethodReturnType]) {
            illegalState("请不要自行拆箱3.")
        }
    }

    /**
     * 检查一下hook方法的返回值类型和 占位符 Invoker.invoke(R.string.next) 返回值类型是否兼容
     *
     * @param insnNode 占位符所在的方法指令 ，如 Invoker.invoke(args)
     */
    private fun checkReturnType(insnNode: MethodInsnNode) {
        //占位符号的方法名显示有返回值类型
        val hasRet = !insnNode.name.startsWith(Invoker.METHOD_RETURN_VOID)
        //替换的方法没有返回值类型
        val hasRetType = boxHookMethodReturnType != Type.VOID_TYPE
        if (hasRet != hasRetType) {
            illegalState("错误的方式调用 " + insnNode.owner + "." + insnNode.name + "方法.")
        }
    }

    /**
     * 检查一下Invoker.invoke(args)强转的类型是否和hook方法返回值类型是否一样
     *
     *
     * @param insnNode 占位符所在的方法指令 ，如 Invoker.invoke(args)
     */
    private fun checkCast(insnNode: AbstractInsnNode) {
        if (insnNode !is TypeInsnNode) {
            illegalState("Invoker返回的对象类型应立即转换为Hook方法的类型。")
        }

        val typeInsnNode = insnNode as TypeInsnNode
        if (typeInsnNode.opcode != Opcodes.CHECKCAST) {
            illegalState("Invoker返回的对象类型应立即转换为Hook方法的类型。")
        }

        if (typeInsnNode.desc != boxHookMethodReturnType.internalName) {
            illegalState("Invoker方法需要的返回值类型： " + boxHookMethodReturnType.internalName + " , 但是转换的是 " + typeInsnNode.desc)
        }
    }
}