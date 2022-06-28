package com.siy.tansaga.test;

import android.content.Context;
import android.util.Log;
import android.view.View;

import com.siy.tansaga.App;
import com.siy.tansaga.R;
import com.siy.tansaga.base.Invoker;
import com.siy.tansaga.base.Self;

/**
 * @author Siy
 * @since 2022/6/2
 */
public class HookJava {

    /**
     * 用来replace {@link OriginJava#replace(int, int)}
     *
     * @param a
     * @param b
     * @return
     */
    @SuppressWarnings("unused")
    public int hookReplace(int a, int b) {
        Log.e("siy", "HookJava-hookReplace-");

        //获取实例方法所在的对象
        OriginJava originJava = (OriginJava) Self.get();
        originJava.showToast();

        //我们可以在这儿修改参数
        int orgResult = (int) Invoker.invoke(a, b);
        return orgResult - b;
    }

    /**
     * 代理用户方法 {@link OriginJava#proxy(int, Integer, String, View, Context, byte, short)}
     *
     * @param a
     * @param b
     * @return
     */
    @SuppressWarnings("unused")
    public int hookProxy(int a, Integer b, String str, View view, Context context, byte bbb, short sh) {
        Log.e("siy", "HookJava-hookProxy-");

        //获取实例方法所在的对象
        OriginJava originJava = (OriginJava) Self.get();
        originJava.showToast();

        int total = (int) Invoker.invoke(1, 3, "hah", new View(App.INSTANCE), null, 1, 2);
        return total - b;
    }


    /**
     * 用来代理系统的方法{@link android.content.Context#getString(int)}
     *
     * @param resId
     * @return
     */
    @SuppressWarnings("unused")
    public String hookProxySys(int resId) {
        Log.e("siy", "HookJava-hookProxySys-");

        //获取实例方法所在的对象
        Context context = (Context) Self.get();
        Log.e("siy", context.getCacheDir().getAbsolutePath());

        int o = 11111;

        String a = "a";

        String b = "b";

        String e = (String) Invoker.invoke(R.string.next); //next

        String d = a + b;  //ab

        //在这儿修改方法

        String f = e + d; //nextab

        int g = o + 2;
        return f;
    }


    //--------------------------------------------------------------------


}
