package com.jiangyang.permission.source;

import android.app.Activity;

/**
 * <pre>
 * @author : No.1
 * @time : 2018/5/7.
 * @desciption :
 * @version :
 * </pre>
 */

public class AppActivitySource extends Source {
    Activity mACtivity;

    public AppActivitySource(Activity mACtivity) {
        this.mACtivity = mACtivity;
    }

    @Override
    public Object getFragmentManager() {
        return mACtivity.getFragmentManager();
    }
}
