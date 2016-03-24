package com.lixue.aibei.universalimageloaderlib.core.assist;

/**
 * 图像类型的枚举
 * Created by Administrator on 2016/3/24.
 */
public enum  ImageScaleType {
    /**没有被缩放的图像**/
    NONE,

    /**如果图像大小大于通常的分辨率，图像将被缩放到合理（按照整数次缩小）**/
    NONE_SAFE,

    /**
     * 把图像降低2倍直到接近目标大小
     * 注：如果原始图像大小小于目标大小，则原始图像不会被缩放。
     **/
    IN_SAMPLE_POWER_2,

    /**
     * 如果内存够用的情况下，图像将被采样在一个整数次（1，2，3，…）
     *  注：如果原始图像大小小于目标大小，则原始图像不会被缩放。
     * **/
    IN_SAMPLE_INT,

    /**图像将按比例缩小到目标的大小（缩放的宽度或高度，或两者都将与目标大小相等；**/
    EXACTLY,

    /**
     * 图像将按比例缩小到目标的大小（缩放的宽度或高度，或两者都将与目标大小相等
     *  注：如果原始图像大小小于目标大小，那么原始图像将被拉伸到目标大小。
     ***/
    EXACTLY_STRETCHED
}
