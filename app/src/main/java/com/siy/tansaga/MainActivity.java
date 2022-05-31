package com.siy.tansaga;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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
                //代理系统方法
                Toast.makeText(MainActivity.this, "hook toast", Toast.LENGTH_LONG).show();
            }
        });


        findViewById(R.id.replaceHook).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //替换非系统方法实现
                printLog("hi hi~~~");
            }
        });

    }


    private void printLog(String str) {
        Log.e("siy", str);
    }
}
