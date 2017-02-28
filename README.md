# RxPermission
#####RxPermission 은 안드로이드 6.0 (Marshmallow, API 23) 이상 버젼에서의 권한 승인을 더 쉽게 만들어주는 라이브러리 입니다. 



## Setup

### Gradle

```java
dependencies {
    compile 'com.github.kam6512:RxPermission:0.25'
}
```




## How to use
```java
public class MainActivity extends AppCompatActivity {

    private CompositeDisposable disposables = new CompositeDisposable();
    private AndroidPermission androidPermission;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ...
        androidPermission = new AndroidPermission(this);
    }

    @Override protected void onDestroy() {
        super.onDestroy();
        ...
        disposables.clear();
    }

     private void requestBluetoothPermission() {
        Disposable disposable = androidPermission.requestPermission(
        CAMERA,ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION, // Dangerous Permissions
        INTERNET // Not Dangerous Permission
        ).subscribe(deniedPermissions -> { // When Permission (or Permissions) Denied
            	for (String deniedPermission : deniedPermissions) {
                	Log.d("Permission Denial",deniedPermission);
            	}
        	},throwable -> Log.d("ERROR",throwable.getMessage()),
        	() -> Log.d("COMPLETE","Permissions All Granted")); // When Permission(or Permissions All) Granted
        disposables.add(disposable);
    }
}
```

## Example
### All Permission (or Permissions) Granted
![](https://github.com/kam6512/RxPermission/blob/master/art/AndroidPermission%20All%20Granted.gif?raw=true)

### Permission (or Permissions) Denied
![](https://github.com/kam6512/RxPermission/blob/master/art/AndroidPermission%20Denial.gif?raw=true)

### More, Setting
![](https://github.com/kam6512/RxPermission/blob/master/art/AndroidPermission%20Setting.gif?raw=true)

######## Developed by [kam6512](https://kam6512.github.io/)
