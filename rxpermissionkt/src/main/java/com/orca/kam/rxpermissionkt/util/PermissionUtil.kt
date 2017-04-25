package com.orca.kam.rxpermissionkt.util

import com.google.common.collect.ImmutableSet
import com.google.common.collect.Iterables
import com.google.common.collect.Lists

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.Manifest.permission.ADD_VOICEMAIL
import android.Manifest.permission.BODY_SENSORS
import android.Manifest.permission.CALL_PHONE
import android.Manifest.permission.CAMERA
import android.Manifest.permission.GET_ACCOUNTS
import android.Manifest.permission.PROCESS_OUTGOING_CALLS
import android.Manifest.permission.READ_CALENDAR
import android.Manifest.permission.READ_CALL_LOG
import android.Manifest.permission.READ_CONTACTS
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_PHONE_STATE
import android.Manifest.permission.READ_SMS
import android.Manifest.permission.RECEIVE_MMS
import android.Manifest.permission.RECEIVE_SMS
import android.Manifest.permission.RECEIVE_WAP_PUSH
import android.Manifest.permission.RECORD_AUDIO
import android.Manifest.permission.SEND_SMS
import android.Manifest.permission.USE_SIP
import android.Manifest.permission.WRITE_CALENDAR
import android.Manifest.permission.WRITE_CALL_LOG
import android.Manifest.permission.WRITE_CONTACTS
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import com.google.common.base.Predicate

/**
 * Project RxPermission

 * @author Kang Young Won
 * *
 * @create 2017-02-21 - 오후 1:20
 */
object PermissionUtil {
    private val PERMISSIONS_DANGER = Lists.newArrayList(
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
            READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE)


    /**
     * @param permission need to check Permission is Dangerous
     * *
     * @return is Dangerous Permission
     */
    fun isDangerousPermission(permission: String): Boolean {
        for (dangerous in PERMISSIONS_DANGER) {
            if (dangerous == permission) {
                return true
            }
        }
        return false
    }


    /**
     * @param permissions need to filtering List
     * *
     * @return Dangerous Permissions
     * * Filtering Normal Permission in list
     */
    fun filteringNormalPermission(permissions: List<String>): List<String> {
        return Lists.newArrayList<String>(Iterables.filter<String>(permissions, Predicate<String> { isDangerousPermission(it) }))
    }


    /**
     * @param permissions need to filtering List
     * *
     * @return Normal Permissions
     * * Filtering Dangerous Permission in list
     */
    fun filteringDangerousPermission(permissions: List<String>): List<String> {
        return Lists.newArrayList(Iterables.filter(permissions) { permission -> !isDangerousPermission(permission) })
    }


    /**
     * @param strings need to filtering List
     * *
     * @return List what removed Duplicate item
     */
    fun removeDuplicatedPermission(strings: List<String>): List<String> {
        return ImmutableSet.copyOf(strings).asList()
    }


    /**
     * @param o need to check object is empty
     * *
     * @return is object Empty
     * * Check the Permissions is Empty
     */
    fun isEmpty(o: Any?): Boolean {
        if (o == null) {
            return true
        } else {
            if (o is String && o.trim { it <= ' ' }.isEmpty()) {
                return true
            }
            if (o is Map<*, *>) {
                return o.isEmpty()
            }
            if (o is List<*>) {
                return o.isEmpty()
            }
            if (o is Set<*>) {
                return o.isEmpty()
            }
        }
        return o is Array<Any> && o.size == 0
    }
}