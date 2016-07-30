package com.xycode.xylibrary.uiKit.views.banner;

public class BannerBehavior {

    OnBannerClickListener bannerClickListener;

    public BannerBehavior(OnBannerClickListener bannerClickListener) {
        this.bannerClickListener = bannerClickListener;
    }

    protected String getPreviewUrlFromPosition(String url, int pos) {
        return "";
    }
}
