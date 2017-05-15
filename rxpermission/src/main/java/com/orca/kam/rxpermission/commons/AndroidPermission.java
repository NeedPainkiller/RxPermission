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
import com.orca.kam.rxpermission.commons.dialog.DialogMessage;
import com.orca.kam.rxpermission.commons.fragment.PermissionFragment;
import com.orca.kam.rxpermission.commons.permission.Permission;
import com.orca.kam.rxpermission.commons.permission.PermissionPair;
import com.orca.kam.rxpermission.util.PermissionUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.subjects.PublishSubject;

import static com.google.common.base.Strings.isNullOrEmpty;
import static com.orca.kam.rxpermission.util.PermissionUtil.*;
import static com.orca.kam.rxpermission.util.PermissionUtil.TAG_FRAGMENT;
import static com.orca.kam.rxpermission.util.PermissionUtil.isEmpty;

public class AndroidPermission {

    private Context context;

    private final DialogMessage dialogMessage;
//    private final Permission permission;

    private List<String> permissionList = new ArrayList<>();

    private PermissionFragment permissionFragment;
    private static WeakReference<PermissionActivity> permissionActivityRef;
//    private Map<String, PublishSubject<PermissionPair>> permissionSubject = new HashMap<>();
    private static boolean isAvailableInflate;


    public AndroidPermission(Context context) {
        Preconditions.checkArgument(!isNullContext(context), "Context is Invalid");
        isAvailableInflate = context instanceof Activity;
        this.context = context;
        this.dialogMessage = new DialogMessage();
//        this.permission = new Permission();
    }


    private boolean isNullContext(Context context) {
        return context == null || isNullOrEmpty(context.getPackageName());
    }


    public AndroidPermission setExplanationMessage(String message) {
        Preconditions.checkArgument(!isNullOrEmpty(message),
                "The text for ExplanationMessage is empty");
        dialogMessage.setExplanationMessage(message);
        return this;
    }


    public AndroidPermission setExplanationMessage(@StringRes int messageId) {
        dialogMessage.setExplanationMessage(getString(messageId,
                "Invalid ID value for ExplanationMessage"));
        return this;
    }


    public AndroidPermission setExplanationConfirmButtonText(String buttonText) {
        Preconditions.checkArgument(!isNullOrEmpty(buttonText),
                "The text for explanationConfirmText is empty");
        dialogMessage.setExplanationConfirmButtonText(buttonText);
        return this;
    }


    public AndroidPermission setExplanationConfirmButtonText(@StringRes int buttonTextId) {
        dialogMessage.setExplanationConfirmButtonText(getString(buttonTextId,
                "Invalid ID value for explanationConfirmText"));
        return this;
    }


    public AndroidPermission setDeniedMessage(String deniedMessage) {
        Preconditions.checkArgument(!isNullOrEmpty(deniedMessage),
                "The text for DeniedMessage is empty");
        dialogMessage.setDeniedMessage(deniedMessage);
        return this;
    }


    public AndroidPermission setDeniedMessage(@StringRes int deniedMessageResID) {
        dialogMessage.setDeniedMessage(getString(deniedMessageResID,
                "Invalid ID value for DeniedMessage"));
        return this;
    }


    public AndroidPermission setDeniedCloseButtonText(String deniedCloseButtonText) {
        Preconditions.checkArgument(!isNullOrEmpty(deniedCloseButtonText),
                "The text for DeniedCloseButtonText is empty");
        dialogMessage.setDeniedCloseButtonText(deniedCloseButtonText);
        return this;
    }


    public AndroidPermission setDeniedCloseButtonText(@StringRes int deniedCloseButtonTextResID) {
        dialogMessage.setDeniedCloseButtonText(getString(deniedCloseButtonTextResID,
                "Invalid ID value for DeniedCloseButtonText"));
        return this;
    }


    public AndroidPermission setShowSettingButtonText(String showSettingButtonText) {
        Preconditions.checkArgument(!isNullOrEmpty(showSettingButtonText),
                "The text for ShowSettingButtonText is empty");
        dialogMessage.setSettingButtonText(showSettingButtonText);
        return this;
    }


    public AndroidPermission setShowSettingButtonText(@StringRes int showSettingButtonTextResID) {
        dialogMessage.setSettingButtonText(getString(showSettingButtonTextResID,
                "Invalid ID value for ShowSettingButtonText"));
        return this;
    }


    private String getString(int resId, String exceptionMessage) {
        String stringRes = context.getString(resId);
        Preconditions.checkArgument(!isNullOrEmpty(stringRes), exceptionMessage);
        return stringRes;
    }


    public AndroidPermission request(String permission) {
//        content.addPermission(permission);
        permissionList.add(permission);
        return this;
    }


    public AndroidPermission request(String... permissions) {
        return request(Lists.newArrayList(permissions));
    }


    public AndroidPermission request(List<String> permissions) {
        permissionList.addAll(permissions);
        return this;
    }


    public Observable<List<String>> requestPermission() {
        if (isEmpty(permissionList)) {
            return Observable.error(new IllegalArgumentException("You must add one or more Permissions unconditionally"));
        }
        if (isAllPermissionGranted(permissionList)) {
            return Observable.empty();
        }
        return Observable.create((ObservableOnSubscribe<List<String>>) subscriber -> {
            Permission permission = new Permission(deduplicatePermission(permissionList),
                    context.getPackageName());


            if (isAvailableInflate) {

                startPermissionFragment((Activity) context);
            } else {
                startPermissionActivity();
            }
        }).doOnDispose(this::terminateLeakyObjects);
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
//        context = null;
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