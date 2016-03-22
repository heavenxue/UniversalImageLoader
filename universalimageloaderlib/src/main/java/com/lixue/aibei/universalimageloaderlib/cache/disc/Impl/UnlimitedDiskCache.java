package com.lixue.aibei.universalimageloaderlib.cache.disc.Impl;

import com.lixue.aibei.universalimageloaderlib.cache.disc.naming.FileNameGenerator;

import java.io.File;

/**
 *  默认磁盘缓存器.
 *  缓存大小无限制
 * Created by Administrator on 2016/3/22.
 */
public class UnlimitedDiskCache extends BaseDiskCache {
    public UnlimitedDiskCache(File cacheDir) {
        super(cacheDir);
    }

    public UnlimitedDiskCache(File cacheDir, File reserveCacheDir) {
        super(cacheDir, reserveCacheDir);
    }

    public UnlimitedDiskCache(File cacheDir, File reserveCacheDir, FileNameGenerator fileNameGenerator) {
        super(cacheDir, reserveCacheDir, fileNameGenerator);
    }
}
