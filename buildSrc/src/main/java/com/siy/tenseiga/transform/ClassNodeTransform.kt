package com.siy.tenseiga.transform

import com.didiglobal.booster.transform.TransformContext
import com.siy.tenseiga.ext.TENSEIGA_TYPE
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.MethodInsnNode
import org.objectweb.asm.tree.MethodNode


/**
 * 类转换器,这里我有点儿模仿ASM coreApi的意思
 * @author  Siy
 * @since  2022/5/31
 */
abstract class ClassNodeTransform(private val cnt: ClassNodeTransform?) {

    private var classNode: ClassNode? = null

    /**
     *访问类
     */
    open fun visitorClassNode(context: TransformContext, klass: ClassNode) {
        this.classNode = klass
        cnt?.visitorClassNode(context, klass)
    }


    /**
     * 访问方法
     */
    open fun visitorMethod(context: TransformContext, method: MethodNode) {
        cnt?.visitorMethod(context, method)
    }

    /**
     * 访问方法的指令集
     */
    open fun visitorInsnMethod(context: TransformContext, insnMethod: MethodInsnNode) {
        cnt?.visitorInsnMethod(context, insnMethod)
    }

    /**
     * 是否hook的那个类
     */
    fun isTenseigaHookClass(klass: ClassNode? = classNode): Boolean {
        val tenseiga = klass?.visibleAnnotations?.find {
            it.desc == TENSEIGA_TYPE.descriptor
        }
        return tenseiga != null
    }

}