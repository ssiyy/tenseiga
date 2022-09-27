package com.siy.tenseiga

import com.android.build.gradle.AppExtension
import com.didiglobal.booster.gradle.GTE_V3_4
import com.didiglobal.booster.gradle.getAndroid
import com.siy.tenseiga.base.transform.DoKitBaseTransform
import com.siy.tenseiga.base.transform.DoKitCommTransform
import com.siy.tenseiga.base.transform.DoKitCommTransformV34
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * @author  Siy
 */
class TenseigaPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        when {
            project.plugins.hasPlugin("com.android.application") || project.plugins.hasPlugin("com.android.dynamic-feature") -> {
                project.getAndroid<AppExtension>().registerTransform(commNewInstance(project))
            }
        }
    }


    private fun commNewInstance(project: Project): DoKitBaseTransform = when {
        GTE_V3_4 -> DoKitCommTransformV34(project)
        else -> DoKitCommTransform(project)
    }
}