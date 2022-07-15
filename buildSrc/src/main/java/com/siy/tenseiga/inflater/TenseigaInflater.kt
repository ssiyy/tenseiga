package com.siy.tenseiga.inflater

import com.didiglobal.booster.transform.asm.asIterable
import com.siy.tenseiga.ext.OPCODES_INVOKER
import com.siy.tenseiga.ext.OPCODES_PUTFIELD
import com.siy.tenseiga.ext.errOut
import org.objectweb.asm.tree.AbstractInsnNode
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.InsnList
import org.objectweb.asm.tree.MethodNode


/**
 *
 * @author  Siy
 * @since  2022/7/15
 */
class TenseigaInflater(classNode: ClassNode) {

    private val inflaterMap = mapOf(
        OPCODES_INVOKER to InvoderInflater,
        OPCODES_PUTFIELD to SelfInflater(classNode)
    )


    /**
     * @param methodNode
     *
     * @param abstractInsnNode
     */
    fun inflate(methodNode: MethodNode, abstractInsnNode: AbstractInsnNode?): InsnList {
        val insn = methodNode.instructions
        insn.asIterable().forEach {
           errOut("ffffffffffff:${inflaterMap[it.opcode]}")
            inflaterMap[it.opcode]?.inflate(methodNode, it, abstractInsnNode)
        }

        return insn
    }


}