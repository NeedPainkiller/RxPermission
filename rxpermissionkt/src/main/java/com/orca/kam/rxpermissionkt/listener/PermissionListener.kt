package com.orca.kam.rxpermissionkt.listener

/**
 * Project RxPermission

 * @author Kang Young Won
 * *
 * @create 2016-08-29 - 오전 11:17
 */
interface PermissionListener {

    /**
     * Call From Permission Activity...
     * When All Permissions Granted
     */
    fun permissionGranted()

    /**
     * Call From Permission Activity....
     * When Permissions Denied

     * @param deniedPermissions The Denied Permissions from PermissionActivity
     */
    fun permissionDenied(deniedPermissions: List<String>)
}