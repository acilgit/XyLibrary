package com.xycode.xylibrary.takephoto.permission;

import com.xycode.xylibrary.takephoto.model.InvokeParam;

/**
 * 授权管理回调
 */
public interface InvokeListener {
    PermissionManager.TPermissionType invoke(InvokeParam invokeParam);
}
