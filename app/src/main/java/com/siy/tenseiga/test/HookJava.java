package com.siy.tenseiga.test;

import android.content.Context;
import android.util.Log;
import android.view.View;

import com.siy.tenseiga.App;
import com.siy.tenseiga.Tools;
import com.siy.tenseiga.base.Invoker;
import com.siy.tenseiga.base.Self;
import com.siy.tenseiga.base.annotations.Filter;
import com.siy.tenseiga.base.annotations.InsertFunc;
import com.siy.tenseiga.base.annotations.Proxy;
import com.siy.tenseiga.base.annotations.Replace;
import com.siy.tenseiga.base.annotations.TargetClass;
import com.siy.tenseiga.base.annotations.Tenseiga;

/**
 * @author Siy
 * @since 2022/6/2
 */
@Tenseiga
public class HookJava {

    @Replace(value = "replace")
    @TargetClass(value = "com.siy.tenseiga.test.OriginJava")
    public float hookReplace(int a, int  b) {
        Log.e("siy", "OriginJava-replace-");

        OriginJava originJava = (OriginJava) Self.get();
        originJava.showToast();

        //插入一个值，7.1应该是double类型的
        Self.putField(7.1, "newField");

        //这里不能强转成float，会报错
        double fieldValue = (double) Tools.loadField(OriginJava.class, originJava, "newField");
        Log.e("siy", "putField的值反射方式获取：" + fieldValue);

        //这里为什么可以？因为里面我做了处理
        float newField = (float) Self.getField("newField");
        Log.e("siy", "putField的值getField方式获取：" + newField);

        return a + b + newField;
    }


    @Proxy(value = "proxy")
    @TargetClass(value = "com.siy.tenseiga.test.OriginJava")
    public int hookProxy(int a, Integer b, String str, View view, Context context, byte bbb, short sh) {
        Log.e("siy", "HookJava-hookProxy-");

        //获取实例方法所在的对象
        OriginJava originJava = (OriginJava) Self.get();
        originJava.showToast();

        //这里的1，3，1，2都是int类型，这里就会需要先转成Number再转换成byte ,short
        int total = (int) Invoker.invoke(a, b, "hah", new View(App.INSTANCE), null, 1, 2);
        return total - b;
    }


    @Proxy(value = "d")
    @TargetClass(value = "android.util.Log")
    @Filter(value = {"com.siy.tenseiga.MainActivity"})
    public static int hookSysLogd(String tag, String msg) {
        return Log.e(tag, msg);
    }


   /* @Filter(value = {"com.siy.tenseiga.MainActivity"})
    @SafeTryCatchHandler
    public static void hookExceptionHandler(Exception exception) {
        Log.e("siy", Log.getStackTraceString(exception));
    }*/


    @Filter(value = {"com.siy.tenseiga.log.*"})
    @InsertFunc
    private static void insertFunction(int a,int b){
        int c = a+b;
    }


    @Filter(value = {"com.siy.tenseiga.log.*"})
    @InsertFunc
    private static void insertFunctionfdfdfdf(int a,int b){
        int c = a+b;
        int d = a+c;
    }
}
