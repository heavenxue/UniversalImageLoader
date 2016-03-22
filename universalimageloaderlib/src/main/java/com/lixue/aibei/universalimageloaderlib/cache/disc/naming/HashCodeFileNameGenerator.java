package com.lixue.aibei.universalimageloaderlib.cache.disc.naming;

/**
 * Created by Administrator on 2016/3/22.
 */
public class HashCodeFileNameGenerator implements FileNameGenerator {
    @Override
    public String gernerate(String imageUri) {
        return String.valueOf(imageUri.hashCode());
    }
}
