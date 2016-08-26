package me.nereo.multi_image_selector.utils;

import android.content.Context;
import android.os.Environment;

import com.facebook.cache.disk.DiskCacheConfig;
import com.facebook.common.internal.Supplier;
import com.facebook.common.util.ByteConstants;
import com.facebook.imagepipeline.cache.MemoryCacheParams;

/**
 * Created by Administrator on 2016/5/11.
 */
public class FrescoManager implements Supplier {
    private static final String IMAGE_PIPELINE_SMALL_CACHE_DIR = "image_small_pipeline_cache";//小图所放路径的文件夹名
    private static final String IMAGE_PIPELINE_CACHE_DIR = "image_pipeline_cache";//默认图所放路径的文件夹名
    public static final int MAX_SMALL_DISK_VERY_LOW_CACHE_SIZE = 10 * ByteConstants.MB;//小图极低磁盘空间缓存的最大值（特性：可将大量的小图放到额外放在另一个磁盘空间防止大图占用磁盘空间而删除了大量的小图）
    public static final int MAX_SMALL_DISK_LOW_CACHE_SIZE = 20 * ByteConstants.MB;//小图低磁盘空间缓存的最大值（特性：可将大量的小图放到额外放在另一个磁盘空间防止大图占用磁盘空间而删除了大量的小图）
    public static final int MAX_SMALL_DISK_CACHE_SIZE = 20 * ByteConstants.MB;//小图磁盘缓存的最大值（特性：可将大量的小图放到额外放在另一个磁盘空间防止大图占用磁盘空间而删除了大量的小图）

    public static final int MAX_DISK_CACHE_VERY_LOW_SIZE = 10 * ByteConstants.MB;//默认图极低磁盘空间缓存的最大值
    public static final int MAX_DISK_CACHE_LOW_SIZE = 20 * ByteConstants.MB;//默认图低磁盘空间缓存的最大值
    public static final int MAX_DISK_CACHE_SIZE = 100 * ByteConstants.MB;//默认图磁盘缓存的最大值

    @Override
    public Object get() {
        return new MemoryCacheParams(
                FrescoFactory.MAX_MEMORY_CACHE_SIZE, // 内存缓存中总图片的最大大小,以字节为单位。
                Integer.MAX_VALUE,                     // 内存缓存中图片的最大数量。
                FrescoFactory.MAX_MEMORY_CACHE_SIZE / 4, // 内存缓存中准备清除但尚未被删除的总图片的最大大小,以字节为单位。
                Integer.MAX_VALUE,                     // 内存缓存中准备清除的总图片的最大数量。
                Integer.MAX_VALUE);
    }

    public DiskCacheConfig getSmallDisCacheConfig(Context context) {
        return DiskCacheConfig.newBuilder(context)
                .setBaseDirectoryPath(context.getApplicationContext().getCacheDir())//缓存图片基路径
                .setBaseDirectoryName(IMAGE_PIPELINE_SMALL_CACHE_DIR)//文件夹名
                .setMaxCacheSize(FrescoFactory.MAX_DISK_CACHE_SIZE)//默认缓存的最大大小。
                .build();
    }

    //正常缓存图片
    public DiskCacheConfig getNormalDisCacheConfig(Context context) {
        return DiskCacheConfig.newBuilder(context)
                .setBaseDirectoryPath(Environment.getExternalStorageDirectory().getAbsoluteFile())//缓存图片基路径
                .setBaseDirectoryName(IMAGE_PIPELINE_CACHE_DIR)//文件夹名。
                .setMaxCacheSizeOnVeryLowDiskSpace(MAX_DISK_CACHE_VERY_LOW_SIZE)//缓存的最大大小,当设备极低磁盘空间
                .build();
    }
}
