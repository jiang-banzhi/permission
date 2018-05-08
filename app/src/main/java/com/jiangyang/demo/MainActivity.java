package com.jiangyang.demo;

import android.Manifest;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.jiangyang.permission.AndPermisstion;
import com.jiangyang.permission.AndPermisstion2;
import com.jiangyang.permission.PermissionCallback;

import java.util.List;

/**
 * @author No.1
 * @data 创建时间：2018/5/8 9:05
 * @Desciption MainActivity.java
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                permission1();
                new AndPermisstion2.Builder().with(MainActivity.this)
                        .permissions(Manifest.permission.READ_SMS,
                                Manifest.permission.CALL_PHONE,
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.READ_EXTERNAL_STORAGE).setCallback(new PermissionCallback() {
                    @Override
                    public void onGranted() {
                        Log.e("MainActivity", "onGranted");
                    }

                    @Override
                    public void onDenied(List<String> list) {
                        Log.e("MainActivity", "onDenied: ******>" + list);
                    }
                }).create().request();

            }
        });
    }

    private void permission1() {
        AndPermisstion.instance()
                .with(MainActivity.this)
                .requestPermission(new PermissionCallback() {
                                       @Override
                                       public void onGranted() {
                                           Log.i("MainActivity", "onGranted");
                                       }

                                       @Override
                                       public void onDenied(List<String> list) {
                                           Log.i("MainActivity", "onDenied: " + list);
                                       }
                                   }, Manifest.permission.READ_SMS, Manifest.permission.CALL_PHONE, Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.READ_EXTERNAL_STORAGE);
    }


}
