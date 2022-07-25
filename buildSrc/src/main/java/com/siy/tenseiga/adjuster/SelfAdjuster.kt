package com.siy.tenseiga.adjuster

import com.siy.tenseiga.ext.*
import com.siy.tenseiga.interfaces.NodeAdjuster
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
import org.objectweb.asm.tree.*


/**
 *
 * @author  Siy
 * @since  2022/7/12
 */
class SelfAdjuster(private val methodNode: MethodNode, private val transformType: Type) : NodeAdjuster {

    override fun replace(insnNode: MethodInsnNode): AbstractInsnNode {
        if (isStaticMethod(methodNode.access)) {
            illegalState("静态方法不应该调用这个函数")
        }
        when (insnNode.name) {
            "get" -> {
//                methodVisitor.visitMethodInsn(INVOKESTATIC, "com/siy/tenseiga/base/Self", "get", "()Ljava/lang/Object;", false);
//                methodVisitor.visitTypeInsn(CHECKCAST, "com/siy/tenseiga/test/OriginJava");   //把这个指令移除了
//                methodVisitor.visitVarInsn(ASTORE, 8);
                checkGetCast(insnNode.next)
                methodNode.instructions.remove(insnNode.next)

                //把Self.get()指令替换成变量
                val varInsnNode = VarInsnNode(Opcodes.ALOAD, 0)
                methodNode.instructions.set(insnNode, varInsnNode)
                return varInsnNode
            }

            "putField" -> {
//                methodVisitor.visitIntInsn(BIPUSH, 7);
//                methodVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
//                methodVisitor.visitLdcInsn("newField");       //把这个指令移除了
//                methodVisitor.visitMethodInsn(INVOKESTATIC, "com/siy/tenseiga/base/Self", "putField", "(Ljava/lang/Object;Ljava/lang/String;)V", false);     //把newField名字给它
                checkPlaceHolderAllow(insnNode.name)
                insnNode.opcode = OPCODES_PUTFIELD
                insnNode.name = getFieldName(insnNode.previous)
                methodNode.instructions.remove(insnNode.previous)
            }

            "getField" -> {
//                methodVisitor.visitLdcInsn("newField");      //把这个指令移除了
//                methodVisitor.visitMethodInsn(INVOKESTATIC, "com/siy/tenseiga/base/Self", "getField", "(Ljava/lang/String;)Ljava/lang/Object;", false);

                //如果是强转成基本数据类型就加3条指令
//                methodVisitor.visitTypeInsn(CHECKCAST, "java/lang/Number");
//                methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Number", "floatValue", "()F", false);
//                methodVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;", false);

//                methodVisitor.visitTypeInsn(CHECKCAST, "java/lang/Float");
//                methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Float", "floatValue", "()F", false);
//                methodVisitor.visitVarInsn(FSTORE, 5);
                checkPlaceHolderAllow(insnNode.name)
                insnNode.opcode = OPCODES_GETFIELD
                insnNode.name = getFieldName(insnNode.previous)
                methodNode.instructions.remove(insnNode.previous)
                checkCastType(insnNode)
            }
        }
        return insnNode
    }

    /**
     * Self.get() 必须要强转到需要的类型
     */
    private fun checkGetCast(insnNode: AbstractInsnNode) {
        if (insnNode !is TypeInsnNode) {
            illegalState("Get返回的对象类型应立即转换为Hook对象的类型。")
        }

        val typeInsnNode = insnNode as TypeInsnNode
        if (typeInsnNode.opcode != Opcodes.CHECKCAST) {
            illegalState("Get返回的对象类型应立即转换为Hook对象的类型。")
        }
    }

    /**
     * 检查当前placeHolder是否可用
     */
    private fun checkPlaceHolderAllow(placeHolder: String) {
        if (transformType != REPLACE_TYPE) {
            illegalState("Self.$placeHolder 只允许在${REPLACE_TYPE.internalName}中使用")
        }
    }

    /**
     * 获取字段名
     *
     * @param node
     */
    private fun getFieldName(node: AbstractInsnNode): String {
        if (node !is LdcInsnNode) {
            illegalState("只接受常量字符串作为字段名")
        }

        val ldc = node as LdcInsnNode
        if (ldc.cst !is String) {
            illegalState("只接受常量字符串作为字段名")
        }

        return ldc.cst as String
    }

    /**
     * 检查强转类型
     */
    private fun checkCastType(getFieldNode: AbstractInsnNode) {
        val nextNode = getFieldNode.next
        if (nextNode is TypeInsnNode && nextNode.opcode == Opcodes.CHECKCAST) {
            val type = Type.getObjectType(nextNode.desc)
            if (PrimitiveBox.isNumberType(type)) {
                //如果是基础数据类型的包装类型
                val insn = InsnList()
                val numberType = PrimitiveBox.typeToNumberType(type)
                insn.add(TypeInsnNode(Opcodes.CHECKCAST, numberType.internalName))
                insn.add(
                    MethodInsnNode(
                        Opcodes.INVOKEVIRTUAL,
                        numberType.internalName,
                        PrimitiveBox.unboxMethod[type.boxedType],
                        "()${type.unBoxedType.descriptor}",
                        false
                    )
                )

                insn.add(
                    MethodInsnNode(
                        Opcodes.INVOKESTATIC,
                        type.boxedType.internalName,
                        "valueOf",
                        "(${type.unBoxedType.descriptor})${type.boxedType.descriptor}",
                        false
                    )
                )

                methodNode.instructions.insert(getFieldNode, insn)
            }
        }
    }


}