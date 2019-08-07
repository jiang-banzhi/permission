package com.banzhi.permission_kt

/**
 *<pre>
 * @author : No.1
 * @time : 2019/8/7.
 * @desciption :
 * @version :
 *</pre>
 */
interface SettingServer {
    /**
     * 执行
     */
     fun execute()


    /**
     * 取消
     */
     fun cancle()


    /**
     * 执行
     *
     * @param requestCode
     */
     fun execute(requestCode: Int)
}
