package com.siy.tenseiga.adjuster

import com.siy.tenseiga.base.Invoker
import com.siy.tenseiga.base.Self
import com.siy.tenseiga.interfaces.NONE
import org.objectweb.asm.tree.AbstractInsnNode
import org.objectweb.asm.tree.MethodInsnNode
import org.objectweb.asm.tree.MethodNode


class AopMethodAdjuster constructor(private val methodNode: MethodNode) {

    private val CALL_REPLACER = InvokerAdjuster(methodNode)

    private val THIS_REPLACER = SelfAdjuster(methodNode)

    fun adjust() {
        var insn = methodNode.instructions.first
        while (insn != null) {
            if (insn is MethodInsnNode) {
                insn = transform(insn)
            }
            insn = insn.next
        }
    }

    private fun transform(node: MethodInsnNode): AbstractInsnNode {
        val owner = node.owner
        val name = node.name

        var replacer = NONE

        if (owner == Invoker.CLASS_NAME) {
            //如果是Invoker
            if (name.startsWith(Invoker.FUN_PREFIX)) {
                replacer = CALL_REPLACER
            }
        } else if (owner == Self.CLASS_NAME) {
            //Self
            replacer = THIS_REPLACER
        }
        return replacer.replace(node)
    }

}


 fun illegalState(msg: String) {
    throw IllegalStateException(msg)
}

