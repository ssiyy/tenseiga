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
    @Replace(value = "makeText")
    @TargetClass(value = "android.widget.Toast")
    public static Toast hookJ(Context context, CharSequence c, int d){
        return   Toast.makeText(context, "xxxx", d);
    }



    /**
     * Fix {@code WindowManager$BadTokenException} for Android N
     *
     * @param toast
     *         The original toast
     */
    public static void show(final Toast toast) {
     toast.setText("xxxxxxxx");
     toast.show();
    }
}
