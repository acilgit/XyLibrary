package com.xycode.xylibrary.uiKit.imageSelector.bean;

/**
 */
public class ImageBean {
    public String path;
    public String name;
    public long time;

    public ImageBean(String path, String name, long time){
        this.path = path;
        this.name = name;
        this.time = time;
    }

    @Override
    public boolean equals(Object o) {
        try {
            ImageBean other = (ImageBean) o;
            return this.path.equalsIgnoreCase(other.path);
        }catch (ClassCastException e){
            e.printStackTrace();
        }
        return super.equals(o);
    }
}
