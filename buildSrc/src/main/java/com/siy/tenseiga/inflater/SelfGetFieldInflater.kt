package com.siy.tenseiga.inflater

import com.siy.tenseiga.ext.OBJECT_TYPE
import com.siy.tenseiga.ext.illegalState
import com.siy.tenseiga.parser.Inflater
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.*


/**
 *
 * @author  Siy
 * @since  2022/7/19
 */
class SelfGetFieldInflater(private val classNode: ClassNode) : Inflater {

    override fun inflate(methodNode: MethodNode, inflaterNodes: List<AbstractInsnNode>, replaceInsn: MethodInsnNode?) {
        if (inflaterNodes.isEmpty()) {
            return
        }

        val methodNodeInsn = methodNode.instructions

        inflaterNodes.filterIsInstance(MethodInsnNode::class.java).forEach {
            val ns = getFieldloadArg(it)
            methodNodeInsn.insertBefore(it, ns)
            methodNodeInsn.remove(it)
        }
    }

    private fun getFieldloadArg(insn: MethodInsnNode): InsnList {
        val exist = classNode.fields.any {
            it.name == insn.name
        }
        if (!exist) {
            //不存在字段
            illegalState("getField 的名字不存在")
        }

        val newInsn = InsnList()

        newInsn.add(VarInsnNode(Opcodes.ALOAD, 0))
        newInsn.add(FieldInsnNode(Opcodes.GETFIELD, classNode.name, insn.name, OBJECT_TYPE.descriptor))
        return newInsn
    }
}