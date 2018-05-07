package com.jiangyang.permission.source;

import android.support.v4.app.Fragment;

/**
 * <pre>
 * @author : No.1
 * @time : 2018/5/7.
 * @desciption :
 * @version :
 * </pre>
 */

public class SupportFragmentSource extends Source {
    Fragment mFragment;

    public SupportFragmentSource(Fragment mFragment) {
        this.mFragment = mFragment;
    }

    @Override
    public Object getFragmentManager() {
        return mFragment.getChildFragmentManager();
    }
}
