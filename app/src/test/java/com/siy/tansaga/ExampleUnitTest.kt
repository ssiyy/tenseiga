package com.siy.tansaga

import org.junit.Test

import org.junit.Assert.*
import java.util.regex.Pattern

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
//        val it = "com.siy.tansaga.MainActivity(|\\$.*)"

      val it = "com.siy.tansaga.*(|\\$.*)"
        System.out.println("result:" + it)
        val result = Pattern.compile(it).matcher("com/siy/tansaga/MainActivity$111").matches()

        System.out.println("result:" + result)
    }
}