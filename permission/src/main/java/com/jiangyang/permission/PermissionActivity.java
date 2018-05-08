package com.jiangyang.permission;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 * @author : No.1
 * @time : 2018/5/8.
 * @desciption :
 * @version :
 * </pre>
 */
@RequiresApi(api = Build.VERSION_CODES.M)
public class PermissionActivity extends Activity {
    static PermissionListener sPermissionListener;
    private static String PERMISSION_TAG = "permissions";
    private static final int CODE_REQUEST_PERMISSION = 663;

    /**
     * 请求权限
     *
     * @param context
     * @param permissions
     * @param permissionListener
     */
    public static void request(Context context, String[] permissions, PermissionListener permissionListener) {
        sPermissionListener = permissionListener;
        Intent intent = new Intent(context, PermissionActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(PERMISSION_TAG, permissions);
        context.startActivity(intent);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        invasionStatusBar();
        requestPermission();
    }


    private void requestPermission() {
        Intent intent = getIntent();
        String[] mPermissions = intent.getStringArrayExtra(PERMISSION_TAG);
        if (mPermissions != null && sPermissionListener != null) {
            String[] requestPermissions = getDeniedPermissions(mPermissions);
            //所有权限都已授权
            if (requestPermissions == null || requestPermissions.length == 0) {
                sPermissionListener.onPermissionListerer(new ArrayList<String>());
                sPermissionListener = null;
                finish();
            } else {
                requestPermissions(requestPermissions, CODE_REQUEST_PERMISSION);
            }
        } else {
            sPermissionListener = null;
            finish();
        }
    }

    /**
     * 获取权限集中需要申请权限的列表
     *
     * @param permissions
     * @return
     */
    private String[] getDeniedPermissions(String[] permissions) {
        List<String> needRequestPermissonList = new ArrayList<>();
        for (String permission : permissions) {
            if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED
                    || shouldShowRequestPermissionRationale(permission)) {
                needRequestPermissonList.add(permission);
            }
        }
        return needRequestPermissonList.toArray(new String[needRequestPermissonList.size()]);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CODE_REQUEST_PERMISSION) {
            //未授权集合
            List<String> deniedList = new ArrayList<>();
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    deniedList.add(permissions[i]);
                }
            }
            if (sPermissionListener != null) {
                sPermissionListener.onPermissionListerer(deniedList);
                sPermissionListener = null;
                finish();
            }
        }
    }

    public interface PermissionListener {
        /**
         * 权限请求回调
         *
         * @param permissions
         */
        void onPermissionListerer(List<String> permissions);
    }


    /**
     * 设置沉浸式状态栏
     */
    private void invasionStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            View decorView = window.getDecorView();
            decorView.setSystemUiVisibility(decorView.getSystemUiVisibility()
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }
}
