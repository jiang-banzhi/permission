package com.banzhi.permission.source;

import android.app.Activity;
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

public class AppActivitySource extends BaseSource {
    private final Activity mACtivity;

    public AppActivitySource(Activity mACtivity) {
        this.mACtivity = mACtivity;
    }


    @Override
    public Context getContext() {
        return mACtivity;
    }

    @Override
    public void startActivity(Intent intent) {
        mACtivity.startActivity(intent);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        mACtivity.startActivityForResult(intent, requestCode);
    }
}
