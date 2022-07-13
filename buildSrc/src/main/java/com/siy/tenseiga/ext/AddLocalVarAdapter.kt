package com.siy.tenseiga.ext

import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Type
import org.objectweb.asm.commons.AdviceAdapter

/**
 *
 * 给方法添加一个Object[]局部变量
 *
 * @author Siy
 * @since 2022/6/24
 */
class AddLocalVarAdapter(
    api: Int,
    mv: MethodVisitor,
    access: Int,
    name: String,
    descriptor: String
) : AdviceAdapter(api, mv, access, name, descriptor) {

    private var mSlotIndex = -1
    val slotIndex
        get() = mSlotIndex


    override fun onMethodEnter() {
        mSlotIndex = newLocal(Type.getType(Array<Any>::class.java))
        mv.visitInsn(ICONST_0)
        mv.visitTypeInsn(ANEWARRAY, OBJECT_TYPE.internalName)
        mv.visitVarInsn(ASTORE, mSlotIndex)
    }
}