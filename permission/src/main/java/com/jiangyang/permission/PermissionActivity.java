package com.jiangyang.permission;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
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
    private static PermissionListener sPermissionListener;
    private static final String PERMISSION_TAG = "permissions";
    private static final int CODE_REQUEST_PERMISSION = 663;
    private static final int CODE_REQUEST_INSTALL = 213;
    private static final int CODE_REQUEST_OVERLAY = 214;

    boolean isRequestInstall;//是否已经请求安装权限
    boolean isRequestOverlay;//是否已经请求悬浮窗权限
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

    List<String> requestPermissions = new ArrayList<>();

    private void requestPermission() {
        Intent intent = getIntent();
        String[] mPermissions = intent.getStringArrayExtra(PERMISSION_TAG);
        if (mPermissions != null && sPermissionListener != null) {
            requestPermissions = getDeniedPermissions(mPermissions);
            //所有权限都已授权
            if (requestPermissions.size() == 0) {
                hasAllPermissions();
            } else {
                requestPermissions();
            }
        } else {
            sPermissionListener = null;
            finish();
        }
    }



    /**
     * 检查权限是否授予
     */
    private void requestPermissions() {
        //跳转到允许安装未知来源设置页面
        if (requestPermissions.contains(Manifest.permission.REQUEST_INSTALL_PACKAGES)) {
            if (PermissionUtils.hasInstallPermission(this)) {
                requestPermissions.remove(Manifest.permission.REQUEST_INSTALL_PACKAGES);
                if (requestPermissions.size() == 0) {
                    hasAllPermissions();
                    return;
                }
            } else {
                if (!isRequestInstall) {
                    installSetting();
                    return;
                }
            }
        }
        //跳转到悬浮窗设置页面
        if (requestPermissions.contains(Manifest.permission.SYSTEM_ALERT_WINDOW)) {
            if (PermissionUtils.hasOverlaysPermission(this)) {
                requestPermissions.remove(Manifest.permission.SYSTEM_ALERT_WINDOW);
                if (requestPermissions.size() == 0) {
                    hasAllPermissions();
                    return;
                }
            } else {
                if (!isRequestOverlay) {
                    overlaySetting();
                    return;
                }
            }
        }
        String[] permissions = requestPermissions.toArray(new String[requestPermissions.size()]);
        requestPermissions(permissions, CODE_REQUEST_PERMISSION);

    }

    /**
     * 已经获取所需权限
     */
    private void hasAllPermissions() {
        sPermissionListener.onPermissionListerer(new ArrayList<String>());
        sPermissionListener = null;
        finish();
    }


    /**
     * 获取权限集中需要申请权限的列表
     *
     * @param permissions
     * @return
     */
    private List<String> getDeniedPermissions(String[] permissions) {
        List<String> needRequestPermissonList = new ArrayList<>();
        for (String permission : permissions) {
            //权限未授权
            if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED
                    //应该显示请求权限
                    || shouldShowRequestPermissionRationale(permission)) {

                needRequestPermissonList.add(permission);
            }
        }
        return needRequestPermissonList;
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
         * @param permissions 未授权权限列表
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

    /**
     * 跳转到允许安装未知来源设置页面
     */
    private void installSetting() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, CODE_REQUEST_INSTALL);
    }

    /**
     * 跳转到悬浮窗设置页面
     */
    private void overlaySetting() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, CODE_REQUEST_OVERLAY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CODE_REQUEST_INSTALL:
                isRequestInstall = true;
                if (resultCode == RESULT_OK) {
                    requestPermissions.remove(Manifest.permission.REQUEST_INSTALL_PACKAGES);

                }
                break;
            case CODE_REQUEST_OVERLAY:
                isRequestOverlay = true;
                if (resultCode == RESULT_OK) {
                    requestPermissions.remove(Manifest.permission.SYSTEM_ALERT_WINDOW);
                }
                break;
            default:
                break;

        }
        requestPermissions();
    }
}
