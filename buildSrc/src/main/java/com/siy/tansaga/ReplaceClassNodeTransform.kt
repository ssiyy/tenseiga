package com.siy.tansaga

import com.didiglobal.booster.transform.asm.filter
import com.siy.tansaga.entity.ReplaceInfo
import com.siy.tansaga.ext.TypeUtil
import com.siy.tansaga.ext.createMethod
import com.siy.tansaga.ext.replaceMethodBody
import com.siy.tansaga.interfaces.ClassNodeTransform
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
 *     com_siy_tansaga_HookJava_replaceHook(this, str);
 *  }
 *
 * private void printLog$___backup___(String var1) {
 *     Log.e("siy", var1);
 *  }
 *
 *
 *  private static void com_siy_tansaga_HookJava_hookPrintLog(OrginJava var0, String var1) {
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
class ReplaceClassNodeTransform(private val replaceInfos: List<ReplaceInfo>, cnt: ClassNodeTransform?) :
    BaseClassNodeTransform(cnt) {

    /**
     * 如果不为空就是需要hook的类
     */
    private var klass: ClassNode? = null

    override fun visitorClassNode(klass: ClassNode) {
        super.visitorClassNode(klass)
        if (replaceInfos.map {
                it.targetClass
            }.contains(klass.name)) {
            this.klass = klass
        }
    }

    override fun visitorMethod(method: MethodNode) {
        super.visitorMethod(method)
        klass?.let { clazz ->
            replaceInfos.forEach { info ->
                val sameOwner = clazz.name == info.targetClass
                val sameName = info.targetMethod == method.name
                val sameDesc = info.targetDesc == method.desc
                if (sameOwner && sameName && sameDesc) {
                    //判断一下hook方法和真实方法是不是都是静态的
                    if (((info.hookMethod.access xor method.access) and Opcodes.ACC_STATIC) != 0) {
                        throw IllegalStateException(
                            info.hookClass + "." + info.hookMethod.name + " should have the same static flag with "
                                    + clazz.name + "." + method.name
                        )
                    }

                    val backupTargetMethod = createBackupForTargetMethod(method)
                    val hookMethod = copyHookMethodAndReplacePlaceholder(info, backupTargetMethod)

                    replaceMethodBody(method) {
                        it.add(
                            MethodInsnNode(
                                TypeUtil.getOpcodeByAccess(hookMethod.access),
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
     *
     *
     * @param info 替换相关信息的数据体
     * @param methodNode 替换Origin方法调用的方法
     *
     * @return 返回新生成的方法
     */
    private fun copyHookMethodAndReplacePlaceholder(info: ReplaceInfo, methodNode: MethodNode): MethodNode {
        //新生成一个方法，把hook方法拷贝过来，方法变成静态方法，替换里面Origin,This占位符
        return createMethod(
            Opcodes.ACC_PRIVATE + Opcodes.ACC_STATIC,
            info.hookClass.replace('/', '_').plus("_${info.hookMethod.name}"),
            TypeUtil.descToStatic(info.hookMethod.access, info.hookMethod.desc, info.targetClass),
            info.hookMethod.exceptions
        ) {
            val insns = info.hookMethod.instructions

            val callInsns = insns.filter {insn->
                insn.opcode == OP_CALL
            }
            callInsns.forEach { opcall ->
                val ns = loadArgsAndInvoke(methodNode)
                insns.insertBefore(opcall, ns)
                insns.remove(opcall)
            }

            val getCallerInsns = insns.filter {insn->
                insn.opcode == GET_CALLER
            }

            getCallerInsns .forEach {

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
    private fun loadArgsAndInvoke(methodNode: MethodNode): InsnList {
        val insns = InsnList()

        //加载参数
        val params = Type.getArgumentTypes(methodNode.desc)
        var index = 0;
        if (!TypeUtil.isStatic(methodNode.access)) {
            index++
            insns.add(VarInsnNode(Opcodes.ALOAD, 0))
        }

        for (t in params) {
            insns.add(VarInsnNode(t.getOpcode(Opcodes.ILOAD), index))
            index += t.size
        }
        insns.add(MethodInsnNode(TypeUtil.getOpcodeByAccess(methodNode.access), klass?.name, methodNode.name, methodNode.desc))
        return insns
    }


}