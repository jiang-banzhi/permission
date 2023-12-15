package com.banzhi.permission_kt

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment


/**
 *<pre>
 * @author : jiang
 * @time : 2021/11/17.
 * @desciption :
 * @version :
 *</pre>
 */
class PermissionFragment : Fragment() {
    private val allSpecialPermissions = listOf(
        Manifest.permission.ACCESS_BACKGROUND_LOCATION,
        Manifest.permission.SYSTEM_ALERT_WINDOW,
        Manifest.permission.WRITE_SETTINGS,
        Manifest.permission.MANAGE_EXTERNAL_STORAGE,
        Manifest.permission.REQUEST_INSTALL_PACKAGES
    )

    private var sPermissionListener: PermissionListener? = null


    private val normalList = mutableListOf<String>()
    private val specialList = mutableListOf<String>()

    //未授权集合
    private val deniedList = ArrayList<String>()

    fun requestPermissionNow(
        requestList: List<String>?,
        permissionListener: PermissionListener
    ) {
        sPermissionListener = permissionListener
        requestList?.forEach {
            if (it in allSpecialPermissions) {
                if (!checkSpecialPermission(it)) {
                    specialList.add(it)
                }
            } else {
                normalList.add(it)
            }
        }
        val osVersion = Build.VERSION.SDK_INT
        val targetSdkVersion = requireActivity().applicationInfo.targetSdkVersion
        if (requestList?.contains(Manifest.permission.ACCESS_BACKGROUND_LOCATION) == true &&
            (osVersion == Build.VERSION_CODES.Q ||
                    (osVersion == Build.VERSION_CODES.R && targetSdkVersion < Build.VERSION_CODES.R))
        ) {
            specialList.remove(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            normalList.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        }

        requestNormalPermissions()
    }


    private fun requestPermissions() {
        if (specialList.isNotEmpty()) {
            val permission = specialList.firstOrNull()
            val message =
                requireActivity().packageManager.getApplicationLabel(requireActivity().applicationInfo)
                    .toString()
            DefaultDialog(requireActivity(), permission, message) {
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
        detachActivity()
    }

    /**
     * 解绑 Activity
     */
    private fun detachActivity() {
        requireActivity().supportFragmentManager.beginTransaction().remove(this)
            .commitAllowingStateLoss()
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
                requestBackgroundLocationLauncher()
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
            && requireActivity().applicationInfo.targetSdkVersion >= Build.VERSION_CODES.O
        ) {
            if (requireActivity().packageManager.canRequestPackageInstalls()) {
                onRequestInstallPackagesPermissionResult()
                return
            }
            val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES)
            intent.data = Uri.parse("package:${requireActivity().packageName}")
            installPackagesLauncher.launch(intent)
        } else {
            onRequestInstallPackagesPermissionResult()
        }


    }

    private fun onRequestInstallPackagesPermissionResult() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!requireActivity().packageManager.canRequestPackageInstalls()) {
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
            && requireActivity().applicationInfo.targetSdkVersion >= Build.VERSION_CODES.O
        ) {
            if (Settings.canDrawOverlays(requireActivity())) {
                onRequestSystemAlertWindowPermissionResult()
                return
            }
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
            intent.data = Uri.parse("package:${requireActivity().packageName}")
            requestSystemAlertWindowLauncher.launch(intent)
        } else {
            onRequestSystemAlertWindowPermissionResult()
        }
    }

    private fun onRequestSystemAlertWindowPermissionResult() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(requireActivity())) {
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
            && !Settings.System.canWrite(requireActivity())
        ) {
            val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
            intent.data = Uri.parse("package:${requireActivity().packageName}")
            writeSettingsLauncher.launch(intent)
        } else {
            onRequestWriteSettingsPermissionResult()
        }
    }

    private fun onRequestWriteSettingsPermissionResult() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
        ) {
            if (!Settings.System.canWrite(requireActivity())) {
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

    /**
     * 是否已经授予特殊权限
     */
    private fun checkSpecialPermission(permission: String?): Boolean {
        return when (permission) {
            Manifest.permission.ACCESS_BACKGROUND_LOCATION -> {
                PermissionChecker.hasPermission(requireContext(), permission)
            }
            Manifest.permission.SYSTEM_ALERT_WINDOW -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    Settings.canDrawOverlays(requireActivity()) else true

            }
            Manifest.permission.WRITE_SETTINGS -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    Settings.System.canWrite(requireActivity())
                } else {
                    true
                }
            }
            Manifest.permission.MANAGE_EXTERNAL_STORAGE -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) Environment.isExternalStorageManager() else true

            }
            Manifest.permission.REQUEST_INSTALL_PACKAGES -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    requireActivity().packageManager.canRequestPackageInstalls() else true
            }
            else -> false
        }
    }
}