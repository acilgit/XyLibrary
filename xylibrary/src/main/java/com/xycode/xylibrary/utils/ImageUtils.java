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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.xycode.xylibrary.utils.Tools.checkFile;
import static com.xycode.xylibrary.utils.Tools.getScreenSize;

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

    public static ByteArrayOutputStream getBytesOutputStreamForFile(File file) {
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

    /*public static ByteArrayOutputStream getBytesOutputStreamFromPhotoFile(File file ) {
        try {
            BitmapFactory.Options info = new BitmapFactory.Options();
            //这里设置true的时候，decode时候Bitmap返回的为空，
            //将图片宽高读取放在Options里.
            info.inJustDecodeBounds = true;
//            info.inPreferredConfig = Bitmap.Config.RGB_565;
            BitmapFactory.decodeFile(file.getPath(), info);
            int maxSide = Math.max(info.outWidth, info.outHeight);
            int ssize = photoSide > maxSide ? 1 : (int) (Math.floor(1.0 * maxSide) / photoSide);
            info = new BitmapFactory.Options();
//            info.inPreferredConfig = Bitmap.Config.RGB_565;
            info.inSampleSize = ssize;
            info.inJustDecodeBounds = false;
            Bitmap bitmap = BitmapFactory.decodeFile(file.getPath(), info);
//            byte[] bytes = new byte[1024];
            ByteArrayOutputStream os = new ByteArrayOutputStream();

            int quality = Def.JPEG_QUALITY;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, os);
            L.e("------质量-- after " + "  ------" + os.toByteArray().length / 1024f);
            bitmap.recycle();
            return os;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }*/

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


getBitmapFromFrescoUri(context, null, new IGetFrescoBitmap() {
    @Override
    public void afterGotBitmap(Bitmap bitmap) {
        ivPhoto.setBitmap(bitmap);
    }
});
    }

    public static void getBitmapFromFrescoUri(Context context, Uri uri, final IGetFrescoBitmap iGetFrescoBitmap) {
        ImageRequest imageRequest = ImageRequestBuilder.newBuilderWithSource(uri)
                .setAutoRotateEnabled(true)
                .build();
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

    public static void setLocalBitmapFromFrescoUriInUI(final Activity context, Uri uri, final IGetFrescoBitmap iGetFrescoBitmap) {
        int side = getScreenSize(context).y;
        ImageRequest imageRequest = ImageRequestBuilder.newBuilderWithSource(uri)
                .setAutoRotateEnabled(true)
                .setResizeOptions(new ResizeOptions(side, side)).build();
        ImagePipeline pipeline = Fresco.getImagePipeline();
        DataSource<CloseableReference<CloseableImage>> dataSource = pipeline.fetchDecodedImage(imageRequest, context);
        dataSource.subscribe(new BaseBitmapDataSubscriber() {
            @Override
            protected void onNewResultImpl(final Bitmap bitmap) {
                final Bitmap bmp = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        iGetFrescoBitmap.afterGotBitmap(bmp);
                    }
                });
            }

            @Override
            protected void onFailureImpl(DataSource<CloseableReference<CloseableImage>> dataSource) {
                L.e("onFailureImpl" + "(" + ")");
//                iGetFrescoBitmap.afterGotBitmap(null);
            }
        }, CallerThreadExecutor.getInstance());
    }

    public static void setNetFrescoToImageView(final Activity context, String cacheFolder, final ImageView imageView, String netUri) {
        setNetFrescoToImageView(context, cacheFolder, imageView, netUri, ImageRequest.ImageType.DEFAULT, null, null);
    }

    public static void setNetFrescoToImageView(final Activity context, String cacheFolder, final ImageView imageView, String netUri, final IGetFrescoImageInfo iGetFrescoImageInfo, final IGetFrescoBitmap iGetFrescoBitmap) {
        setNetFrescoToImageView(context, cacheFolder, imageView, netUri, ImageRequest.ImageType.DEFAULT, iGetFrescoImageInfo, iGetFrescoBitmap);
    }

    public static void setNetFrescoToImageView(final Activity context, final String cacheFolder, final ImageView imageView, String netUri, final ImageRequest.ImageType imageType, final IGetFrescoImageInfo iGetFrescoImageInfo, final IGetFrescoBitmap iGetFrescoBitmap) {
        if (netUri.isEmpty()) {
            return;
        }
        final File localFile = checkFile(cacheFolder, netUri);
        final Uri uri;

//        L.e("setNetFrescoToImageView "+"("+localFile.getName()+")");
        // 从本地显示
        if (localFile.exists()) {
            uri = Uri.parse("file://" + localFile.getAbsolutePath());
//            L.e("localFile exists()"+"("+uri.getPath()+")");
            if (imageView instanceof DraweeView) {
                ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
//                        .setLocalThumbnailPreviewsEnabled(true)
                        .setProgressiveRenderingEnabled(true)
//                        .setImageType(ImageRequest.ImageType.DEFAULT)
                        .build();
                DraweeController controller = Fresco.newDraweeControllerBuilder()
                        .setImageRequest(request)
                        .setControllerListener(new ControllerListener<ImageInfo>() {
                            boolean showFinalImageInfo = true;

                            @Override
                            public void onSubmit(String id, Object callerContext) {
//                                L.e("onSubmit 1");
                            }

                            @Override
                            public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
                                if (showFinalImageInfo && iGetFrescoImageInfo != null) {
                                    iGetFrescoImageInfo.afterGotImageInfo(imageInfo);
                                }
//                                L.e("onFinalImageSet 1");
                            }

                            @Override
                            public void onIntermediateImageSet(String id, ImageInfo imageInfo) {
                                showFinalImageInfo = false;
                                if (iGetFrescoImageInfo != null) {
                                    iGetFrescoImageInfo.afterGotImageInfo(imageInfo);
                                }
                            }

                            @Override
                            public void onIntermediateImageFailed(String id, Throwable throwable) {

                            }

                            @Override
                            public void onFailure(String id, Throwable throwable) {

                            }

                            @Override
                            public void onRelease(String id) {

                            }
                        }).setOldController(((DraweeView) imageView).getController()).build();
                ((DraweeView) imageView).setController(controller);
//                L.e("imageView.setImageURI" + "(" + "Local)");
                return;
            } else if (imageView != null) {
                ByteArrayOutputStream os = getBytesOutputStreamForFile(localFile);
                Bitmap bmp = getBitmapFromBytes(os.toByteArray());
                imageView.setImageBitmap(bmp);
                if (iGetFrescoBitmap != null) {
                    iGetFrescoBitmap.afterGotBitmap(((BitmapDrawable) imageView.getDrawable()).getBitmap());
                }
//                getNetBmpFromFresco(context, imageView, localFile, uri, iGetFrescoBitmap);
            } else {
                ByteArrayOutputStream os = getBytesOutputStreamForFile(localFile);
                Bitmap bmp = getBitmapFromBytes(os.toByteArray());
                if (iGetFrescoBitmap != null) {
                    iGetFrescoBitmap.afterGotBitmap(bmp);
                }
            }
        } else {
            // 从网络显示并下载
            uri = Uri.parse(netUri);
//            L.e("localFile not exists()" + "(" + netUri + ")");

            if (imageView instanceof DraweeView) {
                DraweeController controller = Fresco.newDraweeControllerBuilder()
                        .setControllerListener(new ControllerListener<ImageInfo>() {
                            boolean showFinalImageInfo = true;

                            @Override
                            public void onSubmit(String id, Object callerContext) {
                            }

                            @Override
                            public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
                                if (showFinalImageInfo && iGetFrescoImageInfo != null) {
                                    iGetFrescoImageInfo.afterGotImageInfo(imageInfo);
                                }
                                getNetBmpFromFresco(localFile, cacheFolder, uri);
                            }

                            @Override
                            public void onIntermediateImageSet(String id, ImageInfo imageInfo) {
                                showFinalImageInfo = false;
                                if (iGetFrescoImageInfo != null) {
                                    iGetFrescoImageInfo.afterGotImageInfo(imageInfo);
                                }
                            }

                            @Override
                            public void onIntermediateImageFailed(String id, Throwable throwable) {

                            }

                            @Override
                            public void onFailure(String id, Throwable throwable) {

                            }

                            @Override
                            public void onRelease(String id) {

                            }
                        }).setLowResImageRequest(ImageRequest.fromUri(Uri.parse(netUri + "p")))
                        .setImageRequest(ImageRequest.fromUri(uri))
                        .setOldController(((DraweeView) imageView).getController())
                        .build();
                ((DraweeView) imageView).setController(controller);
            } else {
                getNetBmpFromFresco(context, cacheFolder, imageView, localFile, uri, iGetFrescoBitmap);
            }
        }
    }

    private static void getNetBmpFromFresco(final File localFile, String cacheFolder, Uri uri) {
        final File bmpFile = getCachedImageOnFresco(uri);
        if (bmpFile == null) {
            return;
        }
        if (!localFile.exists()) {
            File dir = new File(cacheFolder);
            if (!dir.exists()) {
                dir.mkdirs();// 创建照片的存储目录
            }
            try {
                FileInputStream inputStream = new FileInputStream(bmpFile);
                FileOutputStream outputStream = new FileOutputStream(localFile);
                byte bt[] = new byte[1024];
                int c;
                while ((c = inputStream.read(bt)) > 0) {
                    outputStream.write(bt, 0, c); //将内容写到新文件当中
                }
                inputStream.close();
                outputStream.flush();
                outputStream.close();
                L.e("localFile" + "(" + localFile.getPath() + ") -- (" + localFile.length() / 1024 + "K)");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void getNetBmpFromFresco(final Activity context, final String cacheFolder, final ImageView imageView, final File localFile, final Uri uri, final IGetFrescoBitmap iGetFrescoBitmap) {
        ImageRequest imageRequest = ImageRequestBuilder.newBuilderWithSource(uri).setAutoRotateEnabled(true).build();
        ImagePipeline pipeline = Fresco.getImagePipeline();
        DataSource<CloseableReference<CloseableImage>> dataSource = pipeline.fetchDecodedImage(imageRequest, context);
        dataSource.subscribe(new BaseBitmapDataSubscriber() {
            @Override
            protected void onNewResultImpl(final Bitmap bitmap) {
                getNetBmpFromFresco(localFile, cacheFolder, uri);
                final Bitmap bmp = BitmapFactory.decodeFile(localFile.getPath());
                if (imageView != null && localFile.exists()) {
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            imageView.setImageBitmap(bmp);
                            if (iGetFrescoBitmap != null) {
                                iGetFrescoBitmap.afterGotBitmap(bmp);
                            }
                        }
                    });
                } else {
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (iGetFrescoBitmap != null) {
                                iGetFrescoBitmap.afterGotBitmap(bmp);
                            }
                        }
                    });
                }
            }

            @Override
            protected void onFailureImpl(DataSource<CloseableReference<CloseableImage>> dataSource) {
            }
        }, CallerThreadExecutor.getInstance());
    }

    private static void getNetBmpFromFresco(final Activity context, final Uri uri, final IGetFrescoBitmap iGetFrescoBitmap) {
        ImageRequest imageRequest = ImageRequestBuilder.newBuilderWithSource(uri).setAutoRotateEnabled(true).build();
        ImagePipeline pipeline = Fresco.getImagePipeline();
        DataSource<CloseableReference<CloseableImage>> dataSource = pipeline.fetchDecodedImage(imageRequest, context);
        dataSource.subscribe(new BaseBitmapDataSubscriber() {
            @Override
            protected void onNewResultImpl(final Bitmap bitmap) {
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (iGetFrescoBitmap != null) {
                            iGetFrescoBitmap.afterGotBitmap(bitmap);
                        }
                    }
                });
            }

            @Override
            protected void onFailureImpl(DataSource<CloseableReference<CloseableImage>> dataSource) {
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
