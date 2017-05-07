package com.orca.kam.rxpermission.legacy.fragment;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Window;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.orca.kam.rxpermission.legacy.PermissionContent_Legacy;
import com.orca.kam.rxpermission.legacy.activity.PermissionListener_Legacy;

import java.util.ArrayList;
import java.util.List;

import static android.content.pm.PackageManager.PERMISSION_DENIED;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS;
import static android.provider.Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS;
import static android.view.WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
import static com.orca.kam.rxpermission.util.PermissionUtil.REQ_CODE_PERMISSION_REQUEST;
import static com.orca.kam.rxpermission.util.PermissionUtil.REQ_CODE_REQUEST_SETTING;
import static com.orca.kam.rxpermission.util.PermissionUtil.TAG;

/**
 * @author kam6512
 * @Create on 2017-05-05.
 */
public class PermissionFragment_Legacy extends Fragment {

    private Context context;
    private PermissionListener_Legacy listener;
    private PermissionContent_Legacy content;


    public void setListener(PermissionListener_Legacy listener) {
        if (listener != null) {
            this.listener = listener;
        }
    }


    public void setContent(PermissionContent_Legacy content) {
        if (content != null) {
            this.content = content;
        }
    }


    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        setWindowUntouchable();
        checkPermissions();
        Log.e(TAG, "IS FRAGMENT!!!!!!!!");
    }


    void setWindowUntouchable() {
        Window window = getActivity().getWindow();
        if (window != null) window.addFlags(FLAG_NOT_TOUCHABLE);
    }


    void checkPermissions() {
        List<String> deniedPermission = getDeniedPermissionList(content.getPermissions());
        if (deniedPermission.isEmpty()) {
            permissionGranted();
        } else {
            showRationaleDialog(deniedPermission);
        }
    }


    private void finish() {
        getActivity().getFragmentManager().beginTransaction().remove(this).commit();
    }


    @TargetApi(Build.VERSION_CODES.M)
    @Override public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissionResults, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissionResults, grantResults);
        if (requestCode != REQ_CODE_PERMISSION_REQUEST) return;

        List<String> deniedPermissions = getDeniedPermissionList(permissionResults, grantResults);
        if (deniedPermissions.isEmpty()) {
            permissionGranted();
        } else {
            showPermissionDenyDialog(deniedPermissions);
        }
    }


    @TargetApi(Build.VERSION_CODES.M)
    private void requestPermissions(@NonNull List<String> needPermissions) {
        requestPermissions(needPermissions.toArray(new String[needPermissions.size()]),
                REQ_CODE_PERMISSION_REQUEST);
    }


    private void showRationaleDialog(final List<String> needPermissions) {
        new MaterialDialog.Builder(context)
                .content(content.getExplanationMessage())
                .negativeText(content.getExplanationConfirmButtonText())
                .onNegative((dialog, which) -> requestPermissions(needPermissions))
                .cancelable(false)
                .show();
    }


    private void showPermissionDenyDialog(final List<String> deniedPermissions) {
        new MaterialDialog.Builder(context)
                .content(content.getDeniedMessage())
                .positiveText(content.getSettingButtonText())
                .negativeText(content.getDeniedCloseButtonText())
                .onPositive((dialog, which) -> showAppSettings())
                .onNegative((dialog, which) -> permissionDenied(deniedPermissions))
                .cancelable(false)
                .show();
    }


    private void showAppSettings() {
        Intent intent = new Intent();
        String packageName = content.getPackageName();
        if (!Strings.isNullOrEmpty(packageName)) {
            intent.setAction(ACTION_APPLICATION_DETAILS_SETTINGS)
                    .setData(Uri.parse("package:" + content.getPackageName()));
        } else {
            intent.setAction(ACTION_MANAGE_APPLICATIONS_SETTINGS);
        }
        startActivityForResult(intent, REQ_CODE_REQUEST_SETTING);
    }


    private List<String> getDeniedPermissionList(String[] permissions, int[] grantResults) {
        List<String> deniedPermissions = new ArrayList<>();
        for (int i = 0; i < permissions.length; i++) {
            if (grantResults[i] == PERMISSION_DENIED) {
                deniedPermissions.add(permissions[i]);
            }
        }
        return deniedPermissions;
    }


    private List<String> getDeniedPermissionList(List<String> permissions) {
        return Lists.newArrayList(Iterables.filter(permissions, permission -> !isGrantedPermission(permission)));
    }


    private boolean isGrantedPermission(String permission) {
        return ContextCompat.checkSelfPermission(context, permission) == PERMISSION_GRANTED;
    }


    void permissionGranted() {
        listener.permissionGranted();
        finish();
    }


    void permissionDenied(List<String> deniedPermissions) {
        listener.permissionDenied(deniedPermissions);
        finish();
    }
}
