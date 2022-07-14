package com.siy.tenseiga.adjuster

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
                checkGetCast(insnNode.next)
//                methodVisitor.visitMethodInsn(INVOKESTATIC, "com/siy/tenseiga/base/Self", "get", "()Ljava/lang/Object;", false);
//                methodVisitor.visitTypeInsn(CHECKCAST, "com/siy/tenseiga/test/OriginJava");   //把这个指令移除了
//                methodVisitor.visitVarInsn(ASTORE, 8);
                methodNode.instructions.remove(insnNode.next)

                //把Self.get()指令替换成变量
                val varInsnNode = VarInsnNode(Opcodes.ALOAD, 0)
                methodNode.instructions.set(insnNode, varInsnNode)
                return varInsnNode
            }

            "putField" -> {
                checkPlaceHolderAllow(insnNode.name)
                insnNode.opcode = OPCODES_PUTFIELD
            }

            "getField" -> {
                checkPlaceHolderAllow(insnNode.name)
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


}