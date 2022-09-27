package com.siy.tenseiga.test;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.siy.tenseiga.App;


/**
 * @author Siy
 * @since 2022/6/2
 */
public class OriginJava {

    public float replace(int a, int b) {
        Log.e("siy", "OriginJava-replace-");
        return a + b;
    }

    public int proxy(int a, Integer b, String str, View view, Context context, byte bb, short sh) {
        Log.e("siy", "OriginJava-proxy-" + "str:" + str + "-view:" + view.toString() + "-context:" + context + "-byte:" + bb + "-short:" + sh);
        return a + b;
    }


    /**
     * 用来给this调用得
     */
    public void showToast() {
        Toast.makeText(App.INSTANCE, "OriginJava-showToast", Toast.LENGTH_SHORT).show();
    }
}
