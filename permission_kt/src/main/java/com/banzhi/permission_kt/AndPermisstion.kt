package com.banzhi.permission_kt

import android.app.Application
import android.app.Dialog
import android.content.Context
import android.os.Build
import androidx.appcompat.app.AlertDialog

/**
 *<pre>
 * @author : No.1
 * @time : 2019/8/7.
 * @desciption :
 * @version :
 *</pre>
 */
class AndPermisstion : PermissionListener {


    private var permissionCallback: PermissionCallback? = null
    private var mSetting: SettingServer? = null
    private var tipDialog: Dialog? = null


    companion object {
        val instance = InnerClassHolder.holder

        fun init(context: Application) {
            PermissionInit.init(context)
        }
    }

    private object InnerClassHolder {
        val holder = AndPermisstion()
    }

    fun request(builder: Builder?) {
        val topActivity = PermissionInit.getTopActivity()
        PermissionUtils.checkPermissions(topActivity, builder?.permissions!!)
        val permissions = PermissionUtils.getDeniedPermissions(topActivity, builder?.permissions!!)
        this.permissionCallback = builder.permissionCallback
        if (permissions.isEmpty()) {
            permissionCallback?.onGranted()
            return
        }
        if (builder.tipDialog == null && builder.showTip) {
            if (builder.mSetting == null) {
                mSetting = PermissionSetting()
            }
            tipDialog = createDialog(topActivity)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PermissionActivity.request(topActivity, permissions, this)
        }
    }

    private fun createDialog(context: Context): Dialog {
        return AlertDialog.Builder(context).setTitle("提示信息").setMessage(
            "当前应用缺少必要" +
                    "权限，该功能暂时无法使用。如若需要，请单击【确定】按钮前往设置中心进行权限授权。"
        )
            .setNegativeButton("取消") { dialog, which -> mSetting?.cancle() }
            .setPositiveButton("确定") { dialog, which -> mSetting?.execute() }.create()
    }

    fun newBuilder(): Builder {
        return Builder()
    }

    override fun onPermissionListerer(permissions: List<String>) {
        if (permissions?.size == 0) {
            //全部都授予了权限
            permissionCallback?.onGranted()
        } else {
            //将未授予的权限集合返回
            permissionCallback?.onDenied(permissions)
            if (tipDialog != null) {
                tipDialog?.show()
            }
        }
    }

    class Builder {
        var permissions: Array<String>? = null
        var permissionCallback: PermissionCallback? = null
        var mSetting: SettingServer? = null
        var tipDialog: Dialog? = null
        internal var showTip = true

        /**
         * 需要申请的权限
         *
         * @param permissions
         * @return
         */
        fun permissions(vararg permissions: String): Builder {
            this.permissions = Array(permissions.size) { i -> permissions[i] }

            return this
        }

        /**
         * 自定义跳转到权限设置
         *
         * @param mSetting
         * @return
         */
        fun setSetting(mSetting: SettingServer): Builder {
            this.mSetting = mSetting
            return this
        }

        /**
         * 自定义权限拒绝弹出框
         *
         * @param tipDialog
         * @return
         */
        fun setTipDialog(tipDialog: Dialog): Builder {
            this.tipDialog = tipDialog
            return this
        }

        /**
         * 显示默认弹出框
         *
         * @param showTip
         * @return
         */
        fun setShowTip(showTip: Boolean): Builder {
            this.showTip = showTip
            return this
        }

        fun request() {
            instance.request(this)
        }

        fun request(callback: PermissionCallback) {
            this.permissionCallback = callback
            if (permissions == null || permissions!!.size == 0) {
                throw NullPointerException("no permission need request")
            }
            instance.request(this)
        }
    }
}