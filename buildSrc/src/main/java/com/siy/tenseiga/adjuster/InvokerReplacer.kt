package com.siy.tenseiga.adjuster

import com.siy.tenseiga.base.Invoker
import com.siy.tenseiga.base.Self
import com.siy.tenseiga.interfaces.NONE
import com.siy.tenseiga.interfaces.NodeAdjuster
import org.objectweb.asm.Type
import org.objectweb.asm.tree.AbstractInsnNode
import org.objectweb.asm.tree.MethodInsnNode
import org.objectweb.asm.tree.MethodNode

/**
 * 用来处理PlaceHolder
 */
class PlaceHolderNodeJuster constructor(
    private val methodNode: MethodNode,
    private val transformType: Type
    ) : NodeAdjuster {

    /**
     * 处理[Invoker]
     */
    private val invokerAdjuster = InvokerAdjuster(methodNode)

    /**
     * 处理[Self]
     */
    private val selfAdjuster = SelfAdjuster(methodNode,transformType)

    fun adjust() {
        var insn = methodNode.instructions.first
        while (insn != null) {
            if (insn is MethodInsnNode) {
                insn = replace(insn)
            }
            insn = insn.next
        }
    }

    override fun replace(insnNode: MethodInsnNode): AbstractInsnNode {
        val owner = insnNode.owner
        val name = insnNode.name

        var replacer = NONE

        if (owner == Invoker.CLASS_NAME) {
            //如果是Invoker
            if (name.startsWith(Invoker.METHOD_PREFIX)) {
                replacer = invokerAdjuster
            }
        } else if (owner == Self.CLASS_NAME) {
            //如果是Self
            replacer = selfAdjuster
        }
        return replacer.replace(insnNode)
    }

}


fun illegalState(msg: String) {
    throw IllegalStateException(msg)
}

