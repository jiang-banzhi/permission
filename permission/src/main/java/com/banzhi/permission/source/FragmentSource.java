package com.banzhi.permission.source;

import android.app.Fragment;
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

public class FragmentSource extends BaseSource {
    private final Fragment mFragment;

    public FragmentSource(Fragment mFragment) {
        this.mFragment = mFragment;
    }

    @Override
    public Context getContext() {
        return mFragment.getContext();
    }

    @Override
    public void startActivity(Intent intent) {
        mFragment.startActivity(intent);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        mFragment.startActivityForResult(intent, requestCode);
    }
}
