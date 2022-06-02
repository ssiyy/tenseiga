package com.siy.tansaga;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.siy.tansaga.test.OriginJava;

/**
 * @author Siy
 * @since 2022/5/30
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.proxyHook).setOnClickListener(new View.OnClickListener() {
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
        });

        findViewById(R.id.proxyHookSys).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = getString(R.string.app_name);
                Toast.makeText(MainActivity.this, str, Toast.LENGTH_SHORT).show();
            }
        });

    }


}
