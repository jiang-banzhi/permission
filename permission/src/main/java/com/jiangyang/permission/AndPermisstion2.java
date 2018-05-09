package com.jiangyang.permission;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;

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
    SettingServer mSetting;
    Dialog tipDialog;
    boolean showTip;

    private AndPermisstion2(BaseSource source, String[] permissions, PermissionCallback permissionCallback
            , Dialog dialog, boolean showTip, SettingServer settingServer) {
        this.mSource = source;
        this.permissions = permissions;
        this.permissionCallback = permissionCallback;
        this.tipDialog = dialog;
        this.showTip = showTip;
        this.mSetting = settingServer;
        if (tipDialog == null && showTip) {
            if (mSetting == null) {
                mSetting = new PermissionSetting(mSource);
            }
            tipDialog = createDialog(mSource.getContext());
        }
    }

    private Dialog createDialog(Context context) {
        return new AlertDialog.Builder(context).setTitle("提示信息").setMessage("当前应用缺少必要"
                + "权限，该功能暂时无法使用。如若需要，请单击【确定】按钮前往设置中心进行权限授权。")
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mSetting.cancle();
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mSetting.execute();
                    }
                }).create();
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
            if (tipDialog != null) {
                tipDialog.show();
            }
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

        Dialog dialog;
        boolean showTip;

        public Builder showTip() {
            showTip = true;
            return this;
        }

        public Builder customTip(Dialog tipDialog) {
            showTip = true;
            dialog = tipDialog;
            return this;
        }

        SettingServer settingServer;

        public Builder settingServer(SettingServer settingServer) {
            this.settingServer = settingServer;
            return this;
        }

        public AndPermisstion2 create() {
            return new AndPermisstion2(mSource, permissions, permissionCallback, dialog, showTip, settingServer);
        }
    }
}
