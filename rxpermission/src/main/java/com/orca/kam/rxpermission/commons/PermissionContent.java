package com.orca.kam.rxpermission.commons;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * Project RxPermission
 *
 * @author Kang Young Won
 * @create 2017-02-16 - 오후 4:08
 */
class PermissionContent implements Parcelable {
    private List<String> permissions;
    private String packageName;
    private String explanationMessage;
    private String explanationConfirmButtonText;
    private String deniedMessage;
    private String deniedCloseButtonText;
    private String settingButtonText;

    static final String KEY_PERMISSION_CONTENT = "KEY_PERMISSION_CONTENT";


    PermissionContent() {
    }


    private PermissionContent(Parcel source) {
        readFromParcel(source);
    }


    PermissionContent(List<String> permissions, String packageName,
                      String explanationMessage, String explanationConfirmButtonText,
                      String deniedMessage, String deniedCloseButtonText,
                      String settingButtonText) {
        this.permissions = permissions;
        this.packageName = packageName;
        this.explanationMessage = explanationMessage;
        this.explanationConfirmButtonText = explanationConfirmButtonText;
        this.deniedMessage = deniedMessage;
        this.deniedCloseButtonText = deniedCloseButtonText;
        this.settingButtonText = settingButtonText;
    }


    void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }


    void setPackageName(String packageName) {
        this.packageName = packageName;
    }


    void setExplanationMessage(String explanationMessage) {
        this.explanationMessage = explanationMessage;
    }


    void setExplanationConfirmButtonText(String explanationConfirmButtonText) {
        this.explanationConfirmButtonText = explanationConfirmButtonText;
    }


    void setDeniedMessage(String deniedMessage) {
        this.deniedMessage = deniedMessage;
    }


    void setDeniedCloseButtonText(String deniedCloseButtonText) {
        this.deniedCloseButtonText = deniedCloseButtonText;
    }


    void setSettingButtonText(String settingButtonText) {
        this.settingButtonText = settingButtonText;
    }


    public List<String> getPermissions() {
        return permissions;
    }


    String getPackageName() {
        return packageName;
    }


    String getExplanationMessage() {
        return !isNullOrEmpty(explanationMessage) ? explanationMessage :
                "we need permission for read contact and find your location";
    }


    String getExplanationConfirmButtonText() {
        return !isNullOrEmpty(explanationConfirmButtonText) ?
                explanationConfirmButtonText : "Confirm";
    }


    String getDeniedMessage() {
        return !isNullOrEmpty(deniedMessage) ? deniedMessage :
                "If you reject permission,you can not use this service\n" +
                        "Please turn on permissions at [Setting] > [Permission]";
    }


    String getDeniedCloseButtonText() {
        return !isNullOrEmpty(deniedCloseButtonText) ?
                deniedCloseButtonText : "Close";
    }


    String getSettingButtonText() {
        return !isNullOrEmpty(settingButtonText) ?
                settingButtonText : "Go to Setting";
    }


    @Override public int describeContents() {
        return 0;
    }


    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringList(permissions);
        dest.writeString(packageName);
        dest.writeString(explanationMessage);
        dest.writeString(explanationConfirmButtonText);
        dest.writeString(deniedMessage);
        dest.writeString(deniedCloseButtonText);
        dest.writeString(settingButtonText);
    }


    private void readFromParcel(Parcel source) {
        this.permissions = source.createStringArrayList();
        this.packageName = source.readString();
        this.explanationMessage = source.readString();
        this.explanationConfirmButtonText = source.readString();
        this.deniedMessage = source.readString();
        this.deniedCloseButtonText = source.readString();
        this.settingButtonText = source.readString();
    }


    public static final Parcelable.Creator<PermissionContent> CREATOR
            = new Creator<PermissionContent>() {
        @Override public PermissionContent createFromParcel(Parcel source) {
            return new PermissionContent(source);
        }


        @Override public PermissionContent[] newArray(int size) {
            return new PermissionContent[size];
        }
    };
}