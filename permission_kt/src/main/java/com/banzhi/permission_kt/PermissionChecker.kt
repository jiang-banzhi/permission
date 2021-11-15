package com.banzhi.permission_kt

import android.app.AppOpsManager
import android.content.Context
import android.content.pm.PackageManager
import android.text.TextUtils

/**
 *<pre>
 * @author : No.1
 * @time : 2019/8/7.
 * @desciption :
 * @version :
 *</pre>
 */
class PermissionChecker {
    companion object {
        /**
         * 检查是否授予权限
         *
         * @param context
         * @param permission
         * @return
         */
        fun hasPermission(context: Context, permission: String): Boolean {
            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.M) {
                return true
            }
            var opsManager: AppOpsManager? = null
            var result = context.checkPermission(permission, android.os.Process.myPid(), android.os.Process.myUid())
            if (result == PackageManager.PERMISSION_DENIED) {
                return false
            }
            val op = AppOpsManager.permissionToOp(permission)
            if (TextUtils.isEmpty(op)) {
                return true
            }
            if (opsManager == null) {
                opsManager = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
            }
            result = opsManager.checkOpNoThrow(op!!, android.os.Process.myUid(), context.packageName)
            return result == AppOpsManager.MODE_ALLOWED
        }
    }
}