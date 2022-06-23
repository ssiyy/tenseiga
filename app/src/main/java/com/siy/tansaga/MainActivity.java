package com.siy.tansaga;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

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

        findViewById(R.id.replaceUser).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("siy", String.valueOf(new OriginJava().replace(3, 2)));
            }
        });

        findViewById(R.id.proxySys).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
//                Log.e("siy", getBaseContext().getString(R.string.app_name));
                proxySys();
            }
        });

        findViewById(R.id.proxyUser).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("siy", String.valueOf(new OriginJava().proxy(1, 2)));
            }
        });
    }

    private void proxySys(){
        Log.e("siy", getBaseContext().getString(R.string.app_name));
    }
}
