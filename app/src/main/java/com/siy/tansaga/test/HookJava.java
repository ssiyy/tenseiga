package com.siy.tansaga.test;

import android.util.Log;

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
//        OriginJava originJava = (OriginJava) Self.get();
//        originJava.showToast();

        //我们可以在这儿修改参数
//        a = a + 5;
//        b = b + 5;
        int orgResult = (int) Invoker.invoke();

//        Log.e("siy", "orginResult:" + orgResult);
        return orgResult - b;
    }

    /**
     * 代理用户方法 {@link OriginJava#proxy(int, int)}
     *
     * @param a
     * @param b
     * @return
     */
    @SuppressWarnings("unused")
    public int hookProxy(int a, int b) {
        Log.e("siy", "HookJava-hookProxy-");
//        int total = (int) Invoker.invoke();
        return 6 - b;
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
        resId = R.string.next;
        String changeStr = (String) Invoker.invoke();
        return changeStr;
//        return "woshi shui ";
    }
}
