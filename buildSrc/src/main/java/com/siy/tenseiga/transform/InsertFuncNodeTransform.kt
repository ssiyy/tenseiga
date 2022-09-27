package com.siy.tenseiga.transform

import com.didiglobal.booster.transform.TransformContext
import com.siy.tenseiga.entity.InsertFuncInfo
import com.siy.tenseiga.ext.createMethod
import com.siy.tenseiga.ext.descToStaticMethod
import com.siy.tenseiga.inflater.TenseigaInflater
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.MethodNode


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

    private lateinit var infos: List<InsertFuncInfo>

    override fun visitorClassNode(context: TransformContext, klass: ClassNode) {
        super.visitorClassNode(context, klass)

        if (isTenseigaHookClass(klass)){
            return
        }

        infos = insertFuncInfo.filter {
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
            insertFuncToThisClass()
        }
    }

    private fun insertFuncToThisClass() {
        klass?.let {_->
            infos.forEach {
                copyHookMethod(it)
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
    private fun copyHookMethod(info: InsertFuncInfo): MethodNode {
        val newMethodDesc = descToStaticMethod(info.hookMethodNode.access, info.hookMethodNode.desc, klass!!.name)
        val newMethodNname = "${klass?.name?.replace('/', '_')}_insert_func_${info.hookMethodNode.name}"
        return klass?.methods?.find {
            //找一下有没有这个方法了如果有了就不创建了
            it.desc == newMethodDesc && it.name == newMethodNname
        } ?:
        //新生成一个方法，把hook方法拷贝过来，方法变成静态方法，替换里面的Self占位符
        createMethod(
            Opcodes.ACC_PRIVATE + Opcodes.ACC_STATIC,
            newMethodNname,
            newMethodDesc,
            info.hookMethodNode.exceptions
        ) {
            it.instructions.add(info.cloneHookMethodNode().instructions)
        }.also {
            klass?.methods?.add(it)
        }
    }
}