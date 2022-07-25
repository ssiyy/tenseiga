package com.siy.tenseiga.transform

import com.didiglobal.booster.transform.TransformContext
import com.didiglobal.booster.transform.asm.filter
import com.siy.tenseiga.entity.SafeTryCatchHandlerInfo
import com.siy.tenseiga.ext.*
import com.siy.tenseiga.inflater.TenseigaInflater
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.LabelNode
import org.objectweb.asm.tree.MethodNode


/**
 *
 * @author  Siy
 * @since  2022/7/22
 */
class SafeTryCatchHandlerNodeTransform(private val safeTryCatchHandlerInfo: List<SafeTryCatchHandlerInfo>, cnt: ClassNodeTransform?) : ClassNodeTransform(cnt) {

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
            val filterPattern = it.filterPattern
            if (filterPattern.isEmpty()) {
                //如果没有过滤pattern，就不过滤
                true
            } else {
                val result = it.filterPattern.filter { pattern ->
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


    override fun visitorMethod(context: TransformContext, method: MethodNode) {
        super.visitorMethod(context, method)

        if (isNativeMethod(method.access) || isAbstractMethod(method.access) || isInitMethod(method) || isCInitMethod(method)) {
            return
        }

        klass?.let { clazz ->
            infos.forEach { info ->


                val hookMethod = copyHookMethodAndReplacePlaceholder(info)
            }
        }
    }

    private fun addTryCatchHandler(methodNode: MethodNode) {
        val startLabelNode = LabelNode()
        val endLabelNode = LabelNode()
        val exceptionHandlerNode = LabelNode()
        val returnLabelNode = LabelNode()

        val insns = methodNode.instructions
        insns.filter {
            //RETURN:
            //IRETURN
            //FRETURN
            //ARETURN
            //LRETURN
            //DRETURN
            //ATHROW
            (it.opcode >= Opcodes.IRETURN && it.opcode <= Opcodes.RETURN) || it.opcode == Opcodes.ATHROW
        }
    }


    /**
     * 把hook方法的方法体拷贝一份并且替换里面的placeholder
     *
     * @param info 替换相关信息的数据体，里面有要拷贝的方法
     * @param methodNode 被hook的方法，需要读取它的参数信息
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