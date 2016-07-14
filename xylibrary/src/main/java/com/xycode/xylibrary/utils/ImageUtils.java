package com.xycode.xylibrary.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.widget.ImageView;

import com.facebook.binaryresource.BinaryResource;
import com.facebook.binaryresource.FileBinaryResource;
import com.facebook.cache.common.CacheKey;
import com.facebook.common.executors.CallerThreadExecutor;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.DraweeView;
import com.facebook.imagepipeline.cache.DefaultCacheKeyFactory;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.core.ImagePipelineFactory;
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.xycode.xylibrary.utils.Tools.checkFile;

/**
 * Created by XY on 2016/7/12.
 */
public class ImageUtils {

    public static void scanPhotoPath(Context context, String filePath) {
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(new File(filePath));
        intent.setData(uri);
        context.sendBroadcast(intent);
    }

    /**
     * 把图片格式转换为流格式，可以存到BLOB类型中
     *
     * @param bitmap       图片
     * @param imageQuality 压缩质量 <100
     * @return 流
     */
    public static byte[] bitmapToBytes(Bitmap bitmap, int imageQuality) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        imageQuality = imageQuality > 100 ? 100 : imageQuality < 1 ? 1 : imageQuality;
        bitmap.compress(Bitmap.CompressFormat.JPEG, imageQuality, outputStream);
        return outputStream.toByteArray();
    }

    public static ByteArrayOutputStream bitmapToStream(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        return outputStream;
    }

    /**
     * 获取图片的旋转角度
     */
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    public static ByteArrayOutputStream getByteArrayOutputStreamFromFile(File file) {
        try {
            byte[] bytes = new byte[1024];
            int length;
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            FileInputStream fileInputStream = new FileInputStream(file);
            while ((length = fileInputStream.read(bytes)) > 0) {
                os.write(bytes, 0, length);
            }
            os.flush();
            os.close();
            fileInputStream.close();
            return os;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Bitmap getBitmapFromFile(File file) {
        try {
            byte[] bytes = new byte[1024];
            int length;
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            FileInputStream fileInputStream = new FileInputStream(file);
            while ((length = fileInputStream.read(bytes)) > 0) {
                os.write(bytes, 0, length);
            }
            os.flush();
            os.close();
            fileInputStream.close();
            return getBitmapFromBytes(os.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Bitmap getBitmapFromBytes(byte[] bytes) {
        try {
            BitmapFactory.Options opt = new BitmapFactory.Options();
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, opt);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ByteArrayOutputStream compressBitmapToStream(Bitmap bitmap, int jpegQuality) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, jpegQuality, baos);
        L.e("------质量-- after " + "  ------" + baos.toByteArray().length / 1024f);
        return baos;
    }

    /**
     * 获取合适的Bitmap平时获取Bitmap就用这个方法吧.
     *
     * @param data      byte[]数组.
     * @param targetPix 模板宽或者高的大小.
     *                  //     * @param width   是否是宽度
     * @return
     */
    public static Bitmap resizeToBitmap(byte[] data, int targetPix) {
        BitmapFactory.Options options = null;

        if (targetPix > 0) {
            BitmapFactory.Options info = new BitmapFactory.Options();
            //这里设置true的时候，decode时候Bitmap返回的为空，
            //将图片宽高读取放在Options里.
            info.inJustDecodeBounds = true;
//            info.inPreferredConfig = Bitmap.Config.RGB_565;
//            info.inSampleSize = 16;
            BitmapFactory.decodeByteArray(data, 0, data.length, info);

            int dim = Math.max(info.outWidth, info.outHeight);
            L.e("pic Width: " + dim + "pic outHeight: " + info.outHeight);
//            if (!width) dim = Math.max(dim, info.outHeight);
//            int ssize = sampleSize(dim, targetPix);
            int ssize = (targetPix > dim ? 1 : dim / targetPix);
            L.d("pic Width: " + ssize);

            options = new BitmapFactory.Options();
            options.inSampleSize = ssize;
//            options.inPreferredConfig = Bitmap.Config.RGB_565;
        }

        Bitmap bm = null;
        try {
            bm = BitmapFactory.decodeByteArray(data, 0, data.length, options);
        } catch (OutOfMemoryError e) {

            e.printStackTrace();
        }
        return bm;
    }

    public static Bitmap resizeToBitmap(int minSide, byte[] data) {
        BitmapFactory.Options options = null;
        BitmapFactory.Options info = new BitmapFactory.Options();
        //这里设置true的时候，decode时候Bitmap返回的为空，
        //将图片宽高读取放在Options里.
        info.inJustDecodeBounds = true;
//        info.inPreferredConfig = Bitmap.Config.RGB_565;
//            info.inSampleSize = 16;
        BitmapFactory.decodeByteArray(data, 0, data.length, info);

        int min = Math.min(info.outWidth, info.outHeight);
        int max = Math.max(info.outWidth, info.outHeight);
        L.e("pic Width: " + info.outWidth + "pic outHeight: " + info.outHeight);

        int scaleSize = (minSide > min ? 1 : (minSide / min));
        L.d("pic scaleSize: " + scaleSize);

        options = new BitmapFactory.Options();
        options.inSampleSize = scaleSize;
//        options.inPreferredConfig = Bitmap.Config.RGB_565;

        Bitmap bm = null;
        try {
            bm = BitmapFactory.decodeByteArray(data, 0, data.length, options);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
        return bm;
    }

    public static Bitmap resizeToBitmap(String filePath, int photoSide, int miniSide) {

        BitmapFactory.Options info = new BitmapFactory.Options();
        //这里设置true的时候，decode时候Bitmap返回的为空，
        //将图片宽高读取放在Options里.
        info.inJustDecodeBounds = true;
//        info.inPreferredConfig = Bitmap.Config.RGB_565;
        BitmapFactory.decodeFile(filePath, info);
        int maxSide = Math.max(info.outWidth, info.outHeight);
        int minSide = Math.min(info.outWidth, info.outHeight);
        L.d("sideLength outWidth:" + "(" + info.outWidth + ") outHeight:" + "(" + info.outHeight + ") ...:" + (1.0 * maxSide) / photoSide);
        int ssize = photoSide > maxSide ? 1 : (int) (Math.floor((1.0 * maxSide) / photoSide));
        int nowMinSide = minSide / ssize;
        if (ssize > 1 && nowMinSide <= miniSide) {
            ssize = (int) Math.floor(ssize * ((1.0f * nowMinSide) / miniSide));
            if (ssize < 1) {
                ssize = 1;
            }
        }
        info = new BitmapFactory.Options();
//        info.inPreferredConfig = Bitmap.Config.RGB_565;
        info.inSampleSize = ssize;
        info.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(filePath, info);

        Matrix matrix = new Matrix();
        int h = bitmap.getHeight(), w = bitmap.getWidth();
        int max = photoSide;
        int min = miniSide;
        int newMaxSide = Math.max(h, w);
        int newMinSide = Math.min(h, w);
        L.e("sideLength w:" + "(" + w + ") h:" + "(" + h + ") scaleSize:" + ssize);
        if (max >= newMaxSide || min >= newMinSide) {
        } else {
            float ratioMax = (1.0f * max / newMaxSide);
            float ratioMin = (1.0f * min / newMinSide);
            float ratio = Math.max(ratioMax, ratioMin);
            matrix.postScale(ratio, ratio);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
            L.e("sideLength w:" + "(" + bitmap.getWidth() + ") h:" + "(" + bitmap.getHeight() + ")");
        }

        return bitmap;
    }

    public static boolean isImageInFrescoCache(Uri loadUri) {
        if (loadUri == null) {
            return false;
        }
        CacheKey cacheKey = DefaultCacheKeyFactory.getInstance().getEncodedCacheKey(ImageRequest.fromUri(loadUri));
        return ImagePipelineFactory.getInstance().getMainDiskStorageCache().hasKey(cacheKey) || ImagePipelineFactory.getInstance().getSmallImageDiskStorageCache().hasKey(cacheKey);
    }

    //return file or null
    public static File getCachedImageOnFresco(Uri loadUri) {
        File localFile = null;
        if (loadUri != null) {
            CacheKey cacheKey = DefaultCacheKeyFactory.getInstance().getEncodedCacheKey(ImageRequest.fromUri(loadUri));
            if (ImagePipelineFactory.getInstance().getMainDiskStorageCache().hasKey(cacheKey)) {
                BinaryResource resource = ImagePipelineFactory.getInstance().getMainDiskStorageCache().getResource(cacheKey);
                localFile = ((FileBinaryResource) resource).getFile();
            } else if (ImagePipelineFactory.getInstance().getSmallImageDiskStorageCache().hasKey(cacheKey)) {
                BinaryResource resource = ImagePipelineFactory.getInstance().getSmallImageDiskStorageCache().getResource(cacheKey);
                localFile = ((FileBinaryResource) resource).getFile();
            }
        }
        return localFile;

    }

    public static void loadBitmapFromFresco(Context context, Uri uri, final IGetFrescoBitmap iGetFrescoBitmap) {
        File localFile = getCachedImageOnFresco(uri);
        if (localFile != null) {
            iGetFrescoBitmap.afterGotBitmap(getBitmapFromFile(localFile));
        } else {
            loadBitmapFromFrescoNet(context, uri, iGetFrescoBitmap);
        }
    }

    public static void loadBitmapFromFrescoNet(Context context, Uri uri, final IGetFrescoBitmap iGetFrescoBitmap) {
        ImageRequest imageRequest = ImageRequestBuilder.newBuilderWithSource(uri).setAutoRotateEnabled(true).build();
        ImagePipeline pipeline = Fresco.getImagePipeline();
        DataSource<CloseableReference<CloseableImage>> dataSource = pipeline.fetchDecodedImage(imageRequest, context);
        dataSource.subscribe(new BaseBitmapDataSubscriber() {
            @Override
            protected void onNewResultImpl(Bitmap bitmap) {
                Bitmap bmp = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                iGetFrescoBitmap.afterGotBitmap(bmp);
            }

            @Override
            protected void onFailureImpl(DataSource<CloseableReference<CloseableImage>> dataSource) {
                iGetFrescoBitmap.afterGotBitmap(null);
        }
        }, CallerThreadExecutor.getInstance());
    }

    public static boolean saveBitmapToFile(Context context, File file, Bitmap bitmap) {
        File dir = new File(file.getParent());
        if (!dir.exists()) {
            dir.mkdirs();// 创建照片的存储目录
        }
        try {
            FileOutputStream outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
            scanPhotoPath(context, file.getPath());
            L.d("saveBitmapToFile localFile" + "(" + file.getPath() + ") -- (" + file.length() / 1024 + "K)");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public interface IGetFrescoBitmap {
        void afterGotBitmap(Bitmap bitmap);
    }

    public interface IGetFrescoImageInfo {
        void afterGotImageInfo(ImageInfo imageInfo);

    }


}
