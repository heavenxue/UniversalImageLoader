package com.lixue.aibei.universalimageloaderlib.cache.disc.naming;

/**
 * Created by Administrator on 2016/3/22.
 */
public interface FileNameGenerator {
    /**通过uri为每个图像生成一个唯一的一个文件名**/
    String gernerate(String imageUri);
}
