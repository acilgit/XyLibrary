package com.xycode.xylibrary.takephoto.model;

import java.io.Serializable;

/**
 * 裁剪配置类
 * Author: JPH
 * Date: 2016/7/27 13:19
 */
public class CropOptions implements Serializable {
    /**
     * 使用TakePhoto自带的裁切工具进行裁切
     */
    private boolean isCrop = false;
    private boolean withOwnCrop = false;
    private int aspectX = 512;
    private int aspectY = 512;
    private int outputX = 512;
    private int outputY = 512;

    private CropOptions() {
    }

    public boolean isCrop() {
        return isCrop;
    }

    public CropOptions setCrop(boolean crop) {

        isCrop = crop;
        return this;
    }

    public int getAspectX() {
        return aspectX;
    }

    public CropOptions setAspectX(int aspectX) {

        this.aspectX = aspectX;
        return this;
    }

    public int getAspectY() {
        return aspectY;
    }

    public CropOptions setAspectY(int aspectY) {

        this.aspectY = aspectY;
        return this;
    }

    public int getOutputX() {
        return outputX;
    }

    public CropOptions setOutputX(int outputX) {

        this.outputX = outputX;
        return this;
    }

    public int getOutputY() {
        return outputY;
    }

    public CropOptions setOutputY(int outputY) {

        this.outputY = outputY;
        return this;
    }

    public boolean isWithOwnCrop() {
        return withOwnCrop;
    }

    public CropOptions setWithOwnCrop(boolean withOwnCrop) {

        this.withOwnCrop = withOwnCrop;
        return this;
    }

    public static class Builder {
        private CropOptions options;

        public Builder() {
            options = new CropOptions();
        }

        public Builder setAspectX(int aspectX) {
            options.setAspectX(aspectX);
            return this;
        }

        public Builder setAspectY(int aspectY) {
            options.setAspectY(aspectY);
            return this;
        }

        public Builder setOutputX(int outputX) {
            options.setOutputX(outputX);
            return this;
        }

        public Builder setOutputY(int outputY) {
            options.setOutputY(outputY);
            return this;
        }

        public Builder setWithOwnCrop(boolean withOwnCrop) {
            options.setWithOwnCrop(withOwnCrop);
            return this;
        }
        public Builder setCrop(boolean isCrop) {
            options.setCrop(isCrop);
            return this;
        }

        public CropOptions create() {
            return options;
        }
    }
}
