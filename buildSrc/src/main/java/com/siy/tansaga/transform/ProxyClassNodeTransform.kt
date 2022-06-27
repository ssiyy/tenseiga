package com.siy.tansaga.transform

import com.didiglobal.booster.transform.TransformContext
import com.didiglobal.booster.transform.asm.filter
import com.siy.tansaga.entity.ProxyInfo
import com.siy.tansaga.ext.*
import com.siy.tansaga.parser.OP_CALL
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
class ProxyClassNodeTransform(private val proxyInfos: List<ProxyInfo>, cnt: ClassNodeTransform?) : ClassNodeTransform(cnt) {

    /**
     * 如果不为空就是需要hook的类
     */
    private var klass: ClassNode? = null

    /**
     * 当前Transform需要转换的ProxyInfo,一个类可能对应几个ProxyInfo
     */
    private lateinit var infos: List<ProxyInfo>

    override fun visitorClassNode(context: TransformContext, klass: ClassNode) {
        super.visitorClassNode(context, klass)

        infos = proxyInfos.filter {
            val filterPattern = it.filterPattern
            if (filterPattern.isEmpty()){
                //如果没有过滤patern，就不过滤
                true
            }else {
                val result = it.filterPattern.filter { pattern ->
                    pattern.matcher(klass.name).matches()
                }
                result.isNotEmpty()
            }
        }

        if (infos.isNotEmpty()) {
            this.klass = klass

            errOut("-----xxxxxxxxxxxx--------------${klass.name}")
        }
    }

    //private fun


    override fun visitorInsnMethod(context: TransformContext, insnMethod: MethodInsnNode) {
        super.visitorInsnMethod(context, insnMethod)
        klass?.let { clazz ->
            for (info in infos) {
                val sameOwner = (insnMethod.owner == info.targetClass) || (context.klassPool[info.targetClass].isAssignableFrom(insnMethod.owner))
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
     * @param methodInsnNode 替换Origin方法调用的方法
     *
     * @return 返回新生成的方法
     */
    private fun copyHookMethodAndReplacePlaceholder(
        info: ProxyInfo,
        methodInsnNode: MethodInsnNode
    ): MethodNode {
        //新生成一个方法，把hook方法拷贝过来，方法变成静态方法，替换里面Origin,This占位符
        return createMethod(
            Opcodes.ACC_PRIVATE + Opcodes.ACC_STATIC,
            info.hookClass.replace('/', '_').plus("_${info.hookMethod.name}"),
            TypeUtil.descToStatic(info.hookMethod.access, info.hookMethod.desc, info.targetClass),
            info.hookMethod.exceptions
        ) {

            var hookMethod = info.hookMethod
            val newMethodNode = MethodNode(Opcodes.ASM7, hookMethod.access, hookMethod.name, hookMethod.desc, hookMethod.signature, hookMethod.exceptions.toTypedArray())
            val mv = AddLocalVarAdapter(Opcodes.ASM7, newMethodNode, hookMethod.access, hookMethod.name, hookMethod.desc)
            hookMethod.accept(mv)
            hookMethod = newMethodNode

            val insns = hookMethod.instructions

            val callInsns = insns.filter { insn ->
                insn.opcode == OP_CALL
            }

            callInsns.forEach { opcall ->
                //加载参数
                val ns = loadArgsAndInvoke(methodInsnNode, mv.slotIndex)
                //插入到Invoker.invoke之前
                insns.insertBefore(opcall, ns)
                //删除Invoker.invoke
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
     * @param methodInsnNode 需要调用的方法
     *
     * @param slotIndex
     *
     * @return 返回加载参数和方法调用的指令集
     */
    private fun loadArgsAndInvoke(methodInsnNode: MethodInsnNode, slotIndex: Int): InsnList {
        val insns = InsnList()

        insns.add(VarInsnNode(Opcodes.ASTORE, slotIndex))

        //判断一下调用的方法是不是静态的，如果不是静态就先加载一下this参数
        if (methodInsnNode.opcode != Opcodes.INVOKESTATIC) {
            insns.add(VarInsnNode(Opcodes.ALOAD, 0))
        }

        //加载方法传入参数
        val params = Type.getArgumentTypes(methodInsnNode.desc)
        params.forEachIndexed { index, type ->
            insns.add(VarInsnNode(Opcodes.ALOAD, slotIndex))
            when (index) {
                0 -> insns.add(InsnNode(Opcodes.ICONST_0))
                1 -> insns.add(InsnNode(Opcodes.ICONST_1))
                2 -> insns.add(InsnNode(Opcodes.ICONST_2))
                3 -> insns.add(InsnNode(Opcodes.ICONST_3))
                4 -> insns.add(InsnNode(Opcodes.ICONST_4))
                5 -> insns.add(InsnNode(Opcodes.ICONST_5))
                in 6..127 -> insns.add(IntInsnNode(Opcodes.BIPUSH, index))
                in 128..255 -> insns.add(IntInsnNode(Opcodes.SIPUSH, index))
            }
            insns.add(InsnNode(Opcodes.AALOAD))
            if (PrimitiveUtil.isPrimitive(type.descriptor)) {
                //如果是基本类型，就要拆箱成基本变量
                val owner = PrimitiveUtil.box(type.descriptor)
                insns.add(TypeInsnNode(Opcodes.CHECKCAST, PrimitiveUtil.virtualType(owner)))
                insns.add(
                    MethodInsnNode(
                        Opcodes.INVOKEVIRTUAL,
                        PrimitiveUtil.virtualType(owner),
                        PrimitiveUtil.unboxMethod(owner),
                        "()${type.descriptor}",
                        false
                    )
                )
            } else {
                //如果不是基本数据类型，是引用类型
                insns.add(TypeInsnNode(Opcodes.CHECKCAST, type.internalName))
            }
        }

        insns.add(MethodInsnNode(methodInsnNode.opcode, methodInsnNode.owner, methodInsnNode.name, methodInsnNode.desc))
        return insns
    }
}