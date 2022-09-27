package com.siy.tenseiga.ext

import com.siy.tenseiga.base.annotations.*
import org.objectweb.asm.Type

val BYTE_TYPE = Type.getType(Byte::class.javaObjectType)!!

val SHORT_TYPE = Type.getType(Short::class.javaObjectType)!!

val INTEGER_TYPE = Type.getType(Integer::class.javaObjectType)!!

val FLOAT_TYPE = Type.getType(Float::class.javaObjectType)!!

val LONG_TYPE = Type.getType(Long::class.javaObjectType)!!

val DOUBLE_TYPE = Type.getType(Double::class.javaObjectType)!!

val CHARACTER_TYPE = Type.getType(Char::class.javaObjectType)!!

val BOOLEAN_TYPE = Type.getType(Boolean::class.javaObjectType)!!

val NUMBER_TYPE = Type.getType(Number::class.javaObjectType)!!


val EXCEPTION_TYPE = Type.getType(Exception::class.java)!!

val OBJECT_TYPE = Type.getType(Any::class.javaObjectType)!!

val REPLACE_TYPE = Type.getType(Replace::class.java)!!

val PROXY_TYPE = Type.getType(Proxy::class.java)!!

val SAFETRYCATCHHANDLER_TYPE = Type.getType(SafeTryCatchHandler::class.java)!!

val INSERTFUNC_TYPE = Type.getType(InsertFunc::class.java)!!

val SERIALIZABLE_TYPE = Type.getType(Serializable::class.java)!!

val TARGETCLASS_TYPE = Type.getType(TargetClass::class.java)!!

val FILTER_TYPE = Type.getType(Filter::class.java)!!

val TENSEIGA_TYPE = Type.getType(Tenseiga::class.java)!!

const val OPCODES_INVOKER = Int.MAX_VALUE - 5555

const val OPCODES_PUTFIELD = OPCODES_INVOKER - 5555

const val OPCODES_GETFIELD = OPCODES_PUTFIELD - 5555


