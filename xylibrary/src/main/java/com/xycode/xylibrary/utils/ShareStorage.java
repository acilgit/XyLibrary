package com.xycode.xylibrary.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

/**
 * Created by XY on 2016-07-27.
 */
public class ShareStorage {

    private SharedPreferences storage;

    public ShareStorage(@NonNull Context context, @NonNull String preferenceName) {
        storage = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
    }

    /**
     * clear all content
     */
    public void clear() {
        storage.edit().clear().apply();
    }

    public void put(String key, Object value) {
        storage.edit().putString(key, value.toString()).apply();
    }

    public void put(String key, int value) {
        storage.edit().putInt(key, value).apply();
    }

    public void put(String key, float value) {
        storage.edit().putFloat(key, value).apply();
    }

    public void put(String key, boolean value) {
        storage.edit().putBoolean(key, value).apply();
    }

    public void put(String key, String value) {
        storage.edit().putString(key, value).apply();
    }

    public void put(String key, long value) {
        storage.edit().putLong(key, value).apply();
    }

    public void put(String key, double value) {
        Double newValue = value;
        storage.edit().putString(key, newValue.toString()).apply();
    }

    public String getString(String key) {
        return storage.getString(key, "");
    }

    public String getString(String key, String defValue) {
        return storage.getString(key, defValue);
    }

    public boolean getBoolean(String key, boolean defValue) {
        return storage.getBoolean(key, defValue);
    }

    public int getInt(String key, int defValue) {
        return storage.getInt(key, defValue);
    }

    public float getFloat(String key, float defValue) {
        return storage.getFloat(key, defValue);
    }

    public double getDouble(String key, double defValue) {
        String value = storage.getString(key, null);
        return value == null ? defValue : Double.parseDouble(value);
    }

    public long getLong(String key, long defValue) {
        return storage.getLong(key, defValue);
    }

}
