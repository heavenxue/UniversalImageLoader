package com.lixue.aibei.universalimageloaderlib.core.decode;

import android.annotation.TargetApi;
import android.graphics.BitmapFactory;
import android.os.Build;

import com.lixue.aibei.universalimageloaderlib.core.DisplayImageOptions;
import com.lixue.aibei.universalimageloaderlib.core.assist.ImageScaleType;
import com.lixue.aibei.universalimageloaderlib.core.assist.ImageSize;
import com.lixue.aibei.universalimageloaderlib.core.assist.ViewScaleType;
import com.lixue.aibei.universalimageloaderlib.core.download.ImageDownloader;

/**
 * 解码图像所需要的信息
 * Created by Administrator on 2016/3/24.
 */
public class ImageDecodingInfo {
    private final String imageKey;
    private final String imageUri;
    private final String originalImageUri;//原始图像uri
    private final ImageSize targetSize;

    private final ImageScaleType imageScaleType;//图像
    private final ViewScaleType viewScaleType;

    private final ImageDownloader downloader;
    private final Object extraForDownloader;

    private final boolean considerExifParams;
    private final BitmapFactory.Options decodingOptions;

    public ImageDecodingInfo(String imageKey, String imageUri, String originalImageUri, ImageSize targetSize, ViewScaleType viewScaleType,
                             ImageDownloader downloader, DisplayImageOptions displayOptions) {
        this.imageKey = imageKey;
        this.imageUri = imageUri;
        this.originalImageUri = originalImageUri;
        this.targetSize = targetSize;

        this.imageScaleType = displayOptions.getImageScaleType();
        this.viewScaleType = viewScaleType;

        this.downloader = downloader;
        this.extraForDownloader = displayOptions.getExtraForDownloader();

        considerExifParams = displayOptions.isConsiderExifParams();
        decodingOptions = new BitmapFactory.Options();
        copyOptions(displayOptions.getDecodingOptions(), decodingOptions);
    }

    private void copyOptions(BitmapFactory.Options srcOptions, BitmapFactory.Options destOptions) {
        destOptions.inDensity = srcOptions.inDensity;//图像密度
        destOptions.inDither = srcOptions.inDither;//图像抖动
        destOptions.inInputShareable = srcOptions.inInputShareable;//他要结合inpurgeable来用
        destOptions.inJustDecodeBounds = srcOptions.inJustDecodeBounds;//如果设置为true，解码器将返回空（无位图），但输出宽高参数
        destOptions.inPreferredConfig = srcOptions.inPreferredConfig;//如果为true,解码器将尝试解码到这个内部配置
        destOptions.inPurgeable = srcOptions.inPurgeable;//如果系统需要回收内存,那么由此产生的位图将分配像素能够删除
        destOptions.inSampleSize = srcOptions.inSampleSize;//如果设置的值大于1，要求解码器子样的原始图像，返回一个较小的图像
        destOptions.inScaled = srcOptions.inScaled;//当这个标志被设置，如果强度和intargetdensity不是0，位图将缩放匹配intargetdensity时加载，而不是依靠图形系统的缩放它
        destOptions.inScreenDensity = srcOptions.inScreenDensity;//屏幕像素密度
        destOptions.inTargetDensity = srcOptions.inTargetDensity;//目标像素密度
        destOptions.inTempStorage = srcOptions.inTempStorage;//用于解码的临时存储
        if (Build.VERSION.SDK_INT >= 10) copyOptions10(srcOptions, destOptions);
        if (Build.VERSION.SDK_INT >= 11) copyOptions11(srcOptions, destOptions);
    }

    @TargetApi(10)
    private void copyOptions10(BitmapFactory.Options srcOptions, BitmapFactory.Options destOptions) {
        //如果inpreferqualityoverspeed设置为true，解码器将解码重构图像质量较高，牺牲解码速度为代价
        destOptions.inPreferQualityOverSpeed = srcOptions.inPreferQualityOverSpeed;
    }

    @TargetApi(11)
    private void copyOptions11(BitmapFactory.Options srcOptions, BitmapFactory.Options destOptions) {
        destOptions.inBitmap = srcOptions.inBitmap;
        destOptions.inMutable = srcOptions.inMutable;//解码方法总是返回一个可变的位图而不是一个一成不变的
    }
    public String getImageKey() {
        return imageKey;
    }

    /** @return Image URI for decoding (usually image from disk cache) */
    public String getImageUri() {
        return imageUri;
    }

    /** @return The original image URI which was passed to ImageLoader */
    public String getOriginalImageUri() {
        return originalImageUri;
    }

    /**
     * @return Target size for image. Decoded bitmap should close to this size according to {@linkplain ImageScaleType
     * image scale type} and {@linkplain ViewScaleType view scale type}.
     */
    public ImageSize getTargetSize() {
        return targetSize;
    }

    /**
     * @return {@linkplain ImageScaleType Scale type for image sampling and scaling}. This parameter affects result size
     * of decoded bitmap.
     */
    public ImageScaleType getImageScaleType() {
        return imageScaleType;
    }

    /** @return {@linkplain ViewScaleType View scale type}. This parameter affects result size of decoded bitmap. */
    public ViewScaleType getViewScaleType() {
        return viewScaleType;
    }

    /** @return Downloader for image loading */
    public ImageDownloader getDownloader() {
        return downloader;
    }

    /** @return Auxiliary object for downloader */
    public Object getExtraForDownloader() {
        return extraForDownloader;
    }

    /** @return <b>true</b> - if EXIF params of image should be considered; <b>false</b> - otherwise */
    public boolean shouldConsiderExifParams() {
        return considerExifParams;
    }

    /** @return Decoding options */
    public BitmapFactory.Options getDecodingOptions() {
        return decodingOptions;
    }

}
