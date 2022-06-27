package com.siy.tansaga.parser

import com.siy.tansaga.entity.TransformInfo
import com.siy.tansaga.interfaces.TransformParser
import java.io.File


/**
 * 用来解析注解配置的类
 * @author  Siy
 * @since  2022/6/27
 */
class AnnotationParser : TransformParser{

    override fun parse(dir: Iterator<File>): TransformInfo {

        return TransformInfo()
    }
}