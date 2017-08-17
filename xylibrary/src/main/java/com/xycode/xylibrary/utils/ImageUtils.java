package com.xycode.xylibrary.utils;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.Html;
import android.text.Spanned;
import android.view.View;

import com.facebook.binaryresource.BinaryResource;
import com.facebook.binaryresource.FileBinaryResource;
import com.facebook.cache.common.CacheKey;
import com.facebook.common.executors.CallerThreadExecutor;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.cache.DefaultCacheKeyFactory;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.core.ImagePipelineFactory;
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.xycode.xylibrary.Xy;
import com.xycode.xylibrary.instance.FrescoLoader;
import com.xycode.xylibrary.interfaces.Interfaces;
import com.xycode.xylibrary.utils.LogUtil.L;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by XY on 2016/7/12.
 * invoke Xy.init() first init Application
 */
public class ImageUtils {

    private static final String TEMP_FILE_NAME = "temp";
    private static final String TEMP_IMAGE_FILE_NAME = "tempImage.jpg";
    private static final String TEMP_CROP_IMAGE_FILE_NAME = "tempCropImage";

    public static Uri getTempImageUri() {
        File file = new File(Xy.getContext().getExternalCacheDir(), TEMP_IMAGE_FILE_NAME);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Uri uri = Uri.fromFile(file);
        return Uri.parse("file://" + uri.getPath());
    }

    public static Uri getTempCropImageUri() {
        File file = new File(Xy.getContext().getFilesDir(), TEMP_CROP_IMAGE_FILE_NAME + DateUtils.getNow() + ".jpg");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Uri uri = Uri.fromFile(file);
        return Uri.parse("file://" + uri.getPath());
    }

    public static Uri getNewTempImageUri() {
        File file = new File(Xy.getContext().getFilesDir(), TEMP_FILE_NAME + DateUtils.getNow() + ".jpg");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Uri uri = Uri.fromFile(file);
        return Uri.parse("file://" + uri.getPath());
    }

  /*  private static boolean isGif(String url) {
        String s = url.toLowerCase();
        return s.endsWith(".gif");
    }*/

    public static void scanPhotoPath(String filePath) {
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(new File(filePath));
        intent.setData(uri);
        Xy.getContext().sendBroadcast(intent);
    }

    public static Uri getResUri(int resId) {
        return Uri.parse("res:///" + resId);
    }

    public static Intent cropImage(Uri cropUri, Uri outputUri) {
        return cropImage(cropUri, outputUri, 1, 1, 300, 300);
    }

    public static Intent cropImage(Uri cropUri, Uri outputUri, int aspectX, int aspectY, int outputX, int outputY) {
        if (null == cropUri) return null;
        Intent intent = new Intent();
        intent.setAction("com.android.camera.action.CROP");
        intent.setDataAndType(cropUri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", aspectX);
        intent.putExtra("aspectY", aspectY);
        intent.putExtra("outputX", outputX);
        intent.putExtra("outputY", outputY);
        intent.putExtra("scale", false);
        intent.putExtra("scaleUpIfNeeded", false);
        intent.putExtra("return-data", outputUri == null);
        if (outputUri != null) {
            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
        }
        return intent;
    }

    public static ScalingUtils.ScaleType checkFrescoScaleType(int scaleType) {
        switch (scaleType) {
            case 0:
                return ScalingUtils.ScaleType.FIT_XY;
            case 1:
                return ScalingUtils.ScaleType.FIT_START;
            case 2:
                return ScalingUtils.ScaleType.FIT_CENTER;
            case 3:
                return ScalingUtils.ScaleType.FIT_END;
            case 4:
                return ScalingUtils.ScaleType.CENTER;
            case 5:
                return ScalingUtils.ScaleType.CENTER_INSIDE;
            case 6:
                return ScalingUtils.ScaleType.CENTER_CROP;
            case 7:
                return ScalingUtils.ScaleType.FOCUS_CROP;
        }
        return ScalingUtils.ScaleType.FIT_CENTER;
    }

    /**
     * @param bitmap
     * @param imageQuality less than 100
     * @return
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

    public static BitmapFactory.Options getBitmapInfo(String bmpPath) {
        BitmapFactory.Options info = new BitmapFactory.Options();
        info.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(bmpPath, info);
        return info;
    }

    /**
     * get ucrop_rotate angle
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
        L.i("------quality-- after " + "  ------" + baos.toByteArray().length / 1024f);
        return baos;
    }

    public static ByteArrayOutputStream compressBitmapFileToStream(String filePath, int jpegQuality, int targetMaxSide, int targetMiniSide) {
        Bitmap bitmap = resizeToBitmap(filePath, targetMaxSide, targetMiniSide);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, jpegQuality, baos);
        L.i("(" + filePath + ")------quality-- after " + "  ------" + baos.toByteArray().length / 1024f);
        return baos;
    }

    public static boolean compressBitmapFromPathToFile(String sourcePath, File targetFile, int jpegQuality, int targetMaxSide, int targetMiniSide) {
        Bitmap bitmap = resizeToBitmap(sourcePath, targetMaxSide, targetMiniSide);
        File dir = new File(targetFile.getParent());
        if (!dir.exists()) {
            dir.mkdirs();
        }
        try {
            FileOutputStream outputStream = new FileOutputStream(targetFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, jpegQuality, outputStream);
            outputStream.flush();
            outputStream.close();
            L.i("saveBitmapToFile localFile" + "(" + targetFile.getPath() + ") -- (" + targetFile.length() / 1024 + "K)");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        bitmap.recycle();
        return true;
    }

    /**
     * get ordinary Bitmap .
     *
     * @param data      byte[].
     * @param targetPix
     * @return
     */
    @Deprecated
    public static Bitmap resizeToBitmap(byte[] data, int targetPix) {
        BitmapFactory.Options options = null;

        if (targetPix > 0) {
            BitmapFactory.Options info = new BitmapFactory.Options();
            //when set true decode Bitmap return null
            //read bitmap Options
            info.inJustDecodeBounds = true;
//            info.inPreferredConfig = Bitmap.Config.RGB_565;
//            info.inSampleSize = 16;
            BitmapFactory.decodeByteArray(data, 0, data.length, info);

            int dim = Math.max(info.outWidth, info.outHeight);
//            L.e("pic Width: " + dim + "pic outHeight: " + info.outHeight);
//            if (!width) dim = Math.max(dim, info.outHeight);
//            int ssize = sampleSize(dim, targetPix);
            int ssize = (targetPix > dim ? 1 : dim / targetPix);
//            L.d("pic Width: " + ssize);

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

    @Deprecated
    public static Bitmap resizeToBitmap(int minSide, byte[] data) {
        BitmapFactory.Options options = null;
        BitmapFactory.Options info = new BitmapFactory.Options();
        info.inJustDecodeBounds = true;
//        info.inPreferredConfig = Bitmap.Config.RGB_565;
//            info.inSampleSize = 16;
        BitmapFactory.decodeByteArray(data, 0, data.length, info);

        int min = Math.min(info.outWidth, info.outHeight);
        int max = Math.max(info.outWidth, info.outHeight);
//        L.e("pic Width: " + info.outWidth + "pic outHeight: " + info.outHeight);

        int scaleSize = (minSide > min ? 1 : (minSide / min));
//        L.d("pic scaleSize: " + scaleSize);

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

    /**
     * @param filePath
     * @param targetMaxSide  if bitmap maxSide smaller than this, do nothing
     * @param targetMiniSide if bitmap miniSide smaller than this, do nothing
     * @return
     */
    public static Bitmap resizeToBitmap(String filePath, int targetMaxSide, int targetMiniSide) {
        BitmapFactory.Options info = new BitmapFactory.Options();
        info.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, info);
        int maxSide = Math.max(info.outWidth, info.outHeight);
        int minSide = Math.min(info.outWidth, info.outHeight);
        L.i("sideLength outWidth:" + "(" + info.outWidth + ") outHeight:" + "(" + info.outHeight + ") ...:" + (1.0 * maxSide) / targetMaxSide);
        int scaleSsize = targetMaxSide >= maxSide ? 1 : (int) (Math.floor((1.0 * maxSide) / targetMaxSide));
        int nowMinSide = minSide / scaleSsize;
        if (scaleSsize > 1 && nowMinSide <= targetMiniSide) {
            scaleSsize = (int) Math.floor(scaleSsize * ((1.0f * nowMinSide) / targetMiniSide));
            if (scaleSsize < 1) {
                scaleSsize = 1;
            }
        }
        info = new BitmapFactory.Options();
//        info.inPreferredConfig = Bitmap.Config.RGB_565;
        info.inSampleSize = scaleSsize;
        info.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(filePath, info);

        Matrix matrix = new Matrix();
        int h = bitmap.getHeight(), w = bitmap.getWidth();
        int max = targetMaxSide;
        int min = targetMiniSide;
        int newMaxSide = Math.max(h, w);
        int newMinSide = Math.min(h, w);
//        L.e("sideLength w:" + "(" + w + ") h:" + "(" + h + ") scaleSize:" + scaleSsize);
        if (max >= newMaxSide || min >= newMinSide) {
        } else {
            float ratioMax = (1.0f * max / newMaxSide);
            float ratioMin = (1.0f * min / newMinSide);
            float ratio = Math.max(ratioMax, ratioMin);
            matrix.postScale(ratio, ratio);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
        }
        return bitmap;
    }

    public static Bitmap doGaussianBlur(Bitmap sourceBitmap, int radius, boolean resize) {

        // Stack Blur v1.0 from
        // http://www.quasimondo.com/StackBlurForCanvas/StackBlurDemo.html
        //
        // Java Author: Mario Klingemann <mario at quasimondo.com>
        // http://incubator.quasimondo.com
        // created Feburary 29, 2004
        // Android port : Yahel Bouaziz <yahel at kayenko.com>
        // http://www.kayenko.com
        // ported april 5th, 2012

        // This is a compromise between Gaussian Blur and Box blur
        // It creates much better looking blurs than Box Blur, but is
        // 7x faster than my Gaussian Blur implementation.
        //
        // I called it Stack Blur because this describes best how this
        // filter works internally: it creates a kind of moving stack
        // of colors whilst scanning through the image. Thereby it
        // just has to add one new block of color to the right side
        // of the stack and remove the leftmost color. The remaining
        // colors on the topmost layer of the stack are either added on
        // or reduced by one, depending on if they are on the right or
        // on the left side of the stack.
        //
        // If you are using this algorithm in your code please add
        // the following line:
        //
        // Stack Blur Algorithm by Mario Klingemann <mario@quasimondo.com>

        Bitmap bitmap;
        if (resize) {
            sourceBitmap = resizeToBitmap(320, compressBitmapToStream(sourceBitmap, 50).toByteArray());
        }

       /* if (canReuseInBitmap) {
            bitmap = sentBitmap;
        } else {
            bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);
        }*/

        bitmap = sourceBitmap.copy(sourceBitmap.getConfig(), true);
        if (radius < 1) {
            return (null);
        }

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        int[] pix = new int[w * h];
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);

        int wm = w - 1;
        int hm = h - 1;
        int wh = w * h;
        int div = radius + radius + 1;

        int r[] = new int[wh];
        int g[] = new int[wh];
        int b[] = new int[wh];
        int rSum, gSum, bSum, x, y, i, p, yp, yi, yw;
        int vMin[] = new int[Math.max(w, h)];

        int divSum = (div + 1) >> 1;
        divSum *= divSum;
        int dv[] = new int[256 * divSum];
        for (i = 0; i < 256 * divSum; i++) {
            dv[i] = (i / divSum);
        }

        yw = yi = 0;

        int[][] stack = new int[div][3];
        int stackPointer;
        int stackStart;
        int[] sir;
        int rbs;
        int r1 = radius + 1;
        int routSum, goutSum, boutSum;
        int rinSum, ginSum, binSum;

        for (y = 0; y < h; y++) {
            rinSum = ginSum = binSum = routSum = goutSum = boutSum = rSum = gSum = bSum = 0;
            for (i = -radius; i <= radius; i++) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))];
                sir = stack[i + radius];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rbs = r1 - Math.abs(i);
                rSum += sir[0] * rbs;
                gSum += sir[1] * rbs;
                bSum += sir[2] * rbs;
                if (i > 0) {
                    rinSum += sir[0];
                    ginSum += sir[1];
                    binSum += sir[2];
                } else {
                    routSum += sir[0];
                    goutSum += sir[1];
                    boutSum += sir[2];
                }
            }
            stackPointer = radius;

            for (x = 0; x < w; x++) {

                r[yi] = dv[rSum];
                g[yi] = dv[gSum];
                b[yi] = dv[bSum];

                rSum -= routSum;
                gSum -= goutSum;
                bSum -= boutSum;

                stackStart = stackPointer - radius + div;
                sir = stack[stackStart % div];

                routSum -= sir[0];
                goutSum -= sir[1];
                boutSum -= sir[2];

                if (y == 0) {
                    vMin[x] = Math.min(x + radius + 1, wm);
                }
                p = pix[yw + vMin[x]];

                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);

                rinSum += sir[0];
                ginSum += sir[1];
                binSum += sir[2];

                rSum += rinSum;
                gSum += ginSum;
                bSum += binSum;

                stackPointer = (stackPointer + 1) % div;
                sir = stack[(stackPointer) % div];

                routSum += sir[0];
                goutSum += sir[1];
                boutSum += sir[2];

                rinSum -= sir[0];
                ginSum -= sir[1];
                binSum -= sir[2];

                yi++;
            }
            yw += w;
        }
        for (x = 0; x < w; x++) {
            rinSum = ginSum = binSum = routSum = goutSum = boutSum = rSum = gSum = bSum = 0;
            yp = -radius * w;
            for (i = -radius; i <= radius; i++) {
                yi = Math.max(0, yp) + x;

                sir = stack[i + radius];

                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];

                rbs = r1 - Math.abs(i);

                rSum += r[yi] * rbs;
                gSum += g[yi] * rbs;
                bSum += b[yi] * rbs;

                if (i > 0) {
                    rinSum += sir[0];
                    ginSum += sir[1];
                    binSum += sir[2];
                } else {
                    routSum += sir[0];
                    goutSum += sir[1];
                    boutSum += sir[2];
                }

                if (i < hm) {
                    yp += w;
                }
            }
            yi = x;
            stackPointer = radius;
            for (y = 0; y < h; y++) {
                // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                pix[yi] = (0xff000000 & pix[yi]) | (dv[rSum] << 16) | (dv[gSum] << 8) | dv[bSum];

                rSum -= routSum;
                gSum -= goutSum;
                bSum -= boutSum;

                stackStart = stackPointer - radius + div;
                sir = stack[stackStart % div];

                routSum -= sir[0];
                goutSum -= sir[1];
                boutSum -= sir[2];

                if (x == 0) {
                    vMin[y] = Math.min(y + r1, hm) * w;
                }
                p = x + vMin[y];

                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];

                rinSum += sir[0];
                ginSum += sir[1];
                binSum += sir[2];

                rSum += rinSum;
                gSum += ginSum;
                bSum += binSum;

                stackPointer = (stackPointer + 1) % div;
                sir = stack[stackPointer];

                routSum += sir[0];
                goutSum += sir[1];
                boutSum += sir[2];

                rinSum -= sir[0];
                ginSum -= sir[1];
                binSum -= sir[2];

                yi += w;
            }
        }

        bitmap.setPixels(pix, 0, w, 0, 0, w, h);

        return (bitmap);
    }


    public static boolean isImageInFrescoCache(Uri loadUri) {
        if (loadUri == null) {
            return false;
        }
        CacheKey cacheKey = DefaultCacheKeyFactory.getInstance().getEncodedCacheKey(ImageRequest.fromUri(loadUri));
        return ImagePipelineFactory.getInstance().getMainFileCache().hasKey(cacheKey) || ImagePipelineFactory.getInstance().getSmallImageFileCache().hasKey(cacheKey);
    }

    //return file or null
    public static File getCachedImageOnFresco(Uri loadUri) {
        File localFile = null;
        if (loadUri != null) {
            CacheKey cacheKey = DefaultCacheKeyFactory.getInstance().getEncodedCacheKey(ImageRequest.fromUri(loadUri));
            if (ImagePipelineFactory.getInstance().getMainFileCache().hasKey(cacheKey)) {
                BinaryResource resource = ImagePipelineFactory.getInstance().getMainFileCache().getResource(cacheKey);
                localFile = ((FileBinaryResource) resource).getFile();
            } else if (ImagePipelineFactory.getInstance().getSmallImageFileCache().hasKey(cacheKey)) {
                BinaryResource resource = ImagePipelineFactory.getInstance().getSmallImageFileCache().getResource(cacheKey);
                localFile = ((FileBinaryResource) resource).getFile();
            }
        }
        return localFile;

    }

    public static void loadBitmapFromFresco(Uri uri, final IGetFrescoBitmap iGetFrescoBitmap) {
        File localFile = getCachedImageOnFresco(uri);
        if (localFile != null) {

            ResizeOptions ro = FrescoLoader.getMaxResizeOptions(uri.toString());
            if (ro != null) {
                iGetFrescoBitmap.afterGotBitmap(resizeToBitmap(localFile.getAbsolutePath(), ro.width, ro.height));
            } else {
                iGetFrescoBitmap.afterGotBitmap(getBitmapFromFile(localFile));
            }
        } else {
            loadBitmapFromFrescoNet(uri, iGetFrescoBitmap);
        }
    }

    /**
     * 通过Fresco加载图片
     *
     * @param uri
     * @param iGetFrescoBitmap
     */
    public static void loadBitmapFromFrescoNet(Uri uri, final IGetFrescoBitmap iGetFrescoBitmap) {
        if (uri == null) return;
        ImageRequestBuilder imageRequestBuilder = ImageRequestBuilder.newBuilderWithSource(uri).setAutoRotateEnabled(true);

        ResizeOptions ro = FrescoLoader.getMaxResizeOptions(uri.toString());
        if (ro != null) {
            imageRequestBuilder.setResizeOptions(ro);
        }
        ImagePipeline pipeline = Fresco.getImagePipeline();
        DataSource<CloseableReference<CloseableImage>> dataSource = pipeline.fetchDecodedImage(imageRequestBuilder.build(), Xy.getContext());
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

    public static void setFrescoViewUri(final SimpleDraweeView imageView, Uri uri, Uri previewUri) {
        setFrescoViewUri(imageView, uri, previewUri, (imageInfo, ratio) -> imageView.setAspectRatio(ratio));
    }

    public static void setFrescoViewUri(SimpleDraweeView imageView, Uri uri, Uri previewUri, final IGetFrescoImageInfo iGetFrescoImageInfo) {
        if (uri == null) {
            if (imageView.getAspectRatio() <= 0) {
                imageView.setVisibility(View.GONE);
            }
            return;
        }
        ImageRequestBuilder imageRequestBuilder = ImageRequestBuilder.newBuilderWithSource(uri)
                .setProgressiveRenderingEnabled(true);

        ResizeOptions ro = FrescoLoader.getMaxResizeOptions(uri.toString());
        if (ro != null) {
            imageRequestBuilder.setResizeOptions(ro);
        }
        PipelineDraweeControllerBuilder builder = Fresco.newDraweeControllerBuilder()
                .setControllerListener(new ControllerListener<ImageInfo>() {
                    boolean showFinalImageInfo = true;

                    @Override
                    public void onSubmit(String id, Object callerContext) {
                    }

                    @Override
                    public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
                        if (showFinalImageInfo && iGetFrescoImageInfo != null) {
                            float ratio = (1.0f * imageInfo.getWidth()) / imageInfo.getHeight();
                            iGetFrescoImageInfo.afterGotImageInfo(imageInfo, ratio);
                            imageView.setVisibility(ratio > 0 ? View.VISIBLE : View.GONE);
                        }
                    }

                    @Override
                    public void onIntermediateImageSet(String id, ImageInfo imageInfo) {
                        showFinalImageInfo = false;
                        if (iGetFrescoImageInfo != null) {
                            iGetFrescoImageInfo.afterGotImageInfo(imageInfo, (1.0f * imageInfo.getWidth()) / imageInfo.getHeight());
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
                })
                .setImageRequest(imageRequestBuilder.build());
        if (previewUri != null) builder.setLowResImageRequest(ImageRequest.fromUri(previewUri));
        builder.setOldController(imageView.getController());
        DraweeController controller = builder.build();
        imageView.setController(controller);
    }

    /**
     * Please use {@link FrescoLoader#setImageUrl(android.widget.ImageView, Object)} for this method
     * @param simpleDraweeView
     * @param uri
     * @param previewUri
     * @param resizeOptions
     */
    public static void setImageUriWithPreview(SimpleDraweeView simpleDraweeView, String uri, String previewUri, ResizeOptions resizeOptions) {
        setImageUriWithPreview(simpleDraweeView, Uri.parse(uri), previewUri == null ? null : Uri.parse(previewUri), resizeOptions);
    }

    static void setImageUriWithPreview(SimpleDraweeView simpleDraweeView, Uri uri, Uri previewUri, ResizeOptions resizeOptions) {
        if (uri == null) {
            simpleDraweeView.setImageURI(null);
            return;
        }
        // 新建加载请求
        ImageRequest request;
        PipelineDraweeControllerBuilder builder = Fresco.newDraweeControllerBuilder()
                .setOldController(simpleDraweeView.getController())
                .setAutoPlayAnimations(true);  // 图片动态展示如GIF
        // 尺寸转换器 如果图片太大会容易引起OOM，可使用此选项来使图片以更小的尺寸显示
        if (resizeOptions != null) {
            request = ImageRequestBuilder.newBuilderWithSource(uri)
                    .setResizeOptions(resizeOptions)
//                    .setImageType(resizeOptions.width < 400 ? ImageRequest.ImageType.SMALL : ImageRequest.ImageType.DEFAULT) // 当图片小于400时设置小类型
                    .setAutoRotateEnabled(true)  // 图片自动旋转
                    .build();
        } else {
            request = ImageRequestBuilder.newBuilderWithSource(uri)
                    .setAutoRotateEnabled(true)
                    .build();
        }
        if (previewUri != null) {
            // 设置图片预览图
            builder.setLowResImageRequest(ImageRequest.fromUri(previewUri));
        }
        builder.setImageRequest(request);
        DraweeController controller = builder.build();
        simpleDraweeView.setController(controller);
    }

    public static boolean saveBitmapToFile(File file, Bitmap bitmap) {
        File dir = new File(file.getParent());
        if (!dir.exists()) {
            dir.mkdirs();
        }
        try {
            FileOutputStream outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
            scanPhotoPath(file.getPath());
            L.i("saveBitmapToFile localFile" + "(" + file.getPath() + ") -- (" + file.length() / 1024 + "K)");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static void removeFromFrescoCache(Uri uri) {
        if (uri == null) return;
        Fresco.getImagePipeline().evictFromCache(uri);
        Fresco.getImagePipeline().evictFromDiskCache(uri);
    }

    public static void reloadFromFrescoCache(SimpleDraweeView simpleDraweeView, Uri uri) {
        simpleDraweeView.setImageURI(null);
        Fresco.getImagePipeline().evictFromCache(uri);
        simpleDraweeView.setImageURI(uri);
    }

    /**
     * this method can only use once when view is created
     *
     * @param simpleDraweeView
     * @param setDraweeHierarchy
     */
    public static void setSimpleDraweeParams(SimpleDraweeView simpleDraweeView, ISetDraweeHierarchy setDraweeHierarchy) {
        GenericDraweeHierarchyBuilder builder = new GenericDraweeHierarchyBuilder(simpleDraweeView.getResources());
        setDraweeHierarchy.setHierarchyBuilder(builder);
        simpleDraweeView.setHierarchy(builder.build());
    }

    public interface ISetDraweeHierarchy {
        void setHierarchyBuilder(GenericDraweeHierarchyBuilder hierarchyBuilder);
    }

    public interface IGetFrescoBitmap {
        void afterGotBitmap(Bitmap bitmap);
    }

    public interface IGetFrescoImageInfo {
        void afterGotImageInfo(ImageInfo imageInfo, float ratio);
    }

    /**
     * HTML TextView
     *
     * @param htmlText
     * @param holder
     * @param cbSaved
     */
    public static void saveHtmlPicToLocal(String htmlText, Drawable holder, String serverAddress, Interfaces.CB cbSaved) {
        Html.fromHtml(htmlText, source -> {
            File file = Tools.checkFile(Tools.getFileDir().getAbsolutePath(), source);
            if (!file.exists()) {
                String path = source;
                if (!path.startsWith("http")) {
                    if (!path.startsWith("/")) {
                        path = "/" + path;
                    }
                    path = serverAddress + path;
                }
                ImageUtils.loadBitmapFromFresco(Uri.parse(path), bitmap -> {
                    ImageUtils.saveBitmapToFile(file, bitmap);
                    cbSaved.go(null);
                });
            }
            return holder;
        }, null);
    }

    private static final int DRAWABLE_SIZE = -1;
    private static final int SCREEN_SIZE = 0;

    /**
     * 加载包含HTML的文本（从本地加载图片）
     *
     * @param activity
     * @param htmlText
     * @param picFitScreen true:屏幕等宽， false:图片默认尺寸
     * @return
     */
    public static Spanned getHtmlTextWithLocalPic(String htmlText, boolean picFitScreen) {
        return getHtmlTextWithLocalPic(htmlText, picFitScreen ? SCREEN_SIZE : DRAWABLE_SIZE);
    }

    /**
     * 加载包含HTML的文本（从本地加载图片）
     *
     * @param activity
     * @param htmlText
     * @param drawableWidth 图片的显示尺寸
     * @return
     */
    public static Spanned getHtmlTextWithLocalPic(String htmlText, int drawableWidth) {
        return getHtmlTextWithLocalPicBase(htmlText, drawableWidth);
    }

    private static Spanned getHtmlTextWithLocalPicBase(String htmlText, int drawableWidth) {
        return Html.fromHtml(htmlText, s -> {
            File f = Tools.checkFile(Tools.getFileDir().getAbsolutePath(), s);
            if (!f.exists()) {
                return null;
            }
            Bitmap bmp = ImageUtils.getBitmapFromFile(f);
            BitmapDrawable d = new BitmapDrawable(bmp);
//                            drawable.addLevel(1, 1, d);
            if (bmp != null) {
                if (drawableWidth == SCREEN_SIZE) {
                    long newHeight = 0;
                    int newWidth = Tools.getScreenSize().x;
                    newHeight = (newWidth * bmp.getHeight()) / bmp.getWidth();
                    d.setBounds(0, 0, newWidth, (int) newHeight);
                } else if (drawableWidth == DRAWABLE_SIZE) {
                    d.setBounds(0, 0, bmp.getWidth(), bmp.getHeight());
                } else {
                    long newHeight = 0;
                    newHeight = (drawableWidth * bmp.getHeight()) / bmp.getWidth();
                    d.setBounds(0, 0, drawableWidth, (int) newHeight);
                }
                d.setLevel(1);
            }
            return d;
        }, null);
    }

}
