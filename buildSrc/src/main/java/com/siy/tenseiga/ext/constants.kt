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


val CHARACTER_TYPE = Type.getType(Char::class.javaObjectType)!!

val BOOLEAN_TYPE = Type.getType(Boolean::class.javaObjectType)!!

val BYTE_TYPE = Type.getType(Byte::class.javaObjectType)!!

val SHORT_TYPE = Type.getType(Short::class.javaObjectType)!!

val INTEGER_TYPE = Type.getType(Integer::class.javaObjectType)!!

val FLOAT_TYPE =  Type.getType(Float::class.javaObjectType)!!

val LONG_TYPE = Type.getType(Long::class.javaObjectType)!!

val DOUBLE_TYPE = Type.getType(Double::class.javaObjectType)!!



val OBJECT_TYPE = Type.getType(Object::class.java)!!

val REPLACE_TYPE = Type.getType(Replace::class.java)!!

val PROXY_TYPE = Type.getType(Proxy::class.java)!!

val TARGETCLASS_TYPE = Type.getType(TargetClass::class.java)!!

val FILTER_TYPE = Type.getType(Filter::class.java)!!

const val OP_CALL = Int.MAX_VALUE - 5555

const val VOID = 1

const val REFERENCE = 2

const val PRIMITIVE = 3
