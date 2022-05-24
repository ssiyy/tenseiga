package com.siy.tansaga

import com.didiglobal.booster.transform.TransformContext
import com.didiglobal.booster.transform.asm.ClassTransformer
import com.google.auto.service.AutoService
import org.objectweb.asm.tree.ClassNode


/**
 *
 * @author  Siy
 * @since  2022/5/24
 */
@AutoService(ClassTransformer::class)
class FirstClassTranformer : ClassTransformer {

    override fun transform(context: TransformContext, klass: ClassNode): ClassNode {
        println("Transforming ${klass.name}")
        return klass
    }
}