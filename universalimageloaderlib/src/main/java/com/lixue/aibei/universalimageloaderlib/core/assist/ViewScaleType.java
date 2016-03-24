package com.lixue.aibei.universalimageloaderlib.core.assist;

import android.widget.ImageView;

/**
 * 视图缩放类型枚举
 * Created by Administrator on 2016/3/24.
 */
public enum  ViewScaleType {
    /**对图像进行均匀的比例（保持图像的宽高比），从而使图像的至少一个尺寸（宽度或高度）与视图的尺寸相等或更少。**/
    FIT_INSIDE,
    /**对图像进行均匀的比例（保持图像的宽高比）
     * 图像将等于或大于视图的相应尺寸。**/
    CROP;

    public static ViewScaleType fromImageView(ImageView imageView) {
        switch (imageView.getScaleType()) {
            case FIT_CENTER:
            case FIT_XY:
            case FIT_START:
            case FIT_END:
            case CENTER_INSIDE:
                return FIT_INSIDE;
            case MATRIX:
            case CENTER:
            case CENTER_CROP:
            default:
                return CROP;
        }
    }
}
