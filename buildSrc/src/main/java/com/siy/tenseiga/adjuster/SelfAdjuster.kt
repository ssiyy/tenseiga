package com.siy.tenseiga.adjuster

import com.siy.tenseiga.ext.OPCODES_GETFIELD
import com.siy.tenseiga.ext.OPCODES_PUTFIELD
import com.siy.tenseiga.ext.REPLACE_TYPE
import com.siy.tenseiga.ext.isStaticMethod
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
//                methodVisitor.visitLdcInsn("newField");    //把这个指令移除了
//                methodVisitor.visitMethodInsn(INVOKESTATIC, "com/siy/tenseiga/base/Self", "getField", "(Ljava/lang/String;)Ljava/lang/Object;", false);
//                methodVisitor.visitVarInsn(ASTORE, 5);
                checkPlaceHolderAllow(insnNode.name)
                insnNode.opcode = OPCODES_GETFIELD
                insnNode.name = getFieldName(insnNode.previous)
                methodNode.instructions.remove(insnNode.previous)
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
            illegalState("Self.$placeHolder 只允许再${REPLACE_TYPE.internalName}中使用")
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


}