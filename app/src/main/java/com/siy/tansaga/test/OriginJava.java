package com.siy.tansaga.test;

import android.util.Log;
import android.widget.Toast;

import com.siy.tansaga.App;

/**
 * @author Siy
 * @since 2022/6/2
 */
public class OriginJava {

    public int replace(int a, int b) {
        Log.e("siy", "OriginJava-replace-");
        return a + b;
    }

    public int proxy(int a, int b) {
        Log.e("siy", "OriginJava-proxy-");
        return a + b;
    }


    public void showToast() {
        Toast.makeText(App.INSTANCE, "OriginJava-showToast", Toast.LENGTH_SHORT).show();
    }
}
