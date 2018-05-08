package com.jiangyang.permission;

import android.app.Activity;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.jiangyang.permission.source.AppActivitySource;
import com.jiangyang.permission.source.BaseSource;
import com.jiangyang.permission.source.FragmentActivitySource;
import com.jiangyang.permission.source.FragmentSource;
import com.jiangyang.permission.source.SupportFragmentSource;

import java.util.List;

/**
 * <pre>
 * @author : No.1
 * @time : 2018/5/2.
 * @desciption :
 * @version :
 * </pre>
 */

public class AndPermisstion2 implements PermissionActivity.PermissionListener {


    private static final String TAG = PermissionFragment.class.getSimpleName();
    BaseSource mSource;
    String[] permissions;
    PermissionCallback permissionCallback;

    private AndPermisstion2(BaseSource source, String[] permissions, PermissionCallback permissionCallback) {
        this.mSource = source;
        this.permissions = permissions;
        this.permissionCallback = permissionCallback;
    }

    public void request() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PermissionActivity.request(mSource.getContext(), permissions, this);
        }
    }

    @Override
    public void onPermissionListerer(List<String> permissions) {
        if (permissions.size() == 0) {
            //全部都授予了权限
            permissionCallback.onGranted();
        } else {
            //将未授予的权限集合返回
            permissionCallback.onDenied(permissions);
        }
    }

    public static class Builder {
        BaseSource mSource;
        String[] permissions;
        PermissionCallback permissionCallback;


        public Builder with(BaseSource source) {
            mSource = source;
            return this;
        }

        public Builder with(Fragment fragment) {
            return with(new SupportFragmentSource(fragment));
        }

        public Builder with(android.app.Fragment fragment) {
            return with(new FragmentSource(fragment));
        }

        public Builder with(Activity activity) {
            return with(new AppActivitySource(activity));
        }

        public Builder with(FragmentActivity activity) {
            return with(new FragmentActivitySource(activity));
        }

        public Builder permissions(String... permissions) {
            this.permissions = permissions;
            return this;
        }

        public Builder setCallback(PermissionCallback callback) {
            this.permissionCallback = callback;
            return this;
        }


        public AndPermisstion2 create() {
            return new AndPermisstion2(mSource, permissions, permissionCallback);
        }
    }
}
