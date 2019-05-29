package com.banzhi.permission;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
    private static List<String> appPermissions;

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
     * 获取需要授权的权限
     *
     * @param context
     * @param permissions
     * @return
     */
    public static List<String> getDeniedPermissions(Context context, String[] permissions) {
        List<String> deniedList = new ArrayList<>();
        for (String permission : permissions) {
            if (!PermissionChecker.hasPermission(context, permission)) {
                deniedList.add(permission);
            }
        }
        return deniedList;
    }

    /**
     * 检查权限是否在manifest注册
     *
     * @param context
     * @param permissions
     */
    public static void checkPermissions(Context context, String... permissions) {
        if (appPermissions == null) {
            appPermissions = getManifestPermissions(context);
        }

        if (permissions.length == 0) {
            throw new IllegalArgumentException("Please enter at least one permission.");
        }

        for (String p : permissions) {
            if (!appPermissions.contains(p)) {
                throw new IllegalStateException(
                        String.format("The permission %1$s is not registered in manifest.xml", p));

            }
        }
    }

    /**
     * 获取应用程序在清单文件中注册的权限
     *
     * @param context
     * @return
     */
    public static List<String> getManifestPermissions(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), PackageManager.GET_PERMISSIONS);
            String[] permissions = packageInfo.requestedPermissions;
            if (permissions == null || permissions.length == 0) {
                throw new IllegalStateException("did not register any permissions in the manifest.xml.");
            }
            return Collections.unmodifiableList(Arrays.asList(permissions));
        } catch (PackageManager.NameNotFoundException e) {
            throw new AssertionError("Package name cannot be found.");
        }
    }
}

