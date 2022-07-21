package com.siy.tenseiga.asmtools

import com.android.build.api.transform.DirectoryInput
import com.android.build.api.transform.Format
import com.android.build.api.transform.TransformInvocation
import com.didiglobal.booster.transform.TransformContext
import com.siy.tenseiga.entity.ProxyInfo
import com.siy.tenseiga.entity.ReplaceInfo
import com.siy.tenseiga.entity.TransformInfo
import org.objectweb.asm.ClassReader
import org.objectweb.asm.util.ASMifier
import org.objectweb.asm.util.Textifier
import org.objectweb.asm.util.TraceClassVisitor
import java.io.File
import java.io.PrintWriter

/**
 * 用来调式
 *
 */
fun forDebug(context: TransformContext, transformInfo: TransformInfo?, logger: PrintWriter) {
    logger.println("\n\n")
    logger.println("---------------------------------------------------------------------    下面是ASM CODE    ---------------------------------------------------------------------")

    val transformInvocation = context as? TransformInvocation

    val provider = transformInvocation?.outputProvider

    transformInvocation?.inputs?.asSequence()?.map {
        it.jarInputs + it.directoryInputs
    }?.flatten()?.map { input ->
        val format = if (input is DirectoryInput) Format.DIRECTORY else Format.JAR
        provider?.getContentLocation(input.name, input.contentTypes, input.scopes, format)
    }?.filter {
        it?.isDirectory == true
    }?.forEach { file ->
        printClassAsmCode(file, "com.siy.tenseiga.test.OriginJava",logger)
        printClassAsmCode(file, "com.siy.tenseiga.MainActivity",logger)
    }
}

private fun forReplaceDebug(file: File, replaceInfos: List<ReplaceInfo>, logger: PrintWriter) {
    replaceInfos.forEach {
        val targetFile = File(file, it.targetClass.replace("/", "\\").plus(".class"))

        if (targetFile.exists()) {
            targetFile.inputStream().use { fs ->
                val parsingOptions = ClassReader.SKIP_FRAMES or ClassReader.SKIP_DEBUG
                val asmCode = true

                val printer = if (asmCode) ASMifier() else Textifier()
                val traceClassVisitor = TraceClassVisitor(null, printer, logger)
                ClassReader(fs).accept(traceClassVisitor, parsingOptions)
            }
        }
    }
}

private fun forProxyDebug(file: File, proxyInfo: List<ProxyInfo>, logger: PrintWriter) {
    val targetFile = File(file, "com.siy.tenseiga.MainActivity".replace(".", "\\").plus(".class"))

    if (targetFile.exists()) {
        targetFile.inputStream().use { fs ->
            val parsingOptions = ClassReader.SKIP_FRAMES or ClassReader.SKIP_DEBUG
            val asmCode = true

            val printer = if (asmCode) ASMifier() else Textifier()
            val traceClassVisitor = TraceClassVisitor(null, printer, logger)
            ClassReader(fs).accept(traceClassVisitor, parsingOptions)
        }
    }
}

/**
 *@param className 类的全类名 com.siy.tenseiga.MainActivity
 */
private fun printClassAsmCode(file: File?, className: String, logger: PrintWriter) {
    val targetFile = File(file, className.replace(".", "\\").plus(".class"))

    if (targetFile.exists()) {
        targetFile.inputStream().use { fs ->
            val parsingOptions = ClassReader.SKIP_FRAMES or ClassReader.SKIP_DEBUG
            val asmCode = true

            val printer = if (asmCode) ASMifier() else Textifier()
            val traceClassVisitor = TraceClassVisitor(null, printer, logger)
            ClassReader(fs).accept(traceClassVisitor, parsingOptions)
        }
    }
}