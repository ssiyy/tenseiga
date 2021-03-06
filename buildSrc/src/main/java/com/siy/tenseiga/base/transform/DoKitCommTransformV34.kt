package com.siy.tenseiga.base.transform

import com.android.build.api.variant.VariantInfo
import com.didiglobal.booster.transform.Transformer
import com.siy.tenseiga.base.asmtransformer.DoKitAsmTransformer
import com.siy.tenseiga.entity.TExtension
import com.siy.tenseiga.transform.TenseigaClassTransform
import org.gradle.api.Project

class DoKitCommTransformV34(project: Project,t: TExtension) : DoKitBaseTransform(project) {



    override val transformers = listOf<Transformer>(
        DoKitAsmTransformer(
            listOf(
                TenseigaClassTransform(t)
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

