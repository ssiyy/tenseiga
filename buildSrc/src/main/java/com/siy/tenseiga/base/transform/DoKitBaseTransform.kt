package com.siy.tenseiga.base.transform

import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformInvocation
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.internal.pipeline.TransformManager
import com.didiglobal.booster.gradle.*
import com.didiglobal.booster.transform.AbstractKlassPool
import com.didiglobal.booster.transform.Transformer
import com.siy.tenseiga.base.BoosterTransformInvocation
import org.gradle.api.Project


/**
 *
 * @author  Siy
 * @since  2022/5/26
 */
open class DoKitBaseTransform protected constructor(val project: Project) : Transform() {

    /*transformers
     * Preload transformers as List to fix NoSuchElementException caused by ServiceLoader in parallel mode
     * booster 的默认出炉逻辑 DoKit已重写自处理
     */
    open val transformers = listOf<Transformer>()

    internal val verifyEnabled = project.getProperty(OPT_TRANSFORM_VERIFY, false)

    private val android: BaseExtension = project.getAndroid()

    private lateinit var androidKlassPool: AbstractKlassPool

    init {
        project.afterEvaluate {
            androidKlassPool = object : AbstractKlassPool(android.bootClasspath) {}
        }
    }

    val bootKlassPool: AbstractKlassPool
        get() = androidKlassPool

    override fun getName() = this.javaClass.simpleName

    override fun isIncremental() = !verifyEnabled

    override fun isCacheable() = !verifyEnabled

    override fun getInputTypes(): MutableSet<QualifiedContent.ContentType> =
        TransformManager.CONTENT_CLASS

    override fun getScopes(): MutableSet<in QualifiedContent.Scope> = when {
        transformers.isEmpty() -> mutableSetOf()
        project.plugins.hasPlugin("com.android.library") -> SCOPE_PROJECT
        project.plugins.hasPlugin("com.android.application") -> SCOPE_FULL_PROJECT
        project.plugins.hasPlugin("com.android.dynamic-feature") -> SCOPE_FULL_WITH_FEATURES
        else -> TODO("Not an Android project")
    }

    override fun getReferencedScopes(): MutableSet<in QualifiedContent.Scope> = when {
        transformers.isEmpty() -> when {
            project.plugins.hasPlugin("com.android.library") -> SCOPE_PROJECT
            project.plugins.hasPlugin("com.android.application") -> SCOPE_FULL_PROJECT
            project.plugins.hasPlugin("com.android.dynamic-feature") -> SCOPE_FULL_WITH_FEATURES
            else -> TODO("Not an Android project")
        }
        else -> super.getReferencedScopes()
    }

    final override fun transform(invocation: TransformInvocation) {
        BoosterTransformInvocation(invocation, this).apply {
            if (isIncremental) {
                doIncrementalTransform()
            } else {
                outputProvider?.deleteAll()
                doFullTransform()
            }
        }
    }



}

/**
 * The option for transform outputs verifying, default is false
 */
private const val OPT_TRANSFORM_VERIFY = "dokit.transform.verify"
