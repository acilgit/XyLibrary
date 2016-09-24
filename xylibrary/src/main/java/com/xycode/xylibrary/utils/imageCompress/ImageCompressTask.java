package com.xycode.xylibrary.utils.imageCompress;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;

import com.xycode.xylibrary.R;
import com.xycode.xylibrary.utils.ImageUtils;
import com.xycode.xylibrary.utils.L;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Administrator on 2016/9/23 0023.
 */
public class ImageCompressTask implements Runnable {
    public interface CompressListener {
        void done(List<File> files, List<File> fails, boolean allsuccess);
        void error(Exception e);
    }

    String JPG = ".jpg";
    String PNG = ".png";
    File parent;
    List<File> mFiles = new ArrayList<>();
    private static int maxSide = 1600;

    public static void setSides(int minSide, int maxSide) {
        if (minSide < 0) {
            return;
        } else {
            ImageCompressTask.minSide = minSide;
        }
        if (maxSide < 0) {
            return;
        } else {
            ImageCompressTask.maxSide = maxSide;
        }
    }

    private static final String defulat = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "xyLib";
    private Context mContext;
    private static int minSide = 512;
    private int number;
    private int success;
    private int fail;
    private CompressListener mListener;

    public ImageCompressTask(@NonNull Context context, @NonNull List<File> files, @NonNull String path, @NonNull CompressListener listener) {
        init(files, path, listener, context);
    }

    public ImageCompressTask(@NonNull List<String> files, @NonNull Context context, @NonNull String path, @NonNull CompressListener listener) {
        if (files == null) {
            throw new NullPointerException("参数不能为空");
        }
        List<File> file = new ArrayList<>();
        for (String s : files) {
            file.add(new File(s));
        }
        init(file, path, listener, context);
    }

    public ImageCompressTask(@NonNull Context context, @NonNull String files, @NonNull String path, @NonNull CompressListener listener) {
        if (files == null) {
            throw new NullPointerException("参数不能为空");
        }
        List<File> file = new ArrayList<>();
        file.add(new File(files));
        init(file, path, listener, context);
    }

    public ImageCompressTask(@NonNull Context context, @NonNull File files, @NonNull String path, @NonNull CompressListener listener) {
        if (files == null) {
            throw new NullPointerException("参数不能为空");
        }
        List<File> file = new ArrayList<>();
        file.add(files);
        init(file, path, listener, context);
    }

    public ImageCompressTask(@NonNull Context context, @NonNull Uri files, @NonNull String path, @NonNull CompressListener listener) {
        if (files == null) {
            throw new NullPointerException("参数不能为空");
        }
        List<File> file = new ArrayList<>();
        try {
            file.add(new File(new URI(files.toString())));
        } catch (URISyntaxException e) {
            e.printStackTrace();
            throw new NullPointerException(context.getString(R.string.file_uri_error));
        }
        init(file, path, listener, context);
    }

    public ImageCompressTask(@NonNull Context context, @NonNull List<Uri> files, @NonNull CompressListener listener, @NonNull String path) {
        List<File> file = new ArrayList<>();
        file.clear();
        if (files != null && files.size() > 0) {
            for (Uri u : files) {
                try {
                    file.add(new File(new URI(u.toString())));
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                    throw new NullPointerException(context.getString(R.string.file_uri_error));
                }
            }
        } else {
            throw new NullPointerException("参数不能为空");
        }
        init(file, path, listener, context);
    }

    /**
     * @param files    files to compress
     * @param paths    cache file parent path
     * @param listener compress listener
     * @param context  - -
     */
    private void init(List<File> files, String paths, CompressListener listener, Context context) {
        if (files == null || listener == null || context == null) {
            throw new NullPointerException("参数不能为空");
        }
        mContext = context;
        mFiles.clear();
        mFiles.addAll(files);
        number = mFiles.size();
        success = 0;
        fail = 0;
        if (paths == null || paths.isEmpty()) {
            parent = new File(defulat);
        } else {
            parent = new File(paths);
        }
        mListener = listener;
    }

    public static void delecCache(File file) {
        if (file != null && file.exists()) {
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                if (files != null) {
                    for (File f : files
                            ) {
                        if (file.isDirectory()) {
                            delecCache(file);
                        } else {
                            f.delete();
                        }
                    }
                }
            } else {
                file.delete();
            }
        }
    }

    @Override
    public void run() {
        if (mListener == null) {
            throw new NullPointerException(mContext.getString(R.string.compress_listener_not_null));
        }
        if (parent == null) {
            mListener.error(new NullPointerException(mContext.getString(R.string.compress_parent_path_can_not_be_null)));
        }
        if (!parent.exists()) {
            if (parent.isDirectory()) {
                parent.mkdirs();
            } else {
                mListener.error(new NullPointerException(mContext.getString(R.string.father_can_not_be_a_file)));
            }
        }
        List<File> success = new ArrayList<>();
        List<File> fail = new ArrayList<>();
        for (File file : mFiles) {
            if (file.exists()) {
                File local = new File(parent.getAbsolutePath() + File.separator + UUID.randomUUID() + JPG);
                boolean flag = ImageUtils.saveBitmapToFile(mContext, local, ImageUtils.resizeToBitmap(file.getPath(), maxSide, minSide));
                if (flag) {
                    success.add(local);
                } else {
                    fail.add(file);
                }
            } else {
                L.e(mContext.getString(R.string.file_no_exits));
                fail.add(file);
            }
        }
        mListener.done(success, fail, fail.size() < 1 && (success.size() + fail.size() == mFiles.size()));
    }
}
