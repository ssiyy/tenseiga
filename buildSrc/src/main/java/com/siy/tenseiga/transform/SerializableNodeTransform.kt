package com.siy.tenseiga.transform

import com.didiglobal.booster.transform.TransformContext
import com.siy.tenseiga.entity.SerializableInfo
import com.siy.tenseiga.inflater.TenseigaInflater
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

    private var tenseigaInflater: TenseigaInflater? = null

    /**
     * 如果不为空就是需要hook的类
     */
    private var klass: ClassNode? = null
        set(value) {
            value?.let {
                tenseigaInflater = TenseigaInflater(it)
            }
            field = value
        }

    override fun visitorClassNode(context: TransformContext, klass: ClassNode) {
        super.visitorClassNode(context, klass)

        serializableInfos.filter {
            it.targetClass == klass.name
        }

        if (serializableInfos.isNotEmpty()){
            this.klass = klass
        }
    }
}