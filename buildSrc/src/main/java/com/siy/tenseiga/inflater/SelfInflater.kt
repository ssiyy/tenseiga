package com.siy.tenseiga.inflater

import com.siy.tenseiga.adjuster.illegalState
import com.siy.tenseiga.ext.OBJECT_TYPE
import com.siy.tenseiga.parser.Inflater
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.*


/**
 *
 * @author  Siy
 * @since  2022/7/15
 */
class SelfInflater(private val classNode: ClassNode) : Inflater {


    override fun inflate(methodNode: MethodNode, inflaterNodes: List<AbstractInsnNode>, replaceInsn: MethodInsnNode?) {
        if (inflaterNodes.isEmpty()) {
            return
        }

        val methodNodeInsn = methodNode.instructions

        inflaterNodes.filterIsInstance(MethodInsnNode::class.java).forEach {
            val ns = putFieldloadArg(it)
            methodNodeInsn.insertBefore(it, ns)
            methodNodeInsn.remove(it)
        }
    }

    private fun putFieldloadArg(insn: MethodInsnNode): InsnList {
        if (classNode.fields.any {
                it.name == insn.name
            }) {
            //如果已经存在了字段
            illegalState("putField 的名字已经存在")
        }

        //创建字段
        classNode.fields.add(FieldNode(Opcodes.ACC_PRIVATE, insn.name, OBJECT_TYPE.descriptor, null, null))

        val newInsn = InsnList()

        newInsn.add(VarInsnNode(Opcodes.ALOAD, 0))
        newInsn.add(InsnNode(Opcodes.SWAP))
        newInsn.add(FieldInsnNode(Opcodes.PUTFIELD, classNode.name, insn.name, OBJECT_TYPE.descriptor))
        return newInsn
    }

}