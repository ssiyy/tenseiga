package com.siy.tenseiga.interfaces

import com.siy.tenseiga.entity.TransformInfo
import java.io.File


/**
 *
 * 用来解析需要转换类的相关信息
 *
 * @author  Siy
 * @since  2022/5/31
 */
interface TransformParser {
    /**
     * @param dir 编译的class的文件根目录
     */
    fun parse(dir: Sequence<File>): TransformInfo
}