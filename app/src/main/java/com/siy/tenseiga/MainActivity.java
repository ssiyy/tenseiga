package com.siy.tenseiga;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.siy.tenseiga.test.OriginJava;


/**
 * @author Siy
 * @since 2022/5/30
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.replaceUser).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("siy", String.valueOf(new OriginJava().replace(100, 101)));
            }
        });


        findViewById(R.id.proxyUser).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("siy", String.valueOf(new OriginJava().proxy(1, 2, "x", findViewById(R.id.proxyUser), MainActivity.this, (byte) 0, (short) 11)));
            }
        });

        findViewById(R.id.proxySys).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("siy", "我用的Log.d");
            }
        });

        findViewById(R.id.catchHandler).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                throwException();
            }
        });
    }

    private void throwException(){
        int a = 2/0;
        Log.e("siy",String.valueOf(a));
    }
}
