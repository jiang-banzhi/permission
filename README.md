# AndPermission

  使用透明activity请求权限,详细信息请查看源码。
  
### 使用

      dependencies {
       
            compile 'com.banzhi.permission:permission:1.0.1'
        
      }
      AndPermisstion permission = new AndPermisstion.Builder(MainActivity.this)
                        .permissions(Manifest.permission.CAMERA)//需要请求的权限
                        .showTip()//拒绝时 显示提示
                        .request();
      andPermisstion.requset(new PermissionCallback() {
                @Override
                public void onGranted() {
                //TODO 授权成功
               
                }
     
                @Override
                public void onDenied(List<String> list) {
                 //TODO 授权失败 返回未授权权限
                }
      });               
                        
### 支持类型
                   1.Fragment（包括v4包）
                   2.Activity
                   3.FragmentActivity
                   3.Service
                   
                   
### 方法说明
                with() 需要请求权限的组件
                permissions() 需要请求的权限
                setCallback() 回调
                showTip() 默认提示
                customTip() 自定义提示
                settingServer() 自定义转到系统设置 需事先SettingServer接口 默认使用内置方法

### 更新内容
    1.增加安装权限、悬浮窗权限申请处理
    2.修改使用方法
                   