package com.banzhi.permission_kt

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings

/**
 *<pre>
 * @author : No.1
 * @time : 2019/8/7.
 * @desciption :
 * @version :
 *</pre>
 */
class PermissionSetting : SettingServer {
    /**
     * 制造商
     */
    private val MAKER = Build.MANUFACTURER.toLowerCase()
    /**
     * 华为
     */
    private val HUAWEI = "huawei"
    /**
     * 小米
     */
    private val XIAOMI = "xiaomi"
    /**
     * oppo
     */
    private val OPPO = "oppo"
    /**
     * vivo
     */
    private val VIVO = "vivo"
    /**
     * 三星
     */
    private val SAMSUNG = "samsung"
    /**
     * 魅族
     */
    private val MEIZU = "meizu"
    /**
     * 锤子
     */
    private val SMARTISAN = "smartisan"
    /**
     * 索尼
     */
    private val SONY = "sony"
    /**
     * LG
     */
    private val LG = "lg"
    /**
     * 乐视
     */
    private val LETV = "letv"

    internal var topActivity: Activity

    init {
        topActivity = PermissionInit.getTopActivity()
    }


    override fun execute() {
        val intent = obtainSettingIntent(topActivity)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            topActivity.startActivity(intent)
        } catch (e: Exception) {
            topActivity.startActivity(defaultIntent(topActivity))
        }

    }

    override fun execute(requestCode: Int) {
        val intent = obtainSettingIntent(topActivity)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            topActivity.startActivityForResult(intent, requestCode)
        } catch (e: Exception) {
            topActivity.startActivityForResult(defaultIntent(topActivity), requestCode)
        }

    }

    override fun cancle() {

    }

    /**
     * @param context
     * @return
     */
    private fun defaultIntent(context: Context): Intent {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = Uri.fromParts("package", context.packageName, null)
        return intent
    }

    private fun obtainSettingIntent(context: Context): Intent {
        if (MAKER.contains(HUAWEI)) {
            return huaweiIntent(context)
        } else if (MAKER.contains(XIAOMI)) {
            return xiaomiIntent(context)
        } else if (MAKER.contains(OPPO)) {
            return oppoIntent(context)
        } else if (MAKER.contains(VIVO)) {
            return vivoIntent(context)
        } else if (MAKER.contains(SAMSUNG)) {
            return samsungIntent(context)
        } else if (MAKER.contains(MEIZU)) {
            return meizuIntent(context)
        } else if (MAKER.contains(SMARTISAN)) {
            return smartisanIntent(context)
        } else if (MAKER.contains(SONY)) {
            return sonyIntent(context)
        } else if (MAKER.contains(LG)) {
            return LGIntent(context)
        } else if (MAKER.contains(LETV)) {
            return LetIntent(context)
        }
        return defaultIntent(context)
    }


    /**
     * 华为
     *
     * @param context
     * @return
     */
    private fun huaweiIntent(context: Context): Intent {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return defaultIntent(context)
        }
        val intent = Intent()
        intent.component = ComponentName("com.huawei.systemmanager", "com.huawei.permissionmanager.ui.MainActivity")
        return intent
    }

    /**
     * 小米
     *
     * @param context
     * @return
     */
    private fun xiaomiIntent(context: Context): Intent {
        val intent = Intent("miui.intent.action.APP_PERM_EDITOR")
        intent.putExtra("extra_pkgname", context.packageName)
        return intent
    }

    /**
     * vivo
     *
     * @param context
     * @return
     */
    private fun vivoIntent(context: Context): Intent {
        val intent = Intent()
        intent.putExtra("packagename", context.packageName)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            intent.component = ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.SoftPermissionDetailActivity")
        } else {
            intent.component = ComponentName("com.iqoo.secure", "com.iqoo.secure.safeguard.SoftPermissionDetailActivity")
        }
        return intent
    }

    /**
     * Oppo
     *
     * @param context
     * @return
     */
    private fun oppoIntent(context: Context): Intent {
        return defaultIntent(context)
    }

    /**
     * 魅族
     *
     * @param context
     * @return
     */
    private fun meizuIntent(context: Context): Intent {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            return defaultIntent(context)
        }
        val intent = Intent("com.meizu.safe.security.SHOW_APPSEC")
        intent.putExtra("packageName", context.packageName)
        intent.component = ComponentName("com.meizu.safe", "com.meizu.safe.security.AppSecActivity")
        return intent
    }

    /**
     * Smartisan
     *
     * @param context
     * @return
     */
    private fun smartisanIntent(context: Context): Intent {
        return defaultIntent(context)
    }

    /**
     * 三星
     *
     * @param context
     * @return
     */
    private fun samsungIntent(context: Context): Intent {
        return defaultIntent(context)
    }

    /**
     * 索尼
     *
     * @param context
     * @return
     */
    private fun sonyIntent(context: Context): Intent {
        val intent = Intent()
        intent.putExtra("packageName", BuildConfig.APPLICATION_ID)
        val comp = ComponentName("com.sonymobile.cta", "com.sonymobile.cta.SomcCTAMainActivity")
        intent.component = comp
        return intent
    }

    /**
     * LG
     *
     * @param context
     * @return
     */
    private fun LGIntent(context: Context): Intent {
        val intent = Intent("android.intent.action.MAIN")
        intent.putExtra("packageName", BuildConfig.APPLICATION_ID)
        val comp = ComponentName("com.android.settings", "com.android.settings.Settings\$AccessLockSummaryActivity")
        intent.component = comp
        return intent
    }

    /**
     * 乐视
     *
     * @param context
     * @return
     */
    private fun LetIntent(context: Context): Intent {
        val intent = Intent()
        intent.putExtra("packageName", BuildConfig.APPLICATION_ID)
        val comp = ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.PermissionAndApps")
        intent.component = comp
        return intent
    }
}