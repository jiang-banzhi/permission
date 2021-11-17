package com.banzhi.permission_kt

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.util.Log
import androidx.fragment.app.FragmentActivity
import java.lang.reflect.InvocationTargetException

/**
 *<pre>
 * @author : No.1
 * @time : 2019/8/7.
 * @desciption :
 * @version :
 *</pre>
 */
class PermissionInit {

    companion object {
        private var sApplication: Application? = null
        private val lifecycle = PermissionActivityLifecycle()

        fun init(context: Context?) {
            if (context == null) {
                init(getApplicationByReflect())
            } else {
                init(context.applicationContext as Application)
            }
        }

        fun init(app: Application?) {
            Log.e("init","PermissionInit")
            if (sApplication == null) {
                sApplication
                if (app == null) {
                    sApplication = getApplicationByReflect()
                } else {
                    sApplication = app
                }
                sApplication!!.registerActivityLifecycleCallbacks(lifecycle)
            } else {
                if (app != null && app.javaClass != sApplication!!.javaClass) {
                    sApplication!!.unregisterActivityLifecycleCallbacks(lifecycle)
                    lifecycle.clear()
                    sApplication = app
                    sApplication!!.registerActivityLifecycleCallbacks(lifecycle)
                }
            }
        }

        private fun getApplicationByReflect(): Application {
            try {
                @SuppressLint("PrivateApi")
                val activityThread = Class.forName("android.app.ActivityThread")
                val thread = activityThread.getMethod("currentActivityThread").invoke(null)
                val app = activityThread.getMethod("getApplication").invoke(thread)
                        ?: throw NullPointerException("u should init first")
                return app as Application
            } catch (e: NoSuchMethodException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            } catch (e: InvocationTargetException) {
                e.printStackTrace()
            } catch (e: ClassNotFoundException) {
                e.printStackTrace()
            }

            throw NullPointerException("u should init first")
        }

        fun getTopActivity(): Activity? {

            return lifecycle.getTopActivity()
        }
    }
}