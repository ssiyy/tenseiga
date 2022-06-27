package com.siy.tansaga.base.transform

import com.didiglobal.booster.transform.Transformer
import com.siy.tansaga.base.asmtransformer.DoKitAsmTransformer
import com.siy.tansaga.entity.TExtension
import com.siy.tansaga.transform.TansagaTransform
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
                TansagaTransform(t)
            )
        )
    )

}
