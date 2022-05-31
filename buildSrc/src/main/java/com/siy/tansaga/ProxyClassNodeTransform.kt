package com.siy.tansaga

import com.siy.tansaga.entity.ProxyInfo
import com.siy.tansaga.entity.ReplaceInfo
import com.siy.tansaga.interfaces.ClassNodeTransform
import org.objectweb.asm.tree.ClassNode


/**
 *
 * @author  Siy
 * @since  2022/5/31
 */
class ProxyClassNodeTransform (private val proxyInfos: List<ProxyInfo>, cnt: ClassNodeTransform?) :
    BaseClassNodeTransform(cnt) {

    override fun visitorClassNode(klass: ClassNode) {
        super.visitorClassNode(klass)

    }
}