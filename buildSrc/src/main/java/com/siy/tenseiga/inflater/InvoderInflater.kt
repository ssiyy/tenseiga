package com.siy.tenseiga.inflater

import com.didiglobal.booster.transform.asm.filter
import com.siy.tenseiga.ext.*
import com.siy.tenseiga.parser.Inflater
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
import org.objectweb.asm.commons.AdviceAdapter
import org.objectweb.asm.tree.*


/**
 *用来处理Invoker
 *
 * @param invokeInsn 需要代替Invoker.invoke的指令
 *
 * @author  Siy
 * @since  2022/7/14
 */
object InvoderInflater : Inflater {

    override fun inflate(methodNode: MethodNode, inflaterNode: AbstractInsnNode, replaceInsn: AbstractInsnNode?): InsnList {
        if (!havaInvoker(methodNode)) {
            //判断下是否有invoker,没有就直接返回
            return methodNode.instructions
        }

        //判断下方法方法有没有参数，如果有就插入一个局部变量存放Invoker.invoke(...)传入的参数
        val newMethodNode = MethodNode(Opcodes.ASM7, methodNode.access, methodNode.name, methodNode.desc, methodNode.signature, methodNode.exceptions.toTypedArray())
        val addLocalVarAdapter = AddLocalVar(Opcodes.ASM7, newMethodNode, methodNode.access, methodNode.name, methodNode.desc, OBJECT_TYPE)
        methodNode.accept(addLocalVarAdapter)

        val insn = newMethodNode.instructions

        val invokerInsns = insn.filter {
            it.opcode == OPCODES_INVOKER
        }

        invokerInsns.forEach {
            val ns = loadArgsAndInvoke(replaceInsn, addLocalVarAdapter.slotIndex)
            ns?.let { newInsn ->
                insn.insertBefore(it, newInsn)
            }
            insn.remove(it)
        }

        return insn
    }

    /**
     * 加载方法参数并且调用方法
     *
     * @param invokeInsn 需要调用的方法
     *
     * @param slotIndex
     *
     * @return 返回加载参数和方法调用的指令集
     */
    private fun loadArgsAndInvoke(invokeInsn: AbstractInsnNode?, slotIndex: Int): InsnList? {
        if (invokeInsn !is MethodInsnNode) {
            errOut("11111111111111111111111111111111111111111111111111111111111111111111111111111111111")
            return null
        }
        errOut("2222222")

        val insns = InsnList()

        //把Invoker.invoke(...)传入的参数存储起来
        insns.add(VarInsnNode(Opcodes.ASTORE, slotIndex))

        if (invokeInsn.opcode != Opcodes.INVOKESTATIC) {
            //看看调用的方法是不是静态方法，如果不是就要加载一下0index变量
            insns.add(VarInsnNode(Opcodes.ALOAD, 0))
        }

        //加载方法传入参数
        val params = Type.getArgumentTypes(invokeInsn.desc)
        params.forEachIndexed { index, type ->
            insns.add(VarInsnNode(Opcodes.ALOAD, slotIndex))
            when (index) {
                0 -> insns.add(InsnNode(Opcodes.ICONST_0))
                1 -> insns.add(InsnNode(Opcodes.ICONST_1))
                2 -> insns.add(InsnNode(Opcodes.ICONST_2))
                3 -> insns.add(InsnNode(Opcodes.ICONST_3))
                4 -> insns.add(InsnNode(Opcodes.ICONST_4))
                5 -> insns.add(InsnNode(Opcodes.ICONST_5))
                in 6..127 -> insns.add(IntInsnNode(Opcodes.BIPUSH, index))
                in 128..255 -> insns.add(IntInsnNode(Opcodes.SIPUSH, index))
            }
            insns.add(InsnNode(Opcodes.AALOAD))
            if (type.isPrimitive) {
                //如果是基本类型，就要拆箱成基本变量
                val numberType = PrimitiveBox.typeToNumberType(type)
                insns.add(TypeInsnNode(Opcodes.CHECKCAST, numberType.internalName))
                insns.add(
                    MethodInsnNode(
                        Opcodes.INVOKEVIRTUAL,
                        numberType.internalName,
                        PrimitiveBox.unboxMethod[type.boxedType],
                        "()${type.descriptor}",
                        false
                    )
                )
            } else {
                //如果不是基本数据类型，是引用类型
                insns.add(TypeInsnNode(Opcodes.CHECKCAST, type.internalName))
            }
        }

        insns.add(invokeInsn)
        return insns
    }

    private fun havaInvoker(methodNode: MethodNode): Boolean {
        return methodNode.instructions.filter { insn ->
            insn.opcode == OPCODES_INVOKER
        }.isNotEmpty()
    }
}

private class AddLocalVar(
    api: Int,
    mv: MethodVisitor,
    access: Int,
    name: String,
    descriptor: String,
    private val localVar: Type
) : AdviceAdapter(api, mv, access, name, descriptor) {

    private var mSlotIndex = -1
    val slotIndex
        get() = mSlotIndex


    override fun onMethodEnter() {
        mSlotIndex = newLocal(Type.getType(Array<Any>::class.java))
        mv.visitInsn(ICONST_0)
        mv.visitTypeInsn(ANEWARRAY, localVar.internalName)
        mv.visitVarInsn(ASTORE, mSlotIndex)
    }
}