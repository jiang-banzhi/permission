package com.jiangyang.permission;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

/**
 * <pre>
 * @author : No.1
 * @time : 2018/5/2.
 * @desciption :
 * @version :
 * </pre>
 */

public class AndPermisstion {

    private volatile static AndPermisstion mAndPermisstion = null;
    private String TAG = PermissionFragment.class.getSimpleName();

    private AndPermisstion() {
    }

    public static AndPermisstion instance() {
        if (mAndPermisstion == null) {
            synchronized (AndPermisstion.class) {
                if (mAndPermisstion == null) {
                    mAndPermisstion = new AndPermisstion();
                }
            }
        }
        return mAndPermisstion;
    }

    PermissionFragment permissionFragment;

    public PermissionFragment with(FragmentActivity activity) {
        return permissionFragment = findPermissionFragment(activity);
    }

    public PermissionFragment with(Fragment fragment) {
        return permissionFragment = findPermissionFragment(fragment);
    }

    public void request(PermissionCallback callback) {
        if (callback != null) {
            permissionFragment.requestPermission(callback);
        }
    }

    private PermissionFragment findPermissionFragment(Object object) {
        permissionFragment = findPermissionFragmentByTag(object);
        FragmentManager fragmentManager;
        fragmentManager = getFragmentManager(object);
        if (fragmentManager == null) {
            throw new IllegalStateException("fragment or activity cast exception");
        }
        if (permissionFragment == null) {
            permissionFragment = PermissionFragment.newInstance();
            fragmentManager
                    .beginTransaction()
                    .add(permissionFragment, TAG)
                    .commitAllowingStateLoss();
            fragmentManager.executePendingTransactions();
        }

        return permissionFragment;
    }

    private PermissionFragment findPermissionFragmentByTag(Object object) {
        if (object instanceof FragmentActivity) {
            return (PermissionFragment) (getFragmentManager(object).findFragmentByTag(TAG));
        } else if (object instanceof FragmentManager) {
            return (PermissionFragment) (getFragmentManager(object).findFragmentByTag(TAG));
        }
        return null;
    }

    protected FragmentManager getFragmentManager(Object object) {
        if (object instanceof FragmentActivity) {
            return ((FragmentActivity) object).getSupportFragmentManager();
        } else if (object instanceof FragmentManager) {
            return ((Fragment) object).getChildFragmentManager();
        }
        return null;
    }


}
