package com.orca.kam.rxpermission;

import com.google.common.collect.Lists;
import com.orca.kam.rxpermission.util.PermissionUtil;

import org.junit.Test;

import java.util.List;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.ADD_VOICEMAIL;
import static android.Manifest.permission.BODY_SENSORS;
import static android.Manifest.permission.CALL_PHONE;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.GET_ACCOUNTS;
import static android.Manifest.permission.INTERNET;
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

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    private final List<String> examplePermissions = Lists.newArrayList(
            ACCESS_COARSE_LOCATION,
            ACCESS_FINE_LOCATION,
            BODY_SENSORS,
            CALL_PHONE,
            CAMERA,
            INTERNET,
            PROCESS_OUTGOING_CALLS,
            READ_CALENDAR,
            READ_CALL_LOG,
            READ_CONTACTS,
            READ_EXTERNAL_STORAGE,
            READ_PHONE_STATE,
            READ_SMS,
            RECEIVE_MMS,
            RECEIVE_SMS,
            RECEIVE_WAP_PUSH,
            RECORD_AUDIO,
            SEND_SMS,
            USE_SIP,
            WRITE_CALENDAR,
            WRITE_CALL_LOG,
            WRITE_CONTACTS,
            WRITE_EXTERNAL_STORAGE);
    private final List<String> exampleDuplicatePermissions = Lists.newArrayList(
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
            READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE,
            INTERNET,
            PROCESS_OUTGOING_CALLS,
            READ_CALENDAR,
            READ_CALL_LOG,
            READ_CONTACTS,
            READ_EXTERNAL_STORAGE,
            READ_PHONE_STATE,
            READ_SMS,
            RECEIVE_MMS,
            RECEIVE_SMS,
            RECEIVE_WAP_PUSH,
            RECORD_AUDIO,
            SEND_SMS,
            USE_SIP,
            WRITE_CALENDAR,
            WRITE_CALL_LOG,
            WRITE_CONTACTS,
            WRITE_EXTERNAL_STORAGE);


    @Test
    public void filter() {

        System.out.println("TEST 1) : examplePermissions");
        showLog(examplePermissions);
        System.out.println("TEST 2) : filteringDangerousPermission");
        List<String> normalPermissions = PermissionUtil.filteringDangerousPermission(examplePermissions);
        showLog(normalPermissions);
        System.out.println("TEST 3) : filteringNormalPermission");
        List<String> dangerousPermissions = PermissionUtil.filteringNormalPermission(examplePermissions);
        showLog(dangerousPermissions);
        System.out.println("TEST 4) : deduplication");
        showLog(exampleDuplicatePermissions);
        System.out.println("TEST 5) : deduplication");
        List<String> removedDuplicateItemPermissions = PermissionUtil.deduplication(exampleDuplicatePermissions);
        showLog(removedDuplicateItemPermissions);
    }


    private void showLog(List<String> list) {
        System.out.println("size : " + list.size());
        for (String s : list) {
            System.out.println(s);
        }
        System.out.println("========================");
    }
}