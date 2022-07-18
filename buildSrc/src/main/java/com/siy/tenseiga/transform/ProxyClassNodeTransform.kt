package com.siy.tenseiga.transform

import com.didiglobal.booster.transform.TransformContext
import com.siy.tenseiga.entity.ProxyInfo
import com.siy.tenseiga.ext.*
import com.siy.tenseiga.inflater.TenseigaInflater
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.MethodInsnNode
import org.objectweb.asm.tree.MethodNode


/**
 *
 * 代理的基本逻辑就是下面啦
 *
 * private void proxyHook(){
 *       plus(1);
 *  }
 *
 *          |
 *          |
 *          |
 *          ↓
 *
 *  private void proxyHook() {
 *       com_siy_tenseiga_HookJava_replacePlus(this, 1);
 *  }
 *
 *   private static int com_siy_tenseiga_HookJava_replacePlus(OrginJava var0, int var1) {
 *       byte var2 = 2;
 *       int var3 = var2 + var0.plussss$___backup___(var1) + var0.plussss$___backup___(var1);
 *       Toast.makeText(App.INSTANCE, "replacePlus" + var3 + var0.plussss$___backup___(var1), 1).show();
 *       return var3;
 * }
 *
 *
 * @author  Siy
 * @since  2022/5/31
 */
class ProxyClassNodeTransform(private val proxyInfos: List<ProxyInfo>, cnt: ClassNodeTransform?) : ClassNodeTransform(cnt) {


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
     * 当前Transform需要转换的ProxyInfo,一个类可能对应几个ProxyInfo
     */
    private lateinit var infos: List<ProxyInfo>

    override fun visitorClassNode(context: TransformContext, klass: ClassNode) {
        super.visitorClassNode(context, klass)

        infos = proxyInfos.filter {
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

    private var mMethodNode: MethodNode? = null

    override fun visitorMethod(context: TransformContext, method: MethodNode) {
        super.visitorMethod(context, method)
        this.mMethodNode = method
    }

    /**
     * 判断当前方法调用的指令是否是调用的需要代理的方法
     *
     * @param context
     * @param insnMethod 被hook方法调用的那个指令
     * @param info
     */
    private fun checkMethodInsnIsHook(context: TransformContext, insnMethod: MethodInsnNode, info: ProxyInfo): Boolean {
        //方法所在的类一样  或者是其子类
        val sameOwner = (insnMethod.owner == info.targetClass) || (context.klassPool[info.targetClass].isAssignableFrom(insnMethod.owner))
        //方法名一样
        val sameName = insnMethod.name == info.targetMethod
        //方法的描述一样
        val sameDesc = insnMethod.desc == info.targetDesc

        //当前找到的方法是不是hookMethod,不能套娃
        val isOrgHook = klass?.name == info.hookClass
        val isOrgMethod = (mMethodNode?.name == info.hookMethod.name) && (mMethodNode?.desc == info.hookMethod.desc)

        return sameOwner && sameName && sameDesc && !(isOrgHook && isOrgMethod)
    }

    override fun visitorInsnMethod(context: TransformContext, insnMethod: MethodInsnNode) {
        super.visitorInsnMethod(context, insnMethod)
        klass?.let { clazz ->
            for (info in infos) {
                if (checkMethodInsnIsHook(context, insnMethod, info)) {
                    //判断一下hook方法和真实方法是不是都是静态的
                    if (isStaticMethod(info.hookMethod.access) != isStaticMethodInsn(insnMethod.opcode)) {
                        throw IllegalStateException(info.hookClass + "." + info.hookMethod.name + " 应该有相同的静态标志 " + clazz.name + "." + insnMethod.name)
                    }

                    val hookMethod = copyHookMethodAndReplacePlaceholder(info, insnMethod)

                    insnMethod.run {
                        owner = clazz.name
                        name = hookMethod.name
                        desc = hookMethod.desc
                        opcode = Opcodes.INVOKESTATIC
                        itf = false
                    }
                }
            }
        }
    }

    /**
     *
     *
     * @param info 替换相关信息的数据体
     * @param methodInsnNode 被hook方法调用的那个指令
     *
     * @return 返回新生成的方法
     */
    private fun copyHookMethodAndReplacePlaceholder(
        info: ProxyInfo,
        methodInsnNode: MethodInsnNode
    ): MethodNode {
        //新生成一个方法，把hook方法拷贝过来，方法变成静态方法，替换里面Invoker,Self占位符
        return createMethod(
            Opcodes.ACC_PRIVATE + Opcodes.ACC_STATIC,
            info.hookClass.replace('/', '_').plus("_${info.hookMethod.name}"),
            descToStaticMethod(info.hookMethod.access, info.hookMethod.desc, info.targetClass),
            info.hookMethod.exceptions
        ) {
            it.instructions.add(tenseigaInflater?.inflate(info.hookMethod, null, methodInsnNode, PROXY_TYPE))
        }.also {
            klass?.methods?.add(it)
        }
    }
}