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
import android.widget.Toast;

import com.xycode.xylibrary.R;
import com.xycode.xylibrary.uiKit.imageSelector.adapter.FolderAdapter;
import com.xycode.xylibrary.uiKit.imageSelector.adapter.GridAdapter;
import com.xycode.xylibrary.uiKit.imageSelector.bean.FolderBean;
import com.xycode.xylibrary.uiKit.imageSelector.bean.ImageBean;
import com.xycode.xylibrary.uiKit.imageSelector.utils.FileUtils;
import com.xycode.xylibrary.utils.Tools;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.xycode.xylibrary.uiKit.imageSelector.ImageSelectorOptions.options;


/**
 * 图片选择Fragment
 * Created by Nereo on 2015/4/7.
 */
public class MultiImageSelectorFragment extends Fragment {
    /**
     * 单选
     */
    public static final int MODE_SINGLE = 0;
    /**
     * 多选
     */
    public static final int MODE_MULTI = 1;
    // 不同loader定义
    private static final int LOADER_ALL = 0;
    private static final int LOADER_CATEGORY = 1;
    // 请求加载系统照相机
    private static final int REQUEST_CAMERA = 100;


    // 结果数据
    private ArrayList<String> resultList = new ArrayList<>();
    // 文件夹数据
    private ArrayList<FolderBean> resultFolderBean = new ArrayList<>();

    // 图片Grid
    private RecyclerView recyclerView;
    private Callback callback;

    private GridAdapter imageAdapter;
    private FolderAdapter folderAdapter;

    private ListPopupWindow folderPopupWindow;

    // 类别
    private TextView categoryText;
    // 预览按钮
    private Button btnPreview;
    // 底部View
    private View popupAnchorView;

    private int desireImageCount;

    private boolean hasFolderGened = false;
    private boolean isShowCamera = false;

    private File mTmpFile;
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
        // 选择图片数量
        desireImageCount = getArguments().getInt(MultiImageSelectorActivity.EXTRA_SELECT_COUNT);
        // 图片选择模式
        final int mode = getArguments().getInt(MultiImageSelectorActivity.EXTRA_SELECT_MODE);
        // 默认选择
        if (mode == MODE_MULTI) {
            ArrayList<String> tmp = getArguments().getStringArrayList(MultiImageSelectorActivity.EXTRA_DEFAULT_SELECTED_LIST);
            if (tmp != null && tmp.size() > 0) {
                resultList = tmp;
            }
        }
        // 是否显示照相机
        isShowCamera = getArguments().getBoolean(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, true);
        imageAdapter = new GridAdapter(getActivity(), isShowCamera, 6, MultiImageSelectorFragment.this);
        imageAdapter.showSelectIndicator(mode == MODE_MULTI);
        popupAnchorView = view.findViewById(R.id.footer);

        categoryText = (TextView) view.findViewById(R.id.btnCategory);
        categoryText.setText(options().textAllFolder);
        categoryText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
            }
        });

        btnPreview = (Button) view.findViewById(R.id.preview);
        // 初始化，按钮状态初始化
        if (resultList == null || resultList.size() <= 0) {
            btnPreview.setText(options().textPreview);
            btnPreview.setEnabled(false);
        }
        btnPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        recyclerView = (RecyclerView) view.findViewById(R.id.grid);
        GridLayoutManager manager = new GridLayoutManager(getActivity(), 3);
        manager.setSpanCount(3);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(imageAdapter);
        folderAdapter = new FolderAdapter(getActivity());
    }

    /**
     * 创建弹出的ListView
     */
    private void createPopupFolderList() {
        Point point = Tools.getScreenSize(getActivity());
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
        folderPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                folderAdapter.setSelectIndex(i);
                final int index = i;
                final AdapterView v = adapterView;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        folderPopupWindow.dismiss();
                        if (index == 0) {
                            getActivity().getSupportLoaderManager().restartLoader(LOADER_ALL, null, loaderCallbacks);
                            categoryText.setText(options().textAllFolder);
                            if (isShowCamera) {
                                imageAdapter.setShowCamera(true);
                            } else {
                                imageAdapter.setShowCamera(false);
                            }
                        } else {
                            FolderBean folderBean = (FolderBean) v.getAdapter().getItem(index);
                            if (null != folderBean) {
                                imageAdapter.setData(folderBean.imageBeen);
                                categoryText.setText(folderBean.name);
                                // 设定默认选择
                                if (resultList != null && resultList.size() > 0) {
                                    imageAdapter.setDefaultSelected(resultList);
                                }
                            }
                            imageAdapter.setShowCamera(false);
                        }
                        // 滑动到最初始位置
                        recyclerView.smoothScrollToPosition(0);
                    }
                }, 100);

            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(KEY_TEMP_FILE, mTmpFile);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            mTmpFile = (File) savedInstanceState.getSerializable(KEY_TEMP_FILE);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // 首次加载所有图片
        //new LoadImageTask().execute();
        getActivity().getSupportLoaderManager().initLoader(LOADER_ALL, null, loaderCallbacks);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 相机拍照完成后，返回图片路径
        if (requestCode == REQUEST_CAMERA) {
            if (resultCode == Activity.RESULT_OK) {
                if (mTmpFile != null) {
                    if (callback != null) {
                        callback.onCameraShot(mTmpFile);
                    }
                }
            } else {
                while (mTmpFile != null && mTmpFile.exists()) {
                    boolean success = mTmpFile.delete();
                    if (success) {
                        mTmpFile = null;
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
                mTmpFile = FileUtils.createTmpFile(getActivity());
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (mTmpFile != null && mTmpFile.exists()) {
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mTmpFile));
                startActivityForResult(cameraIntent, REQUEST_CAMERA);
            } else {
                Toast.makeText(getActivity(), "图片错误", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getActivity(),options().msgNoCamera, Toast.LENGTH_SHORT).show();
        }
    }

    public void selectImageFromGrid(ImageBean image, int mode) {
        if (image != null) {
            // 多选模式
            if (mode == MODE_MULTI) {
                if (resultList.contains(image.path)) {
                    resultList.remove(image.path);
                    if (resultList.size() != 0) {
                        btnPreview.setEnabled(true);
                        btnPreview.setText(options().textPreview + "(" + resultList.size() + ")");
                    } else {
                        btnPreview.setEnabled(false);
                        btnPreview.setText(options().textPreview);
                    }
                    if (callback != null) {
                        callback.onImageUnselected(image.path);
                    }
                } else {
                    if (desireImageCount == resultList.size()) {
                        Toast.makeText(getActivity(), options().msgAmountLimit, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    resultList.add(image.path);
                    btnPreview.setEnabled(true);
                    btnPreview.setText(options().textPreview + "(" + resultList.size() + ")");
                    if (callback != null) {
                        callback.onImageSelected(image.path);
                    }
                }
                imageAdapter.select(image);
            } else if (mode == MODE_SINGLE) {
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
                        if (!hasFolderGened) {
                            // 获取文件夹名称
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
                    // 设定默认选择
                    if (resultList != null && resultList.size() > 0) {
                        imageAdapter.setDefaultSelected(resultList);
                    }

                    if (!hasFolderGened) {
                        folderAdapter.setData(resultFolderBean);
                        hasFolderGened = true;
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

    /**
     * 回调接口
     */
    public interface Callback {
        void onSingleImageSelected(String path);

        void onImageSelected(String path);

        void onImageUnselected(String path);

        void onCameraShot(File imageFile);
    }
}
