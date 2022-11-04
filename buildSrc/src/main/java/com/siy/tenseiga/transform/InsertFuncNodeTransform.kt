package com.siy.tenseiga.transform

import com.didiglobal.booster.transform.TransformContext
import com.didiglobal.booster.transform.asm.isInterface
import com.didiglobal.booster.transform.asm.simpleName
import com.siy.tenseiga.entity.InsertFuncInfo
import com.siy.tenseiga.ext.STRING_TYPE
import com.siy.tenseiga.ext.illegalState
import com.siy.tenseiga.ext.isAbstractMethod
import com.siy.tenseiga.ext.isNativeMethod
import com.siy.tenseiga.inflater.TenseigaInflater
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.*
import kotlin.random.Random


/**
 *
 * @author  Siy
 * @since  2022/7/22
 */
class InsertFuncNodeTransform(
    private val insertFuncInfo: List<InsertFuncInfo>,
    cnt: ClassNodeTransform?
) : ClassNodeTransform(cnt) {

    private var tenseigaInflater: TenseigaInflater? = null

    /**
     * 如果不为空就是需要hook的类
     */
    private var klass: ClassNode? = null
        set(value) {
            value?.let {
                tenseigaInflater = TenseigaInflater(it)
            }
            field = value
        }

    private lateinit var includeInfos: List<InsertFuncInfo>

    override fun visitorClassNode(context: TransformContext, klass: ClassNode) {
        super.visitorClassNode(context, klass)

        if (isTenseigaHookClass(klass)) {
            return
        }

        val excludeInfos = insertFuncInfo.filter {
            val excludePattern = it.excludePattern
            if (excludePattern.isEmpty()) {
                //如果没有过滤pattern，就不过滤
                false
            } else {
                val result = it.excludePattern.filter { pattern ->
                    pattern.matcher(klass.name).matches()
                }
                result.isNotEmpty()
            }
        }

        if (excludeInfos.isNotEmpty()) {
            //如果是需要排除就直接返回
            return
        }

        includeInfos = insertFuncInfo.filter {
            val filterPattern = it.includePattern
            if (filterPattern.isEmpty()) {
                //如果没有过滤pattern，就不过滤
                true
            } else {
                val result = it.includePattern.filter { pattern ->
                    pattern.matcher(klass.name).matches()
                }
                result.isNotEmpty()
            }
        }


        if (includeInfos.isNotEmpty()) {
            //如果有proxyInfo就把当前klass记录下来
            this.klass = klass
            putFieldloadArg(klass)
        }
    }

    private fun putFieldloadArg(classNode: ClassNode) {

        val insn = "${classNode.simpleName}_${Random.nextInt(99999)}"

        if (classNode.fields.any {
                it.name == insn
            }) {
            //如果已经存在了字段
            illegalState("插入的字段已经存在")
        }
        //创建字段
        val fieldNode = if (classNode.isInterface) {
            FieldNode(Opcodes.ACC_PUBLIC or Opcodes.ACC_FINAL or Opcodes.ACC_STATIC, insn, STRING_TYPE.descriptor, null, null)
        } else {
            FieldNode(Opcodes.ACC_PRIVATE, insn, STRING_TYPE.descriptor, null, null)
        }
        classNode.fields.add(fieldNode)
    }

    override fun visitorMethod(context: TransformContext, method: MethodNode) {
        super.visitorMethod(context, method)

        if (isNativeMethod(method.access) || isAbstractMethod(method.access)) {
            return
        }

        klass?.let {
            includeInfos.forEach { info ->
                val first = method.instructions.first
                method.instructions.insertBefore(first, InsnList().apply {
                    add(MethodInsnNode(Opcodes.INVOKESTATIC, info.hookClass, info.hookMethodNode.name, info.hookMethodNode.desc, false))
                })
            }
        }
    }
}