package edu.nuist.dateout.misc;

import java.io.File;

import android.graphics.Bitmap;
import android.os.Handler;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nuist.dateout.R;

import edu.nuist.dateout.app.DateoutApp;
import edu.nuist.dateout.util.FileAssit;
import edu.nuist.dateout.value.AppConfig;

public class ImageLoaderCfg
{
    public static DisplayImageOptions optionChat = new DisplayImageOptions.Builder().cacheInMemory(true)
        .cacheOnDisk(true)
        .cacheInMemory(true)
        .showImageOnLoading(R.drawable.default_received_image)
        .showImageForEmptyUri(R.drawable.default_received_image)
        .showImageOnFail(R.drawable.default_received_image)
        .imageScaleType(ImageScaleType.EXACTLY)
        .bitmapConfig(Bitmap.Config.RGB_565)
        .displayer(new SimpleBitmapDisplayer())
        .handler(new Handler())
        .considerExifParams(false)
        .delayBeforeLoading(50)
        .displayer(new FadeInBitmapDisplayer(100))
        .build();
    
    public static DisplayImageOptions optionHead = new DisplayImageOptions.Builder().cacheInMemory(true)
        .cacheInMemory(true)
        .showImageOnLoading(R.drawable.default_head)
        .showImageForEmptyUri(R.drawable.default_head)
        .showImageOnFail(R.drawable.default_head)
        .imageScaleType(ImageScaleType.EXACTLY)
        .bitmapConfig(Bitmap.Config.RGB_565)
        .displayer(new SimpleBitmapDisplayer())
        .handler(new Handler())
        .considerExifParams(false)
        .delayBeforeLoading(0)
        .build();
    
    public ImageLoaderConfiguration getImageLoaderConfig()
    {
        String imageLoaderCachePath = new FileAssit().getValidSdCardPath() + "/" + AppConfig.DIR_APP_CACHE_IMAGELOADER;// ImageLoader缓存路径
        
        ImageLoaderConfiguration config =
            new ImageLoaderConfiguration.Builder(DateoutApp.getInstance()).memoryCacheExtraOptions(480, 800) // maxwidth,
                .threadPoolSize(3)
                // 线程池内加载的数量
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                // You can pass your own memory cache implementation/你可以通过自己的内存缓存实现
                .memoryCacheSize(2 * 1024 * 1024)
                // 将保存的时候的URI名称用MD5 加密
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                // 缓存的文件数量
                .diskCacheFileCount(128)
                .diskCache(new UnlimitedDiscCache(new File(imageLoaderCachePath)))
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .diskCacheSize(64 * 1024 * 1024)
                // 自定义缓存路径
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
                .imageDownloader(new BaseImageDownloader(DateoutApp.getInstance(), 10 * 1000, 30 * 1000))
                // connectTimeout (5 s), readTimeout (30 s)超时时间
                .writeDebugLogs()
                .build();// 开始构建
        return config;
    }
}
