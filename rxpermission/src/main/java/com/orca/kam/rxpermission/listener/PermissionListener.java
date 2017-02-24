package com.orca.kam.rxpermission.listener;

import java.util.ArrayList;

/**
 * Project RxPermission
 *
 * @author Kang Young Won
 * @create 2016-08-29 - 오전 11:17
 */
public interface PermissionListener {

    /**
     * Call From Permission Activity...
     * When All Permissions Granted
     */
    void permissionGranted();

    /**
     * Call From Permission Activity....
     * When Permissions Denied
     *
     * @param deniedPermissions The Denied Permissions from PermissionActivity
     */
    void permissionDenied(ArrayList<String> deniedPermissions);
}