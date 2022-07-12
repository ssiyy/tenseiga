package com.siy.tenseiga.adjuster

import com.siy.tenseiga.ext.*
import com.siy.tenseiga.interfaces.NodeAdjuster
import org.codehaus.groovy.ast.tools.GenericsUtils.JAVA_LANG_OBJECT
import org.objectweb.asm.Opcodes
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

    private var retType: Int = 0

    private var returnDesc: String? = null

    init {
        val desc = methodNode.desc
        //返回类型的描述
        var retDesc = desc.substring(desc.lastIndexOf(")") + 1)
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
        checkReturnType(node)
        node.opcode = OP_CALL
        if (retType != VOID && returnDesc != JAVA_LANG_OBJECT) {
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
        val hasRet = !node.name.startsWith("callVoid")//占位符号有返回值类型
        val hasRetType = retType != VOID //替换的方法没有返回值类型
        if (hasRet != hasRetType) {
            illegalState("Called function " + node.owner + "." + node.name + "is illegal.")
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