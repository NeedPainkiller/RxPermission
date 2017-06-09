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

import static android.Manifest.permission.*;
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


    private static final List<String> PERMISSIONS = Lists.newArrayList(
            ACCESS_CHECKIN_PROPERTIES,
            ACCESS_COARSE_LOCATION,
            ACCESS_FINE_LOCATION,
            ACCESS_LOCATION_EXTRA_COMMANDS,
            ACCESS_NETWORK_STATE,
            ACCESS_NOTIFICATION_POLICY,
            ACCESS_WIFI_STATE,
            ACCOUNT_MANAGER,
            ADD_VOICEMAIL,
            BATTERY_STATS,
            BIND_ACCESSIBILITY_SERVICE,
            BIND_APPWIDGET,
            BIND_CARRIER_MESSAGING_SERVICE,
            BIND_CARRIER_SERVICES,
            BIND_CHOOSER_TARGET_SERVICE,
            BIND_CONDITION_PROVIDER_SERVICE,
            BIND_DEVICE_ADMIN,
            BIND_DREAM_SERVICE,
            BIND_INCALL_SERVICE,
            BIND_INPUT_METHOD,
            BIND_MIDI_DEVICE_SERVICE,
            BIND_NFC_SERVICE,
            BIND_NOTIFICATION_LISTENER_SERVICE,
            BIND_PRINT_SERVICE,
            BIND_QUICK_SETTINGS_TILE,
            BIND_REMOTEVIEWS,
            BIND_SCREENING_SERVICE,
            BIND_TELECOM_CONNECTION_SERVICE,
            BIND_TEXT_SERVICE,
            BIND_TV_INPUT,
            BIND_VOICE_INTERACTION,
            BIND_VPN_SERVICE,
            BIND_VR_LISTENER_SERVICE,
            BIND_WALLPAPER,
            BLUETOOTH,
            BLUETOOTH_ADMIN,
            BLUETOOTH_PRIVILEGED,
            BODY_SENSORS,
            BROADCAST_PACKAGE_REMOVED,
            BROADCAST_SMS,
            BROADCAST_STICKY,
            BROADCAST_WAP_PUSH,
            CALL_PHONE,
            CALL_PRIVILEGED,
            CAMERA,
            CAPTURE_AUDIO_OUTPUT,
            CAPTURE_SECURE_VIDEO_OUTPUT,
            CAPTURE_VIDEO_OUTPUT,
            CHANGE_COMPONENT_ENABLED_STATE,
            CHANGE_CONFIGURATION,
            CHANGE_NETWORK_STATE,
            CHANGE_WIFI_MULTICAST_STATE,
            CHANGE_WIFI_STATE,
            CLEAR_APP_CACHE,
            CONTROL_LOCATION_UPDATES,
            DELETE_CACHE_FILES,
            DELETE_PACKAGES,
            DIAGNOSTIC,
            DISABLE_KEYGUARD,
            DUMP,
            EXPAND_STATUS_BAR,
            FACTORY_TEST,
            GET_ACCOUNTS,
            GET_ACCOUNTS_PRIVILEGED,
            GET_PACKAGE_SIZE,
            GET_TASKS,
            GLOBAL_SEARCH,
            INSTALL_LOCATION_PROVIDER,
            INSTALL_PACKAGES,
            INSTALL_SHORTCUT,
            INTERNET,
            KILL_BACKGROUND_PROCESSES,
            LOCATION_HARDWARE,
            MANAGE_DOCUMENTS,
            MASTER_CLEAR,
            MEDIA_CONTENT_CONTROL,
            MODIFY_AUDIO_SETTINGS,
            MODIFY_PHONE_STATE,
            MOUNT_FORMAT_FILESYSTEMS,
            MOUNT_UNMOUNT_FILESYSTEMS,
            NFC,
            PACKAGE_USAGE_STATS,
            PERSISTENT_ACTIVITY,
            PROCESS_OUTGOING_CALLS,
            READ_CALENDAR,
            READ_CALL_LOG,
            READ_CONTACTS,
            READ_EXTERNAL_STORAGE,
            READ_FRAME_BUFFER,
            READ_INPUT_STATE,
            READ_LOGS,
            READ_PHONE_STATE,
            READ_SMS,
            READ_SYNC_SETTINGS,
            READ_SYNC_STATS,
            READ_VOICEMAIL,
            REBOOT,
            RECEIVE_BOOT_COMPLETED,
            RECEIVE_MMS,
            RECEIVE_SMS,
            RECEIVE_WAP_PUSH,
            RECORD_AUDIO,
            REORDER_TASKS,
            REQUEST_IGNORE_BATTERY_OPTIMIZATIONS,
            REQUEST_INSTALL_PACKAGES,
            RESTART_PACKAGES,
            SEND_RESPOND_VIA_MESSAGE,
            SEND_SMS,
            SET_ALARM,
            SET_ALWAYS_FINISH,
            SET_ANIMATION_SCALE,
            SET_DEBUG_APP,
            SET_PREFERRED_APPLICATIONS,
            SET_PROCESS_LIMIT,
            SET_TIME,
            SET_TIME_ZONE,
            SET_WALLPAPER,
            SET_WALLPAPER_HINTS,
            SIGNAL_PERSISTENT_PROCESSES,
            STATUS_BAR,
            SYSTEM_ALERT_WINDOW,
            TRANSMIT_IR,
            UNINSTALL_SHORTCUT,
            UPDATE_DEVICE_STATS,
            USE_FINGERPRINT,
            USE_SIP,
            VIBRATE,
            WAKE_LOCK,
            WRITE_APN_SETTINGS,
            WRITE_CALENDAR,
            WRITE_CALL_LOG,
            WRITE_CONTACTS,
            WRITE_EXTERNAL_STORAGE,
            WRITE_GSERVICES,
            WRITE_SECURE_SETTINGS,
            WRITE_SETTINGS,
            WRITE_SYNC_SETTINGS,
            WRITE_VOICEMAIL);


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