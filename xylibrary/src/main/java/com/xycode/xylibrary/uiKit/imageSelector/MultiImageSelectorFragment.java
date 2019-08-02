package com.xycode.xylibrary.uiKit.imageSelector;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;

import com.xycode.xylibrary.R;
import com.xycode.xylibrary.uiKit.imageSelector.adapter.FolderAdapter;
import com.xycode.xylibrary.uiKit.imageSelector.adapter.ImageAdapter;
import com.xycode.xylibrary.uiKit.imageSelector.bean.FolderBean;
import com.xycode.xylibrary.uiKit.imageSelector.bean.ImageBean;
import com.xycode.xylibrary.uiKit.imageSelector.utils.FileUtils;
import com.xycode.xylibrary.utils.toast.TS;
import com.xycode.xylibrary.utils.Tools;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.xycode.xylibrary.uiKit.imageSelector.ImageSelectorOptions.options;


/**
 * Fragment
 */
public class MultiImageSelectorFragment extends Fragment {

    private static final int LOADER_ALL = 0;
    private static final int LOADER_CATEGORY = 1;

    private static final int REQUEST_CAMERA = 100;

    private ArrayList<FolderBean> resultFolderBean = new ArrayList<>();

    private RecyclerView recyclerView;
    private Callback callback;

    private ImageAdapter imageAdapter;
    private FolderAdapter folderAdapter;

    private ListPopupWindow folderPopupWindow;

    private TextView categoryText;
    private Button btnPreview;
    // bottom View
    private View popupAnchorView;

    private boolean hasFolderGenerated = false;

    private File tmpFile;
    private String KEY_TEMP_FILE = "key_temp_file";

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            callback = (Callback) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("The Activity must implement MultiImageSelectorFragment.Callback interface...");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_image_selector, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // select limited
        // default chose
 
        // weather use camera
        imageAdapter = new ImageAdapter(getActivity(), options().showCamera, 6, MultiImageSelectorFragment.this);
        imageAdapter.showSelectIndicator(options().selectMode == MultiImageSelectorActivity.MODE_MULTI);
        popupAnchorView = view.findViewById(R.id.footer);

        categoryText = (TextView) view.findViewById(R.id.btnCategory);
        categoryText.setText(R.string.text_folder_all);
        categoryText.setOnClickListener(view1 -> {
            if (folderPopupWindow == null) {
                createPopupFolderList();
            }
            if (folderPopupWindow.isShowing()) {
                folderPopupWindow.dismiss();
            } else {
                folderPopupWindow.show();
                int index = folderAdapter.getSelectIndex();
                index = index == 0 ? index : index - 1;
                folderPopupWindow.getListView().setSelection(index);
            }
        });

        btnPreview = (Button) view.findViewById(R.id.preview);
        if (options().selectedList == null || options().selectedList.size() <= 0) {
            btnPreview.setText(R.string.text_preview);
            btnPreview.setEnabled(false);
        }
        btnPreview.setOnClickListener(view12 -> {

        });

        recyclerView = (RecyclerView) view.findViewById(R.id.grid);
        GridLayoutManager manager = new GridLayoutManager(getActivity(), options().gridColumnSize);
        manager.setSpanCount(options().gridColumnSize);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(imageAdapter);
        folderAdapter = new FolderAdapter(getActivity());
    }

    private void createPopupFolderList() {
        Point point = Tools.getScreenSize();
        int width = point.x;
        int height = (int) (point.y * (4.5f / 8.0f));
        folderPopupWindow = new ListPopupWindow(getActivity());
        folderPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        folderPopupWindow.setAdapter(folderAdapter);
        folderPopupWindow.setContentWidth(width);
        folderPopupWindow.setWidth(width);
        folderPopupWindow.setHeight(height);
        folderPopupWindow.setAnchorView(popupAnchorView);
        folderPopupWindow.setModal(true);
        folderPopupWindow.setOnItemClickListener((adapterView, view, i, l) -> {
            folderAdapter.setSelectIndex(i);
            final int index = i;
            final AdapterView v = adapterView;
            new Handler().postDelayed(() -> {
                folderPopupWindow.dismiss();
                if (index == 0) {
                    getActivity().getSupportLoaderManager().restartLoader(LOADER_ALL, null, loaderCallbacks);
                    categoryText.setText(R.string.text_folder_all);
                    if (options().showCamera) {
                        imageAdapter.setShowCamera(true);
                    } else {
                        imageAdapter.setShowCamera(false);
                    }
                } else {
                    FolderBean folderBean = (FolderBean) v.getAdapter().getItem(index);
                    if (null != folderBean) {
                        imageAdapter.setData(folderBean.imageBeen);
                        categoryText.setText(folderBean.name);
                        if (options().selectedList != null && options().selectedList.size() > 0) {
                            imageAdapter.setDefaultSelected(options().selectedList);
                        }
                    }
                    imageAdapter.setShowCamera(false);
                }
                recyclerView.smoothScrollToPosition(0);
            }, 100);

        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(KEY_TEMP_FILE, tmpFile);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            tmpFile = (File) savedInstanceState.getSerializable(KEY_TEMP_FILE);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //new LoadImageTask().execute();
        getActivity().getSupportLoaderManager().initLoader(LOADER_ALL, null, loaderCallbacks);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CAMERA) {
            if (resultCode == Activity.RESULT_OK) {
                if (tmpFile != null) {
                    if (callback != null) {
                        callback.onCameraShot(tmpFile);
                    }
                }
            } else {
                while (tmpFile != null && tmpFile.exists()) {
                    boolean success = tmpFile.delete();
                    if (success) {
                        tmpFile = null;
                    }
                }
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (folderPopupWindow != null) {
            if (folderPopupWindow.isShowing()) {
                folderPopupWindow.dismiss();
            }
        }
        super.onConfigurationChanged(newConfig);
    }

    public void showCameraAction() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            try {
                tmpFile = FileUtils.createTmpFile(getActivity());
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (tmpFile != null && tmpFile.exists()) {
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tmpFile));
                startActivityForResult(cameraIntent, REQUEST_CAMERA);
            } else {
                TS.show(R.string.msg_photo_error);
            }
        } else {
            TS.show(R.string.msg_no_camera);
        }
    }

    public void selectImageFromGrid(ImageBean image, int mode) {
        if (image != null) {
            if (mode == MultiImageSelectorActivity.MODE_MULTI) {
                if (options().selectedList.contains(image.path)) {
                    options().selectedList.remove(image.path);
                    if (options().selectedList.size() != 0) {
                        btnPreview.setEnabled(true);
                        btnPreview.setText(R.string.text_preview + "(" + options().selectedList.size() + ")");
                    } else {
                        btnPreview.setEnabled(false);
                        btnPreview.setText(R.string.text_preview);
                    }
                    if (callback != null) {
                        callback.onImageUnselected(image.path);
                    }
                } else {
                    if (options().defaultCount == options().selectedList.size()) {
                        TS.show(R.string.msg_amount_limit);
                        return;
                    }

                    options().selectedList.add(image.path);
                    btnPreview.setEnabled(true);
                    btnPreview.setText(R.string.text_preview + "(" + options().selectedList.size() + ")");
                    if (callback != null) {
                        callback.onImageSelected(image.path);
                    }
                }
                imageAdapter.select(image);
            } else if (mode == MultiImageSelectorActivity.MODE_SINGLE) {
                if (callback != null) {
                    callback.onSingleImageSelected(image.path);
                }
            }
        }
    }

    private LoaderManager.LoaderCallbacks<Cursor> loaderCallbacks = new LoaderManager.LoaderCallbacks<Cursor>() {

        private final String[] IMAGE_PROJECTION = {
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATE_ADDED,
                MediaStore.Images.Media.MIME_TYPE,
                MediaStore.Images.Media.SIZE,
                MediaStore.Images.Media._ID};

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            if (id == LOADER_ALL) {
                CursorLoader cursorLoader = new CursorLoader(getActivity(),
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_PROJECTION,
                        IMAGE_PROJECTION[4] + ">0 AND " + IMAGE_PROJECTION[3] + "=? OR " + IMAGE_PROJECTION[3] + "=? ",
                        new String[]{"image/jpeg", "image/png"}, IMAGE_PROJECTION[2] + " DESC");
                return cursorLoader;
            } else if (id == LOADER_CATEGORY) {
                CursorLoader cursorLoader = new CursorLoader(getActivity(),
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_PROJECTION,
                        IMAGE_PROJECTION[4] + ">0 AND " + IMAGE_PROJECTION[0] + " like '%" + args.getString("path") + "%'",
                        null, IMAGE_PROJECTION[2] + " DESC");
                return cursorLoader;
            }

            return null;
        }

        private boolean fileExist(String path) {
            if (!TextUtils.isEmpty(path)) {
                return new File(path).exists();
            }
            return false;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            if (data != null) {
                if (data.getCount() > 0) {
                    List<ImageBean> images = new ArrayList<>();
                    data.moveToFirst();
                    do {
                        String path = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[0]));
                        String name = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[1]));
                        long dateTime = data.getLong(data.getColumnIndexOrThrow(IMAGE_PROJECTION[2]));
                        ImageBean image = null;
                        if (fileExist(path)) {
                            image = new ImageBean(path, name, dateTime);
                            images.add(image);
                        }
                        if (!hasFolderGenerated) {
                            File folderFile = new File(path).getParentFile();
                            if (folderFile != null && folderFile.exists()) {
                                String fp = folderFile.getAbsolutePath();
                                FolderBean f = getFolderByPath(fp);
                                if (f == null) {
                                    FolderBean folderBean = new FolderBean();
                                    folderBean.name = folderFile.getName();
                                    folderBean.path = fp;
                                    folderBean.cover = image;
                                    List<ImageBean> imageList = new ArrayList<>();
                                    imageList.add(image);
                                    folderBean.imageBeen = imageList;
                                    resultFolderBean.add(folderBean);
                                } else {
                                    f.imageBeen.add(image);
                                }
                            }
                        }

                    } while (data.moveToNext());

                    imageAdapter.setData(images);
                    if (options().selectedList != null && options().selectedList.size() > 0) {
                        imageAdapter.setDefaultSelected(options().selectedList);
                    }

                    if (!hasFolderGenerated) {
                        folderAdapter.setData(resultFolderBean);
                        hasFolderGenerated = true;
                    }

                }
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    };

    private FolderBean getFolderByPath(String path) {
        if (resultFolderBean != null) {
            for (FolderBean folderBean : resultFolderBean) {
                if (TextUtils.equals(folderBean.path, path)) {
                    return folderBean;
                }
            }
        }
        return null;
    }

    public interface Callback {
        void onSingleImageSelected(String path);

        void onImageSelected(String path);

        void onImageUnselected(String path);

        void onCameraShot(File imageFile);
    }
}
