package com.banzhi.permission_kt

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.os.Bundle
import java.lang.reflect.InvocationTargetException
import java.util.*

/**
 *<pre>
 * @author : No.1
 * @time : 2019/8/7.
 * @desciption :
 * @version :
 *</pre>
 */
class PermissionActivityLifecycle : Application.ActivityLifecycleCallbacks {
    private var activities: MutableList<Activity> = ArrayList()

    fun clear() {
        activities.clear()
    }

    override fun onActivityPaused(activity: Activity) {
    }

    override fun onActivityResumed(activity: Activity) {
    }

    override fun onActivityStarted(activity: Activity) {
    }

    override fun onActivityDestroyed(activity: Activity) {
        activities.remove(activity)
    }

    override fun onActivitySaveInstanceState(activity: Activity, bundle: Bundle) {
    }

    override fun onActivityStopped(activity: Activity) {
    }

    override fun onActivityCreated(activity: Activity, bundle: Bundle?) {
        activities.add(activity)
    }

    fun getTopActivity(): Activity? {
        if (activities.isNotEmpty()) {
            return activities[activities.size - 1]
        }
        return getTopActivityByReflect()
    }

    private fun getTopActivityByReflect(): Activity? {
        try {
            @SuppressLint("PrivateApi")
            val activityThreadClass = Class.forName("android.app.ActivityThread")
            val currentActivityThreadMethod =
                activityThreadClass.getMethod("currentActivityThread").invoke(null)
            val mActivityListField = activityThreadClass.getDeclaredField("mActivityList")
            mActivityListField.isAccessible = true
            val activities = mActivityListField.get(currentActivityThreadMethod) as Map<*, *>
            for (activityRecord in activities.values) {
                val activityRecordClass = activityRecord?.javaClass
                val pausedField = activityRecordClass?.getDeclaredField("paused")
                pausedField?.isAccessible = true
                if (pausedField?.getBoolean(activityRecord) == false) {
                    val activityField = activityRecordClass.getDeclaredField("activity")
                    activityField.isAccessible = true
                    return activityField.get(activityRecord) as Activity
                }
            }
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        }

        return null
    }

}