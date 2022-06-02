package com.siy.tansaga

import com.didiglobal.booster.transform.TransformContext
import com.didiglobal.booster.transform.asm.filter
import com.siy.tansaga.entity.ProxyInfo
import com.siy.tansaga.ext.TypeUtil
import com.siy.tansaga.ext.createMethod
import com.siy.tansaga.interfaces.ClassNodeTransform
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
import org.objectweb.asm.tree.*


/**
 *
 * 代理的基本逻辑就是下面啦
 *
 * private void proxyHook(){
 *       plussss(1);
 *  }
 *
 *          |
 *          |
 *          |
 *          ↓
 *
 *  private void proxyHook() {
 *       com_siy_tansaga_HookJava_replacePlus(this, 1);
 *  }
 *
 *   private static int com_siy_tansaga_HookJava_replacePlus(OrginJava var0, int var1) {
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
class ProxyClassNodeTransform(private val proxyInfos: List<ProxyInfo>, cnt: ClassNodeTransform?) :
    BaseClassNodeTransform(cnt) {

    /**
     * 如果不为空就是需要hook的类
     */
    private var klass: ClassNode? = null


    override fun visitorClassNode(context: TransformContext, klass: ClassNode) {
        super.visitorClassNode(context, klass)

        proxyInfos.map {
            it.filterPattern
        }.forEach {
            if (it.isNullOrEmpty()) {
                this.klass = klass
                return
            } else {
                it.forEach { pattern ->
                    if (pattern.matcher(klass.name).matches()) {
                        this.klass = klass
                        return
                    }
                }
            }
        }
    }


    override fun visitorInsnMethod(context: TransformContext, insnMethod: MethodInsnNode) {
        super.visitorInsnMethod(context, insnMethod)
        klass?.let { clazz ->
            for (info in proxyInfos) {
                val sameOwner = (insnMethod.owner == info.targetClass) || (context.klassPool[info.targetClass].isAssignableFrom
                    (insnMethod.owner))
                val sameName = insnMethod.name == info.targetMethod
                val sameDesc = insnMethod.desc == info.targetDesc

                if (sameOwner && sameName && sameDesc) {
                    //判断一下hook方法和真实方法是不是都是静态的
                    if (TypeUtil.isStatic(info.hookMethod.access) != (insnMethod.opcode == Opcodes.INVOKESTATIC)) {
                        throw IllegalStateException(
                            info.hookClass + "." + info.hookMethod.name + " should have the same static flag with "
                                    + clazz.name + "." + insnMethod.name
                        )
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
     * @param methodNode 替换Origin方法调用的方法
     *
     * @return 返回新生成的方法
     */
    private fun copyHookMethodAndReplacePlaceholder(
        info: ProxyInfo,
        methodNode: MethodInsnNode
    ): MethodNode {
        //新生成一个方法，把hook方法拷贝过来，方法变成静态方法，替换里面Origin,This占位符
        return createMethod(
            Opcodes.ACC_PRIVATE + Opcodes.ACC_STATIC,
            info.hookClass.replace('/', '_').plus("_${info.hookMethod.name}"),
            TypeUtil.descToStatic(info.hookMethod.access, info.hookMethod.desc, info.targetClass),
            info.hookMethod.exceptions
        ) {
            val insns = info.hookMethod.instructions
            val callInsns = insns.filter {
                it.opcode == OP_CALL
            }

            callInsns.forEach { opcall ->
                val ns = loadArgsAndInvoke(methodNode)
                insns.insertBefore(opcall, ns)
                insns.remove(opcall)
            }

            it.add(insns)
        }.also {
            klass?.methods?.add(it)
        }
    }

    /**
     * 加载方法参数并且调用方法
     *
     * @param methodNode 需要调用的方法
     *
     * @return 返回加载参数和方法调用的指令集
     */
    private fun loadArgsAndInvoke(methodNode: MethodInsnNode): InsnList {
        val insns = InsnList()

        //加载参数
        val params = Type.getArgumentTypes(methodNode.desc)
        var index = 0;
        if (!TypeUtil.isStatic(methodNode.opcode)) {
            index++
            insns.add(VarInsnNode(Opcodes.ALOAD, 0))
        }

        for (t in params) {
            insns.add(VarInsnNode(t.getOpcode(Opcodes.ILOAD), index))
            index += t.size
        }
        insns.add(MethodInsnNode(methodNode.opcode, klass?.name, methodNode.name, methodNode.desc))
        return insns
    }
}