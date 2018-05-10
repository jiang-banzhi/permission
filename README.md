# AndPermission

  使用透明activity请求权限,详细信息请查看源码。
  
### 使用
      new AndPermisstion.Builder()
                        .with(MainActivity.this)
                        .permissions(Manifest.permission.CAMERA)//需要请求的权限
                        .setCallback(new PermissionCallback() {
                        @Override 
                        public void onGranted() {
                        //TODO  权限授权成功
                        
                        }

                        @Override
                        public void onDenied(List<String> list) {
                           //TODO 未授权权限
                        }
                    })
                        .showTip()//拒绝时 显示提示
                        .create()
                        .request();
                        
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
### 使用     
       root build.gradle下添加:
                
                	allprojects {
                		repositories {
                			...
                			maven { url 'https://jitpack.io' }
                		}
                	}
                
                	dependencies {
                	        implementation 'com.github.jiang-banzhi:permission:v1.0'
                	}
                
#### Thanks 
   严振杰[AndPermission](https://github.com/yanzhenjie/AndPermission)
                   