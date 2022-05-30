package com.siy.tansaga.transform

import com.android.build.api.variant.VariantInfo
import com.siy.tansaga.asmtransformer.DoKitAsmTransformer
import com.didiglobal.booster.transform.Transformer
import com.siy.tansaga.entity.TExtension
import org.gradle.api.Project

class DoKitCommTransformV34(project: Project,t: TExtension) : TansagaBaseTransform(project) {



    override val transformers = listOf<Transformer>(
        DoKitAsmTransformer(
            listOf(
                AsmMetaParserTransform(t)
            )
        )
    )

    @Suppress("UnstableApiUsage")
    override fun applyToVariant(variant: VariantInfo): Boolean {
        return variant.buildTypeEnabled || (variant.flavorNames.isNotEmpty() && variant.fullVariantEnabled)
    }

    @Suppress("UnstableApiUsage")
    private val VariantInfo.fullVariantEnabled: Boolean
        get() = project.findProperty("booster.transform.${fullVariantName}.enabled")?.toString()?.toBoolean() ?: true

    @Suppress("UnstableApiUsage")
    private val VariantInfo.buildTypeEnabled: Boolean
        get() = project.findProperty("booster.transform.${buildTypeName}.enabled")?.toString()?.toBoolean() ?: true

}

