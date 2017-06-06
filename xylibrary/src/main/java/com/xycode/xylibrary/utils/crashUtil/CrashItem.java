package com.xycode.xylibrary.utils.crashUtil;

/**
 * Created by XY on 2017-06-05.
 */
public class CrashItem {
    private String versionName;
    private int versionCode;
    private String release;
    private int sdk;
    private String manufacturer;
    private String model;
    private String errorMsg;

    public CrashItem() {
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

    @Override
    public String toString() {
        return "CrashItem{" +
                "versionName='" + versionName + '\'' +
                ", versionCode=" + versionCode +
                ", release='" + release + '\'' +
                ", sdk=" + sdk +
                ", manufacturer='" + manufacturer + '\'' +
                ", model='" + model + '\'' +
                ", errorMsg='" + errorMsg + '\'' +
                '}';
    }
}
