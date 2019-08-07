package com.banzhi.permission_kt

/**
 *<pre>
 * @author : No.1
 * @time : 2019/8/7.
 * @desciption :
 * @version :
 *</pre>
 */
interface PermissionListener {
    fun onPermissionListerer(permissions: List<String>)
}