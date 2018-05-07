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
    void onGranted();

    void onDenied(List<String> list);
}
