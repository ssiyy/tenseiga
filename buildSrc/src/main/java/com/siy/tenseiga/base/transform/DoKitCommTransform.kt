package com.siy.tenseiga.base.transform

import com.didiglobal.booster.transform.Transformer
import com.siy.tenseiga.base.asmtransformer.DoKitAsmTransformer
import com.siy.tenseiga.entity.TExtension
import com.siy.tenseiga.transform.TenseigaClassTransform
import org.gradle.api.Project

/**
 * Represents the transform base
 * DoKitCommTransform 作用于 CommTransformer、BigImgTransformer、UrlConnectionTransformer、GlobalSlowMethodTransformer、EnterMethodStackTransformer
 * @author johnsonlee
 */
class DoKitCommTransform(androidProject: Project,t: TExtension) : DoKitBaseTransform(androidProject) {


    override val transformers = listOf<Transformer>(
        DoKitAsmTransformer(
            listOf(
                TenseigaClassTransform(t)
            )
        )
    )

}
