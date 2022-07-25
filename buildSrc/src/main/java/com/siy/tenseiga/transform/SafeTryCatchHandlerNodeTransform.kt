package com.siy.tenseiga.transform

import com.didiglobal.booster.transform.TransformContext
import com.siy.tenseiga.entity.SafeTryCatchHandlerInfo
import com.siy.tenseiga.ext.*
import com.siy.tenseiga.inflater.TenseigaInflater
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.ClassNode
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

    private lateinit var infos:List<SafeTryCatchHandlerInfo>

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

        klass?.let{clazz->
            infos.forEach {info->



//                val backupTargetMethod = createBackupForTargetMethod(method)
//                val hookMethod = copyHookMethodAndReplacePlaceholder(info,backupTargetMethod)
            }
        }
    }

    /**
     * 给被hook的方法创建一个拷贝(主要是为了Invoker.invoke(...)调用原来逻辑)
     *
     * @param targetMethod 需要创建拷贝的方法
     *
     * @return 新生成的拷贝方法
     */
    private fun createBackupForTargetMethod(targetMethod: MethodNode): MethodNode {
        val newAccess: Int = targetMethod.access and (Opcodes.ACC_PROTECTED or Opcodes.ACC_PUBLIC).inv() or Opcodes.ACC_PRIVATE
        val newName = targetMethod.name + "${'$'}___backup___"
        return createMethod(newAccess, newName, targetMethod.desc, targetMethod.exceptions) {
            it.instructions.add(targetMethod.instructions)
        }.also {
            klass?.methods?.add(it)
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
    private fun copyHookMethodAndReplacePlaceholder(info: SafeTryCatchHandlerInfo, methodNode: MethodNode): MethodNode {
        val newMethodDesc = descToStaticMethod(info.hookMethodNode.access, info.hookMethodNode.desc, klass!!.name)
        val newMethodNname = info.hookClass.replace('/', '_').plus("_${info.hookMethodNode.name}")
        return klass?.methods?.find {
            it.desc == newMethodDesc && it.name == newMethodNname
        } ?:
        //新生成一个方法，把hook方法拷贝过来，方法变成静态方法，替换里面Invoker,Self占位符
        createMethod(
            Opcodes.ACC_PRIVATE + Opcodes.ACC_STATIC,
            newMethodNname,
            newMethodDesc,
            info.hookMethodNode.exceptions
        ) {
            it.instructions.add(tenseigaInflater?.inflate(info.cloneHookMethodNode(), methodNode, null, REPLACE_TYPE))
        }.also {
            klass?.methods?.add(it)
        }
    }
}