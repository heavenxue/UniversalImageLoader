package com.lixue.aibei.universalimageloaderlib.core;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import com.lixue.aibei.universalimageloaderlib.cache.disc.DiskCache;
import com.lixue.aibei.universalimageloaderlib.cache.disc.naming.FileNameGenerator;
import com.lixue.aibei.universalimageloaderlib.cache.memory.Impl.FuzzyKeyMemoryCache;
import com.lixue.aibei.universalimageloaderlib.cache.memory.MemoryCache;
import com.lixue.aibei.universalimageloaderlib.core.assist.FlushedInputStream;
import com.lixue.aibei.universalimageloaderlib.core.assist.ImageSize;
import com.lixue.aibei.universalimageloaderlib.core.assist.QueueProcessingType;
import com.lixue.aibei.universalimageloaderlib.core.decode.ImageDecoder;
import com.lixue.aibei.universalimageloaderlib.core.download.ImageDownloader;
import com.lixue.aibei.universalimageloaderlib.core.process.BitmapProcessor;
import com.lixue.aibei.universalimageloaderlib.utils.L;
import com.lixue.aibei.universalimageloaderlib.utils.MemoryCacheUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Executor;

/**
 * 图像加载器配置
 * Created by Administrator on 2016/3/24.
 */
public class ImageLoaderConfiguration {
    final Resources resources;//取资源时候用到
    final int maxImageWidthForMemoryCache;//图像缓存时最大宽度
    final int maxImageHeightForMemoryCache;//图像缓存时最大高度
    final int maxImageWidthForDiskCache;//图像sd卡缓存时最大高度
    final int maxImageHeightForDiskCache;//图像sd卡缓存时最大高度

    final BitmapProcessor processorForDiskCache;//图像处理器

    final Executor taskExecutor; //线程池
    final Executor taskExecutorForCachedImages;//缓存图像线程池
    final boolean customExecutor;//是否自定义线程池
    final boolean customExecutorForCachedImages;//是否自定义缓存线程池

    final int threadPoolSize;//线程池大小
    final int threadPriority;//线程池优先级
    final QueueProcessingType tasksProcessingType;//线程队列处理类型

    final MemoryCache memoryCache;//内存缓存器
    final DiskCache diskCache;//sd卡缓存器
    public final ImageDownloader downloader;//图像加载器
    public final ImageDecoder decoder;//图像解析器
    public final DisplayImageOptions defaultDisplayImageOptions;//默认图像显示选项

    public final ImageDownloader networkDeniedDownloader;//网络拒绝下载器
    public final ImageDownloader slowNetworkDownloader;//慢网络下载器

    private ImageLoaderConfiguration(final Builder builder) {
        resources = builder.context.getResources();
        maxImageWidthForMemoryCache = builder.maxImageWidthForMemoryCache;
        maxImageHeightForMemoryCache = builder.maxImageHeightForMemoryCache;
        maxImageWidthForDiskCache = builder.maxImageWidthForDiskCache;
        maxImageHeightForDiskCache = builder.maxImageHeightForDiskCache;
        processorForDiskCache = builder.processorForDiskCache;
        taskExecutor = builder.taskExecutor;
        taskExecutorForCachedImages = builder.taskExecutorForCachedImages;
        threadPoolSize = builder.threadPoolSize;
        threadPriority = builder.threadPriority;
        tasksProcessingType = builder.tasksProcessingType;
        diskCache = builder.diskCache;
        memoryCache = builder.memoryCache;
        defaultDisplayImageOptions = builder.defaultDisplayImageOptions;
        downloader = builder.downloader;
        decoder = builder.decoder;

        customExecutor = builder.customExecutor;
        customExecutorForCachedImages = builder.customExecutorForCachedImages;

        networkDeniedDownloader = new NetworkDeniedImageDownloader(downloader);
        slowNetworkDownloader = new SlowNetworkImageDownloader(downloader);

        L.writeDebugLogs(builder.writeLogs);
    }

    /**创建默认的ImageLoader的配置**/
    public static ImageLoaderConfiguration createDefault(Context context){
        return new Builder(context).build();
    }

    /**如果没有设置图像的大小，那么以屏幕的最大宽高为宽高**/
    ImageSize getMaxImageSize(){
        DisplayMetrics metrics = resources.getDisplayMetrics();
        int width = maxImageWidthForMemoryCache;
        if (width < 0){
            width = metrics.widthPixels;
        }
        int height = maxImageHeightForMemoryCache;
        if (height < 0){
            height = metrics.heightPixels;
        }
        return new ImageSize(width,height);
    }

    public static class Builder {
        private static final String WARNING_OVERLAP_DISK_CACHE_PARAMS = "diskCache(), diskCacheSize() and diskCacheFileCount calls overlap each other";
        private static final String WARNING_OVERLAP_DISK_CACHE_NAME_GENERATOR = "diskCache() and diskCacheFileNameGenerator() calls overlap each other";
        private static final String WARNING_OVERLAP_MEMORY_CACHE = "memoryCache() and memoryCacheSize() calls overlap each other";
        private static final String WARNING_OVERLAP_EXECUTOR = "threadPoolSize(), threadPriority() and tasksProcessingOrder() calls "
                + "can overlap taskExecutor() and taskExecutorForCachedImages() calls.";
        /** 默认线程池大小 */
        public static final int DEFAULT_THREAD_POOL_SIZE = 3;
        /** 默认线程优先级 */
        public static final int DEFAULT_THREAD_PRIORITY = Thread.NORM_PRIORITY - 2;
        /** 默认队列任务为先进先出 */
        public static final QueueProcessingType DEFAULT_TASK_PROCESSING_TYPE = QueueProcessingType.FIFO;

        private Context context;

        private int maxImageWidthForMemoryCache = 0;
        private int maxImageHeightForMemoryCache = 0;
        private int maxImageWidthForDiskCache = 0;
        private int maxImageHeightForDiskCache = 0;
        private BitmapProcessor processorForDiskCache = null;

        private Executor taskExecutor = null;
        private Executor taskExecutorForCachedImages = null;
        private boolean customExecutor = false;
        private boolean customExecutorForCachedImages = false;

        private int threadPoolSize = DEFAULT_THREAD_POOL_SIZE;
        private int threadPriority = DEFAULT_THREAD_PRIORITY;
        private boolean denyCacheImageMultipleSizesInMemory = false;
        private QueueProcessingType tasksProcessingType = DEFAULT_TASK_PROCESSING_TYPE;

        private int memoryCacheSize = 0;
        private long diskCacheSize = 0;
        private int diskCacheFileCount = 0;

        private MemoryCache memoryCache = null;
        private DiskCache diskCache = null;
        private FileNameGenerator diskCacheFileNameGenerator = null;
        private ImageDownloader downloader = null;
        private ImageDecoder decoder;
        private DisplayImageOptions defaultDisplayImageOptions = null;
        private boolean writeLogs = false;

        public Builder(Context context) {
            this.context = context.getApplicationContext();
        }
        /**
         * 内存缓存选项
         */
        public Builder memoryCacheExtraOptions(int maxImageWidthForMemoryCache, int maxImageHeightForMemoryCache) {
            this.maxImageWidthForMemoryCache = maxImageWidthForMemoryCache;
            this.maxImageHeightForMemoryCache = maxImageHeightForMemoryCache;
            return this;
        }
        /**
         * sd卡缓存选项
         */
        @Deprecated
        public Builder discCacheExtraOptions(int maxImageWidthForDiskCache, int maxImageHeightForDiskCache,
                                             BitmapProcessor processorForDiskCache) {
            return diskCacheExtraOptions(maxImageWidthForDiskCache, maxImageHeightForDiskCache, processorForDiskCache);
        }
        /**
         * sd卡缓存选项
         * <b>NOTE: Use this option only when you have appropriate needs. It can make ImageLoader slower.</b>
         */
        public Builder diskCacheExtraOptions(int maxImageWidthForDiskCache, int maxImageHeightForDiskCache,
                                             BitmapProcessor processorForDiskCache) {
            this.maxImageWidthForDiskCache = maxImageWidthForDiskCache;
            this.maxImageHeightForDiskCache = maxImageHeightForDiskCache;
            this.processorForDiskCache = processorForDiskCache;
            return this;
        }
        /**
         * 设置显示和加载图像的线程池
         */
        public Builder taskExecutor(Executor executor) {
            if (threadPoolSize != DEFAULT_THREAD_POOL_SIZE || threadPriority != DEFAULT_THREAD_PRIORITY || tasksProcessingType != DEFAULT_TASK_PROCESSING_TYPE) {
                L.w(WARNING_OVERLAP_EXECUTOR);
            }

            this.taskExecutor = executor;
            return this;
        }
        /**
         * 设置显示sd卡缓存的图片
         * @see #taskExecutor(Executor)
         */
        public Builder taskExecutorForCachedImages(Executor executorForCachedImages) {
            if (threadPoolSize != DEFAULT_THREAD_POOL_SIZE || threadPriority != DEFAULT_THREAD_PRIORITY || tasksProcessingType != DEFAULT_TASK_PROCESSING_TYPE) {
                L.w(WARNING_OVERLAP_EXECUTOR);
            }
            this.taskExecutorForCachedImages = executorForCachedImages;
            return this;
        }
        /**
         * 设置线程池大小
         */
        public Builder threadPoolSize(int threadPoolSize) {
            if (taskExecutor != null || taskExecutorForCachedImages != null) {
                L.w(WARNING_OVERLAP_EXECUTOR);
            }

            this.threadPoolSize = threadPoolSize;
            return this;
        }
        /**
         * 设置线程优先级
         */
        public Builder threadPriority(int threadPriority) {
            if (taskExecutor != null || taskExecutorForCachedImages != null) {
                L.w(WARNING_OVERLAP_EXECUTOR);
            }
            if (threadPriority < Thread.MIN_PRIORITY) {
                this.threadPriority = Thread.MIN_PRIORITY;
            } else {
                if (threadPriority > Thread.MAX_PRIORITY) {
                    this.threadPriority = Thread.MAX_PRIORITY;
                } else {
                    this.threadPriority = threadPriority;
                }
            }
            return this;
        }
        /**
         * 默认的行为是允许在内存中缓存多个大小的一个图像
         * 您可以通过调用此方法来拒绝它：所以当某些图像将缓存在内存中，然后以前的缓存大小（如果它存在）将从内存缓存中删除。
         */
        public Builder denyCacheImageMultipleSizesInMemory() {
            this.denyCacheImageMultipleSizesInMemory = true;
            return this;
        }
        /**
         * 设置缓存池中的处理序列
         */
        public Builder tasksProcessingOrder(QueueProcessingType tasksProcessingType) {
            if (taskExecutor != null || taskExecutorForCachedImages != null) {
                L.w(WARNING_OVERLAP_EXECUTOR);
            }
            this.tasksProcessingType = tasksProcessingType;
            return this;
        }
        /**
         * 设置缓存大小
         */
        public Builder memoryCacheSize(int memoryCacheSize) {
            if (memoryCacheSize <= 0) throw new IllegalArgumentException("memoryCacheSize must be a positive number");
            if (memoryCache != null) {
                L.w(WARNING_OVERLAP_MEMORY_CACHE);
            }
            this.memoryCacheSize = memoryCacheSize;
            return this;
        }
        /**
         * 设置缓存占所有可用缓存的大小百分比，得到可用缓存大小
         */
        public Builder memoryCacheSizePercentage(int availableMemoryPercent) {
            if (availableMemoryPercent <= 0 || availableMemoryPercent >= 100) {
                throw new IllegalArgumentException("availableMemoryPercent must be in range (0 < % < 100)");
            }
            if (memoryCache != null) {
                L.w(WARNING_OVERLAP_MEMORY_CACHE);
            }
            long availableMemory = Runtime.getRuntime().maxMemory();
            memoryCacheSize = (int) (availableMemory * (availableMemoryPercent / 100f));
            return this;
        }
        /**
         * 设置缓存
         */
        public Builder memoryCache(MemoryCache memoryCache) {
            if (memoryCacheSize != 0) {
                L.w(WARNING_OVERLAP_MEMORY_CACHE);
            }
            this.memoryCache = memoryCache;
            return this;
        }
        /**
         * 设置sd卡缓存大小
         * By default: disk cache is unlimited.<br />
         * <b>NOTE:</b> If you use this method then LruDiskCache will be used as disk cache.
         * You can use {@link #diskCache(DiskCache)} method for introduction your own
         * implementation of {@link DiskCache}
         */
        public Builder diskCacheSize(int maxCacheSize) {
            if (maxCacheSize <= 0) throw new IllegalArgumentException("maxCacheSize must be a positive number");

            if (diskCache != null) {
                L.w(WARNING_OVERLAP_DISK_CACHE_PARAMS);
            }

            this.diskCacheSize = maxCacheSize;
            return this;
        }
        /**
         * 设置sd卡缓存目录中最大的文件数量
         * By default: disk cache is unlimited.<br />
         * <b>NOTE:</b> If you use this method then LruDiskCache  will be used as disk cache.
         * You can use {@link #diskCache(DiskCache)} method for introduction your own
         * implementation of {@link DiskCache}
         */
        public Builder diskCacheFileCount(int maxFileCount) {
            if (maxFileCount <= 0) throw new IllegalArgumentException("maxFileCount must be a positive number");

            if (diskCache != null) {
                L.w(WARNING_OVERLAP_DISK_CACHE_PARAMS);
            }
            this.diskCacheFileCount = maxFileCount;
            return this;
        }

        /**sd卡缓存文件名生成器**/
        @Deprecated
        public Builder discCacheFileNameGenerator(FileNameGenerator fileNameGenerator) {
            return diskCacheFileNameGenerator(fileNameGenerator);
        }

        /**
         * sd卡缓存文件名生成器.<br />
         * Default value - DefaultConfigurationFactory.createFileNameGenerator()}
         */
        public Builder diskCacheFileNameGenerator(FileNameGenerator fileNameGenerator) {
            if (diskCache != null) {
                L.w(WARNING_OVERLAP_DISK_CACHE_NAME_GENERATOR);
            }

            this.diskCacheFileNameGenerator = fileNameGenerator;
            return this;
        }
        /**
         * 设置sd卡缓存器<br />
         * Default value - UnlimitedDiskCache.
         * Cache directory is defined by StorageUtils.getCacheDirectory(Context)}.<br />
         * <br />
         * <b>NOTE:</b> If you set custom disk cache then following configuration option will not be considered:
         * <ul>
         * <li>{@link #diskCacheSize(int)}</li>
         * <li>{@link #diskCacheFileCount(int)}</li>
         * <li>{@link #diskCacheFileNameGenerator(FileNameGenerator)}</li>
         * </ul>
         */
        public Builder diskCache(DiskCache diskCache) {
            if (diskCacheSize > 0 || diskCacheFileCount > 0) {
                L.w(WARNING_OVERLAP_DISK_CACHE_PARAMS);
            }
            if (diskCacheFileNameGenerator != null) {
                L.w(WARNING_OVERLAP_DISK_CACHE_NAME_GENERATOR);
            }

            this.diskCache = diskCache;
            return this;
        }

        /**
         *设置图像下载器.<br />
         * Default value -DefaultConfigurationFactory.createImageDownloader()}
         */
        public Builder imageDownloader(ImageDownloader imageDownloader) {
            this.downloader = imageDownloader;
            return this;
        }
        /**
         * 设置图像解码器.<br />
         * Default value -DefaultConfigurationFactory.createImageDecoder()}
         */
        public Builder imageDecoder(ImageDecoder imageDecoder) {
            this.decoder = imageDecoder;
            return this;
        }
        /**
         * 设置图像显示选项
         */
        public Builder defaultDisplayImageOptions(DisplayImageOptions defaultDisplayImageOptions) {
            this.defaultDisplayImageOptions = defaultDisplayImageOptions;
            return this;
        }
        /**
         * 开启详细日志
         */
        public Builder writeDebugLogs() {
            this.writeLogs = true;
            return this;
        }

        /** Builds configured {@link ImageLoaderConfiguration} object */
        public ImageLoaderConfiguration build() {
            initEmptyFieldsWithDefaultValues();
            return new ImageLoaderConfiguration(this);
        }

        /**初始化默认图像配置**/
        private void initEmptyFieldsWithDefaultValues() {
            if (taskExecutor == null) {
                taskExecutor = DefaultConfigurationFactory.createExecutor(threadPoolSize, threadPriority, tasksProcessingType);
            } else {
                customExecutor = true;
            }
            if (taskExecutorForCachedImages == null) {
                taskExecutorForCachedImages = DefaultConfigurationFactory.createExecutor(threadPoolSize, threadPriority, tasksProcessingType);
            } else {
                customExecutorForCachedImages = true;
            }
            if (diskCache == null) {
                if (diskCacheFileNameGenerator == null) {
                    diskCacheFileNameGenerator = DefaultConfigurationFactory.createFileNameGenerator();
                }
                diskCache = DefaultConfigurationFactory.createDiskCache(context, diskCacheFileNameGenerator, diskCacheSize, diskCacheFileCount);
            }
            if (memoryCache == null) {
                memoryCache = DefaultConfigurationFactory.createMemoryCache(context, memoryCacheSize);
            }
            if (denyCacheImageMultipleSizesInMemory) {
                memoryCache = new FuzzyKeyMemoryCache(memoryCache, MemoryCacheUtils.createFuzzkeyComparator());
            }
            if (downloader == null) {
                downloader = DefaultConfigurationFactory.createImageDownloader(context);
            }
            if (decoder == null) {
                decoder = DefaultConfigurationFactory.createImageDecoder(writeLogs);
            }
            if (defaultDisplayImageOptions == null) {
                defaultDisplayImageOptions = DisplayImageOptions.createSimple();
            }
        }
    }

    /**
     * 网络拒绝下载后产生的异常下载器
     */
    private static class NetworkDeniedImageDownloader implements ImageDownloader {
        private final ImageDownloader wrappedDownloader;
        public NetworkDeniedImageDownloader(ImageDownloader wrappedDownloader) {
            this.wrappedDownloader = wrappedDownloader;
        }
        @Override
        public InputStream getStream(String imageUri, Object extra) throws IOException {
            switch (Scheme.ofUri(imageUri)) {
                case HTTP:
                case HTTPS:
                    throw new IllegalStateException();
                default:
                    return wrappedDownloader.getStream(imageUri, extra);
            }
        }
    }

    /**
     * Decorator. Handles <a href="http://code.google.com/p/android/issues/detail?id=6066">this problem</a> on slow networks
     * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
     * @since 1.8.1
     */
    private static class SlowNetworkImageDownloader implements ImageDownloader {

        private final ImageDownloader wrappedDownloader;

        public SlowNetworkImageDownloader(ImageDownloader wrappedDownloader) {
            this.wrappedDownloader = wrappedDownloader;
        }

        @Override
        public InputStream getStream(String imageUri, Object extra) throws IOException {
            InputStream imageStream = wrappedDownloader.getStream(imageUri, extra);
            switch (Scheme.ofUri(imageUri)) {
                case HTTP:
                case HTTPS:
                    return new FlushedInputStream(imageStream);
                default:
                    return imageStream;
            }
        }
    }
}
