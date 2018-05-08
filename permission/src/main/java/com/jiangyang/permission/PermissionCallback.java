package com.jiangyang.permission;

import java.util.List;

/**
 * <pre>
 * @author : No.1
 * @time : 2018/5/2.
 * @desciption :
 * @version :
 * </pre>
 */

public interface PermissionCallback {
    /**
     * 获取权限成功回调
     */
    void onGranted();

    /**
     * 未授权权限
     *
     * @param list 未授权权限集合
     */
    void onDenied(List<String> list);
}
