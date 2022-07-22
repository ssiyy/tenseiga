package com.siy.tenseiga.transform

import com.didiglobal.booster.transform.TransformContext
import com.siy.tenseiga.entity.ReplaceInfo
import com.siy.tenseiga.ext.*
import com.siy.tenseiga.inflater.TenseigaInflater
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.MethodInsnNode
import org.objectweb.asm.tree.MethodNode


/**
 *
 *替换的基本逻辑就是下面的啦
 *
 *  public void printLog(String str) {
 *       Log.e("siy", str);
 *   }
 *              |
 *              |
 *              |
 *              ↓
 *
 *  public void printLog(String str) {
 *     com_siy_tenseiga_HookJava_replaceHook(this, str);
 *  }
 *
 * private void printLog$___backup___(String var1) {
 *     Log.e("siy", var1);
 *  }
 *
 *
 *  private static void com_siy_tenseiga_HookJava_hookPrintLog(OrginJava var0, String var1) {
 *     boolean var2 = true;
 *     var0.printLog$___backup___(var1);
 *     Toast.makeText(App.INSTANCE, "replaceHook", 1).show();
 *  }
 *
 *
 *
 *
 *
 * @author  Siy
 * @since  2022/5/31
 */
class ReplaceClassNodeTransform(private val replaceInfos: List<ReplaceInfo>, cnt: ClassNodeTransform?) : ClassNodeTransform(cnt) {

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

    /**
     * 当前类所对应的ReplaceInfo，一个类可能对应几个ReplaceInfo
     */
    private lateinit var infos: List<ReplaceInfo>

    override fun visitorClassNode(context: TransformContext, klass: ClassNode) {
        super.visitorClassNode(context, klass)

        infos = replaceInfos.filter {
            it.targetClass == klass.name
        }

        if (infos.isNotEmpty()) {
            //如果有ReplaceInfo就把当前的klass记录下来
            this.klass = klass
        }
    }

    /**
     * 需要把 hookMethod过滤掉，不能套娃
     *
     * 判断当前方法是否是需要替换的方法
     *
     * @param context
     * @param clazz 当前所在类
     * @param method 当前所在方法
     * @param info 替换信息
     *
     */
    private fun checkMethodIsHook(context: TransformContext, clazz: ClassNode, method: MethodNode, info: ReplaceInfo): Boolean {
        //方法所在的类一样  或者是其子类
        val sameOwner = clazz.name == info.targetClass || (context.klassPool[info.targetClass].isAssignableFrom(clazz.name))
        //方法名一样
        val sameName = info.targetMethod == method.name
        //方法的描述一样
        val sameDesc = info.targetDesc == method.desc

        //当前找到的方法是不是hookMethod,不能套娃
        val isOrgHook = clazz.name == info.hookClass
        val isOrgMethod = (method.name == info.hookMethodNode.name) && (method.desc == info.hookMethodNode.desc)

        return (sameOwner && sameName && sameDesc) && !(isOrgHook && isOrgMethod)
    }

    override fun visitorMethod(context: TransformContext, method: MethodNode) {
        super.visitorMethod(context, method)
        if (isNativeMethod(method.access) || isAbstractMethod(method.access) || isInitMethod(method) || isCInitMethod(method)) {
            return
        }

        klass?.let { clazz ->
            infos.forEach { info ->
                if (checkMethodIsHook(context, clazz, method, info)) {
                    //判断一下hook方法和真实方法是不是都是静态的
                    if (isStaticMethod(info.hookMethodNode.access) != isStaticMethod(method.access)) {
                        throw IllegalStateException(info.hookClass + "." + info.hookMethodNode.name + " 应该有相同的静态标志 " + clazz.name + "." + method.name)
                    }

                    val backupTargetMethod = createBackupForTargetMethod(method)
                    val hookMethod = copyHookMethodAndReplacePlaceholder(info, backupTargetMethod)

                    replaceMethodBody(method) {
                        it.add(
                            MethodInsnNode(
                                getOpcodesByAccess(hookMethod.access),
                                klass?.name,
                                hookMethod.name,
                                hookMethod.desc
                            )
                        )
                    }
                }
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
    private fun copyHookMethodAndReplacePlaceholder(info: ReplaceInfo, methodNode: MethodNode): MethodNode {
        val newMethodDesc = descToStaticMethod(info.hookMethodNode.access, info.hookMethodNode.desc, info.targetClass)
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