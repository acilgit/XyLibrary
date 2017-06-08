package com.xycode.xylibrary.utils.imageCompress;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;

import com.xycode.xylibrary.R;
import com.xycode.xylibrary.utils.ImageUtils;
import com.xycode.xylibrary.utils.LogUtil.L;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Administrator on 2016/9/23 0023.
 */
@Deprecated
public class ImageCompressTask implements Runnable {
    public interface CompressListener {
        void done(List<File> files, List<File> fails, boolean allSuccess);

        void error(Exception e);
    }

    String JPG = ".jpg";
    String PNG = ".png";
    File parent;
    List<File> files = new ArrayList<>();
    private static int maxSide = 1600;
    private static int minSide = 512;
    private static int imageQuality = 85;

    public static void setSides(int minSide, int maxSide, int imageQuality) {
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

    private static final String defaultPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "xyLib" + File.separator;
    private Context context;
    private int number;
    private int success;
    private int fail;
    private CompressListener listener;

    public ImageCompressTask(@NonNull Context context, @NonNull List<File> files, @NonNull String path, @NonNull CompressListener listener) {
        init(files, path, listener, context);
    }

    public ImageCompressTask(@NonNull List<String> files, @NonNull Context context, @NonNull String path, @NonNull CompressListener listener) {
        if (files == null) {
            throw new NullPointerException(context.getString(R.string.params_can_not_be_null));
        }
        List<File> file = new ArrayList<>();
        for (String s : files) {
            file.add(new File(s));
        }
        init(file, path, listener, context);
    }

    public ImageCompressTask(@NonNull Context context, @NonNull String files, @NonNull String path, @NonNull CompressListener listener) {
        if (files == null) {
            throw new NullPointerException(context.getString(R.string.params_can_not_be_null));
        }
        List<File> file = new ArrayList<>();
        file.add(new File(files));
        init(file, path, listener, context);
    }

    public ImageCompressTask(@NonNull Context context, @NonNull File files, @NonNull String path, @NonNull CompressListener listener) {
        if (files == null) {
            throw new NullPointerException(context.getString(R.string.params_can_not_be_null));
        }
        List<File> file = new ArrayList<>();
        file.add(files);
        init(file, path, listener, context);
    }

    public ImageCompressTask(@NonNull Context context, @NonNull Uri files, @NonNull String path, @NonNull CompressListener listener) {
        if (files == null) {
            throw new NullPointerException(context.getString(R.string.params_can_not_be_null));
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
            throw new NullPointerException(context.getString(R.string.params_can_not_be_null));
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
            throw new NullPointerException(context.getString(R.string.params_can_not_be_null));
        }
        this.context = context;
        this.files.clear();
        this.files.addAll(files);
        number = this.files.size();
        success = 0;
        fail = 0;
        if (paths == null || paths.isEmpty()) {
            parent = new File(defaultPath);
        } else {
            parent = new File(paths);
        }
        this.listener = listener;
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
        if (listener == null) {
            throw new NullPointerException(context.getString(R.string.compress_listener_not_null));
        }
        if (parent == null) {
            throw new NullPointerException(context.getString(R.string.compress_parent_path_can_not_be_null));
        }
        if(!parent.exists()){
            parent.mkdirs();
            if(!parent.isDirectory()){
                throw new NullPointerException(context.getString(R.string.father_can_not_be_a_file));
            }else {
                if(!parent.exists()){
                    parent.mkdirs();
                }
            }
        }
        List<File> success = new ArrayList<>();
        List<File> fail = new ArrayList<>();
        for (File file : files) {
            if (file.exists()) {
                File local = new File(parent.getAbsolutePath() + File.separator + UUID.randomUUID() + JPG);
                boolean flag = ImageUtils.compressBitmapFromPathToFile(file.getPath(), local, imageQuality, maxSide, minSide);
                if (flag) {
                    success.add(local);
                } else {
                    fail.add(file);
                }
            } else {
                L.e(context.getString(R.string.file_no_exits));
                fail.add(file);
            }
        }
        listener.done(success, fail, fail.size() < 1 && (success.size() + fail.size() == files.size()));
    }
}
