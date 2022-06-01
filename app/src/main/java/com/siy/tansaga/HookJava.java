package com.siy.tansaga;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.siy.tansaga.base.Origin;
import com.siy.tansaga.base.Self;
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


    public  void replaceHook( String str) {
        Origin.callVoid();
        Toast.makeText(App.INSTANCE, "replaceHook", Toast.LENGTH_LONG).show();
    }



}
