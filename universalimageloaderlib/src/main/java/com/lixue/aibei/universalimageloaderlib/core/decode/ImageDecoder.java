package com.lixue.aibei.universalimageloaderlib.core.decode;

import android.graphics.Bitmap;

import java.io.IOException;

/**
 * 图像解码器接口
 * Created by Administrator on 2016/3/24.
 */
public interface ImageDecoder {
    Bitmap decode(ImageDecodingInfo imageDecodingInfo) throws IOException;
}
