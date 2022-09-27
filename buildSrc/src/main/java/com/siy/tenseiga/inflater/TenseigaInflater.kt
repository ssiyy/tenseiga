package com.siy.tenseiga.inflater

import com.siy.tenseiga.ext.*
import org.objectweb.asm.Type
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.InsnList
import org.objectweb.asm.tree.MethodInsnNode
import org.objectweb.asm.tree.MethodNode


/**
 *
 * @author  Siy
 * @since  2022/7/15
 */
class TenseigaInflater(private val klass: ClassNode) {


    /**
     * @param hookMethodNode
     *
     * @param methodNode
     *
     * @param replaceInsn
     *
     * @param type
     */
    fun inflate(hookMethodNode: MethodNode, methodNode: MethodNode?, replaceInsn: MethodInsnNode?, type: Type): InsnList {
        val insn = hookMethodNode.instructions

        val groupInsn = insn.groupBy {
            it.opcode
        }

        val invokers = groupInsn[OPCODES_INVOKER] ?: listOf()

        when (type) {
            REPLACE_TYPE -> {
                val invokeInsn = MethodInsnNode(getOpcodesByAccess(methodNode!!.access), klass.name, methodNode.name, methodNode.desc)
                InvoderInflater.inflate(hookMethodNode, invokers, invokeInsn)
            }

            PROXY_TYPE -> {
                val invokeInsn = MethodInsnNode(replaceInsn!!.opcode, replaceInsn.owner, replaceInsn.name, replaceInsn.desc)
                InvoderInflater.inflate(hookMethodNode, invokers, invokeInsn)
            }
        }
        //SAFETRYCATCHHANDLER_TYPE
        SelfPutFieldInflater(klass).inflate(hookMethodNode, groupInsn[OPCODES_PUTFIELD] ?: listOf(), null)
        SelfGetFieldInflater(klass).inflate(hookMethodNode, groupInsn[OPCODES_GETFIELD] ?: listOf(), null)
        return insn
    }


}