package com.orca.kam.rxpermission.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.ContextCompat;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.ADD_VOICEMAIL;
import static android.Manifest.permission.BODY_SENSORS;
import static android.Manifest.permission.CALL_PHONE;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.GET_ACCOUNTS;
import static android.Manifest.permission.PROCESS_OUTGOING_CALLS;
import static android.Manifest.permission.READ_CALENDAR;
import static android.Manifest.permission.READ_CALL_LOG;
import static android.Manifest.permission.READ_CONTACTS;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.READ_SMS;
import static android.Manifest.permission.RECEIVE_MMS;
import static android.Manifest.permission.RECEIVE_SMS;
import static android.Manifest.permission.RECEIVE_WAP_PUSH;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.SEND_SMS;
import static android.Manifest.permission.USE_SIP;
import static android.Manifest.permission.WRITE_CALENDAR;
import static android.Manifest.permission.WRITE_CALL_LOG;
import static android.Manifest.permission.WRITE_CONTACTS;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.pm.PackageManager.PERMISSION_DENIED;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS;
import static android.provider.Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS;

/**
 * Project RxPermission
 *
 * @author Kang Young Won
 * @create 2017-02-21 - 오후 1:20
 */
public class PermissionUtil {
    public static final String TAG = "RxPermission";
    public static final String TAG_ACTIVITY = "RxPermissionActivity";
    public static final String TAG_FRAGMENT = "RxPermissionFragment";

    public static final int REQ_CODE_PERMISSION_REQUEST = 10;
    public static final int REQ_CODE_REQUEST_SETTING = 20;

    private static final List<String> PERMISSIONS_DANGER = Lists.newArrayList(
            READ_CALENDAR, WRITE_CALENDAR,
            CAMERA,
            READ_CONTACTS, WRITE_CONTACTS, GET_ACCOUNTS,
            ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION,
            RECORD_AUDIO,
            READ_PHONE_STATE, CALL_PHONE,
            READ_CALL_LOG, WRITE_CALL_LOG,
            ADD_VOICEMAIL, USE_SIP,
            PROCESS_OUTGOING_CALLS,
            BODY_SENSORS,
            SEND_SMS, RECEIVE_SMS, READ_SMS,
            RECEIVE_WAP_PUSH, RECEIVE_MMS,
            READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE);


    /**
     * @param permission need to check Permission is Dangerous
     * @return is Dangerous Permission
     */
    public static boolean isDangerousPermission(String permission) {
        for (String dangerous : PERMISSIONS_DANGER) {
            if (dangerous.equals(permission)) {
                return true;
            }
        }
        return false;
    }


    /**
     * @param permissions need to filtering List
     * @return Dangerous Permissions
     * Filtering Normal Permission in list
     */
    public static List<String> removeSafeItem(List<String> permissions) {
        return Lists.newArrayList(Iterables.filter(permissions, PermissionUtil::isDangerousPermission));
    }


    /**
     * @param permissions need to filtering List
     * @return Normal Permissions
     * Filtering Dangerous Permission in list
     */
    public static List<String> removeDangerousItem(List<String> permissions) {
        return Lists.newArrayList(Iterables.filter(permissions, permission -> !isDangerousPermission(permission)));
    }


    /**
     * @param strings need to filtering List
     * @return List what removed Duplicate item
     */
    public static List<String> deduplicateList(List<String> strings) {
        return ImmutableSet.copyOf(strings).asList();
    }


    public static boolean isAllGranted(Context context, List<String> permissions) {
        boolean isAllGranted = true;
        for (String permission : permissions) {
            if (!isGranted(context, permission)) isAllGranted = false;
        }
        return isAllGranted;
    }


    public static boolean isGranted(Context context, String permission) {
        return ContextCompat.checkSelfPermission(context, permission) == PERMISSION_GRANTED;
    }


    public static List<String> getDeniedList(String[] permissions, int[] grantResults) {
        List<String> deniedPermissions = new ArrayList<>();
        for (int i = 0; i < permissions.length; i++) {
            if (grantResults[i] == PERMISSION_DENIED) {
                deniedPermissions.add(permissions[i]);
            }
        }
        return deniedPermissions;
    }


    public static List<String> getDeniedList(Context context, List<String> permissions) {
        return Lists.newArrayList(Iterables.filter(permissions, permission -> !isGranted(context, permission)));
    }


    public static Intent getSettingIntent(String packageName) {
        Intent intent = new Intent();
        if (Strings.isNullOrEmpty(packageName)) {
            intent.setAction(ACTION_MANAGE_APPLICATIONS_SETTINGS);
        } else {
            intent.setAction(ACTION_APPLICATION_DETAILS_SETTINGS)
                    .setData(Uri.parse("package:" + packageName));
        }
        return intent;
    }


    /**
     * @param o need to check object is empty
     * @return is object Empty
     * Check the Permissions is Empty
     */
    public static boolean isEmpty(Object o) {
        if (o == null) {
            return true;
        } else {
            if ((o instanceof String) && (((String) o).trim().length() == 0)) {
                return true;
            }
            if (o instanceof Map) {
                return ((Map<?, ?>) o).isEmpty();
            }
            if (o instanceof List) {
                return ((List<?>) o).isEmpty();
            }
            if (o instanceof Set) {
                return ((Set<?>) o).isEmpty();
            }
        }
        return o instanceof Object[] && (((Object[]) o).length == 0);
    }

//    void checkPermissions() {
//        List<String> deniedPermission = PermissionUtil.getDeniedList(context, permission.getPermissionList());
//        if (deniedPermission.isEmpty()) {
//            permissionGranted();
//        } else {
//            dialogManager.showRationaleDialog()
//                    .subscribe(aVoid -> requestPermissions(deniedPermission));
//        }
//    }
}