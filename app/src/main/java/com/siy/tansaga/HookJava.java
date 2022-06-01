package com.siy.tansaga;

import android.content.Context;
import android.widget.Toast;

import com.siy.tansaga.base.Origin;

/**
 * @author Siy
 * @since 2022/5/30
 */
public class HookJava {

    public static Toast proxyHook(Context context, CharSequence c, int d) {
        return Toast.makeText(context, "proxyHook", d);
    }


    public void hookPrintLog(String str) {
        int a = 1 + 1;
        Origin.callVoid();
        Toast.makeText(App.INSTANCE, "replaceHook", Toast.LENGTH_LONG).show();
    }



    public int replacePlus(int a) {
        int c = 1 + 1;
        int d = c+ (int)Origin.call()+(int)Origin.call();
        Toast.makeText(App.INSTANCE, "replacePlus"+d+(int)Origin.call(), Toast.LENGTH_LONG).show();
        return d;
    }


}
