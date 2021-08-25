# AndPermission

  使用透明activity请求权限,详细信息请查看源码。
  
### 使用
      
     allprojects {
		    repositories {
			    ...
			    maven { url 'https://jitpack.io' }
		    }
	    }

      dependencies {
       
            implementation 'com.github.jiang-banzhi:permission:2.0.7'
        
      }
       AndPermisstion.getInstance()
                     .newBuilder()
                     .permissions(Manifest.permission.CAMERA, Manifest.permission.CALL_PHONE)
                     .request(new PermissionCallback() {
                          @Override
                          public void onGranted() {
                              // 授权成功
                  
                          }
   
                          @Override
                          public void onDenied(List<String> list) {
                              // 返回未授权权限
                          }
                     });            
                        
                   
                   
### 方法说明
              
       permissions() 需要请求的权限
       showTip() 默认提示
       customTip() 自定义提示
       settingServer() 自定义转到系统设置 需事先SettingServer接口 默认使用内置方法

### 更新内容
    1.增加安装权限、悬浮窗权限申请处理
    2.修改使用方法
    3.修改权限验证方法
    4.解耦Activity和Fragment、不再需要Context
                   
