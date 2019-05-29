package com.banzhi.permission;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;


/**
 * <pre>
 * @author : No.1
 * @time : 2018/5/9.
 * @desciption : 权限跳转
 * @version :
 * </pre>
 */
public class PermissionSetting implements SettingServer {
    /**
     * 制造商
     */
    private static final String MAKER = Build.MANUFACTURER.toLowerCase();
    /**
     * 华为
     */
    private static final String HUAWEI = "huawei";
    /**
     * 小米
     */
    private static final String XIAOMI = "xiaomi";
    /**
     * oppo
     */
    private static final String OPPO = "oppo";
    /**
     * vivo
     */
    private static final String VIVO = "vivo";
    /**
     * 三星
     */
    private static final String SAMSUNG = "samsung";
    /**
     * 魅族
     */
    private static final String MEIZU = "meizu";
    /**
     * 锤子
     */
    private static final String SMARTISAN = "smartisan";
    /**
     * 索尼
     */
    private static final String SONY = "sony";
    /**
     * LG
     */
    private static final String LG = "lg";
    /**
     * 乐视
     */
    private static final String LETV = "letv";

    Activity topActivity;


    public PermissionSetting() {
            topActivity=PermissionInit.getTopActivity();
    }

    @Override
    public void execute() {
        Intent intent = obtainSettingIntent(topActivity);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            topActivity.startActivity(intent);
        } catch (Exception e) {
            topActivity.startActivity(defaultIntent(topActivity));
        }
    }

    @Override
    public void execute(int requestCode) {
        Intent intent = obtainSettingIntent(topActivity);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            topActivity.startActivityForResult(intent, requestCode);
        } catch (Exception e) {
            topActivity.startActivityForResult(defaultIntent(topActivity), requestCode);
        }
    }

    @Override
    public void cancle() {

    }

    /**
     * @param context
     * @return
     */
    private static Intent defaultIntent(Context context) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.fromParts("package", context.getPackageName(), null));
        return intent;
    }

    private Intent obtainSettingIntent(Context context) {
        if (MAKER.contains(HUAWEI)) {
            return huaweiIntent(context);
        } else if (MAKER.contains(XIAOMI)) {
            return xiaomiIntent(context);
        } else if (MAKER.contains(OPPO)) {
            return oppoIntent(context);
        } else if (MAKER.contains(VIVO)) {
            return vivoIntent(context);
        } else if (MAKER.contains(SAMSUNG)) {
            return samsungIntent(context);
        } else if (MAKER.contains(MEIZU)) {
            return meizuIntent(context);
        } else if (MAKER.contains(SMARTISAN)) {
            return smartisanIntent(context);
        } else if (MAKER.contains(SONY)) {
            return sonyIntent(context);
        } else if (MAKER.contains(LG)) {
            return LGIntent(context);
        } else if (MAKER.contains(LETV)) {
            return LetIntent(context);
        }
        return defaultIntent(context);
    }


    /**
     * 华为
     *
     * @param context
     * @return
     */
    private static Intent huaweiIntent(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return defaultIntent(context);
        }
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.permissionmanager.ui.MainActivity"));
        return intent;
    }

    /**
     * 小米
     *
     * @param context
     * @return
     */
    private static Intent xiaomiIntent(Context context) {
        Intent intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
        intent.putExtra("extra_pkgname", context.getPackageName());
        return intent;
    }

    /**
     * vivo
     *
     * @param context
     * @return
     */
    private static Intent vivoIntent(Context context) {
        Intent intent = new Intent();
        intent.putExtra("packagename", context.getPackageName());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            intent.setComponent(new ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.SoftPermissionDetailActivity"));
        } else {
            intent.setComponent(new ComponentName("com.iqoo.secure", "com.iqoo.secure.safeguard.SoftPermissionDetailActivity"));
        }
        return intent;
    }

    /**
     * Oppo
     *
     * @param context
     * @return
     */
    private static Intent oppoIntent(Context context) {
        return defaultIntent(context);
    }

    /**
     * 魅族
     *
     * @param context
     * @return
     */
    private static Intent meizuIntent(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            return defaultIntent(context);
        }
        Intent intent = new Intent("com.meizu.safe.security.SHOW_APPSEC");
        intent.putExtra("packageName", context.getPackageName());
        intent.setComponent(new ComponentName("com.meizu.safe", "com.meizu.safe.security.AppSecActivity"));
        return intent;
    }

    /**
     * Smartisan
     *
     * @param context
     * @return
     */
    private static Intent smartisanIntent(Context context) {
        return defaultIntent(context);
    }

    /**
     * 三星
     *
     * @param context
     * @return
     */
    private static Intent samsungIntent(Context context) {
        return defaultIntent(context);
    }

    /**
     * 索尼
     *
     * @param context
     * @return
     */
    private static Intent sonyIntent(Context context) {
        Intent intent = new Intent();
        intent.putExtra("packageName", BuildConfig.APPLICATION_ID);
        ComponentName comp = new ComponentName("com.sonymobile.cta", "com.sonymobile.cta.SomcCTAMainActivity");
        intent.setComponent(comp);
        return intent;
    }

    /**
     * LG
     *
     * @param context
     * @return
     */
    private static Intent LGIntent(Context context) {
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.putExtra("packageName", BuildConfig.APPLICATION_ID);
        ComponentName comp = new ComponentName("com.android.settings", "com.android.settings.Settings$AccessLockSummaryActivity");
        intent.setComponent(comp);
        return intent;
    }

    /**
     * 乐视
     *
     * @param context
     * @return
     */
    private static Intent LetIntent(Context context) {
        Intent intent = new Intent();
        intent.putExtra("packageName", BuildConfig.APPLICATION_ID);
        ComponentName comp = new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.PermissionAndApps");
        intent.setComponent(comp);
        return intent;
    }
}
