package com.siy.tenseiga.transform

import com.didiglobal.booster.transform.TransformContext
import com.siy.tenseiga.entity.SerializableInfo
import org.objectweb.asm.tree.ClassNode


/**
 *
 * @author  Siy
 * @since  2022/7/29
 */
class SerializableNodeTransform(
    private val serializableInfos: List<SerializableInfo>,
    cnt: ClassNodeTransform?
) : ClassNodeTransform(cnt) {

    override fun visitorClassNode(context: TransformContext, klass: ClassNode) {
        super.visitorClassNode(context, klass)

       val contain =  serializableInfos.flatMap {
            it.targetClass
        }.contains(klass.name)

        if (contain){
           //如果包含就要处理
        }
    }
}