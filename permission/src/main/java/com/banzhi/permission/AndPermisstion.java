package com.banzhi.permission;

import android.app.Activity;
import android.app.Dialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;

import com.banzhi.permission.source.AppActivitySource;
import com.banzhi.permission.source.BaseSource;
import com.banzhi.permission.source.ContextSource;
import com.banzhi.permission.source.FragmentActivitySource;
import com.banzhi.permission.source.FragmentSource;
import com.banzhi.permission.source.SupportFragmentSource;

import java.util.List;

/**
 * <pre>
 * @author : No.1
 * @time : 2018/5/2.
 * @desciption :
 * @version :
 * </pre>
 */

public class AndPermisstion implements PermissionActivity.PermissionListener {


    private BaseSource mSource;
    private String[] permissions;
    private PermissionCallback permissionCallback;
    private SettingServer mSetting;
    private Dialog tipDialog;

    private AndPermisstion(Builder builder) {
        this.mSource = builder.mSource;
        this.permissions = builder.permissions;
        this.tipDialog = builder.dialog;
        this.mSetting = builder.settingServer;
        if (tipDialog == null && builder.showTip) {
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

    /**
     * 设置权限请求回调
     *
     * @param callback
     */
    public void setPermissionCallback(PermissionCallback callback) {
        this.permissionCallback = callback;
    }

    public void requset(PermissionCallback callback) {
        this.permissionCallback = callback;
        request();
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
                if (mSource.getContext() instanceof Service) {
                    return;
                }
                tipDialog.show();
            }
        }
    }

    public static class Builder {
        BaseSource mSource;
        String[] permissions;
        Dialog dialog;
        boolean showTip;
        SettingServer settingServer;

        public Builder(BaseSource mSource) {
            this.mSource = mSource;
        }

        /**
         * 上下文
         *
         * @param context
         */
        public Builder(Context context) {
            mSource = new ContextSource(context);
        }

        /**
         * android.support.v4.app fragment
         *
         * @param fragment
         */
        public Builder(Fragment fragment) {
            mSource = new SupportFragmentSource(fragment);
        }

        /**
         * fragmenet
         *
         * @param fragment
         */
        public Builder(android.app.Fragment fragment) {
            mSource = new FragmentSource(fragment);
        }

        /**
         * activity
         *
         * @param activity
         */
        public Builder(Activity activity) {
            mSource = new AppActivitySource(activity);
        }

        /**
         * FragmentActivity
         *
         * @param activity
         */
        public Builder(FragmentActivity activity) {
            mSource = new FragmentActivitySource(activity);
        }

        /**
         * 需要请求的权限
         *
         * @param permissions
         * @return
         */
        public Builder permissions(String... permissions) {
            this.permissions = permissions;
            return this;
        }

        /**
         * 请求失败 是否显示提示信息
         *
         * @return
         */
        public Builder showTip() {
            showTip = true;
            return this;
        }

        /**
         * 自定义请求失败提示信息
         *
         * @param tipDialog
         * @return
         */
        public Builder customTip(Dialog tipDialog) {
            showTip = true;
            dialog = tipDialog;
            return this;
        }

        /**
         * 自定义跳转到设置界面
         *
         * @param settingServer
         * @return
         */
        public Builder settingServer(SettingServer settingServer) {
            this.settingServer = settingServer;
            return this;
        }

        public AndPermisstion create() {
            return new AndPermisstion(this);
        }
    }
}
