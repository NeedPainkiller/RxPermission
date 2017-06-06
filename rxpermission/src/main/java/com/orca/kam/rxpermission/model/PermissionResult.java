package com.orca.kam.rxpermission.model;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

public class PermissionResult {
    public final String permissionName;
    public final boolean isGranted;


    public PermissionResult(String permissionName, boolean isGranted) {
        this.permissionName = permissionName;
        this.isGranted = isGranted;
    }


    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PermissionResult that = (PermissionResult) o;
        return isGranted == that.isGranted &&
                Objects.equal(permissionName, that.permissionName);
    }


    @Override public int hashCode() {
        return Objects.hashCode(permissionName, isGranted);
    }


    @Override public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("permissionName", permissionName)
                .add("isGranted", isGranted)
                .toString();
    }
}