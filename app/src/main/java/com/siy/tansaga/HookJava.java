package com.siy.tansaga;

import android.content.Context;
import android.os.Build;
import android.widget.Toast;

import com.siy.tansaga.base.annotations.Replace;
import com.siy.tansaga.base.annotations.TargetClass;

/**
 * @author Siy
 * @since 2022/5/30
 */
public class HookJava {

    public static Toast proxyHook(Context context, CharSequence c, int d) {
        return Toast.makeText(context, "proxyHook", d);
    }


    public static void replaceHook(Context context, String str) {
        Toast.makeText(context, "replaceHook", Toast.LENGTH_LONG).show();
    }

}
