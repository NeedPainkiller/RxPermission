package com.orca.kam.rxpermission.commons;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.orca.kam.rxpermission.Henson;
import com.orca.kam.rxpermission.commons.activity.PermissionListener;
import com.orca.kam.rxpermission.commons.fragment.PermissionFragment;

import java.util.ArrayList;
import java.util.List;

import hugo.weaving.DebugLog;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;

import static com.google.common.base.Strings.isNullOrEmpty;
import static com.orca.kam.rxpermission.util.PermissionUtil.TAG;
import static com.orca.kam.rxpermission.util.PermissionUtil.TAG_FRAGMENT;
import static com.orca.kam.rxpermission.util.PermissionUtil.isEmpty;
import static com.orca.kam.rxpermission.util.PermissionUtil.removeDuplicatedPermission;

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


    private static boolean isAvailableInflate = false;
    private static PermissionListener listener;
    private Context context;
    private final PermissionContent content = new PermissionContent();

    private final List<String> permissions = new ArrayList<>();


    /**
     * Constructor. Prepares a new AndroidPermission Setting up
     *
     * @param context(Activity or Application) Get String Resource, Package name, start Permission Activity and check Permissions
     */
    public AndroidPermission(Context context) {
        Preconditions.checkArgument(!isNullContext(context), "Context is Invalid");
        isAvailableInflate = context instanceof Activity;
        Log.e(TAG, "isAvailableInflate : " + isAvailableInflate);
        this.context = context;
    }


    /**
     * Check the Context is null or empty the Package Name
     *
     * @param context(Activity or Application) need to check
     * @return context has null pointer or cannot get Package Name successful.. will be return true
     */
    private boolean isNullContext(Context context) {
        return context == null || isNullOrEmpty(context.getPackageName());
    }


    /**
     * Setting Message at Prepare Dialog
     *
     * @param message An String defining The Explanation Message
     * @return Method chaining, AndroidPermission.
     */
    public AndroidPermission setExplanationMessage(String message) {
        Preconditions.checkArgument(!isNullOrEmpty(message),
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
        Preconditions.checkArgument(!isNullOrEmpty(buttonText),
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
        Preconditions.checkArgument(!isNullOrEmpty(deniedMessage),
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
        Preconditions.checkArgument(!isNullOrEmpty(deniedCloseButtonText),
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
        Preconditions.checkArgument(!isNullOrEmpty(showSettingButtonText),
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


    /**
     * get String resource from String.xml
     *
     * @param resId            String resource ID
     * @param exceptionMessage error Message
     * @return String from String.xml
     */
    private String getString(int resId, String exceptionMessage) {
        String stringRes = context.getString(resId);
        Preconditions.checkArgument(!isNullOrEmpty(stringRes), exceptionMessage);
        return stringRes;
    }


    public AndroidPermission request(String permission) {
        this.permissions.add(permission);
        return this;
    }


    public AndroidPermission request(String... permissions) {
        return request(Lists.newArrayList(permissions));
    }


    public AndroidPermission request(List<String> permissions) {
        this.permissions.addAll(permissions);
        return this;
    }


    public Observable<List<String>> requestPermission() {
        Observable<List<String>> observable =
                Observable.create((ObservableOnSubscribe<List<String>>) subscriber -> {
                    if (isEmpty(permissions)) {
                        subscriber.onError(new IllegalArgumentException("You must add one or more Permissions unconditionally"));
                    } else {
                        if (isAllPermissionGranted()) {
                            subscriber.onComplete();
                        } else {
                            listener = new PermissionListener() {
                                @Override public void permissionGranted() {
                                    subscriber.onComplete();
                                }


                                @Override
                                public void permissionDenied(List<String> deniedPermissions) {
                                    subscriber.onNext(deniedPermissions);
                                }
                            };
                            content.setPermissions(removeDuplicatedPermission(permissions));
                            content.setPackageName(context.getPackageName());
                            if (isAvailableInflate) {
                                startPermissionFragment((Activity) context);
                            } else {
                                startPermissionActivity();
                            }
                        }
                    }
                }).doOnDispose(this::terminateLeakyObjects);
//        observable.mergeWith(activity());
        return observable;
    }


    @DebugLog private boolean isAllPermissionGranted() {
        boolean isAllGranted = true;
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                isAllGranted = false;
            }
        }
        return isAllGranted;
    }


    /**
     * Show Permission Activity
     */
    @DebugLog private void startPermissionActivity() {
        Intent intent = Henson.with(context)
                .gotoPermissionActivity().content(content).build();
        context.startActivity(intent);
    }


    @DebugLog private void startPermissionFragment(Activity activity) {
        PermissionFragment permissionFragment = findPermissionFragment(activity);
        if (permissionFragment == null) {
            permissionFragment = new PermissionFragment();
        }
        permissionFragment.setListener(listener);
        permissionFragment.setContent(content);
        FragmentManager fragmentManager = getFragmentManager(activity);
        fragmentManager
                .beginTransaction()
                .add(permissionFragment, TAG_FRAGMENT)
                .commitAllowingStateLoss();
        fragmentManager.executePendingTransactions();
    }


    @DebugLog
    private PermissionFragment findPermissionFragment(Activity activity) {
        return (PermissionFragment) activity.getFragmentManager().findFragmentByTag(TAG_FRAGMENT);
    }


    @DebugLog private FragmentManager getFragmentManager(Activity activity) {
        return activity.getFragmentManager();
    }


    /**
     * terminate static Listener and context
     */
    @DebugLog private void terminateLeakyObjects() {
        listener = null;
        context = null;
        permissions.clear();
    }


    /**
     * Call From Permission Activity...
     * When All Permissions Granted
     */
    public static void permissionGranted() {
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
    public static void permissionDenied(List<String> deniedPermissions) {
        if (listener != null) {
            listener.permissionDenied(deniedPermissions);
        }
    }
}