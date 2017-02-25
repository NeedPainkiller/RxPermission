package com.orca.kam.rxpermission.util;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static android.Manifest.permission.*;

/**
 * Project RxPermission
 *
 * @author Kang Young Won
 * @create 2017-02-21 - 오후 1:20
 */
public class PermissionUtil {



      /* // CALENDAR
    String PERMISSION_READ_CALENDAR = READ_CALENDAR;
    String PERMISSION_WRITE_CALENDAR = WRITE_CALENDAR;
    //  CAMERA
    String PERMISSION_CAMERA = CAMERA;
    // CONTACTS
    String PERMISSION_READ_CONTACTS = READ_CONTACTS;
    String PERMISSION_WRITE_CONTACTS = WRITE_CONTACTS;
    String PERMISSION_GET_ACCOUNTS = GET_ACCOUNTS;
    //  LOCATION
    String PERMISSION_ACCESS_FINE_LOCATION = ACCESS_FINE_LOCATION;
    String PERMISSION_ACCESS_COARSE_LOCATION = ACCESS_COARSE_LOCATION;
    //           MICROPHONE
    String PERMISSION_RECORD_AUDIO = RECORD_AUDIO;
    //            PHONE
    String PERMISSION_READ_PHONE_STATE = READ_PHONE_STATE;
    String PERMISSION_CALL_PHONE = CALL_PHONE;
    String PERMISSION_READ_CALL_LOG = READ_CALL_LOG;
    String PERMISSION_WRITE_CALL_LOG = WRITE_CALL_LOG;
    String PERMISSION_ADD_VOICEMAIL = ADD_VOICEMAIL;
    String PERMISSION_USE_SIP = USE_SIP;
    String PERMISSION_PROCESS_OUTGOING_CALLS = PROCESS_OUTGOING_CALLS;
    //          SENSORS
    String PERMISSION_BODY_SENSORS = BODY_SENSORS;
    //          SMS
    String PERMISSION_SEND_SMS = SEND_SMS;
    String PERMISSION_RECEIVE_SMS = RECEIVE_SMS;
    String PERMISSION_READ_SMS = READ_SMS;
    String PERMISSION_RECEIVE_WAP_PUSH = RECEIVE_WAP_PUSH;
    String PERMISSION_RECEIVE_MMS = RECEIVE_MMS;
    //STORAGE
    String PERMISSION_READ_EXTERNAL_STORAGE = READ_EXTERNAL_STORAGE;
    String PERMISSION_WRITE_EXTERNAL_STORAGE = WRITE_EXTERNAL_STORAGE;*/

    private static List<String> dangerousPermissions = Lists.newArrayList(
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
        for (String dangerousPermission : dangerousPermissions) {
            if (dangerousPermission.equals(permission)) {
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
    public static List<String> filteringNormalPermission(List<String> permissions) {
        return Lists.newArrayList(Iterables.filter(permissions, PermissionUtil::isDangerousPermission));
    }


    /**
     * @param permissions need to filtering List
     * @return Normal Permissions
     * Filtering Dangerous Permission in list
     */
    public static List<String> filteringDangerousPermission(List<String> permissions) {
        return Lists.newArrayList(Iterables.filter(permissions, permission -> !isDangerousPermission(permission)));
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
}