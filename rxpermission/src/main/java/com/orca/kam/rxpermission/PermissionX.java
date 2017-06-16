package com.orca.kam.rxpermission;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.StringRes;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.orca.kam.rxpermission.model.DialogMessage;
import com.orca.kam.rxpermission.model.Permission;
import com.orca.kam.rxpermission.model.PermissionResult;
import com.orca.kam.rxpermission.util.PermissionUtil;
import com.orca.kam.rxpermission.view.PermissionFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

import static com.google.common.base.Strings.isNullOrEmpty;
import static com.orca.kam.rxpermission.util.PermissionUtil.TAG_FRAGMENT;
import static com.orca.kam.rxpermission.util.PermissionUtil.deduplicateList;
import static com.orca.kam.rxpermission.util.PermissionUtil.isAllGranted;
import static com.orca.kam.rxpermission.util.PermissionUtil.isEmpty;

public class PermissionX {

    private static boolean isAvailableInflate;

    private Context context;

    private final DialogMessage dialogMessage;
    private Permission permission;

    private List<String> permissionList = new ArrayList<>();
    public static Map<String, PublishSubject<PermissionResult>> permissionSubjects;
    private List<Observable<PermissionResult>> permissionObservables;


    public PermissionX(Context context) {
        Preconditions.checkArgument(!isNullContext(context), "Context is Invalid");
        isAvailableInflate = context instanceof Activity;
        this.context = context;
        this.dialogMessage = new DialogMessage();
    }


    private boolean isNullContext(Context context) {
        return context == null || isNullOrEmpty(context.getPackageName());
    }


    public PermissionX setExplanationMessage(String message) {
        Preconditions.checkArgument(!isNullOrEmpty(message),
                "The text for ExplanationMessage is empty");
        dialogMessage.setExplanationMessage(message);
        return this;
    }


    public PermissionX setExplanationMessage(@StringRes int messageId) {
        dialogMessage.setExplanationMessage(getString(messageId,
                "Invalid ID value for ExplanationMessage"));
        return this;
    }


    public PermissionX setExplanationConfirmButtonText(String buttonText) {
        Preconditions.checkArgument(!isNullOrEmpty(buttonText),
                "The text for explanationConfirmText is empty");
        dialogMessage.setExplanationConfirmButtonText(buttonText);
        return this;
    }


    public PermissionX setExplanationConfirmButtonText(@StringRes int buttonTextId) {
        dialogMessage.setExplanationConfirmButtonText(getString(buttonTextId,
                "Invalid ID value for explanationConfirmText"));
        return this;
    }


    public PermissionX setDeniedMessage(String deniedMessage) {
        Preconditions.checkArgument(!isNullOrEmpty(deniedMessage),
                "The text for DeniedMessage is empty");
        dialogMessage.setDeniedMessage(deniedMessage);
        return this;
    }


    public PermissionX setDeniedMessage(@StringRes int deniedMessageResID) {
        dialogMessage.setDeniedMessage(getString(deniedMessageResID,
                "Invalid ID value for DeniedMessage"));
        return this;
    }


    public PermissionX setDeniedCloseButtonText(String deniedCloseButtonText) {
        Preconditions.checkArgument(!isNullOrEmpty(deniedCloseButtonText),
                "The text for DeniedCloseButtonText is empty");
        dialogMessage.setDeniedCloseButtonText(deniedCloseButtonText);
        return this;
    }


    public PermissionX setDeniedCloseButtonText(@StringRes int deniedCloseButtonTextResID) {
        dialogMessage.setDeniedCloseButtonText(getString(deniedCloseButtonTextResID,
                "Invalid ID value for DeniedCloseButtonText"));
        return this;
    }


    public PermissionX setShowSettingButtonText(String showSettingButtonText) {
        Preconditions.checkArgument(!isNullOrEmpty(showSettingButtonText),
                "The text for ShowSettingButtonText is empty");
        dialogMessage.setSettingButtonText(showSettingButtonText);
        return this;
    }


    public PermissionX setShowSettingButtonText(@StringRes int showSettingButtonTextResID) {
        dialogMessage.setSettingButtonText(getString(showSettingButtonTextResID,
                "Invalid ID value for ShowSettingButtonText"));
        return this;
    }


    private String getString(int resId, String exceptionMessage) {
        String stringRes = context.getString(resId);
        Preconditions.checkArgument(!isNullOrEmpty(stringRes), exceptionMessage);
        return stringRes;
    }


    public PermissionX request(String permission) {
        Preconditions.checkArgument(!PermissionUtil.isPermissionIntegrity(permission),permission + " is not a permission");
        permissionList.add(permission);
        return this;
    }


    public PermissionX request(String... permissions) {
        return request(Lists.newArrayList(permissions));
    }


    public PermissionX request(List<String> permissions) {
        // TODO: 2017-06 Manifest 의 permission 에  있는 값인지 확인이 필요
        permissionList.addAll(permissions);
        return this;
    }


    public Observable<PermissionResult> requestPermission() {
        if (isEmpty(permissionList)) {
            return Observable.error(new IllegalArgumentException("You must add one or more Permissions unconditionally"));
        }
        createPermissions();
        if (isAllGranted(context, permissionList)) {
            return Observable.empty();
        }
        if (isAvailableInflate) {
            startPermissionFragment((Activity) context);
        } else {
            startPermissionActivity();
        }
        return Observable.merge(Observable.fromIterable(permissionObservables));
    }


    private void createPermissions() {
        permissionList = deduplicateList(permissionList);

        permission = new Permission(permissionList, context.getPackageName());
        permissionSubjects = Maps.newHashMap();
        permissionObservables = Lists.newArrayList();

        for (String permission : permissionList) {
            PublishSubject<PermissionResult> subject = PublishSubject.create();
            permissionSubjects.put(permission, subject);
            permissionObservables.add(subject);
        }
    }



    private void startPermissionActivity() {
        Intent intent = Henson.with(context)
                .gotoPermissionActivity()
                .dialogMessage(dialogMessage)
                .permission(permission).build();
        context.startActivity(intent);
    }


    private void startPermissionFragment(Activity activity) {
        PermissionFragment permissionFragment = createFragment(activity);
        permissionFragment.setPermission(permission);
        permissionFragment.setDialogMessage(dialogMessage);
        permissionFragment.setPermissionSubjects(permissionSubjects);
        FragmentManager fragmentManager = activity.getFragmentManager();
        fragmentManager.beginTransaction()
                .add(permissionFragment, TAG_FRAGMENT)
                .commitAllowingStateLoss();
        fragmentManager.executePendingTransactions();
    }


    private PermissionFragment createFragment(Activity activity) {
        PermissionFragment permissionFragment = (PermissionFragment)
                activity.getFragmentManager().findFragmentByTag(TAG_FRAGMENT);
        if (permissionFragment == null) {
            permissionFragment = new PermissionFragment();
        }
        return permissionFragment;
    }
}