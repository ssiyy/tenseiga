package com.siy.tansaga

import com.android.build.gradle.AppExtension
import com.didiglobal.booster.gradle.GTE_V3_4
import com.didiglobal.booster.gradle.getAndroid
import com.siy.tansaga.transform.DoKitCommTransform
import com.siy.tansaga.transform.DoKitCommTransformV34
import com.siy.tansaga.transform.TansagaBaseTransform
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * @author  Siy
 */
class TansagaPlugin : Plugin<Project> {
    override fun apply(project: Project) {

        when {
            project.plugins.hasPlugin("com.android.application") || project.plugins.hasPlugin("com.android.dynamic-feature") -> {
                project.getAndroid<AppExtension>().registerTransform(commNewInstance(project))
            }
        }
    }


    private fun commNewInstance(project: Project): TansagaBaseTransform = when {
        GTE_V3_4 -> DoKitCommTransformV34(project)
        else -> DoKitCommTransform(project)
    }
}