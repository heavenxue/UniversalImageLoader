package com.lixue.aibei.universalimageloaderlib.core.decode;

import android.graphics.Bitmap;

import java.io.IOException;

/**
 * Created by Administrator on 2016/3/25.
 */
public class BaseImageDecoder implements ImageDecoder {
    /**为了打印日志**/
    protected static final String LOG_SUBSAMPLE_IMAGE = "Subsample original image (%1$s) to %2$s (scale = %3$d) [%4$s]";
    protected static final String LOG_SCALE_IMAGE = "Scale subsampled image (%1$s) to %2$s (scale = %3$.5f) [%4$s]";
    protected static final String LOG_ROTATE_IMAGE = "Rotate image on %1$d\u00B0 [%2$s]";
    protected static final String LOG_FLIP_IMAGE = "Flip image horizontally [%s]";
    protected static final String ERROR_NO_IMAGE_STREAM = "No stream for image [%s]";
    protected static final String ERROR_CANT_DECODE_IMAGE = "Image can't be decoded [%s]";

    protected final boolean loggingEnable;
    public BaseImageDecoder(boolean loggingEnable){
        this.loggingEnable = loggingEnable;
    }

    @Override
    public Bitmap decode(ImageDecodingInfo imageDecodingInfo) throws IOException {
        return null;
    }
}
