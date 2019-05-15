package com.banzhi.permission;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;

import java.util.Arrays;
import java.util.List;

/**
 * <pre>
 * @author : No.1
 * @time : 2018/9/21.
 * @desciption :
 * @version :
 * </pre>
 */

public class PermissionUtils {
    /**
     * 是否是6.0以上版本
     */
    public static boolean isOverMarshmallow() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    /**
     * 是否是8.0以上版本
     */
    public static boolean isOverOreo() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O;
    }


    /**
     * 是否有安装权限
     */
    public static boolean hasInstallPermission(Context context) {
        if (isOverOreo()) {
            if (context.getApplicationInfo().targetSdkVersion >= Build.VERSION_CODES.O) {
                return context.getPackageManager().canRequestPackageInstalls();
            }
        }
        return true;
    }

    /**
     * 是否有悬浮窗权限
     */
    public static boolean hasOverlaysPermission(Context context) {
        if (isOverMarshmallow()) {
            if (context.getApplicationInfo().targetSdkVersion >= Build.VERSION_CODES.M) {
                return Settings.canDrawOverlays(context);
            }
        }
        return true;
    }

    /**
     * 获取应用程序在清单文件中注册的权限
     */
    public static List<String> getManifestPermissions(Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            return Arrays.asList(pm.getPackageInfo(context.getPackageName(), PackageManager.GET_PERMISSIONS).requestedPermissions);
        } catch (Exception e) {
            return null;
        }
    }
}
