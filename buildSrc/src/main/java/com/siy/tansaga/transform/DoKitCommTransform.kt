package com.siy.tansaga.transform

import com.siy.tansaga.asmtransformer.DoKitAsmTransformer
import com.didiglobal.booster.transform.Transformer
import com.siy.tansaga.ext.TExtension
import org.gradle.api.Project

/**
 * Represents the transform base
 * DoKitCommTransform 作用于 CommTransformer、BigImgTransformer、UrlConnectionTransformer、GlobalSlowMethodTransformer、EnterMethodStackTransformer
 * @author johnsonlee
 */
class DoKitCommTransform(androidProject: Project,t:TExtension) : TansagaBaseTransform(androidProject) {


    override val transformers = listOf<Transformer>(
        DoKitAsmTransformer(
            listOf(
                AsmMetaParserTransform(t)
            )
        )
    )

}
