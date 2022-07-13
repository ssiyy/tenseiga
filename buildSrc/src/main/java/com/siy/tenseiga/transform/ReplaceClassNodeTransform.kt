package com.siy.tenseiga.transform

import com.didiglobal.booster.transform.TransformContext
import com.didiglobal.booster.transform.asm.filter
import com.siy.tenseiga.entity.ReplaceInfo
import com.siy.tenseiga.ext.*
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
import org.objectweb.asm.tree.*


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

    /**
     * 如果不为空就是需要hook的类
     */
    private var klass: ClassNode? = null

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
     * 判断当前方法是否是需要替换的方法
     */
    private fun checkMethodIsHook(context: TransformContext, clazz: ClassNode, method: MethodNode, info: ReplaceInfo): Boolean {
        //方法所在的类一样  或者是其子类
        val sameOwner = clazz.name == info.targetClass || (context.klassPool[info.targetClass].isAssignableFrom(clazz.name))
        //方法名一样
        val sameName = info.targetMethod == method.name
        //方法的描述一样
        val sameDesc = info.targetDesc == method.desc
        return sameOwner && sameName && sameDesc
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
                    if (isStaticMethod(info.hookMethod.access) != isStaticMethod(method.access)) {
                        throw IllegalStateException(info.hookClass + "." + info.hookMethod.name + " 应该有相同的静态标志 " + clazz.name + "." + method.name)
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
     * 给被hook的方法创建一个拷贝
     *
     * @param targetMethod 需要创建拷贝的方法
     *
     * @return 新生成的拷贝方法
     */
    private fun createBackupForTargetMethod(targetMethod: MethodNode): MethodNode {
        val newAccess: Int = targetMethod.access and (Opcodes.ACC_PROTECTED or Opcodes.ACC_PUBLIC).inv() or Opcodes.ACC_PRIVATE
        val newName = targetMethod.name + "${'$'}___backup___"
        return createMethod(newAccess, newName, targetMethod.desc, targetMethod.exceptions) {
            it.add(targetMethod.instructions)
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
        //新生成一个方法，把hook方法拷贝过来，方法变成静态方法，替换里面Origin,This占位符
        return createMethod(
            Opcodes.ACC_PRIVATE + Opcodes.ACC_STATIC,
            info.hookClass.replace('/', '_').plus("_${info.hookMethod.name}"),
            descToStaticMethod(info.hookMethod.access, info.hookMethod.desc, info.targetClass),
            info.hookMethod.exceptions
        ) {
            var hookMethod = info.hookMethod
            val newMethodNode = MethodNode(Opcodes.ASM7, hookMethod.access, hookMethod.name, hookMethod.desc, hookMethod.signature, hookMethod.exceptions.toTypedArray())
            val mv = AddLocalVarAdapter(Opcodes.ASM7, newMethodNode, hookMethod.access, hookMethod.name, hookMethod.desc)
            hookMethod.accept(mv)
            hookMethod = newMethodNode


            val insns = hookMethod.instructions

            val callInsns = insns.filter { insn ->
                insn.opcode == OPCODES_INVOKER
            }

            callInsns.forEach { opcall ->
                val ns = loadArgsAndInvoke(methodNode, mv.slotIndex)
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
     * @param slotIndex
     *
     * @return 返回加载参数和方法调用的指令集
     */
    private fun loadArgsAndInvoke(methodNode: MethodNode, slotIndex: Int): InsnList {
        val insns = InsnList()

        insns.add(VarInsnNode(Opcodes.ASTORE, slotIndex))

        if (!isStaticMethod(methodNode.access)) {
            insns.add(VarInsnNode(Opcodes.ALOAD, 0))
        }

        //加载方法传入参数
        val params = Type.getArgumentTypes(methodNode.desc)
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
            if (type.isPrimitive) {
                //如果是基本类型，就要拆箱成基本变量
                val numberType = PrimitiveBox.typeToNumberType(type)
                insns.add(TypeInsnNode(Opcodes.CHECKCAST, numberType.internalName))
                insns.add(
                    MethodInsnNode(
                        Opcodes.INVOKEVIRTUAL,
                        numberType.internalName,
                        PrimitiveBox.unboxMethod[type.boxedType],
                        "()${type.descriptor}",
                        false
                    )
                )
            } else {
                //如果不是基本数据类型，是引用类型
                insns.add(TypeInsnNode(Opcodes.CHECKCAST, type.internalName))
            }
        }

        insns.add(MethodInsnNode(getOpcodesByAccess(methodNode.access), klass?.name, methodNode.name, methodNode.desc))
        return insns
    }
}