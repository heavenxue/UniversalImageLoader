package com.lixue.aibei.universalimageloaderlib.cache.disc;

import android.graphics.Bitmap;

import com.lixue.aibei.universalimageloaderlib.utils.IoUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * 磁盘缓存接口
 * Created by Administrator on 2016/3/22.
 */
public interface DiscCache {
    /**返回磁盘缓存的根目录**/
    File getDirectory();
    /**返回缓存的图片**/
    File get(String imageUri);
    /**向磁盘中保存图片流**/
    boolean save(String imageUri,InputStream imageStream,IoUtils.CopyListener listener) throws IOException;
    /**想磁盘中保存图片**/
    boolean save(String imageUri,Bitmap bitmap) throws IOException;
    /**删除跟imageUri相关联的图片**/
    boolean remove(String imageUri);
    /**关闭磁盘缓存，释放资源**/
    void close();
    /**清除图片缓存**/
    void clear();
}
