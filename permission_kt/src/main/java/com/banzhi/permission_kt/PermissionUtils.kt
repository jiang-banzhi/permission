package com.banzhi.permission_kt

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import java.util.*

/**
 *<pre>
 * @author : No.1
 * @time : 2019/8/7.
 * @desciption :
 * @version :
 *</pre>
 */
class PermissionUtils {
    companion object {
        private var appPermissions: List<String>? = null

        /**
         * 是否是6.0以上版本
         */
        fun isOverMarshmallow(): Boolean {
            return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
        }

        /**
         * 是否是8.0以上版本
         */
        fun isOverOreo(): Boolean {
            return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
        }


        /**
         * 是否有安装权限
         */
        fun hasInstallPermission(context: Context): Boolean {
            if (isOverOreo()) {
                if (context.applicationInfo.targetSdkVersion >= Build.VERSION_CODES.O) {
                    return context.packageManager.canRequestPackageInstalls()
                }
            }
            return true
        }

        /**
         * 是否有悬浮窗权限
         */
        fun hasOverlaysPermission(context: Context): Boolean {
            if (isOverMarshmallow()) {
                if (context.applicationInfo.targetSdkVersion >= Build.VERSION_CODES.M) {
                    return Settings.canDrawOverlays(context)
                }
            }
            return true
        }


        /**
         * 获取需要授权的权限
         *
         * @param context
         * @param permissions
         * @return
         */
        fun getDeniedPermissions(context: Context, permissions: Array<String>?): List<String> {
            val deniedList = ArrayList<String>()
            permissions?.forEach {
                if (!PermissionChecker.hasPermission(context, it)) {
                    deniedList.add(it)
                }
            }
            return deniedList
        }

        /**
         * 检查权限是否在manifest注册
         *
         * @param context
         * @param permissions
         */
        fun checkPermissions(context: Context, permissions: Array<String>?) {
            if (appPermissions == null) {
                appPermissions = getManifestPermissions(context)
            }

            if (permissions.isNullOrEmpty()) {
                throw IllegalArgumentException("Please enter at least one permission.")
            }

            for (p in permissions) {
                if (!appPermissions!!.contains(p)) {
                    throw IllegalStateException(
                        String.format("The permission %1\$s is not registered in manifest.xml", p)
                    )

                }
            }
        }

        /**
         * 获取应用程序在清单文件中注册的权限
         *
         * @param context
         * @return
         */
        fun getManifestPermissions(context: Context): List<String> {
            try {
                val packageInfo = context.packageManager
                    .getPackageInfo(context.packageName, PackageManager.GET_PERMISSIONS)
                val permissions = packageInfo.requestedPermissions
                if (permissions == null || permissions.size == 0) {
                    throw IllegalStateException("did not register any permissions in the manifest.xml.")
                }
                return Collections.unmodifiableList(Arrays.asList(*permissions))
            } catch (e: PackageManager.NameNotFoundException) {
                throw AssertionError("Package name cannot be found.")
            }


        }
    }
}