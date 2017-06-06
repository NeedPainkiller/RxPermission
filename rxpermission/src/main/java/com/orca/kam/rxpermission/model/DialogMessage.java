package com.orca.kam.rxpermission.model;

import android.os.Parcel;
import android.os.Parcelable;

import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * @author kam6512
 * @Create on 2017-05-15.
 * @// TODO: 2017-05-16  Creater 클래스를 만들어도 될꺼같은데...
 */
public class DialogMessage implements Parcelable {
    private String explanationMessage;
    private String explanationConfirmButtonText;
    private String deniedMessage;
    private String deniedCloseButtonText;
    private String settingButtonText;


    public DialogMessage() {
    }


    private DialogMessage(Parcel source) {
        readFromParcel(source);
    }


    public DialogMessage(String explanationMessage, String explanationConfirmButtonText,
                         String deniedMessage, String deniedCloseButtonText,
                         String settingButtonText) {
        this.explanationMessage = explanationMessage;
        this.explanationConfirmButtonText = explanationConfirmButtonText;
        this.deniedMessage = deniedMessage;
        this.deniedCloseButtonText = deniedCloseButtonText;
        this.settingButtonText = settingButtonText;
    }


    public void setExplanationMessage(String explanationMessage) {
        this.explanationMessage = explanationMessage;
    }


    public void setExplanationConfirmButtonText(String explanationConfirmButtonText) {
        this.explanationConfirmButtonText = explanationConfirmButtonText;
    }


    public void setDeniedMessage(String deniedMessage) {
        this.deniedMessage = deniedMessage;
    }


    public void setDeniedCloseButtonText(String deniedCloseButtonText) {
        this.deniedCloseButtonText = deniedCloseButtonText;
    }


    public void setSettingButtonText(String settingButtonText) {
        this.settingButtonText = settingButtonText;
    }


    public String getExplanationMessage() {
        return !isNullOrEmpty(explanationMessage) ? explanationMessage :
                "we need permission for read contact and find your location";
    }


    public String getExplanationConfirmButtonText() {
        return !isNullOrEmpty(explanationConfirmButtonText) ?
                explanationConfirmButtonText : "Confirm";
    }


    public String getDeniedMessage() {
        return !isNullOrEmpty(deniedMessage) ? deniedMessage :
                "If you reject permission,you can not use this service\n" +
                        "Please turn on permissionList at [Setting] > [Permission]";
    }


    public String getDeniedCloseButtonText() {
        return !isNullOrEmpty(deniedCloseButtonText) ?
                deniedCloseButtonText : "Close";
    }


    public String getSettingButtonText() {
        return !isNullOrEmpty(settingButtonText) ?
                settingButtonText : "Go to Setting";
    }


    @Override public int describeContents() {
        return 0;
    }


    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(explanationMessage);
        dest.writeString(explanationConfirmButtonText);
        dest.writeString(deniedMessage);
        dest.writeString(deniedCloseButtonText);
        dest.writeString(settingButtonText);
    }


    private void readFromParcel(Parcel source) {
        this.explanationMessage = source.readString();
        this.explanationConfirmButtonText = source.readString();
        this.deniedMessage = source.readString();
        this.deniedCloseButtonText = source.readString();
        this.settingButtonText = source.readString();
    }


    public static final Parcelable.Creator<DialogMessage> CREATOR
            = new Parcelable.Creator<DialogMessage>() {
        @Override public DialogMessage createFromParcel(Parcel source) {
            return new DialogMessage(source);
        }


        @Override public DialogMessage[] newArray(int size) {
            return new DialogMessage[size];
        }
    };
}
