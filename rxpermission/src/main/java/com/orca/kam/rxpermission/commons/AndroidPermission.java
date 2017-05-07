package com.orca.kam.rxpermission.commons;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.orca.kam.rxpermission.Henson;
import com.orca.kam.rxpermission.commons.activity.PermissionActivity;
import com.orca.kam.rxpermission.commons.fragment.PermissionFragment;

import java.lang.ref.WeakReference;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;

import static com.google.common.base.Strings.isNullOrEmpty;
import static com.orca.kam.rxpermission.util.PermissionUtil.TAG_FRAGMENT;
import static com.orca.kam.rxpermission.util.PermissionUtil.isEmpty;

public class AndroidPermission {


    private Context context;
    private final PermissionContent content = new PermissionContent();

    private PermissionFragment permissionFragment;
    private static WeakReference<PermissionActivity> permissionActivityRef;

    private static boolean isAvailableInflate = false;


    public AndroidPermission(Context context) {
        Preconditions.checkArgument(!isNullContext(context), "Context is Invalid");
        isAvailableInflate = context instanceof Activity;
        this.context = context;
    }


    private boolean isNullContext(Context context) {
        return context == null || isNullOrEmpty(context.getPackageName());
    }


    public AndroidPermission setExplanationMessage(String message) {
        Preconditions.checkArgument(!isNullOrEmpty(message),
                "The text for ExplanationMessage is empty");
        content.setExplanationMessage(message);
        return this;
    }


    public AndroidPermission setExplanationMessage(@StringRes int messageId) {
        content.setExplanationMessage(getString(messageId,
                "Invalid ID value for ExplanationMessage"));
        return this;
    }


    public AndroidPermission setExplanationConfirmButtonText(String buttonText) {
        Preconditions.checkArgument(!isNullOrEmpty(buttonText),
                "The text for explanationConfirmText is empty");
        content.setExplanationConfirmButtonText(buttonText);
        return this;
    }


    public AndroidPermission setExplanationConfirmButtonText(@StringRes int buttonTextId) {
        content.setExplanationConfirmButtonText(getString(buttonTextId,
                "Invalid ID value for explanationConfirmText"));
        return this;
    }


    public AndroidPermission setDeniedMessage(String deniedMessage) {
        Preconditions.checkArgument(!isNullOrEmpty(deniedMessage),
                "The text for DeniedMessage is empty");
        content.setDeniedMessage(deniedMessage);
        return this;
    }


    public AndroidPermission setDeniedMessage(@StringRes int deniedMessageResID) {
        content.setDeniedMessage(getString(deniedMessageResID,
                "Invalid ID value for DeniedMessage"));
        return this;
    }


    public AndroidPermission setDeniedCloseButtonText(String deniedCloseButtonText) {
        Preconditions.checkArgument(!isNullOrEmpty(deniedCloseButtonText),
                "The text for DeniedCloseButtonText is empty");
        content.setDeniedCloseButtonText(deniedCloseButtonText);
        return this;
    }


    public AndroidPermission setDeniedCloseButtonText(@StringRes int deniedCloseButtonTextResID) {
        content.setDeniedCloseButtonText(getString(deniedCloseButtonTextResID,
                "Invalid ID value for DeniedCloseButtonText"));
        return this;
    }


    public AndroidPermission setShowSettingButtonText(String showSettingButtonText) {
        Preconditions.checkArgument(!isNullOrEmpty(showSettingButtonText),
                "The text for ShowSettingButtonText is empty");
        content.setSettingButtonText(showSettingButtonText);
        return this;
    }


    public AndroidPermission setShowSettingButtonText(@StringRes int showSettingButtonTextResID) {
        content.setSettingButtonText(getString(showSettingButtonTextResID,
                "Invalid ID value for ShowSettingButtonText"));
        return this;
    }


    private String getString(int resId, String exceptionMessage) {
        String stringRes = context.getString(resId);
        Preconditions.checkArgument(!isNullOrEmpty(stringRes), exceptionMessage);
        return stringRes;
    }


    public AndroidPermission request(String permission) {
        content.addPermission(permission);
        return this;
    }


    public AndroidPermission request(String... permissions) {
        return request(Lists.newArrayList(permissions));
    }


    public AndroidPermission request(List<String> permissions) {
        content.addAllPermission(permissions);
        return this;
    }


    public Observable<List<String>> requestPermission() {
        List<String> permissions = content.getPermissionList();
        Observable<List<String>> observable =
                Observable.create((ObservableOnSubscribe<List<String>>) subscriber -> {
                    if (isEmpty(permissions)) {
                        subscriber.onError(new IllegalArgumentException("You must add one or more Permissions unconditionally"));
                    } else {
                        if (isAllPermissionGranted(permissions)) {
                            subscriber.onComplete();
                        } else {


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


    private boolean isAllPermissionGranted(List<String> permissions) {
        boolean isAllGranted = true;
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                isAllGranted = false;
            }
        }
        return isAllGranted;
    }


    private void terminateLeakyObjects() {
        context = null;
        permissionFragment = null;
        permissionActivityRef.clear();
        content.clearPermissionList();
    }


    public void setPermissionActivityRef(PermissionActivity activity) {
        permissionActivityRef = new WeakReference<>(activity);
    }


    private void startPermissionActivity() {
        Intent intent = Henson.with(context)
                .gotoPermissionActivity().content(content).build();
        context.startActivity(intent);
    }


    private void startPermissionFragment(Activity activity) {
        permissionFragment = setPermissionFragment(activity);
        permissionFragment.setContent(content);
        FragmentManager fragmentManager = activity.getFragmentManager();
        fragmentManager
                .beginTransaction()
                .add(permissionFragment, TAG_FRAGMENT)
                .commitAllowingStateLoss();
        fragmentManager.executePendingTransactions();
    }


    private PermissionFragment setPermissionFragment(Activity activity) {
        PermissionFragment permissionFragment = (PermissionFragment)
                activity.getFragmentManager().findFragmentByTag(TAG_FRAGMENT);
        if (permissionFragment == null) {
            permissionFragment = new PermissionFragment();
        }
        return permissionFragment;
    }
}