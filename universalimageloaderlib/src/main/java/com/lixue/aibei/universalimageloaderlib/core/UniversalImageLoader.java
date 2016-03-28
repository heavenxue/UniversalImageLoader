package com.lixue.aibei.universalimageloaderlib.core;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.lixue.aibei.universalimageloaderlib.cache.disc.DiskCache;
import com.lixue.aibei.universalimageloaderlib.cache.memory.MemoryCache;
import com.lixue.aibei.universalimageloaderlib.core.assist.ImageSize;
import com.lixue.aibei.universalimageloaderlib.core.assist.LoadedFrom;
import com.lixue.aibei.universalimageloaderlib.core.assist.ViewScaleType;
import com.lixue.aibei.universalimageloaderlib.core.imageaware.ImageAware;
import com.lixue.aibei.universalimageloaderlib.core.imageaware.ImageViewAware;
import com.lixue.aibei.universalimageloaderlib.core.imageaware.NonViewAware;
import com.lixue.aibei.universalimageloaderlib.core.listener.ImageLoadingListener;
import com.lixue.aibei.universalimageloaderlib.core.listener.ImageLoadingProgressListener;
import com.lixue.aibei.universalimageloaderlib.core.listener.SimpleImageLoadingListener;
import com.lixue.aibei.universalimageloaderlib.utils.ImageSizeUtil;
import com.lixue.aibei.universalimageloaderlib.utils.L;
import com.lixue.aibei.universalimageloaderlib.utils.MemoryCacheUtils;

/**
 * 主入口
 * Created by Administrator on 2016/3/22.
 */
public class UniversalImageLoader {
    public static final String TAG = "UniversalImageLoader";
    /**为了打印日志**/
    static final String LOG_INT_CONFIT = "Initialize ImageLoader with config";
    static final String LOG_DESTROY = "Destroy Imageloader";
    static final String LOG_LOAD_IMAGE_FROM_MEMORY_CACHE = "Load image form memory cache [%s]";
    //初始化ImageLoader，要先要重新初始化一个有新配置的ImageLoader要首先调用UniversalImagerLoadre.destroy()
    private static final String WARNING_RE_INIT_CONFIG = "Try to initialize ImageLoader which had already bean initialized before." + "To re-init ImageLoader with new configuration call ImageLoader.destroy() at first.";
    //调用displayImage()方法时参数错误
    private static final String ERROR_WRONG_ARGUMENTS = "Wrong arguments were passed to displayImage() method (ImageView reference must not be null)";
    //使用ImageLoader之前要先初始化配置
    private static final String ERROR_NOT_INIT = "ImageLoader must be init with configuration before using";
    //初始化配置不能为null
    private static final String ERROR_INIT_CONFIG_WITH_NULL = "ImageLoader configuration can not be initialized with null";

    private ImageLoaderConfiguration configuration;
    private ImageLoaderEngine engine;

    private ImageLoadingListener defaultListener = new SimpleImageLoadingListener();

    private volatile static UniversalImageLoader instance;

    public static UniversalImageLoader getInstance(){
        if (instance == null){
            synchronized (UniversalImageLoader.class){
                if (instance == null)
                    instance = new UniversalImageLoader();
            }
        }
        return instance;
    }

    protected UniversalImageLoader(){}

    /**图像加载器的初始化**/
    public synchronized void init(ImageLoaderConfiguration configuration){
        if (configuration == null){
            throw new IllegalArgumentException(ERROR_INIT_CONFIG_WITH_NULL);
        }
        if(this.configuration == null){
            L.d(LOG_INT_CONFIT);
            engine = new ImageLoaderEngine(configuration);
            this.configuration = configuration;
        }else{
            L.w(WARNING_RE_INIT_CONFIG);
        }
    }

    /**是否已经初始化过**/
    public boolean isInited(){
        return configuration != null;
    }

    /**显示图像**/
    public void displayImage(String uri,ImageAware imageAware){
        displayImage(uri, imageAware, null, null, null);
    }

    public void displayImage(String uri,ImageAware imageAware,ImageLoadingListener listener){
        displayImage(uri,imageAware,null,listener,null);
    }

    public void displayImage(String uri, ImageAware imageAware, DisplayImageOptions options ){
        displayImage(uri, imageAware, options, null, null);
    }

    public void displayImage(String uri, ImageAware imageAware, DisplayImageOptions options, ImageLoadingListener listener) {
        displayImage(uri, imageAware, options, listener, null);
    }

    public void displayImage(String uri, ImageAware imageAware, DisplayImageOptions options,
                             ImageLoadingListener listener, ImageLoadingProgressListener progressListener) {
        displayImage(uri, imageAware, options, null, listener, progressListener);
    }

    public void displayImage(String uri,ImageAware imageAware,DisplayImageOptions options,ImageSize targetSize,
                             ImageLoadingListener listener,ImageLoadingProgressListener progressListener){
        //检查配置
        checkConfiguration();
        if (imageAware == null){
            throw new IllegalArgumentException(ERROR_WRONG_ARGUMENTS);
        }
        if (listener == null){
            listener = defaultListener;
        }
        if (options == null){
            options = configuration.defaultDisplayImageOptions;
        }
        if(TextUtils.isEmpty(uri)){
            engine.cancelDisplayTaskFor(imageAware);
            listener.onLoadingCancled(uri,imageAware.getWrappedView());
            if (options.shouldShowImageForEmptyUri()){
                imageAware.setImageDrawable(options.getImageForEmptyUri(configuration.resources));
            }else {
                imageAware.setImageDrawable(null);
            }
            listener.onLoadingCompleted(uri,imageAware.getWrappedView(),null);
            return;
        }
        if (targetSize == null){
            targetSize = ImageSizeUtil.defineTargetSizeForView(imageAware, configuration.getMaxImageSize());
        }
        String memoryCacheKey = MemoryCacheUtils.generatekey(uri,targetSize);
        engine.prepareDisplayTaskFor(imageAware,memoryCacheKey);
        listener.onLoadingStarted(uri, imageAware.getWrappedView());

        Bitmap bitmap = configuration.memoryCache.get(memoryCacheKey);
        if (bitmap != null && !bitmap.isRecycled()){
            L.d(LOG_LOAD_IMAGE_FROM_MEMORY_CACHE,memoryCacheKey);
            if (options.shouldPreProcess()){
                ImageLoadingInfo imageLoadingInfo = new ImageLoadingInfo(uri,imageAware,targetSize,memoryCacheKey,options,listener,progressListener,engine.getLockForUri(uri));
                ProcessAndDisplayImageTask displayTask = new ProcessAndDisplayImageTask(engine,bitmap,imageLoadingInfo,defineHandler(options));
                if (options.isSyncLoading()){
                    displayTask.run();
                }else{
                    engine.submit(displayTask);
                }
            }else{
                options.getDisplayer().dispaly(bitmap,imageAware, LoadedFrom.MEMORY_CACHE);
                listener.onLoadingCompleted(uri,imageAware.getWrappedView(),bitmap);
            }
        }else{
            if (options.shouldShowImageOnLoading()){
                imageAware.setImageDrawable(options.getImageOnLoading(configuration.resources));
            }else if(options.isResetViewBeforeLoading()){
                imageAware.setImageDrawable(null);
            }
            ImageLoadingInfo loadingInfo = new ImageLoadingInfo(uri,imageAware,targetSize,memoryCacheKey,options,listener,progressListener,engine.getLockForUri(uri));
            LoadAndDisplayImageTask displayTask = new LoadAndDisplayImageTask(engine,loadingInfo,defineHandler(options));
            if (options.isSyncLoading()){
                displayTask.run();
            }else{
                engine.submit(displayTask);
            }
        }
    }

    public void displayImage(String uri, ImageView imageView) {
        displayImage(uri, new ImageViewAware(imageView), null, null, null);
    }

    public void displayImage(String uri, ImageView imageView, ImageSize targetImageSize) {
        displayImage(uri, new ImageViewAware(imageView), null, targetImageSize, null, null);
    }

    public void displayImage(String uri, ImageView imageView, DisplayImageOptions options) {
        displayImage(uri, new ImageViewAware(imageView), options, null, null);
    }

    public void displayImage(String uri, ImageView imageView, ImageLoadingListener listener) {
        displayImage(uri, new ImageViewAware(imageView), null, listener, null);
    }

    public void displayImage(String uri, ImageView imageView, DisplayImageOptions options,
                             ImageLoadingListener listener, ImageLoadingProgressListener progressListener) {
        displayImage(uri, new ImageViewAware(imageView), options, listener, progressListener);
    }

    public void displayImage(String uri, ImageView imageView, DisplayImageOptions options,
                             ImageLoadingListener listener) {
        displayImage(uri, imageView, options, listener, null);
    }

    public void loadImage(String uri, ImageLoadingListener listener) {
        loadImage(uri, null, null, listener, null);
    }

    public void loadImage(String uri, DisplayImageOptions options, ImageLoadingListener listener) {
        loadImage(uri, null, options, listener, null);
    }

    public void loadImage(String uri, ImageSize targetImageSize, ImageLoadingListener listener) {
        loadImage(uri, targetImageSize, null, listener, null);
    }

    public void loadImage(String uri, ImageSize targetImageSize, DisplayImageOptions options,
                          ImageLoadingListener listener) {
        loadImage(uri, targetImageSize, options, listener, null);
    }

    public void loadImage(String uri, ImageSize targetImageSize, DisplayImageOptions options,
                          ImageLoadingListener listener, ImageLoadingProgressListener progressListener) {
        checkConfiguration();
        if (targetImageSize == null) {
            targetImageSize = configuration.getMaxImageSize();
        }
        if (options == null) {
            options = configuration.defaultDisplayImageOptions;
        }

        NonViewAware imageAware = new NonViewAware(uri, targetImageSize, ViewScaleType.CROP);
        displayImage(uri, imageAware, options, listener, progressListener);
    }

    public Bitmap loadImageSync(String uri) {
        return loadImageSync(uri, null, null);
    }

    public Bitmap loadImageSync(String uri, DisplayImageOptions options) {
        return loadImageSync(uri, null, options);
    }

    public Bitmap loadImageSync(String uri, ImageSize targetImageSize) {
        return loadImageSync(uri, targetImageSize, null);
    }

    public Bitmap loadImageSync(String uri, ImageSize targetImageSize, DisplayImageOptions options) {
        if (options == null) {
            options = configuration.defaultDisplayImageOptions;
        }
        options = new DisplayImageOptions.Builder().cloneFrom(options).syncLoading(true).build();

        SyncImageLoadingListener listener = new SyncImageLoadingListener();
        loadImage(uri, targetImageSize, options, listener);
        return listener.getLoadedBitmap();
    }

    public void setDefaultLoadingListener(ImageLoadingListener listener) {
        defaultListener = listener == null ? new SimpleImageLoadingListener() : listener;
    }

    /**检查UniversalImageLoader的配置是否初始化了**/
    private void checkConfiguration(){
        if (configuration == null)
            throw new IllegalStateException(ERROR_NOT_INIT);
    }

    public MemoryCache getMemoryCache() {
        checkConfiguration();
        return configuration.memoryCache;
    }

    public void clearMemoryCache() {
        checkConfiguration();
        configuration.memoryCache.clear();
    }

    public DiskCache getDiskCache() {
        checkConfiguration();
        return configuration.diskCache;
    }

    public void clearDiskCache() {
        checkConfiguration();
        configuration.diskCache.clear();
    }

    public String getLoadingUriForView(ImageAware imageAware) {
        return engine.getLoadingUriForView(imageAware);
    }

    /**
     * 返回当前显示的加载的图像的uri
     */
    public String getLoadingUriForView(ImageView imageView) {
        return engine.getLoadingUriForView(new ImageViewAware(imageView));
    }

    /**
     * 取消加载显示图像的任务
     */
    public void cancelDisplayTask(ImageAware imageAware) {
        engine.cancelDisplayTaskFor(imageAware);
    }

    /**
     *取消加载显示图像的任务
     */
    public void cancelDisplayTask(ImageView imageView) {
        engine.cancelDisplayTaskFor(new ImageViewAware(imageView));
    }

    /**
     * Denies or allows ImageLoader to download images from the network.<br />
     */
    public void denyNetworkDownloads(boolean denyNetworkDownloads) {
        engine.denyNetworkDownloads(denyNetworkDownloads);
    }

    /**
     * href="http://code.google.com/p/android/issues/detail?id=6066">this known problem</a> or not.
     */
    public void handleSlowNetwork(boolean handleSlowNetwork) {
        engine.handleSlowNetwork(handleSlowNetwork);
    }

    /**
     * Pause ImageLoader.
     */
    public void pause() {
        engine.pause();
    }

    /** Resumes waiting "load&display" tasks */
    public void resume() {
        engine.resume();
    }

    /**
     * 取消所有运行中的或正要运行的显示图像的任务
     * <b>NOTE:</b> 这个方法不能关闭线程池中的任务
     * ImageLoader still can be used after calling this method.
     */
    public void stop() {
        engine.stop();
    }

    /**
     * 停止ImageLoader并且清除当前的配置
     */
    public void destroy() {
        if (configuration != null) L.d(LOG_DESTROY);
        stop();
        configuration.diskCache.close();
        engine = null;
        configuration = null;
    }

    /**自定义handler**/
    private static Handler defineHandler(DisplayImageOptions options) {
        Handler handler = options.getHandler();
        if (options.isSyncLoading()) {
            handler = null;
        } else if (handler == null && Looper.myLooper() == Looper.getMainLooper()) {
            handler = new Handler();
        }
        return handler;
    }

    /**
     * 自定义同步显示图像的监听器
     */
    private static class SyncImageLoadingListener extends SimpleImageLoadingListener {

        private Bitmap loadedImage;

        @Override
        public void onLoadingCompleted(String imageUri, View view, Bitmap loadedImage) {
            this.loadedImage = loadedImage;
        }

        public Bitmap getLoadedBitmap() {
            return loadedImage;
        }
    }
}
