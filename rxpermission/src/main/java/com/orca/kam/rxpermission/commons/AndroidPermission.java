package com.orca.kam.rxpermission.commons;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.orca.kam.rxpermission.listener.PermissionListener;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;

import static com.orca.kam.rxpermission.commons.PermissionContent.KEY_PERMISSION_CONTENT;
import static com.orca.kam.rxpermission.util.PermissionUtil.isEmpty;
import static com.orca.kam.rxpermission.util.PermissionUtil.removeDuplicateStringInList;

/**
 * Project RxPermission
 *
 * @author Kang Young Won
 * @create 2016-08-29 - 오후 12:10
 */

/**
 * This class does all the work for setting up, start Permission Activity and
 * request  permission with message.
 */
public class AndroidPermission {

    private final Context context;
    private final PermissionContent content;
    private static PermissionListener listener;


    /**
     * Constructor. Prepares a new AndroidPermission Setting up
     *
     * @param context(Activity or Application) Get String Resource, Package name, start Permission Activity and check Permissions
     */
    public AndroidPermission(Context context) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(context.getPackageName()), "Context is Invalid");
        this.context = context;
        content = new PermissionContent();
    }


    /**
     * Setting Message at Prepare Dialog
     *
     * @param message An String defining The Explanation Message
     * @return Method chaining, AndroidPermission.
     */
    public AndroidPermission setExplanationMessage(String message) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(message),
                "The text for ExplanationMessage is empty");
        content.setExplanationMessage(message);
        return this;
    }


    /**
     * Setting Message at Prepare Dialog
     *
     * @param messageId An String Resource defining The Explanation Message
     * @return Method chaining, AndroidPermission.
     */
    public AndroidPermission setExplanationMessage(@StringRes int messageId) {
        content.setExplanationMessage(getString(messageId,
                "Invalid ID value for ExplanationMessage"));
        return this;
    }


    /**
     * Setting Confirm Button Text at Prepare Dialog
     *
     * @param buttonText An String defining The Confirm Button Text
     * @return Method chaining, AndroidPermission.
     */
    public AndroidPermission setExplanationConfirmButtonText(String buttonText) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(buttonText),
                "The text for explanationConfirmText is empty");
        content.setExplanationConfirmButtonText(buttonText);
        return this;
    }


    /**
     * Setting Confirm Button Text at Prepare Dialog
     *
     * @param buttonTextId An String Resource defining The Confirm Button Text
     * @return Method chaining, AndroidPermission.
     */
    public AndroidPermission setExplanationConfirmButtonText(@StringRes int buttonTextId) {
        content.setExplanationConfirmButtonText(getString(buttonTextId,
                "Invalid ID value for explanationConfirmText"));
        return this;
    }


    /**
     * Setting Message at Denied Dialog
     *
     * @param deniedMessage An String defining The Denied Message
     * @return Method chaining, AndroidPermission.
     */
    public AndroidPermission setDeniedMessage(String deniedMessage) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(deniedMessage),
                "The text for DeniedMessage is empty");
        content.setDeniedMessage(deniedMessage);
        return this;
    }


    /**
     * Setting Message at Denied Dialog
     *
     * @param deniedMessageResID An String Resource defining The Denied Message
     * @return Method chaining, AndroidPermission.
     */
    public AndroidPermission setDeniedMessage(@StringRes int deniedMessageResID) {
        content.setDeniedMessage(getString(deniedMessageResID,
                "Invalid ID value for DeniedMessage"));
        return this;
    }


    /**
     * Setting Close Button at Denied Dialog
     *
     * @param deniedCloseButtonText An String defining The Denied Close Button
     * @return Method chaining, AndroidPermission.
     */
    public AndroidPermission setDeniedCloseButtonText(String deniedCloseButtonText) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(deniedCloseButtonText),
                "The text for DeniedCloseButtonText is empty");
        content.setDeniedCloseButtonText(deniedCloseButtonText);
        return this;
    }


    /**
     * Setting Close Button at Denied Dialog
     *
     * @param deniedCloseButtonTextResID An String Resource defining The Denied Close Button
     * @return Method chaining, AndroidPermission.
     */
    public AndroidPermission setDeniedCloseButtonText(@StringRes int deniedCloseButtonTextResID) {
        content.setDeniedCloseButtonText(getString(deniedCloseButtonTextResID,
                "Invalid ID value for DeniedCloseButtonText"));
        return this;
    }


    /**
     * Setting Setting Button at Denied Dialog
     *
     * @param showSettingButtonText An String defining The Denied Setting Button
     * @return Method chaining, AndroidPermission.
     */
    public AndroidPermission setShowSettingButtonText(String showSettingButtonText) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(showSettingButtonText),
                "The text for ShowSettingButtonText is empty");
        content.setSettingButtonText(showSettingButtonText);
        return this;
    }


    /**
     * Setting Setting Button at Denied Dialog
     *
     * @param showSettingButtonTextResID An String Resource defining The Denied Setting Button
     * @return Method chaining, AndroidPermission.
     */
    public AndroidPermission setShowSettingButtonText(@StringRes int showSettingButtonTextResID) {
        content.setSettingButtonText(getString(showSettingButtonTextResID,
                "Invalid ID value for ShowSettingButtonText"));
        return this;
    }


    public Observable<ArrayList<String>> requestPermission(String... permissions) {
        return requestPermission(Lists.newArrayList(permissions));
    }


    public Observable<ArrayList<String>> requestPermission(List<String> permissions) {
        return Observable.create((ObservableOnSubscribe<ArrayList<String>>) subscriber -> {
            if (isEmpty(permissions)) {
                subscriber.onError(new IllegalArgumentException("You must add one or more Permissions unconditionally"));
            } else {
                boolean isGrantedAll = true;
                for (String permission : permissions) {
                    if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                        isGrantedAll = false;
                    }
                }
                if (isGrantedAll) {
                    subscriber.onComplete();
                } else {
                    listener = new PermissionListener() {
                        @Override public void permissionGranted() {
                            subscriber.onComplete();
                        }


                        @Override public void permissionDenied(ArrayList<String> deniedPermissions) {
                            subscriber.onNext(deniedPermissions);
                        }
                    };
                    content.setPermissions(removeDuplicateStringInList(permissions));
                    content.setPackageName(context.getPackageName());
                    startPermissionActivity();
                }
            }
        }).doOnTerminate(this::removeListener);
    }


    /**
     * unsubscribe static Listener
     */
    private void removeListener() {
        if (listener != null) {
            listener = null;
        }
    }


    /**
     * Call From Permission Activity...
     * When All Permissions Granted
     */
    static void permissionGranted() {
        if (listener != null) {
            listener.permissionGranted();
        }
    }


    /**
     * Call From Permission Activity...
     * When Permissions Denied
     *
     * @param deniedPermissions The Denied Permissions from PermissionActivity
     */
    static void permissionDenied(ArrayList<String> deniedPermissions) {
        if (listener != null) {
            listener.permissionDenied(deniedPermissions);
        }
    }


    /**
     * Show Permission Activity with Bundle -> createBundle()
     */
    private void startPermissionActivity() {
        Intent intent = new Intent(context, PermissionActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(KEY_PERMISSION_CONTENT, content);
        context.startActivity(intent);
    }


    /**
     * get String resource from String.xml
     *
     * @param resId            String resource ID
     * @param exceptionMessage error Message
     * @return String from String.xml
     */
    private String getString(int resId, String exceptionMessage) {
        String stringRes = context.getString(resId);
        Preconditions.checkArgument(!Strings.isNullOrEmpty(stringRes), exceptionMessage);
        return stringRes;
    }
}