package com.siy.tansaga

import com.android.build.gradle.AppExtension
import com.didiglobal.booster.gradle.getAndroid
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * @author  Siy
 */
class TansagaPlugin : Plugin<Project> {
    override fun apply(project: Project) {

        when {
            project.plugins.hasPlugin("com.android.application") || project.plugins.hasPlugin("com.android.dynamic-feature") -> {

                project.getAndroid<AppExtension>().let { androidExt ->

                }
            }
        }
    }
}