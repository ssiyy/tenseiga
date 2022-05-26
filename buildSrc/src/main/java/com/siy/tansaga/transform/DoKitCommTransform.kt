package com.siy.tansaga.transform

import com.siy.tansaga.asmtransformer.DoKitAsmTransformer
import com.didiglobal.booster.transform.Transformer
import org.gradle.api.Project

/**
 * Represents the transform base
 * DoKitCommTransform 作用于 CommTransformer、BigImgTransformer、UrlConnectionTransformer、GlobalSlowMethodTransformer、EnterMethodStackTransformer
 * @author johnsonlee
 */
class DoKitCommTransform(androidProject: Project) : TansagaBaseTransform(androidProject) {

    override val transformers = listOf<Transformer>(
        DoKitAsmTransformer(
            listOf(
                AsmMetaParserTransform()
            )
        )
    )

}
