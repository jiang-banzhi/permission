package com.jiangyang.permission.source;

import android.support.v4.app.FragmentActivity;

/**
 * <pre>
 * @author : No.1
 * @time : 2018/5/7.
 * @desciption :
 * @version :
 * </pre>
 */

public class FragmentActivitySource extends Source {
    FragmentActivity mActivity;

    public FragmentActivitySource(FragmentActivity mActivity) {
        this.mActivity = mActivity;
    }

    @Override
    public Object getFragmentManager() {
        return mActivity.getSupportFragmentManager();
    }
}
