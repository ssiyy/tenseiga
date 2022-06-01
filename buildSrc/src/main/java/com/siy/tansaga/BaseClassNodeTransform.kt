package com.siy.tansaga

import com.siy.tansaga.interfaces.ClassNodeTransform
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.MethodInsnNode
import org.objectweb.asm.tree.MethodNode


/**
 *
 * @author  Siy
 * @since  2022/5/31
 */
open class BaseClassNodeTransform( private val cnt: ClassNodeTransform?) : ClassNodeTransform {

    override fun visitorClassNode(klass: ClassNode) {
        cnt?.visitorClassNode(klass)
    }

    override fun visitorMethod(method: MethodNode) {
        cnt?.visitorMethod(method)
    }

    override fun visitorInsnMethod(insnMethod: MethodInsnNode) {
        cnt?.visitorInsnMethod(insnMethod)
    }
}