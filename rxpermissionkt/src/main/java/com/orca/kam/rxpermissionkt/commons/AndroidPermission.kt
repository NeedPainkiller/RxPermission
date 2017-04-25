package com.orca.kam.rxpermissionkt.commons

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.support.annotation.StringRes
import android.support.v4.content.ContextCompat

import com.google.common.base.Preconditions
import com.google.common.collect.Lists
import com.orca.kam.rxpermission.Henson
import com.orca.kam.rxpermission.listener.PermissionListener

import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe

import com.google.common.base.Strings.isNullOrEmpty
import com.orca.kam.rxpermission.util.PermissionUtil.isEmpty
import com.orca.kam.rxpermission.util.PermissionUtil.removeDuplicatedPermission

/**
 * Project RxPermission

 * @author Kang Young Won
 * *
 * @create 2016-08-29 - 오후 12:10
 */

/**
 * This class does all the work for setting up, start Permission Activity and
 * request  permission with message.
 */
class AndroidPermission
/**
 * Constructor. Prepares a new AndroidPermission Setting up

 * @param context(Activity or Application) Get String Resource, Package name, start Permission Activity and check Permissions
 */
(private var context: Context?) {
    private val content = PermissionContent()


    init {
        Preconditions.checkArgument(!isNullContext(context), "Context is Invalid")
    }


    /**
     * Check the Context is null or empty the Package Name

     * @param context(Activity or Application) need to check
     * *
     * @return context has null pointer or cannot get Package Name successful.. will be return true
     */
    private fun isNullContext(context: Context?): Boolean {
        return context == null || isNullOrEmpty(context.packageName)
    }


    /**
     * Setting Message at Prepare Dialog

     * @param message An String defining The Explanation Message
     * *
     * @return Method chaining, AndroidPermission.
     */
    fun setExplanationMessage(message: String): AndroidPermission {
        Preconditions.checkArgument(!isNullOrEmpty(message),
                "The text for ExplanationMessage is empty")
        content.explanationMessage = message
        return this
    }


    /**
     * Setting Message at Prepare Dialog

     * @param messageId An String Resource defining The Explanation Message
     * *
     * @return Method chaining, AndroidPermission.
     */
    fun setExplanationMessage(@StringRes messageId: Int): AndroidPermission {
        content.explanationMessage = getString(messageId,
                "Invalid ID value for ExplanationMessage")
        return this
    }


    /**
     * Setting Confirm Button Text at Prepare Dialog

     * @param buttonText An String defining The Confirm Button Text
     * *
     * @return Method chaining, AndroidPermission.
     */
    fun setExplanationConfirmButtonText(buttonText: String): AndroidPermission {
        Preconditions.checkArgument(!isNullOrEmpty(buttonText),
                "The text for explanationConfirmText is empty")
        content.explanationConfirmButtonText = buttonText
        return this
    }


    /**
     * Setting Confirm Button Text at Prepare Dialog

     * @param buttonTextId An String Resource defining The Confirm Button Text
     * *
     * @return Method chaining, AndroidPermission.
     */
    fun setExplanationConfirmButtonText(@StringRes buttonTextId: Int): AndroidPermission {
        content.explanationConfirmButtonText = getString(buttonTextId,
                "Invalid ID value for explanationConfirmText")
        return this
    }


    /**
     * Setting Message at Denied Dialog

     * @param deniedMessage An String defining The Denied Message
     * *
     * @return Method chaining, AndroidPermission.
     */
    fun setDeniedMessage(deniedMessage: String): AndroidPermission {
        Preconditions.checkArgument(!isNullOrEmpty(deniedMessage),
                "The text for DeniedMessage is empty")
        content.deniedMessage = deniedMessage
        return this
    }


    /**
     * Setting Message at Denied Dialog

     * @param deniedMessageResID An String Resource defining The Denied Message
     * *
     * @return Method chaining, AndroidPermission.
     */
    fun setDeniedMessage(@StringRes deniedMessageResID: Int): AndroidPermission {
        content.deniedMessage = getString(deniedMessageResID,
                "Invalid ID value for DeniedMessage")
        return this
    }


    /**
     * Setting Close Button at Denied Dialog

     * @param deniedCloseButtonText An String defining The Denied Close Button
     * *
     * @return Method chaining, AndroidPermission.
     */
    fun setDeniedCloseButtonText(deniedCloseButtonText: String): AndroidPermission {
        Preconditions.checkArgument(!isNullOrEmpty(deniedCloseButtonText),
                "The text for DeniedCloseButtonText is empty")
        content.deniedCloseButtonText = deniedCloseButtonText
        return this
    }


    /**
     * Setting Close Button at Denied Dialog

     * @param deniedCloseButtonTextResID An String Resource defining The Denied Close Button
     * *
     * @return Method chaining, AndroidPermission.
     */
    fun setDeniedCloseButtonText(@StringRes deniedCloseButtonTextResID: Int): AndroidPermission {
        content.deniedCloseButtonText = getString(deniedCloseButtonTextResID,
                "Invalid ID value for DeniedCloseButtonText")
        return this
    }


    /**
     * Setting Setting Button at Denied Dialog

     * @param showSettingButtonText An String defining The Denied Setting Button
     * *
     * @return Method chaining, AndroidPermission.
     */
    fun setShowSettingButtonText(showSettingButtonText: String): AndroidPermission {
        Preconditions.checkArgument(!isNullOrEmpty(showSettingButtonText),
                "The text for ShowSettingButtonText is empty")
        content.settingButtonText = showSettingButtonText
        return this
    }


    /**
     * Setting Setting Button at Denied Dialog

     * @param showSettingButtonTextResID An String Resource defining The Denied Setting Button
     * *
     * @return Method chaining, AndroidPermission.
     */
    fun setShowSettingButtonText(@StringRes showSettingButtonTextResID: Int): AndroidPermission {
        content.settingButtonText = getString(showSettingButtonTextResID,
                "Invalid ID value for ShowSettingButtonText")
        return this
    }


    /**
     * get String resource from String.xml

     * @param resId            String resource ID
     * *
     * @param exceptionMessage error Message
     * *
     * @return String from String.xml
     */
    private fun getString(resId: Int, exceptionMessage: String): String {
        val stringRes = context!!.getString(resId)
        Preconditions.checkArgument(!isNullOrEmpty(stringRes), exceptionMessage)
        return stringRes
    }


    fun requestPermission(vararg permissions: String): Observable<List<String>> {
        return requestPermission(Lists.newArrayList(*permissions))
    }


    fun requestPermission(permissions: List<String>): Observable<List<String>> {
        return Observable.create({ subscriber ->
            if (isEmpty(permissions)) {
                subscriber.onError(IllegalArgumentException("You must add one or more Permissions unconditionally"))
            } else {
                var isAllGranted = true
                for (permission in permissions) {
                    if (ContextCompat.checkSelfPermission(context!!, permission) != PackageManager.PERMISSION_GRANTED) {
                        isAllGranted = false
                    }
                }
                if (isAllGranted) {
                    subscriber.onComplete()
                } else {
                    listener = object : PermissionListener() {
                        fun permissionGranted() {
                            subscriber.onComplete()
                        }


                        fun permissionDenied(deniedPermissions: List<String>) {
                            subscriber.onNext(deniedPermissions)
                        }
                    }
                    startPermissionActivity(permissions)
                }
            }
        } as ObservableOnSubscribe<List<String>>).doOnDispose(Action { this.terminateLeakyObjects() })
    }


    /**
     * Show Permission Activity

     * @param permissions post with Content to PermissionActivity
     */
    private fun startPermissionActivity(permissions: List<String>) {
        content.permissions = removeDuplicatedPermission(permissions)
        content.packageName = context!!.packageName

        val intent = Henson.with(context)
                .gotoPermissionActivity().content(content).build()
        context!!.startActivity(intent)
    }


    /**
     * terminate static Listener and context
     */
    private fun terminateLeakyObjects() {
        listener = null
        context = null
    }

    companion object {

        private var listener: PermissionListener? = null


        /**
         * Call From Permission Activity...
         * When All Permissions Granted
         */
        internal fun permissionGranted() {
            if (listener != null) {
                listener!!.permissionGranted()
            }
        }


        /**
         * Call From Permission Activity...
         * When Permissions Denied

         * @param deniedPermissions The Denied Permissions from PermissionActivity
         */
        internal fun permissionDenied(deniedPermissions: List<String>) {
            if (listener != null) {
                listener!!.permissionDenied(deniedPermissions)
            }
        }
    }
}