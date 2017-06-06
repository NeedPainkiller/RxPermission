package com.orca.kam.rxpermission.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.orca.kam.rxpermission.util.PermissionUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Project RxPermission
 *
 * @author Kang Young Won
 * @create 2017-02-16 - 오후 4:08
 */
public class Permission implements Parcelable {
    private List<String> permissionList = new ArrayList<>();
    private String packageName;


    public Permission() {
    }


    private Permission(Parcel source) {
        readFromParcel(source);
    }


    public Permission(List<String> permissionList, String packageName) {
        this.permissionList = permissionList;
        this.packageName = packageName;
    }


    public void setPermissionList(List<String> permissionList) {
        this.permissionList = permissionList;
        deduplicatePermissionList();
    }


    public void addPermission(String permission) {
        permissionList.add(permission);
        deduplicatePermissionList();
    }


    public void addAllPermission(List<String> permissions) {
        permissionList.addAll(permissions);
//        deduplicatePermissionList();
    }


    public void clearPermissionList() {
        permissionList.clear();
    }


    private void deduplicatePermissionList() {
        permissionList = PermissionUtil.deduplicateList(permissionList);
    }


    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }


    public List<String> getPermissionList() {
        return permissionList;
    }


    public String getPackageName() {
        return packageName;
    }


    @Override public int describeContents() {
        return 0;
    }


    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringList(permissionList);
        dest.writeString(packageName);
    }


    private void readFromParcel(Parcel source) {
        this.permissionList = source.createStringArrayList();
        this.packageName = source.readString();
    }


    public static final Parcelable.Creator<Permission> CREATOR
            = new Creator<Permission>() {
        @Override public Permission createFromParcel(Parcel source) {
            return new Permission(source);
        }


        @Override public Permission[] newArray(int size) {
            return new Permission[size];
        }
    };
}