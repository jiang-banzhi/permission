package com.banzhi.permission_kt

/**
 *<pre>
 * @author : No.1
 * @time : 2019/8/7.
 * @desciption :
 * @version :
 *</pre>
 */
interface PermissionCallback {
    /**
     * 获取权限成功回调
     */
    abstract fun onGranted()

    /**
     * 未授权权限
     *
     * @param list 未授权权限集合
     */
    abstract fun onDenied(list: List<String>)
}