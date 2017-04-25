package com.orca.kam.rxpermissionkt.commons

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.Window

import com.afollestad.materialdialogs.MaterialDialog
import com.f2prateek.dart.Dart
import com.f2prateek.dart.InjectExtra
import com.google.common.base.Strings
import com.google.common.collect.Iterables
import com.google.common.collect.Lists

import java.util.ArrayList

import android.content.pm.PackageManager.PERMISSION_DENIED
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import android.provider.Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS
import android.view.WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE

/**
 * Project RxPermission

 * @author Kang Young Won
 * *
 * @create 2016-08-29 - 오후 12:43
 */

class PermissionActivity : AppCompatActivity() {

    @InjectExtra internal var content: PermissionContent? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Dart.inject(this)
        setWindowUntouchable()
        checkPermissions()
    }


    private fun setWindowUntouchable() {
        val window = window
        window?.addFlags(FLAG_NOT_TOUCHABLE)
    }


    private fun checkPermissions() {
        val deniedPermission = getDeniedPermissionList(content!!.permissions)
        if (deniedPermission.isEmpty()) {
            permissionGranted()
        } else {
            showRationaleDialog(deniedPermission)
        }
    }


    override fun onRequestPermissionsResult(
            requestCode: Int, permissionResults: Array<String>, grantResults: IntArray) {
        if (requestCode == REQ_CODE_PERMISSION_REQUEST) {
            val deniedPermissions = getDeniedPermissionList(permissionResults, grantResults)
            if (deniedPermissions.isEmpty()) {
                permissionGranted()
            } else {
                showPermissionDenyDialog(deniedPermissions)
            }
        }
    }


    override fun finish() {
        super.finish()
        overridePendingTransition(0, 0)
    }


    private fun requestPermissions(needPermissions: List<String>) {
        ActivityCompat.requestPermissions(this,
                needPermissions.toTypedArray(),
                REQ_CODE_PERMISSION_REQUEST)
    }


    private fun showRationaleDialog(needPermissions: List<String>) {
        MaterialDialog.Builder(this)
                .content(content!!.getExplanationMessage())
                .negativeText(content!!.getExplanationConfirmButtonText())
                .onNegative { dialog, which -> requestPermissions(needPermissions) }
                .cancelable(false)
                .show()
    }


    private fun showPermissionDenyDialog(deniedPermissions: List<String>) {
        MaterialDialog.Builder(this)
                .content(content!!.getDeniedMessage())
                .positiveText(content!!.getSettingButtonText())
                .negativeText(content!!.getDeniedCloseButtonText())
                .onPositive { dialog, which -> showAppSettings() }
                .onNegative { dialog, which -> permissionDenied(deniedPermissions) }
                .cancelable(false)
                .show()
    }


    private fun showAppSettings() {
        val intent = Intent()
        val packageName = content!!.packageName
        if (!Strings.isNullOrEmpty(packageName)) {
            intent.setAction(ACTION_APPLICATION_DETAILS_SETTINGS).data = Uri.parse("package:" + content!!.packageName!!)
        } else {
            intent.action = ACTION_MANAGE_APPLICATIONS_SETTINGS
        }
        startActivityForResult(intent, REQ_CODE_REQUEST_SETTING)
    }


    private fun getDeniedPermissionList(permissions: Array<String>, grantResults: IntArray): List<String> {
        val deniedPermissions = ArrayList<String>()
        for (i in permissions.indices) {
            if (grantResults[i] == PERMISSION_DENIED) {
                deniedPermissions.add(permissions[i])
            }
        }
        return deniedPermissions
    }


    private fun getDeniedPermissionList(permissions: List<String>): List<String> {
        return Lists.newArrayList(Iterables.filter(permissions) { permission -> !isGrantedPermission(permission) })
    }


    private fun isGrantedPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(this, permission) == PERMISSION_GRANTED
    }


    private fun permissionGranted() {
        AndroidPermission.permissionGranted()
        finish()
    }


    private fun permissionDenied(deniedPermissions: List<String>) {
        AndroidPermission.permissionDenied(deniedPermissions)
        finish()
    }

    companion object {


        private val REQ_CODE_PERMISSION_REQUEST = 10
        private val REQ_CODE_REQUEST_SETTING = 20
    }
}
