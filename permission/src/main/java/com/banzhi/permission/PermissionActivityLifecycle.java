package com.banzhi.permission;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application.ActivityLifecycleCallbacks;
import android.os.Bundle;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <pre>
 * @author : No.1
 * @time : 2019/5/28.
 * @desciption :
 * @version :
 * </pre>
 */

public class PermissionActivityLifecycle implements ActivityLifecycleCallbacks {
    List<Activity> activities = new ArrayList<>();

    public void clear() {
        activities.clear();
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        activities.add(activity);
    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        activities.remove(activity);
    }

    public Activity getTopActivity() {
        if (!activities.isEmpty()) {
            final Activity topActivity = activities.get(activities.size() - 1);
            if (topActivity != null) {
                return topActivity;
            }
        }
        Activity topActivityByReflect = getTopActivityByReflect();
        return topActivityByReflect;
    }

    private Activity getTopActivityByReflect() {
        try {
            @SuppressLint("PrivateApi")
            Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");
            Object currentActivityThreadMethod = activityThreadClass.getMethod("currentActivityThread").invoke(null);
            Field mActivityListField = activityThreadClass.getDeclaredField("mActivityList");
            mActivityListField.setAccessible(true);
            Map activities = (Map) mActivityListField.get(currentActivityThreadMethod);
            if (activities == null) return null;
            for (Object activityRecord : activities.values()) {
                Class activityRecordClass = activityRecord.getClass();
                Field pausedField = activityRecordClass.getDeclaredField("paused");
                pausedField.setAccessible(true);
                if (!pausedField.getBoolean(activityRecord)) {
                    Field activityField = activityRecordClass.getDeclaredField("activity");
                    activityField.setAccessible(true);
                    return (Activity) activityField.get(activityRecord);
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return null;
    }


}
