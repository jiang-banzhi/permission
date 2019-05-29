package com.banzhi.permission;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.text.TextUtils;

/**
 * <pre>
 * @author : No.1
 * @time : 2019/5/29.
 * @desciption :
 * @version :
 * </pre>
 */

public class PermissionChecker {

    /**
     * 检查是否授予权限
     *
     * @param context
     * @param permission
     * @return
     */
    public static boolean hasPermission(Context context, String permission) {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.M) {
            return true;
        }
        AppOpsManager opsManager = null;
        int result = context.checkPermission(permission, android.os.Process.myPid(), android.os.Process.myUid());
        if (result == PackageManager.PERMISSION_DENIED) {
            return false;
        }
        String op = AppOpsManager.permissionToOp(permission);
        if (TextUtils.isEmpty(op)) {
            return true;
        }
        if (opsManager == null) {
            opsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        }
        result = opsManager.checkOpNoThrow(op, android.os.Process.myUid(), context.getPackageName());
        if (result != AppOpsManager.MODE_ALLOWED) {
            return false;
        }
        return true;
    }
}
