package com.siy.tenseiga

import android.app.Application


/**
 *
 * @author  Siy
 * @since  2022/6/1
 */
class App : Application() {

    companion object{
       lateinit var INSTANCE:Application
    }

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
    }
}