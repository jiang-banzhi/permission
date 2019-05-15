package com.banzhi.permission.source;

import android.content.Context;
import android.content.Intent;

/**
 * <pre>
 * @author : No.1
 * @time : 2018/5/7.
 * @desciption :
 * @version :
 * </pre>
 */

public abstract class BaseSource {
    /**
     * 获取cotext
     *
     * @return Context
     */
    public abstract Context getContext();

    /**
     * 跳转
     *
     * @param intent
     */
    public abstract void startActivity(Intent intent);

    /**
     * 跳转
     *
     * @param intent
     * @param requestCode 请求码
     */
    public abstract void startActivityForResult(Intent intent, int requestCode);
}
