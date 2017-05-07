package com.orca.kam.rxpermission.legacy.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ActionBarOverlayLayout;
import android.util.Log;
import android.view.View;
import android.view.Window;

import com.afollestad.materialdialogs.MaterialDialog;
import com.f2prateek.dart.Dart;
import com.f2prateek.dart.InjectExtra;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.orca.kam.rxpermission.R;
import com.orca.kam.rxpermission.legacy.AndroidPermission_Legacy;
import com.orca.kam.rxpermission.legacy.PermissionContent_Legacy;

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
 * Project RxPermission
 *
 * @author Kang Young Won
 * @create 2016-08-29 - 오후 12:43
 */

public class PermissionActivity_Legacy extends AppCompatActivity {


    @InjectExtra PermissionContent_Legacy content;


    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Dart.inject(this);
        setWindowUntouchable();
        checkPermissions();
        Log.e(TAG, "IS ACTIVITY!!!!!!!!");
    }


    private void setWindowUntouchable() {
        Window window = getWindow();
        if (window != null) {
            window.addFlags(FLAG_NOT_TOUCHABLE);
        }
        ActionBarOverlayLayout actionBarOverlayLayout = (ActionBarOverlayLayout) findViewById(R.id.decor_content_parent);
        actionBarOverlayLayout.setVisibility(View.GONE);
    }


    private void checkPermissions() {
        List<String> deniedPermission = getDeniedPermissionList(content.getPermissions());
        if (deniedPermission.isEmpty()) {
            permissionGranted();
        } else {
            showRationaleDialog(deniedPermission);
        }
    }


    @Override public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissionResults, @NonNull int[] grantResults) {
        if (requestCode == REQ_CODE_PERMISSION_REQUEST) {
            List<String> deniedPermissions = getDeniedPermissionList(permissionResults, grantResults);
            if (deniedPermissions.isEmpty()) {
                permissionGranted();
            } else {
                showPermissionDenyDialog(deniedPermissions);
            }
        }
    }


    @Override public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }


    private void requestPermissions(List<String> needPermissions) {
        ActivityCompat.requestPermissions(this,
                needPermissions.toArray(new String[needPermissions.size()]),
                REQ_CODE_PERMISSION_REQUEST);
    }


    private void showRationaleDialog(final List<String> needPermissions) {
        new MaterialDialog.Builder(this)
                .content(content.getExplanationMessage())
                .negativeText(content.getExplanationConfirmButtonText())
                .onNegative((dialog, which) -> requestPermissions(needPermissions))
                .cancelable(false)
                .show();
    }


    private void showPermissionDenyDialog(final List<String> deniedPermissions) {
        new MaterialDialog.Builder(this)
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
        return ContextCompat.checkSelfPermission(this, permission) == PERMISSION_GRANTED;
    }


    private void permissionGranted() {
        AndroidPermission_Legacy.permissionGranted();
        finish();
    }


    private void permissionDenied(List<String> deniedPermissions) {
        AndroidPermission_Legacy.permissionDenied(deniedPermissions);
        finish();
    }
}
