package com.orca.kam.rxpermission.view;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.orca.kam.rxpermission.model.DialogMessage;
import com.orca.kam.rxpermission.model.Permission;
import com.orca.kam.rxpermission.model.PermissionResult;
import com.orca.kam.rxpermission.util.PermissionUtil;

import java.util.List;

import hugo.weaving.DebugLog;
import io.reactivex.Flowable;

import static com.orca.kam.rxpermission.PermissionX.permissionSubjects;
import static com.orca.kam.rxpermission.util.PermissionUtil.REQ_CODE_PERMISSION_REQUEST;
import static com.orca.kam.rxpermission.util.PermissionUtil.REQ_CODE_REQUEST_SETTING;

/**
 * @author kam6512
 * @create on 2017-05-05.
 */
public class PermissionFragment extends Fragment {

    private Permission permission;
    private DialogMessage dialogMessage;
    private DialogManager dialogManager;


    public void setPermission(Permission permission) {
        this.permission = permission;
    }


    public void setDialogMessage(DialogMessage dialogMessage) {
        this.dialogMessage = dialogMessage;
    }


    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dialogManager = new DialogManager(getActivity(), dialogMessage);
        dialogManager.showRationaleDialog().subscribe(none -> requestPermissions());
    }


    @TargetApi(Build.VERSION_CODES.M)
    private void requestPermissions() {
        List<String> permissions = permission.getPermissionList();
        requestPermissions(permissions.toArray(new String[permissions.size()]),
                REQ_CODE_PERMISSION_REQUEST);
    }


    @TargetApi(Build.VERSION_CODES.M)
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
        finish();
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

//        finish();
    }


    @DebugLog private void finish() {
        getActivity().getFragmentManager().beginTransaction().remove(this).commit();
    }
}