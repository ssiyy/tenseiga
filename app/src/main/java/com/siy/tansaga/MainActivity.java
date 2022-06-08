package com.siy.tansaga;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * @author Siy
 * @since 2022/5/30
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        //  super.onCreate(savedInstanceState);
        //    setContentView(R.layout.activity_main);

       /* findViewById(R.id.proxyHook).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new OriginJava().proxy(1, 2);
            }
        });


        findViewById(R.id.replaceHook).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new OriginJava().replace(3, 2);
            }
        });*/

        View v = findViewById(R.id.proxyHookSys);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                String str = getString(R.string.app_name);
//                Toast.makeText(MainActivity.this, str, Toast.LENGTH_SHORT).show();

                Class viewClazz = this.getClass();

                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        getString(R.string.next);
                    }
                });
                Log.e("siy",viewClazz.getName());

                Class actClazz = MainActivity.this.getClass();
                Log.e("siy",actClazz.getName());
            }
        });

    }


}
