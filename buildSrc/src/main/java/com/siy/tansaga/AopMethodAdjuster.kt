package com.siy.tansaga

import com.siy.tansaga.ext.PrimitiveUtil
import com.siy.tansaga.interfaces.NodeReplacer
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.AbstractInsnNode
import org.objectweb.asm.tree.MethodInsnNode
import org.objectweb.asm.tree.TypeInsnNode


const val OP_CALL = Int.MAX_VALUE - 5555

const val JAVA_LANG_OBJECT = "java/lang/Object"

private const val VOID = 1

private const val REFERENCE = 2

private const val PRIMITIVE = 3


class AopMethodAdjuster {
}


private class CallReplacer(desc: String) : NodeReplacer {

    private var retType: Int = 0

    private var returnDesc: String? = null

    init {
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

        }

        if (retType == PRIMITIVE) {

        }
        return node
    }

    private fun checkReturnType(node: MethodInsnNode) {
        val hasRet = !node.name.startsWith("callVoid")
        val hasRetType = retType != VOID
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
        if (typeInsnNode.desc == returnDesc) {
            illegalState("Casted type is expected to be " + returnDesc + " , but is " + typeInsnNode.desc)
        }
    }

}

private fun illegalState(msg: String) {
    throw IllegalStateException(msg)
}

