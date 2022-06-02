package com.siy.tansaga.test;

import android.util.Log;

import com.siy.tansaga.base.Self;

/**
 * @author Siy
 * @since 2022/6/2
 */
public class HookJava {
    public int hookReplace(int a,int b){
        Log.e("siy","HookJava-hookReplace-");
        OriginJava originJava = (OriginJava)Self.get();
        originJava.showToast();
        return a-b;
    }

    public int hookProxy(int a,int b){
        Log.e("siy","HookJava-hookProxy-");
        return a-b;
    }
}
