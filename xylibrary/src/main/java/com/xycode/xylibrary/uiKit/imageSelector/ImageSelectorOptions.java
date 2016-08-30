package com.xycode.xylibrary.uiKit.imageSelector;


import com.xycode.xylibrary.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by XY on 2016-08-29.
 */
public class ImageSelectorOptions {

    private static ImageSelectorOptions options;

    /**
     * mipmap
     */
    public int indicatorImageUnchecked = R.mipmap.ic_image_selector_unselected;
    public int indicatorImageChecked = R.mipmap.ic_image_selector_selected;
    public int imageholder = R.color.grayLite;
    public int errorImage = R.color.black;

    /**
     * dimen
     */
    public int folderCoverSize = R.dimen.imageSelectorFolderCoverSize;
    /**
     * params
     */
     public int defaultCount = 9;
     public int gridColumnSize = 3;
     public int selectMode = MultiImageSelectorActivity.MODE_MULTI;
     public boolean showCamera = true;
     public List<String> selectedList = new ArrayList<>();




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
