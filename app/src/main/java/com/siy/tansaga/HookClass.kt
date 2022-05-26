package com.siy.tansaga

import android.content.Context
import android.widget.Toast
import com.siy.tansaga.base.annotations.Replace
import com.siy.tansaga.base.annotations.TargetClass
import java.time.Duration


/**
 *
 * @author  Siy
 * @since  2022/5/26
 */

@Replace("makeText")
@TargetClass("android.widget.Toast")
fun HookToast(context: Context, text: CharSequence, duration: Int) {
    Toast.makeText(context, "xxx", duration)
}