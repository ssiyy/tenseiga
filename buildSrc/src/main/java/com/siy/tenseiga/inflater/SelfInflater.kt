package com.siy.tenseiga.inflater

import com.didiglobal.booster.transform.asm.filter
import com.siy.tenseiga.ext.OPCODES_PUTFIELD
import com.siy.tenseiga.parser.Inflater
import org.objectweb.asm.tree.AbstractInsnNode
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.InsnList
import org.objectweb.asm.tree.MethodNode


/**
 *
 * @author  Siy
 * @since  2022/7/15
 */
class SelfInflater(private val classNode: ClassNode) : Inflater {


    override fun inflate(methodNode: MethodNode, inflaterNode: AbstractInsnNode, replaceInsn: AbstractInsnNode?): InsnList {
        if (!haveSelfPutField(methodNode)) {
            return methodNode.instructions
        }

        val insn = methodNode.instructions

        val selfPutFields = insn.filter {
            it.opcode == OPCODES_PUTFIELD
        }

        selfPutFields.forEach {

            insn.remove(it)
        }

        return insn
    }

    private fun haveSelfPutField(methodNode: MethodNode): Boolean {
        return methodNode.instructions.filter {
            it.opcode == OPCODES_PUTFIELD
        }.isNotEmpty()
    }

}