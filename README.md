
# 使用
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
                        
## 支持类型
                   1.Fragment（包括v4包）
                   2.Activity
                   3.FragmentActivity
                   3.Service