package com.siy.tenseiga.transform

import com.didiglobal.booster.transform.TransformContext
import com.siy.tenseiga.entity.SafeTryCatchHandlerInfo
import com.siy.tenseiga.ext.*
import com.siy.tenseiga.inflater.TenseigaInflater
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
import org.objectweb.asm.tree.*


/**
 *
 * @author  Siy
 * @since  2022/7/22
 */
class SafeTryCatchHandlerNodeTransform(
    private val safeTryCatchHandlerInfo: List<SafeTryCatchHandlerInfo>,
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

    private lateinit var infos: List<SafeTryCatchHandlerInfo>

    override fun visitorClassNode(context: TransformContext, klass: ClassNode) {
        super.visitorClassNode(context, klass)

        infos = safeTryCatchHandlerInfo.filter {
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

        if (infos.isNotEmpty()) {
            //如果有proxyInfo就把当前klass记录下来
            this.klass = klass
        }
    }


    override fun visitorMethod(context: TransformContext,klass: ClassNode, method: MethodNode) {
        super.visitorMethod(context, klass,method)

        if (isNativeMethod(method.access) || isAbstractMethod(method.access) || isInitMethod(method) || isCInitMethod(method)) {
            return
        }

        klass?.let { clazz ->
            infos.forEach { info ->
                val hookMethod = copyHookMethodAndReplacePlaceholder(info)
                addTryCatchHandler(method, hookMethod)
            }
        }
    }

    private fun addTryCatchHandler(methodNode: MethodNode, hookMethodNode: MethodNode) {
        val startLabelNode = LabelNode()
        val endLabelNode = LabelNode()
        val handlerNode = LabelNode()

        val insns = methodNode.instructions
        methodNode.tryCatchBlocks.add(TryCatchBlockNode(startLabelNode, endLabelNode, handlerNode, EXCEPTION_TYPE.internalName))

        insns.insertBefore(insns.first, startLabelNode)
        insns.add(endLabelNode)
        insns.add(handlerNode)
        insns.add(InsnNode(Opcodes.DUP))

        insns.add(
            MethodInsnNode(
                getOpcodesByAccess(hookMethodNode.access),
                klass?.name,
                hookMethodNode.name,
                hookMethodNode.desc
            )
        )

        //根据返回的类型，判断返回需要的Opcodes
        val returnType = Type.getReturnType(methodNode.desc)
        when (returnType.sort) {
            Type.VOID -> {
                insns.add(InsnNode(Opcodes.RETURN))
            }
            Type.BOOLEAN, Type.CHAR, Type.BYTE, Type.SHORT, Type.INT -> {
                insns.add(InsnNode(Opcodes.ICONST_0))
                insns.add(InsnNode(Opcodes.IRETURN))
            }
            Type.FLOAT -> {
                insns.add(InsnNode(Opcodes.FCONST_0))
                insns.add(InsnNode(Opcodes.FRETURN))
            }
            Type.LONG -> {
                insns.add(InsnNode(Opcodes.LCONST_0))
                insns.add(InsnNode(Opcodes.LRETURN))
            }
            Type.DOUBLE -> {
                insns.add(InsnNode(Opcodes.DCONST_0))
                insns.add(InsnNode(Opcodes.DRETURN))
            }
            else -> {
                insns.add(InsnNode(Opcodes.ACONST_NULL))
                insns.add(InsnNode(Opcodes.ARETURN))
            }
        }
    }


    /**
     * 把hook方法的方法体拷贝一份并且替换里面的placeholder
     *
     * @param info 替换相关信息的数据体，里面有要拷贝的方法
     *
     * @return 返回新生成的方法
     */
    private fun copyHookMethodAndReplacePlaceholder(info: SafeTryCatchHandlerInfo): MethodNode {
        val newMethodDesc = descToStaticMethod(info.hookMethodNode.access, info.hookMethodNode.desc, klass!!.name)
        val newMethodNname = info.hookClass.replace('/', '_').plus("_${info.hookMethodNode.name}")
        return klass?.methods?.find {
            it.desc == newMethodDesc && it.name == newMethodNname
        } ?:
        //新生成一个方法，把hook方法拷贝过来，方法变成静态方法，替换里面的Self占位符
        createMethod(
            Opcodes.ACC_PRIVATE + Opcodes.ACC_STATIC,
            newMethodNname,
            newMethodDesc,
            info.hookMethodNode.exceptions
        ) {
            it.instructions.add(tenseigaInflater?.inflate(info.cloneHookMethodNode(), null, null, SAFETRYCATCHHANDLER_TYPE))
        }.also {
            klass?.methods?.add(it)
        }
    }
}