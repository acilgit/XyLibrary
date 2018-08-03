package com.xycode.xylibrary.utils.crashUtil;

/**
 * Created by XY on 2017-06-05.
 */
public class CrashItem {
    private String packageName;
    private String versionName;
    private int versionCode;
    private String release;
    private int sdk;
    private String manufacturer;
    private String model;
    private String errorMsg;
    private String errorMsgTitle;
    private String id;
    private String user;
    private String mark;

    public CrashItem() {
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getRelease() {
        return release;
    }

    public void setRelease(String release) {
        this.release = release;
    }

    public int getSdk() {
        return sdk;
    }

    public void setSdk(int sdk) {
        this.sdk = sdk;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getErrorMsgTitle() {
        return errorMsgTitle;
    }

    public void setErrorMsgTitle(String errorMsgTitle) {
        this.errorMsgTitle = errorMsgTitle;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    @Override
    public String toString() {
        return "CrashItem{" +
                "packageName='" + packageName + '\'' +
                ", versionName='" + versionName + '\'' +
                ", versionCode=" + versionCode +
                ", release='" + release + '\'' +
                ", sdk=" + sdk +
                ", manufacturer='" + manufacturer + '\'' +
                ", model='" + model + '\'' +
                ", id='" + id + '\'' +
                ", user='" + user + '\'' +
                ", mark='" + mark + '\'' +
                ", errorMsgTitle='" + errorMsgTitle + '\'' +
                ", errorMsg='" + errorMsg + '\'' +
                '}' + '\n';
    }
}
