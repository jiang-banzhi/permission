package com.banzhi.permission;

/**
 * <pre>
 * @author : No.1
 * @time : 2018/5/9.
 * @desciption :
 * @version :
 * </pre>
 */

public interface SettingServer {
    /**
     * 执行
     */
    void execute();

    /**
     * 执行
     *
     * @param requestCode
     */
    void execute(int requestCode);

    /**
     * 取消
     */
    void cancle();
}
