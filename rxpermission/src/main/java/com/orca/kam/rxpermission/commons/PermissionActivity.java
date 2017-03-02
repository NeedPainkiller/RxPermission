package com.orca.kam.rxpermission.commons;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;

import static android.content.pm.PackageManager.PERMISSION_DENIED;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS;
import static android.provider.Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS;
import static android.view.WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
import static com.orca.kam.rxpermission.commons.PermissionContent.KEY_PERMISSION_CONTENT;

/**
 * Project RxPermission
 *
 * @author Kang Young Won
 * @create 2016-08-29 - 오후 12:43
 */

public class PermissionActivity extends AppCompatActivity {

    private static final String TAG = "PermissionActivity";

    private static final int REQ_CODE_PERMISSION_REQUEST = 10;
    private static final int REQ_CODE_REQUEST_SETTING = 20;

    private PermissionContent content;
    private List<String> permissions;


    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setWindowUntouchable();
        createPermissionContent();
        checkPermissions();
    }


    private void setWindowUntouchable() {
        Window window = getWindow();
        if (window != null) window.addFlags(FLAG_NOT_TOUCHABLE);
    }


    private void createPermissionContent() {
        content = getExtraBundle().getParcelable(KEY_PERMISSION_CONTENT);
        Preconditions.checkArgument(content != null, "Invalid PermissionContent");
        permissions = content.getPermissions();
    }


    private Bundle getExtraBundle() {
        Bundle bundle = getIntent().getExtras();
        Preconditions.checkArgument((bundle != null && !bundle.isEmpty()), "Invalid Bundle(Extras)");
        return bundle;
    }


    private void checkPermissions() {
        log("checkPermissions");
        List<String> deniedPermission = getDeniedPermissionList();
        if (deniedPermission.isEmpty()) {
            permissionGranted();
        } else {
            showRationaleDialog(deniedPermission);
        }
    }


    @Override public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissionResults, @NonNull int[] grantResults) {
        log("onRequestPermissionsResult");
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
        log("requestPermissions");
        ActivityCompat.requestPermissions(this,
                needPermissions.toArray(new String[needPermissions.size()]),
                REQ_CODE_PERMISSION_REQUEST);
    }


    private void showRationaleDialog(final List<String> needPermissions) {
        log("showRationaleDialog");
        new MaterialDialog.Builder(this)
                .content(content.getExplanationMessage())
                .negativeText(content.getExplanationConfirmButtonText())
                .onNegative((dialog, which) -> requestPermissions(needPermissions))
                .cancelable(false)
                .show();
    }


    private void showPermissionDenyDialog(final List<String> deniedPermissions) {
        log("showPermissionDenyDialog");
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


    private List<String> getDeniedPermissionList() {
        return Lists.newArrayList(Iterables.filter(permissions, permission -> !isGrantedPermission(permission)));
    }


    private boolean isGrantedPermission(String permission) {
        return ContextCompat.checkSelfPermission(this, permission) == PERMISSION_GRANTED;
    }


    private void permissionGranted() {
        log("permissionGranted");
        AndroidPermission.permissionGranted();
        finish();
    }


    private void permissionDenied(List<String> deniedPermissions) {
        log("permissionDenied");
        AndroidPermission.permissionDenied(deniedPermissions);
        finish();
    }


    private void log(String msg) {
        Log.e(TAG, msg);
    }
}
