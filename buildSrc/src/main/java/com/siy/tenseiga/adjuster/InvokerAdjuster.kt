package com.siy.tenseiga.adjuster

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


 class InvokerAdjuster constructor(private val methodNode: MethodNode) : NodeAdjuster {

//    private var retType: Int = 0

//    private var returnDesc: String? = null

    private val returnType = Type.getReturnType(methodNode.desc)

    init {
        val desc = methodNode.desc
        //返回类型的描述
//        var retDesc = desc.substring(desc.lastIndexOf(")") + 1)

//val returnType = Type.getReturnType(methodNode.desc)



        if (retDesc == "V") {//void
            retType = VOID
        } else if (retDesc.endsWith(";") || retDesc[0] == '[') {//object or array
            retType = REFERENCE

            if (retDesc[0] != '[' && retDesc.endsWith(";")) {//convert to internal
                retDesc = retDesc.substring(1, retDesc.length - 1)
            }
            returnDesc = retDesc
        } else {//primitive
            retType = PRIMITIVE
            returnDesc = PrimitiveUtil.box(retDesc)
        }
    }


    override fun replace(node: MethodInsnNode): AbstractInsnNode {
        //检查一下返回值类型
        checkReturnType(node)
        //替换成自己的opcode
        node.opcode = OP_CALL
        if (returnType != Type.VOID_TYPE && returnType != OBJECT_TYPE) {
            checkCast(node.next)
//            INVOKESTATIC me/ele/lancet/base/Origin.call ()Ljava/lang/Object;
//            CHECKCAST com/sample/playground/Cup   把这个指令移除了
//            ARETURN
            methodNode.instructions.remove(node.next)
        }

        if (retType == PRIMITIVE) {
            checkUnbox(node.next)
            methodNode.instructions.remove(node.next)
        }
        return node
    }

    private fun checkReturnType(node: MethodInsnNode) {
        //占位符号的方法名显示有返回值类型
        val hasRet = !node.name.startsWith("invokeVoid")
        //替换的方法没有返回值类型
        val hasRetType = returnType != Type.VOID_TYPE
        if (hasRet != hasRetType) {
            illegalState("错误的方式调用 " + node.owner + "." + node.name + "方法.")
        }
    }

    private fun checkCast(insnNode: AbstractInsnNode) {
        if (insnNode !is TypeInsnNode) {
            illegalState("Returned Object type should be cast to origin type immediately.")
        }

        val typeInsnNode = insnNode as TypeInsnNode
        if (typeInsnNode.opcode != Opcodes.CHECKCAST) {
            illegalState("Returned Object type should be cast to origin type immediately.")
        }
        if (typeInsnNode.desc != returnDesc) {
            illegalState("Casted type is expected to be " + returnDesc + " , but is " + typeInsnNode.desc)
        }
    }

    private fun checkUnbox(insnNode: AbstractInsnNode) {
        if (insnNode !is MethodInsnNode) {
            illegalState("Please don't unbox by your self.")
        }
        val methodInsnNode = insnNode as MethodInsnNode
        if (methodInsnNode.owner != returnDesc) {
            illegalState("Please don't unbox by your self.")
        }
        if (methodInsnNode.name != PrimitiveUtil.unboxMethod(returnDesc)) {
            illegalState("Please don't unbox by your self.")
        }
    }

}