package com.jiangyang.permission;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 * @author : No.1
 * @time : 2018/5/2.
 * @desciption :
 * @version :
 * </pre>
 */

public class PermissionFragment extends Fragment {
    PermissionCallback callback;
    private static final int PERMISSION_REQUEST_CODE = 791;

    public static PermissionFragment newInstance() {
        PermissionFragment fragment = new PermissionFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    String[] permissions;

    public void requestPermission(PermissionCallback callback, String... permissions) {
        this.callback = callback;
        this.permissions = permissions;
        PermissionFragment.this.requestPermissions(permissions, PERMISSION_REQUEST_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            List<String> deniedList = new ArrayList<>();
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    deniedList.add(permissions[i]);
                }
            }
            if (callback != null) {
                if (deniedList.size() == 0) {
                    //全部都授予了权限
                    callback.onGranted();
                } else {
                    //将未授予的权限集合返回
                    callback.onDenied(deniedList);
                }
            }
        } else {

        }
    }
}
