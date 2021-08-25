package com.banzhi.sample;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.banzhi.permission_kt.AndPermisstion;
import com.banzhi.permission_kt.PermissionCallback;

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
                AndPermisstion.Companion.getInstance()
                        .newBuilder()
                        .permissions(Manifest.permission.REQUEST_INSTALL_PACKAGES)
                        .request(new PermissionCallback() {
                            @Override
                            public void onGranted() {
                                Toast.makeText(MainActivity.this, "授权成功!", Toast.LENGTH_SHORT).show();
                                Log.e("MainActivity", "onGranted");
                            }

                            @Override
                            public void onDenied(List<String> list) {
                                Log.e("MainActivity", "onDenied: ******>" + list);
                            }
                        });

            }
        });
        findViewById(R.id.tv2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(MainActivity.this, PermissionService.class);
                startService(intent);
            }
        });
        FragmentTransaction tran = getSupportFragmentManager().beginTransaction();
        tran.add(R.id.container, PermissionFragment.newInstance());
        tran.commit();
    }

    Intent intent;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(intent);
    }
}
