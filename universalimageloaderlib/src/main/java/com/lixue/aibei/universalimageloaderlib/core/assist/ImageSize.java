package com.lixue.aibei.universalimageloaderlib.core.assist;

/**
 * Created by Administrator on 2016/3/24.
 */
public class ImageSize {
    private static final int TO_STRING_MAX_LENGTH = 9;//"9999x9999".length()
    private static final String SEPARATOR = "x";

    private final int width;
    private final int height;

    public ImageSize(int width,int height){
        this.width = width;
        this.height = height;
    }

    public ImageSize(int width,int height,int rotation){
        if (rotation % 180 == 0){//如果图像倒置
            this.width = width;
            this.height = height;
        }else{
            this.width = height;
            this.height = width;
        }
    }
    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    /** 缩小比率后的图像大小Scales down dimensions in <b>sampleSize</b> times. Returns new object. */
    public ImageSize scaleDown(int sampleSize) {
        return new ImageSize(width / sampleSize, height / sampleSize);
    }

    /**放大图像 Scales dimensions according to incoming scale. Returns new object. */
    public ImageSize scale(float scale) {
        return new ImageSize((int) (width * scale), (int) (height * scale));
    }

    @Override
    public String toString() {
        //宽 * 高
        return new StringBuilder(TO_STRING_MAX_LENGTH).append(width).append(SEPARATOR).append(height).toString();
    }
}
