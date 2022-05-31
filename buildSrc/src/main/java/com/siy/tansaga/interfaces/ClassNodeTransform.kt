package com.siy.tansaga.interfaces

import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.MethodInsnNode
import org.objectweb.asm.tree.MethodNode


/**
 *
 * @author  Siy
 * @since  2022/5/31
 */
interface ClassNodeTransform {

    fun visitorClassNode(klass:ClassNode)


    fun visitorMethod(method:MethodNode)


    fun visitorInsnMethod(insnMethod: MethodInsnNode)

}