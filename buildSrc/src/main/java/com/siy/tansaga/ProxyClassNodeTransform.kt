package com.siy.tansaga

import com.siy.tansaga.entity.ProxyInfo
import com.siy.tansaga.interfaces.ClassNodeTransform
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.MethodInsnNode


/**
 * 代理变换
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

    override fun visitorClassNode(klass: ClassNode) {
        super.visitorClassNode(klass)

        if (!proxyInfos.map {
                it.hookClass
            }.contains(klass.name)) {
            this.klass = klass
        }
    }


    override fun visitorInsnMethod(insnMethod: MethodInsnNode) {
        super.visitorInsnMethod(insnMethod)
        klass?.let {
            for (info in proxyInfos) {
                val sameOwner = insnMethod.owner == info.targetClass
                val sameName = insnMethod.name == info.targetMethod

                if (sameOwner && sameName) {
                    insnMethod.run {
                        owner = info.hookClass
                        name = info.hookMethod.name
                        desc = info.hookMethod.desc
                        opcode = Opcodes.INVOKESTATIC
                        itf = false
                    }
                }
            }
        }
    }
}