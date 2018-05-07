package com.jiangyang.demo;

import android.Manifest;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.jiangyang.permission.AndPermisstion;
import com.jiangyang.permission.PermissionCallback;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AndPermisstion.instance().with(MainActivity.this).requestPermission(new PermissionCallback() {
                    @Override
                    public void onGranted() {
                        Log.i("MainActivity", "onGranted");
                    }

                    @Override
                    public void onDenied(List<String> list) {
                        Log.i("MainActivity", "onDenied: " + list);
                    }
                }, Manifest.permission.READ_SMS, Manifest.permission.CALL_PHONE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE);

            }
        });
    }
}
