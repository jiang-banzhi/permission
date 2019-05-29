package com.banzhi.permission;

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.support.v7.app.AlertDialog;

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

    private PermissionCallback permissionCallback;
    private SettingServer mSetting;
    private Dialog tipDialog;

    public static void init(Application context) {
        PermissionInit.init(context);
    }

    private static class Inner {
        private static final AndPermisstion INSTANCE = new AndPermisstion();
    }

    public static AndPermisstion getInstance() {
        return Inner.INSTANCE;
    }


    public void request(Builder builder) {
        Activity topActivity = PermissionInit.getTopActivity();
        PermissionUtils.checkPermissions(topActivity, builder.permissions);
        List<String> permissions = PermissionUtils.getDeniedPermissions(topActivity, builder.permissions);
        if (permissions.isEmpty()) {
            permissionCallback.onGranted();
            return;
        }
        this.permissionCallback = builder.permissionCallback;
        if (builder.tipDialog == null && builder.showTip) {
            if (builder.mSetting == null) {
                mSetting = new PermissionSetting();
            }
            tipDialog = createDialog(topActivity);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PermissionActivity.request(topActivity, permissions, this);
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

    public Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {
        private String[] permissions;
        private PermissionCallback permissionCallback;
        private SettingServer mSetting;
        private Dialog tipDialog;
        boolean showTip = true;

        /**
         * 需要申请的权限
         *
         * @param permissions
         * @return
         */
        public Builder setPermissions(String... permissions) {
            this.permissions = permissions;
            return this;
        }

        /**
         * 自定义跳转到权限设置
         *
         * @param mSetting
         * @return
         */
        public Builder setSetting(SettingServer mSetting) {
            this.mSetting = mSetting;
            return this;
        }

        /**
         * 自定义权限拒绝弹出框
         *
         * @param tipDialog
         * @return
         */
        public Builder setTipDialog(Dialog tipDialog) {
            this.tipDialog = tipDialog;
            return this;
        }

        /**
         * 显示默认弹出框
         *
         * @param showTip
         * @return
         */
        public Builder setShowTip(boolean showTip) {
            this.showTip = showTip;
            return this;
        }

        public void request() {
            getInstance().request(null);
        }

        public void request(PermissionCallback callback) {
            this.permissionCallback = callback;
            if (permissions == null || permissions.length == 0) {
                throw new NullPointerException("no permission need request");
            }
            getInstance().request(this);
        }
    }

}
