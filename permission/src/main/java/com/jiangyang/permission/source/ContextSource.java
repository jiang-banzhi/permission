package com.jiangyang.permission.source;

import android.content.Context;
import android.content.Intent;

/**
 * <pre>
 * @author : No.1
 * @time : 2018/5/9.
 * @desciption :
 * @version :
 * </pre>
 */
public class ContextSource extends BaseSource {
    private final Context mContext;

    public ContextSource(Context context) {
        this.mContext = context;
    }

    @Override
    public Context getContext() {
        return mContext;
    }

    @Override
    public void startActivity(Intent intent) {
        mContext.startActivity(intent);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        mContext.startActivity(intent);
    }
}
