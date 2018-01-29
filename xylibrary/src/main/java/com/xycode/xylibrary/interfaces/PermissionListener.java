package com.xycode.xylibrary.interfaces;

import java.util.List;

/**
 * @author thisfeng
 * @date 2018/1/29-下午1:40
 */

public interface PermissionListener {
    void onGranted();

    void onDenied(List<String> deniedPermissions);
}
