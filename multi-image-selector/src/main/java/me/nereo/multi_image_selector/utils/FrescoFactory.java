package me.nereo.multi_image_selector.utils;

import android.content.Context;
import android.graphics.drawable.Animatable;
import android.net.Uri;

import com.facebook.cache.disk.DiskCacheConfig;
import com.facebook.common.internal.Supplier;
import com.facebook.common.util.ByteConstants;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.cache.MemoryCacheParams;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

/**
 * Created by Administrator on 2015/12/4 0004.
 */
public class FrescoFactory {

    private static final int MAX_HEAP_SIZE = (int) Runtime.getRuntime().maxMemory();//分配的可用内存
    public static final int MAX_MEMORY_CACHE_SIZE = MAX_HEAP_SIZE / 8;//使用的缓存数量

    public static final int MAX_SMALL_DISK_VERYLOW_CACHE_SIZE = 5 * ByteConstants.MB;//小图极低磁盘空间缓存的最大值（特性：可将大量的小图放到额外放在另一个磁盘空间防止大图占用磁盘空间而删除了大量的小图）
    public static final int MAX_SMALL_DISK_LOW_CACHE_SIZE = 5 * ByteConstants.MB;//小图低磁盘空间缓存的最大值（特性：可将大量的小图放到额外放在另一个磁盘空间防止大图占用磁盘空间而删除了大量的小图）
    public static final int MAX_SMALL_DISK_CACHE_SIZE = 20 * ByteConstants.MB;//小图磁盘缓存的最大值（特性：可将大量的小图放到额外放在另一个磁盘空间防止大图占用磁盘空间而删除了大量的小图）

    public static final int MAX_DISK_CACHE_VERYLOW_SIZE = 20 * ByteConstants.MB;//默认图极低磁盘空间缓存的最大值
    public static final int MAX_DISK_CACHE_LOW_SIZE = 50 * ByteConstants.MB;//默认图低磁盘空间缓存的最大值
    public static final int MAX_DISK_CACHE_SIZE = 100 * ByteConstants.MB;//默认图磁盘缓存的最大值


    private static ImagePipelineConfig sImagePipelineConfig;

    private FrescoFactory() {

    }

    /**
     * 初始化配置，单例
     */
    public static ImagePipelineConfig getImagePipelineConfig(Context context) {
        if (sImagePipelineConfig == null) {
            sImagePipelineConfig = configureCaches(context);
        }
        return sImagePipelineConfig;
    }


    /**
     * 初始化配置
     */
    private static ImagePipelineConfig configureCaches(Context context) {
        FrescoManager manager = new FrescoManager();
        //修改内存图片缓存数量，空间策略（这个方式有点恶心）
        Supplier<MemoryCacheParams> mSupplierMemoryCacheParams = manager;

        //小图片的磁盘配置
        DiskCacheConfig diskSmallCacheConfig = manager.getSmallDisCacheConfig(context);

        //默认图片的磁盘配置
        DiskCacheConfig diskCacheConfig = manager.getNormalDisCacheConfig(context);

        //缓存图片配置
        ImagePipelineConfig.Builder configBuilder = ImagePipelineConfig.newBuilder(context)
                .setBitmapMemoryCacheParamsSupplier(mSupplierMemoryCacheParams)//内存缓存配置（一级缓存，已解码的图片）
                .setMainDiskCacheConfig(diskCacheConfig)//磁盘缓存配置（正常图片，三级缓存）
                .setSmallImageDiskCacheConfig(diskSmallCacheConfig)//磁盘缓存配置（小图片，可选～三级缓存的小图优化缓存）
                .setDownsampleEnabled(true) //支持 png resize
                ;
        return configBuilder.build();
    }

    /**
     * 点击可重试下载图片
     *
     * @param uri  网址
     * @param view SimpleDraweeView
     * @return
     */
    public static DraweeController newAutoPlayAnimationsDraweeController(String uri, SimpleDraweeView view) {
        return Fresco.newDraweeControllerBuilder()
                .setControllerListener(new ControllerListener<ImageInfo>() {
                    @Override
                    public void onSubmit(String id, Object callerContext) {

                    }

                    @Override
                    public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {

                    }

                    @Override
                    public void onIntermediateImageSet(String id, ImageInfo imageInfo) {
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
                .setUri(uri)
                .setAutoPlayAnimations(true)
                .setOldController(view.getController())
                .build();
    }

    public static DraweeController newAutoPlayAnimationsDraweeController(String uri, SimpleDraweeView view, ResizeOptions options) {
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(uri))
                .setResizeOptions(options)
                .build();
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .setControllerListener(new ControllerListener<ImageInfo>() {
                    @Override
                    public void onSubmit(String id, Object callerContext) {

                    }

                    @Override
                    public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {

                    }

                    @Override
                    public void onIntermediateImageSet(String id, ImageInfo imageInfo) {
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
                .setUri(uri)
                .setAutoPlayAnimations(true)
                .setOldController(view.getController())
                .build();
        return controller;
    }

    /**
     * 无点击可重试下载图片
     *
     * @param uri 网址
     * @return
     */
    public static DraweeController newAutoPlayAnimationsDraweeController(String uri) {
        return Fresco.newDraweeControllerBuilder()
                .setUri(uri)
                .setAutoPlayAnimations(true)
                .build();
    }


}
