package com.siy.tansaga

import com.didiglobal.booster.transform.TransformContext
import com.siy.tansaga.interfaces.ClassNodeTransform
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.MethodInsnNode
import org.objectweb.asm.tree.MethodNode


/**
 *
 * @author  Siy
 * @since  2022/5/31
 */
open class BaseClassNodeTransform(private val cnt: ClassNodeTransform?) : ClassNodeTransform {

    override fun visitorClassNode(context: TransformContext, klass: ClassNode) {
        cnt?.visitorClassNode(context, klass)
    }

    override fun visitorMethod(context: TransformContext, method: MethodNode) {
        cnt?.visitorMethod(context, method)
    }

    override fun visitorInsnMethod(context: TransformContext, insnMethod: MethodInsnNode) {
        cnt?.visitorInsnMethod(context, insnMethod)
    }
}