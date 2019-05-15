package com.banzhi.sample;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.banzhi.permission.AndPermisstion;
import com.banzhi.permission.PermissionCallback;

import java.util.List;

/**
 * @author No.1
 * @data 创建时间：2018/5/10 14:09
 * @Desciption PermissionService.java
 */
public class PermissionService extends Service {
    public PermissionService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                request();
            }
        }, 2000);
        return super.onStartCommand(intent, flags, startId);
    }

    private void request() {
        AndPermisstion andPermisstion = new AndPermisstion.Builder(this)
                .permissions(Manifest.permission.READ_SMS,
                        Manifest.permission.CALL_PHONE,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                .create();

        andPermisstion.requset(new PermissionCallback() {
            @Override
            public void onGranted() {
                Log.e("MainActivity", "onGranted");
            }

            @Override
            public void onDenied(List<String> list) {
                Log.e("MainActivity", "onDenied: ******>" + list);
            }
        });

    }
}
