package com.orca.kam.rxpermission.view;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.f2prateek.dart.Dart;
import com.f2prateek.dart.InjectExtra;
import com.orca.kam.rxpermission.model.DialogMessage;
import com.orca.kam.rxpermission.model.Permission;
import com.orca.kam.rxpermission.model.PermissionResult;
import com.orca.kam.rxpermission.util.PermissionUtil;

import java.util.List;

import hugo.weaving.DebugLog;
import io.reactivex.Flowable;

import static android.os.Build.VERSION_CODES.M;
import static com.orca.kam.rxpermission.PermissionX.permissionSubjects;
import static com.orca.kam.rxpermission.util.PermissionUtil.REQ_CODE_PERMISSION_REQUEST;
import static com.orca.kam.rxpermission.util.PermissionUtil.REQ_CODE_REQUEST_SETTING;
import static com.orca.kam.rxpermission.util.PermissionUtil.TAG;

/**
 * @author Kang Young Won
 * @create 2016-08-29 - 오후 12:43
 */

@TargetApi(M)
public class PermissionActivity extends Activity {

    @InjectExtra Permission permission;
    @InjectExtra DialogMessage dialogMessage;
    private DialogManager dialogManager;


    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Dart.inject(this);
        setDialogManager();
        dialogManager.showRationaleDialog()
                .subscribe(aVoid -> requestPermissions());
        Log.e(TAG, "IS ACTIVITY!!!!!!!!");
    }

    public void setDialogManager() {
        this.dialogManager = new DialogManager(this, dialogMessage);
    }


    @TargetApi(M)
    private void requestPermissions() {
        List<String> permissions = permission.getPermissionList();
        ActivityCompat.requestPermissions(this,
                permissions.toArray(new String[permissions.size()]),
                REQ_CODE_PERMISSION_REQUEST);
    }


    @TargetApi(M)
    @Override public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissionResults, @NonNull int[] grantResults) {
        if (requestCode == REQ_CODE_PERMISSION_REQUEST) {
            super.onRequestPermissionsResult(requestCode, permissionResults, grantResults);
            List<String> deniedPermissions = PermissionUtil.getDeniedList(permissionResults, grantResults);
            if (deniedPermissions.isEmpty()) {
                permissionGranted();
            } else {
                dialogManager.showPermissionDenyDialog()
                        .subscribe(isClickPositive -> {
                            if (isClickPositive) {
                                showAppSettings();
                            } else {
                                permissionDenied(deniedPermissions);
                            }
                        });
            }
        }
    }



    private void showAppSettings() {
        Intent intent = PermissionUtil.getSettingIntent(permission.getPackageName());
        startActivityForResult(intent, REQ_CODE_REQUEST_SETTING);
    }


    @DebugLog void permissionGranted() {
        Flowable.fromIterable(permissionSubjects.entrySet())
                .doOnComplete(this::finish)
                .subscribe(entry -> entry.getValue().onNext(new PermissionResult(entry.getKey(), true)));
    }


    @DebugLog void permissionDenied(List<String> deniedPermissions) {
        Flowable.fromIterable(permissionSubjects.entrySet())
                .doOnComplete(this::finish)
                .subscribe(entry -> {
                    boolean isGranted = true;
                    for (String name : deniedPermissions) {
                        if (entry.getKey().equals(name)) {
                            isGranted = false;
                        }
                    }
                    entry.getValue().onNext(new PermissionResult(entry.getKey(), isGranted));
                });
    }


    @Override public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }
}
