package com.siy.tenseiga.test;

import android.util.Log;

import com.siy.tenseiga.base.Invoker;
import com.siy.tenseiga.base.Self;
import com.siy.tenseiga.base.annotations.Replace;
import com.siy.tenseiga.base.annotations.TargetClass;

/**
 * @author Siy
 * @since 2022/6/2
 */
public class HookJava {



    //--------------------------------------------------------------------

    @Replace(value = "annoReplace")
    @TargetClass(value = "com.siy.tenseiga.test.OriginJava")
    public String hookAnnoReplace(int a, Integer b) {
        Log.e("siy", "OriginJava-hookAnnoReplace-");
        OriginJava originJava = (OriginJava) Self.get();
        originJava.showToast();

       String v = (String) Invoker.invoke(a,b);

//        Self.putField("fff", 1);

        return a + b + "hook" + v;
    }
}
