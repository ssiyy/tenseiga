package com.siy.tenseiga.transform

import com.didiglobal.booster.transform.TransformContext
import com.siy.tenseiga.entity.TryCatchHandlerInfo
import com.siy.tenseiga.ext.isAbstractMethod
import com.siy.tenseiga.ext.isCInitMethod
import com.siy.tenseiga.ext.isInitMethod
import com.siy.tenseiga.ext.isNativeMethod
import com.siy.tenseiga.inflater.TenseigaInflater
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.MethodNode


/**
 *
 * @author  Siy
 * @since  2022/7/22
 */
class TryCatchHandlerNodeTransform(private val tryCatchHandlerInfo: List<TryCatchHandlerInfo>, cnt: ClassNodeTransform?) : ClassNodeTransform(cnt) {

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

    private lateinit var infos:List<TryCatchHandlerInfo>

    override fun visitorClassNode(context: TransformContext, klass: ClassNode) {
        super.visitorClassNode(context, klass)

        infos = tryCatchHandlerInfo.filter {
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
            infos.forEach {

            }

        }
    }
}