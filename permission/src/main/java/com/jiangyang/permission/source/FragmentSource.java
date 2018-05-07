package com.jiangyang.permission.source;

import android.app.Fragment;
import android.os.Build;
import android.support.annotation.RequiresApi;

/**
 * <pre>
 * @author : No.1
 * @time : 2018/5/7.
 * @desciption :
 * @version :
 * </pre>
 */

public class FragmentSource extends Source {
    Fragment mFragment;

    public FragmentSource(Fragment mFragment) {
        this.mFragment = mFragment;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public Object getFragmentManager() {
        return mFragment.getChildFragmentManager();
    }
}
