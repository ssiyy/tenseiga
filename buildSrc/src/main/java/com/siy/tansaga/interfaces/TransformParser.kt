package com.siy.tansaga.interfaces

import com.siy.tansaga.entity.TExtension
import com.siy.tansaga.entity.TransformInfo
import java.io.File


/**
 *
 * @author  Siy
 * @since  2022/5/31
 */
interface TransformParser {
    fun parse(extension: TExtension, dir: Iterator<File>): TransformInfo
}