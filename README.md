 
UniversalImageLoader
====================
  `UniversalImageLoader`的目的是提供一个功能强大，灵活且大小可以定制的图像加载器缓存和显示。</br>
  它提供了大量的配置选项和良好的控制的图像加载和缓存过程。</br>
 ![](https://github.com/heavenxue/UniversalImageLoader/raw/master/doc/show.png)
 ![](https://github.com/heavenxue/UniversalImageLoader/raw/master/doc/show2.png)
  
特点
---
  * 多线程加载图像（异步或同步）
  * 广泛的定制`UniversalImageLoader`的配置（线程的执行者，下载器、解码器、内存和磁盘缓存，显示图像选项，等）
  * 可以自定义每个显示图像（存根图像、缓存转换、解码选项、图像处理和显示等）
  * 内存和/或磁盘上的图像缓存（设备的文件系统或SD卡）
  * 监听加载过程（包括下载进度）
  * 支持安卓2 +
  * 支持显示动画图片
    
### 使用
  1、依赖：
  
```java
compile 'com.lixue.aibei.universalimageloaderlib:universalimageloaderlib:1.0'
```
  2、清单
  
```java
<manifest>
<!-- Include following permission if you load images from Internet -->
<uses-permission android:name="android.permission.INTERNET" />
<!-- Include following permission if you want to cache images on SD card -->
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
...
</manifest>
```java
 
  3、初始化
  
  
```java
ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
    ...
    .build();
ImageLoader.getInstance().init(config);
```java

### UniversalImageLoader的配置

```java
// DON'T COPY THIS CODE TO YOUR PROJECT! This is just example of ALL options using.
// See the sample project how to use ImageLoader correctly.
File cacheDir = StorageUtils.getCacheDirectory(context);
ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
.memoryCacheExtraOptions(480, 800) // default = device screen dimensions
.diskCacheExtraOptions(480, 800, null)
.taskExecutor(...)
.taskExecutorForCachedImages(...)
.threadPoolSize(3) // default
.threadPriority(Thread.NORM_PRIORITY - 2) // default
.tasksProcessingOrder(QueueProcessingType.FIFO) // default
.denyCacheImageMultipleSizesInMemory()
.memoryCache(new LruMemoryCache(2 * 1024 * 1024))
.memoryCacheSize(2 * 1024 * 1024)
.memoryCacheSizePercentage(13) // default
.diskCache(new UnlimitedDiskCache(cacheDir)) // default
.diskCacheSize(50 * 1024 * 1024)
.diskCacheFileCount(100)
.diskCacheFileNameGenerator(new HashCodeFileNameGenerator()) // default
.imageDownloader(new BaseImageDownloader(context)) // default
.imageDecoder(new BaseImageDecoder()) // default
.defaultDisplayImageOptions(DisplayImageOptions.createSimple()) // default
.writeDebugLogs()
.build();
```java

### 显示选项

```java
DisplayImageOptions options = new DisplayImageOptions.Builder()
    .showImageOnLoading(R.drawable.ic_stub) // resource or drawable
    .showImageForEmptyUri(R.drawable.ic_empty) // resource or drawable
    .showImageOnFail(R.drawable.ic_error) // resource or drawable
    .resetViewBeforeLoading(false)  // default
    .delayBeforeLoading(1000)
    .cacheInMemory(false) // default
    .cacheOnDisk(false) // default
    .preProcessor(...)
    .postProcessor(...)
    .extraForDownloader(...)
    .considerExifParams(false) // default
    .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2) // default
    .bitmapConfig(Bitmap.Config.ARGB_8888) // default
    .decodingOptions(...)
    .displayer(new SimpleBitmapDisplayer()) // default
    .handler(new Handler()) // default
    .build();
```java        

### 实现逻辑流程图

![](https://github.com/heavenxue/UniversalImageLoader/raw/master/doc/UIL_Flow.png)

### 鸣谢
[nostra13](https://github.com/nostra13/Android-Universal-Image-Loader)<br />
[koral](https://github.com/koral--/android-gif-drawable)
   