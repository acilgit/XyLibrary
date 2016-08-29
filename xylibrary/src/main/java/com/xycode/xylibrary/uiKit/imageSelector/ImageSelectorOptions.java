package com.xycode.xylibrary.uiKit.imageSelector;


import com.xycode.xylibrary.R;

/**
 * Created by XY on 2016-08-29.
 */
public class ImageSelectorOptions {

    private static ImageSelectorOptions options;

    /**
     * text
     */
    public int textSubmitBtn = android.R.string.ok;
    public int textAllFolder = android.R.string.selectAll;
    public int textPreview = R.string.abc_activity_chooser_view_see_all;

    /**
     * message text
     */
    public int msgAmountLimit = android.R.string.ok;
    public int msgNoCamera = android.R.string.ok;

    /**
     * mipmap
     */
    public int indicatorImageUnchecked = R.mipmap.ic_image_selector_unselected;
    public int indicatorImageChecked = R.mipmap.ic_image_selector_selected;

    /**
     * dimen
     */
    public int folderCoverSize = R.dimen.folderCoverSize;

    public ImageSelectorOptions() {

    }

    public static ImageSelectorOptions options() {
        if (options == null) {
            options = new ImageSelectorOptions();
        }
        return options;
    }

    public static void setOptions(ImageSelectorOptions options) {
        ImageSelectorOptions.options = options;
    }
}
