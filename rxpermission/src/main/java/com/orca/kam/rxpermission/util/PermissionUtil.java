package com.orca.kam.rxpermission.util;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
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
    public static List<String> filteringNormalPermission(List<String> permissions) {
        return Lists.newArrayList(Iterables.filter(permissions, PermissionUtil::isDangerousPermission));
    }


    /**
     * @param permissions need to filtering List
     * @return Normal Permissions
     * Filtering Dangerous Permission in list
     */
    public static List<String> filteringDangerousPermission(List<String> permissions) {
        return Lists.newArrayList(Iterables.filter(permissions, new Predicate<String>() {
            @Override public boolean apply(String permission) {
                return !isDangerousPermission(permission);
            }
        }));
    }


    /**
     * @param strings need to filtering List
     * @return List what removed Duplicate item
     */
    public static List<String> removeDuplicatedPermission(List<String> strings) {
        return ImmutableSet.copyOf(strings).asList();
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