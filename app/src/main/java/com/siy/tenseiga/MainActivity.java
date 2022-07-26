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
                Log.e("siy", String.valueOf(new OriginJava().replace(3, 2)));
            }
        });

        findViewById(R.id.replaceUserAnno).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.e("siy", new OriginJava().annoReplace(100, 101));
            }
        });

        findViewById(R.id.proxySys).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                proxySys();
            }
        });

        findViewById(R.id.proxyUser).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                proxyUser();
            }
        });

        findViewById(R.id.annoProxySys).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              Log.d("siy","我用的Log.d");
            }
        });
    }

    private void proxyUser() {
        Log.e("siy", String.valueOf(new OriginJava().proxy(1, 2, "x", findViewById(R.id.proxyUser), this, (byte) 0, (short) 11)));
        int a = 2 / 0;
    }

    private void proxySys() {
        Log.e("siy", getString(R.string.app_name));
    }
}
