package com.banzhi.permission_kt

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.view.WindowManager
import java.util.*

/**
 *<pre>
 * @author : No.1
 * @time : 2019/8/7.
 * @desciption :
 * @version :
 *</pre>
 */
class PermissionActivity : Activity() {

    val CODE_REQUEST_PERMISSION = 663
    val CODE_REQUEST_INSTALL = 213
    val CODE_REQUEST_OVERLAY = 214


    var isRequestInstall = false//是否已经请求安装权限
    var isRequestOverlay = false//是否已经请求悬浮窗权限

    companion object {

        const val PERMISSION_TAG = "permissions_kt"
        private var sPermissionListener: PermissionListener? = null

        /**
         * 请求权限
         *
         * @param context
         * @param permissions
         * @param permissionListener
         */
        fun request(context: Context, permissions: Array<String>, permissionListener: PermissionListener) {
            sPermissionListener = permissionListener
            val intent = Intent(context, PermissionActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.putExtra(PERMISSION_TAG, permissions)
            context.startActivity(intent)
        }

        fun request(context: Context, permissions: List<String>, permissionListener: PermissionListener) {
            request(context, permissions.toTypedArray(), permissionListener)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        invasionStatusBar()
        requestPermission()
    }

    fun invasionStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = window
            val decorView = window.decorView
            decorView.systemUiVisibility = (decorView.systemUiVisibility
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.TRANSPARENT
        }
    }

    internal var requestPermissions: MutableList<String> = ArrayList()
    fun requestPermission() {
        val intent = intent
        val mPermissions = intent.getStringArrayExtra(PERMISSION_TAG)
        if (mPermissions != null && sPermissionListener != null) {
            requestPermissions = getDeniedPermissions(mPermissions)
            //所有权限都已授权
            if (requestPermissions.size == 0) {
                hasAllPermissions()
            } else {
                requestPermissions()
            }
        } else {
            sPermissionListener = null
            finish()
        }
    }

    /**
     * 已经获取所需权限
     */
    private fun hasAllPermissions() {
        sPermissionListener?.onPermissionListerer(ArrayList<String>())
        sPermissionListener = null
        finish()
    }

    /**
     * 获取权限集中需要申请权限的列表
     *
     * @param permissions
     * @return
     */
    private fun getDeniedPermissions(permissions: Array<String>): ArrayList<String> {
        val needRequestPermissonList = ArrayList<String>()
        for (permission in permissions) {
            //权限未授权
            if (!PermissionChecker.hasPermission(this, permission)) {
                needRequestPermissonList.add(permission)
            }
        }
        return needRequestPermissonList
    }

    /**
     * 检查权限是否授予
     */
    private fun requestPermissions() {
        //跳转到允许安装未知来源设置页面
        if (requestPermissions.contains(Manifest.permission.REQUEST_INSTALL_PACKAGES)) {
            if (PermissionUtils.hasInstallPermission(this)) {
                requestPermissions.remove(Manifest.permission.REQUEST_INSTALL_PACKAGES)
                if (requestPermissions.size == 0) {
                    hasAllPermissions()
                    return
                }
            } else {
                if (!isRequestInstall) {
                    installSetting()
                    return
                }
            }
        }
        //跳转到悬浮窗设置页面
        if (requestPermissions.contains(Manifest.permission.SYSTEM_ALERT_WINDOW)) {
            if (PermissionUtils.hasOverlaysPermission(this)) {
                requestPermissions.remove(Manifest.permission.SYSTEM_ALERT_WINDOW)
                if (requestPermissions.size == 0) {
                    hasAllPermissions()
                    return
                }
            } else {
                if (!isRequestOverlay) {
                    overlaySetting()
                    return
                }
            }
        }
        if (requestPermissions.isEmpty()) {
            sPermissionListener?.onPermissionListerer(requestPermissions)
            sPermissionListener = null
            finish()
        } else {
            val permissions = requestPermissions.toTypedArray()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(permissions, CODE_REQUEST_PERMISSION)
            }
        }

    }

    /**
     * 跳转到允许安装未知来源设置页面
     */
    private fun installSetting() {
        val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:$packageName"))
        startActivityForResult(intent, CODE_REQUEST_INSTALL)
    }

    /**
     * 跳转到悬浮窗设置页面
     */
    private fun overlaySetting() {
        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
        startActivityForResult(intent, CODE_REQUEST_OVERLAY)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CODE_REQUEST_PERMISSION) {
            //未授权集合
            val deniedList = ArrayList<String>()
            for (i in permissions.indices) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    deniedList.add(permissions[i])
                }
            }
            if (sPermissionListener != null) {
                sPermissionListener?.onPermissionListerer(deniedList)
                sPermissionListener = null
                finish()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            CODE_REQUEST_INSTALL -> {
                isRequestInstall = true
                if (resultCode == RESULT_OK) {
                    requestPermissions.remove(Manifest.permission.REQUEST_INSTALL_PACKAGES)

                }
            }
            CODE_REQUEST_OVERLAY -> {
                isRequestOverlay = true
                if (resultCode == RESULT_OK) {
                    requestPermissions.remove(Manifest.permission.SYSTEM_ALERT_WINDOW)
                }
            }
            else -> {
            }
        }
        requestPermissions()
    }

}
