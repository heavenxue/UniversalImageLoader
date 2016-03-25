package com.lixue.aibei.universalimageloaderlib.core;

import com.lixue.aibei.universalimageloaderlib.core.listener.ImageLoadingListener;
import com.lixue.aibei.universalimageloaderlib.core.listener.SimpleImageLoadingListener;

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
}
