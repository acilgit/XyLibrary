package com.xycode.xylibrary.uiKit.imageSelector.bean;

import android.text.TextUtils;

import java.util.List;

/**
 * 文件夹
 * Created by Nereo on 2015/4/7.
 */
public class FolderBean {
    public String name;
    public String path;
    public ImageBean cover;
    public List<ImageBean> imageBeen;

    @Override
    public boolean equals(Object o) {
        try {
            FolderBean other = (FolderBean) o;
            return TextUtils.equals(other.path, path);
        }catch (ClassCastException e){
            e.printStackTrace();
        }
        return super.equals(o);
    }
}
