package com.orca.kam.rxpermissionkt.commons

import android.os.Parcel
import android.os.Parcelable

import com.google.common.base.Strings.isNullOrEmpty

/**
 * Project RxPermission

 * @author Kang Young Won
 * *
 * @create 2017-02-16 - 오후 4:08
 */
internal class PermissionContent : Parcelable {
    var permissions: List<String>? = null
    var packageName: String? = null
    private var explanationMessage: String? = null
    private var explanationConfirmButtonText: String? = null
    private var deniedMessage: String? = null
    private var deniedCloseButtonText: String? = null
    private var settingButtonText: String? = null

    constructor() {}


    private constructor(source: Parcel) {
        readFromParcel(source)
    }


    constructor(permissions: List<String>, packageName: String,
                explanationMessage: String, explanationConfirmButtonText: String,
                deniedMessage: String, deniedCloseButtonText: String,
                settingButtonText: String) {
        this.permissions = permissions
        this.packageName = packageName
        this.explanationMessage = explanationMessage
        this.explanationConfirmButtonText = explanationConfirmButtonText
        this.deniedMessage = deniedMessage
        this.deniedCloseButtonText = deniedCloseButtonText
        this.settingButtonText = settingButtonText
    }


    fun setExplanationMessage(explanationMessage: String) {
        this.explanationMessage = explanationMessage
    }


    fun setExplanationConfirmButtonText(explanationConfirmButtonText: String) {
        this.explanationConfirmButtonText = explanationConfirmButtonText
    }


    fun setDeniedMessage(deniedMessage: String) {
        this.deniedMessage = deniedMessage
    }


    fun setDeniedCloseButtonText(deniedCloseButtonText: String) {
        this.deniedCloseButtonText = deniedCloseButtonText
    }


    fun setSettingButtonText(settingButtonText: String) {
        this.settingButtonText = settingButtonText
    }


    fun getExplanationMessage(): String {
        return if (!isNullOrEmpty(explanationMessage))
            explanationMessage
        else
            "we need permission for read contact and find your location"
    }


    fun getExplanationConfirmButtonText(): String {
        return if (!isNullOrEmpty(explanationConfirmButtonText))
            explanationConfirmButtonText
        else
            "Confirm"
    }


    fun getDeniedMessage(): String {
        return if (!isNullOrEmpty(deniedMessage))
            deniedMessage
        else
            "If you reject permission,you can not use this service\n" + "Please turn on permissions at [Setting] > [Permission]"
    }


    fun getDeniedCloseButtonText(): String {
        return if (!isNullOrEmpty(deniedCloseButtonText))
            deniedCloseButtonText
        else
            "Close"
    }


    fun getSettingButtonText(): String {
        return if (!isNullOrEmpty(settingButtonText))
            settingButtonText
        else
            "Go to Setting"
    }


    override fun describeContents(): Int {
        return 0
    }


    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeStringList(permissions)
        dest.writeString(packageName)
        dest.writeString(explanationMessage)
        dest.writeString(explanationConfirmButtonText)
        dest.writeString(deniedMessage)
        dest.writeString(deniedCloseButtonText)
        dest.writeString(settingButtonText)
    }


    private fun readFromParcel(source: Parcel) {
        this.permissions = source.createStringArrayList()
        this.packageName = source.readString()
        this.explanationMessage = source.readString()
        this.explanationConfirmButtonText = source.readString()
        this.deniedMessage = source.readString()
        this.deniedCloseButtonText = source.readString()
        this.settingButtonText = source.readString()
    }

    companion object {


        val CREATOR: Parcelable.Creator<PermissionContent> = object : Parcelable.Creator<PermissionContent> {
            override fun createFromParcel(source: Parcel): PermissionContent {
                return PermissionContent(source)
            }


            override fun newArray(size: Int): Array<PermissionContent> {
                return arrayOfNulls(size)
            }
        }
    }
}