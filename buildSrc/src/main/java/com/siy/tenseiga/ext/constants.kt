package com.siy.tenseiga.ext

import com.siy.tenseiga.base.annotations.Filter
import com.siy.tenseiga.base.annotations.Proxy
import com.siy.tenseiga.base.annotations.Replace
import com.siy.tenseiga.base.annotations.TargetClass
import org.objectweb.asm.Type


/**
 *
 * @author  Siy
 * @since  2022/7/12
 */

val OBJECT_TYPE = Type.getType(Object::class.java)!!

val REPLACE_TYPE = Type.getType(Replace::class.java)!!

val PROXY_TYPE = Type.getType(Proxy::class.java)!!

val TARGETCLASS_TYPE = Type.getType(TargetClass::class.java)!!

val FILTER_TYPE = Type.getType(Filter::class.java)!!