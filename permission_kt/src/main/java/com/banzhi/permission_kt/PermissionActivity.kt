package com.banzhi.permission_kt

import android.Manifest
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.View
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import java.util.*

/**
 *<pre>
 * @author : No.1
 * @time : 2019/8/7.
 * @desciption :
 * @version :
 *</pre>
 */
class PermissionActivity : ComponentActivity() {
    private val allSpecialPermissions = listOf(
        Manifest.permission.ACCESS_BACKGROUND_LOCATION,
        Manifest.permission.SYSTEM_ALERT_WINDOW,
        Manifest.permission.WRITE_SETTINGS,
        Manifest.permission.MANAGE_EXTERNAL_STORAGE,
        Manifest.permission.REQUEST_INSTALL_PACKAGES
    )
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
        fun request(
            context: Context,
            permissions: Array<String>,
            permissionListener: PermissionListener
        ) {
            sPermissionListener = permissionListener
            val intent = Intent(context, PermissionActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.putExtra(PERMISSION_TAG, permissions)
            context.startActivity(intent)
        }

        fun request(
            context: Context,
            permissions: List<String>,
            permissionListener: PermissionListener
        ) {
            request(context, permissions.toTypedArray(), permissionListener)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        invasionStatusBar()
        requestPermissionNow()
    }

    private fun invasionStatusBar() {
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

    private val normalList = mutableListOf<String>()
    private val specialList = mutableListOf<String>()

    //未授权集合
    private val deniedList = ArrayList<String>()
    private fun requestPermissionNow() {
        val requestList = intent.getStringArrayExtra(PERMISSION_TAG)
        requestList?.forEach {
            if (it in allSpecialPermissions) {
                specialList.add(it)
            } else {
                normalList.add(it)
            }
        }
        val osVersion = Build.VERSION.SDK_INT
        val targetSdkVersion = applicationInfo.targetSdkVersion

        if (osVersion == Build.VERSION_CODES.Q ||
            (osVersion == Build.VERSION_CODES.R && targetSdkVersion < Build.VERSION_CODES.R)
        ) {
            specialList.remove(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            normalList.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        }
        requestNormalPermissions()
    }

    private fun requestPermissions() {
        if (specialList.isNotEmpty()) {
            val permission = specialList.firstOrNull()
            val message = packageManager.getApplicationLabel(applicationInfo).toString()
            DefaultDialog(this, permission, message) {
                when (it) {
                    0 -> {
                        requestSpecialPermissions(permission)
                    }
                    1 -> {
                        specialList.remove(permission)
                        requestSpecialPermissions(permission)
                    }
                }
            }.show()

            return
        }
        sPermissionListener?.onPermissionListerer(deniedList)
        sPermissionListener = null
        finish()
    }

    private fun requestSpecialPermissions(permission: String?) {
        when (permission) {
            Manifest.permission.SYSTEM_ALERT_WINDOW -> {
                requestSystemAlertPermission()
            }
            Manifest.permission.REQUEST_INSTALL_PACKAGES -> {
                requestInstallPermission()
            }
            Manifest.permission.ACCESS_BACKGROUND_LOCATION -> {
                requestInstallPermission()
            }
            Manifest.permission.WRITE_SETTINGS -> {
                requestWriteSettingPermission()
            }
            Manifest.permission.MANAGE_EXTERNAL_STORAGE -> {
                requestManageExternalStoragePermission()
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // 通用权限
    ///////////////////////////////////////////////////////////////////////////
    private val normalPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { grantResults ->
            onRequestNormalPermissionsResult(grantResults)
        }

    private fun onRequestNormalPermissionsResult(grantResults: Map<String, Boolean>) {
        for ((permission, granted) in grantResults) {
            if (!granted) {
                deniedList.add(permission)
            }
        }
        requestPermissions()
    }

    private fun requestNormalPermissions() {
        normalPermissionLauncher.launch(normalList.toTypedArray())
    }

    ///////////////////////////////////////////////////////////////////////////
    // 安装未知来源 android.permission.REQUEST_INSTALL_PACKAGES
    ///////////////////////////////////////////////////////////////////////////
    private val installPackagesLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            onRequestInstallPackagesPermissionResult()
        }

    private fun requestInstallPermission() {
        if (specialList.contains(Manifest.permission.REQUEST_INSTALL_PACKAGES)
            && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
            && applicationInfo.targetSdkVersion >= Build.VERSION_CODES.O
        ) {
            if (packageManager.canRequestPackageInstalls()) {
                onRequestInstallPackagesPermissionResult()
                return
            }
            val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES)
            intent.data = Uri.parse("package:${packageName}")
            installPackagesLauncher.launch(intent)
        } else {
            onRequestInstallPackagesPermissionResult()
        }


    }

    private fun onRequestInstallPackagesPermissionResult() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!packageManager.canRequestPackageInstalls()) {
                deniedList.add(Manifest.permission.REQUEST_INSTALL_PACKAGES)
            }
        }
        specialList.remove(Manifest.permission.REQUEST_INSTALL_PACKAGES)
        requestPermissions()
    }

    ///////////////////////////////////////////////////////////////////////////
    // 悬浮窗 android.permission.SYSTEM_ALERT_WINDOW
    ///////////////////////////////////////////////////////////////////////////
    private val requestSystemAlertWindowLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            onRequestSystemAlertWindowPermissionResult()
        }

    private fun requestSystemAlertPermission() {
        if (specialList.contains(Manifest.permission.SYSTEM_ALERT_WINDOW)
            && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
            && applicationInfo.targetSdkVersion >= Build.VERSION_CODES.O
        ) {
            if (Settings.canDrawOverlays(this)) {
                onRequestSystemAlertWindowPermissionResult()
                return
            }
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
            intent.data = Uri.parse("package:${packageName}")
            requestSystemAlertWindowLauncher.launch(intent)
        } else {
            onRequestSystemAlertWindowPermissionResult()
        }
    }

    private fun onRequestSystemAlertWindowPermissionResult() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                deniedList.add(Manifest.permission.SYSTEM_ALERT_WINDOW)
            }
        }
        specialList.remove(Manifest.permission.SYSTEM_ALERT_WINDOW)
        requestPermissions()
    }

    ///////////////////////////////////////////////////////////////////////////
    // 写入设置 android.permission.WRITE_SETTINGS
    ///////////////////////////////////////////////////////////////////////////

    private val writeSettingsLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            onRequestWriteSettingsPermissionResult()
        }

    private fun requestWriteSettingPermission() {
        if (specialList.contains(Manifest.permission.WRITE_SETTINGS)
            && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
            && !Settings.System.canWrite(this)
        ) {
            val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
            intent.data = Uri.parse("package:${packageName}")
            writeSettingsLauncher.launch(intent)
        } else {
            onRequestWriteSettingsPermissionResult()
        }
    }

    private fun onRequestWriteSettingsPermissionResult() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
        ) {
            if (!Settings.System.canWrite(this)) {
                deniedList.add(Manifest.permission.WRITE_SETTINGS)
            }
        }
        specialList.remove(Manifest.permission.WRITE_SETTINGS)
        requestPermissions()
    }

    ///////////////////////////////////////////////////////////////////////////
    // 文件管理 android.permission.MANAGE_EXTERNAL_STORAGE
    ///////////////////////////////////////////////////////////////////////////
    private val manageExternalStorageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            onRequestManageExternalStoragePermissionResult()
        }

    private fun requestManageExternalStoragePermission() {

        if (specialList.contains(Manifest.permission.MANAGE_EXTERNAL_STORAGE)
            && Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
            && !Environment.isExternalStorageManager()
        ) {
            val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
            manageExternalStorageLauncher.launch(intent)
        } else {
            onRequestManageExternalStoragePermissionResult()
        }
    }

    private fun onRequestManageExternalStoragePermissionResult() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                deniedList.add(Manifest.permission.MANAGE_EXTERNAL_STORAGE)
            }
        }
        specialList.remove(Manifest.permission.MANAGE_EXTERNAL_STORAGE)
        requestPermissions()
    }

    ///////////////////////////////////////////////////////////////////////////
    // 后台定位 android.permission.ACCESS_BACKGROUND_LOCATION
    ///////////////////////////////////////////////////////////////////////////
    private val backgroundLocationLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            onRequestBackgroundLocationPermissionResult(granted)
        }

    private fun requestBackgroundLocationLauncher() {
        if (specialList.contains(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
        ) {
            backgroundLocationLauncher.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        }
    }

    private fun onRequestBackgroundLocationPermissionResult(granted: Boolean?) {
        if (granted == false) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                deniedList.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            }
        }
        specialList.remove(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        requestPermissions()
    }

}
