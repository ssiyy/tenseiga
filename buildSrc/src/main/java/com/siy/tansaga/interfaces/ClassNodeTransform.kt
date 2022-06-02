package com.siy.tansaga.interfaces

import com.didiglobal.booster.transform.TransformContext
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.MethodInsnNode
import org.objectweb.asm.tree.MethodNode


/**
 *
 * @author  Siy
 * @since  2022/5/31
 */
interface ClassNodeTransform {

    fun visitorClassNode(context: TransformContext, klass: ClassNode)


    fun visitorMethod(context: TransformContext, method: MethodNode)


    fun visitorInsnMethod(context: TransformContext, insnMethod: MethodInsnNode)

}