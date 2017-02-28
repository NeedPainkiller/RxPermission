package com.orca.kam.rxpermission.commons;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

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

    private static final int REQ_CODE_PERMISSION_REQUEST = 10;
    private static final int REQ_CODE_REQUEST_SETTING = 20;

    private PermissionContent content;


    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setWindowUntouchable();
        initPermissionContent();
        checkPermissions(false);
    }


    private void setWindowUntouchable() {
        Window window = getWindow();
        if (window != null) window.addFlags(FLAG_NOT_TOUCHABLE);
    }


    private void initPermissionContent() {
        Bundle bundle = getIntent().getExtras();
        Preconditions.checkArgument(!bundle.isEmpty(), "Invalid Bundle(Extras)");
        content = bundle.getParcelable(KEY_PERMISSION_CONTENT);
        Preconditions.checkArgument(content != null, "Invalid PermissionContent");
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_CODE_REQUEST_SETTING) checkPermissions(true);
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        ArrayList<String> deniedPermissions = new ArrayList<>();

        for (int i = 0; i < permissions.length; i++) {
            if (grantResults[i] == PERMISSION_DENIED) {
                deniedPermissions.add(permissions[i]);
            }
        }

        if (deniedPermissions.isEmpty()) {
            permissionGranted();
        } else {
            showPermissionDenyDialog(deniedPermissions);
        }
    }


    private void checkPermissions(boolean fromOnActivityResult) {
        ArrayList<String> needPermissions = new ArrayList<>();
        boolean showRationale = false;
        List<String> permissions = content.getPermissions();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PERMISSION_GRANTED) {
                needPermissions.add(permission);
                showRationale = true;
            }
        }

        if (needPermissions.isEmpty()) {
            permissionGranted();
        } else if (fromOnActivityResult) {
            permissionDenied(needPermissions);
        } else if (showRationale) {
            showRationaleDialog(needPermissions);
        } else {
            requestPermissions(needPermissions);
        }
    }


    private void permissionGranted() {
        AndroidPermission.permissionGranted();
        finish();
    }


    private void permissionDenied(ArrayList<String> deniedPermissions) {
        AndroidPermission.permissionDenied(deniedPermissions);
        finish();
    }


    @Override public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }


    private void requestPermissions(ArrayList<String> needPermissions) {
        ActivityCompat.requestPermissions(this,
                needPermissions.toArray(new String[needPermissions.size()]),
                REQ_CODE_PERMISSION_REQUEST);
    }


    private void showRationaleDialog(final ArrayList<String> needPermissions) {
        new MaterialDialog.Builder(this)
                .content(content.getExplanationMessage())
                .negativeText(content.getExplanationConfirmButtonText())
                .onNegative((dialog, which) -> requestPermissions(needPermissions))
                .cancelable(false)
                .show();
    }


    private void showPermissionDenyDialog(final ArrayList<String> deniedPermissions) {
        new MaterialDialog.Builder(this)
                .content(content.getDeniedMessage())
                .positiveText(content.getSettingButtonText())
                .negativeText(content.getDeniedCloseButtonText())
                .onPositive(showAppSettings())
                .onNegative((dialog, which) -> permissionDenied(deniedPermissions))
                .cancelable(false)
                .show();
    }


    private MaterialDialog.SingleButtonCallback showAppSettings() {
        return (dialog, which) -> {
            Intent intent = new Intent();
            String packageName = content.getPackageName();
            if (!Strings.isNullOrEmpty(packageName)) {
                intent.setAction(ACTION_APPLICATION_DETAILS_SETTINGS)
                        .setData(Uri.parse("package:" + content.getPackageName()));
            } else {
                intent.setAction(ACTION_MANAGE_APPLICATIONS_SETTINGS);
            }
            startActivityForResult(intent, REQ_CODE_REQUEST_SETTING);
        };
    }
}
