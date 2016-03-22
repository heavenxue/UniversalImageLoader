package com.lixue.aibei.universalimageloaderlib.cache.disc.Impl;

import android.graphics.Bitmap;

import com.lixue.aibei.universalimageloaderlib.cache.disc.DiskCache;
import com.lixue.aibei.universalimageloaderlib.cache.disc.naming.FileNameGenerator;
import com.lixue.aibei.universalimageloaderlib.core.DefaultConfigurationFactory;
import com.lixue.aibei.universalimageloaderlib.utils.IoUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Base Disk cache
 * Created by Administrator on 2016/3/22.
 */
public abstract class BaseDiskCache implements DiskCache {
    public static final int DEFAULT_BUFFER_SIZE = 32 * 1024;//每次读取的缓存流
    public static final Bitmap.CompressFormat DEFAULT_COPRESS_FORMAT = Bitmap.CompressFormat.PNG;//默认压缩图片格式
    public static final int DEFAULT_COMPRESS_QUALITY = 100;//默认压缩图片质量
    private static final String ERROR_ARG_NULL = "argument must be not null";//参数不能为空
    private static final String TEMP_IMAGE_POSFIX = ".tmp";//临时图像后缀

    protected final File cacheDir;//缓存目录
    protected final File reserveCacheDir;//后备缓存目录
    protected final FileNameGenerator fileNameGenerator;//文件名生成器

    protected int bufferSize = DEFAULT_BUFFER_SIZE;

    protected Bitmap.CompressFormat compressFormat = DEFAULT_COPRESS_FORMAT;
    protected int compressQuality = DEFAULT_COMPRESS_QUALITY;

    public BaseDiskCache(File cacheDir){
        this(cacheDir,null);
    }

    public BaseDiskCache(File cacheDir,File reserveCacheDir){
        this(cacheDir,reserveCacheDir, DefaultConfigurationFactory.FileNameGenerator());
    }

    public BaseDiskCache(File cacheDir,File reserveCacheDir,FileNameGenerator fileNameGenerator){
        if (cacheDir == null){
            throw new IllegalArgumentException("cacheDir:" + ERROR_ARG_NULL);
        }
        if (fileNameGenerator == null){
            throw new IllegalArgumentException("fileNameGenerator:" + ERROR_ARG_NULL);
        }
        this.cacheDir = cacheDir;
        this.reserveCacheDir = reserveCacheDir;
        this.fileNameGenerator = fileNameGenerator;
    }

    @Override
    public File getDirectory() {
        return cacheDir;
    }

    @Override
    public File get(String imageUri) {
        return getFile(imageUri);
    }

    @Override
    public boolean save(String imageUri, Bitmap bitmap) throws IOException {
            File imageFile = getFile(imageUri);
            File tmpFile = new File(imageFile.getAbsolutePath() + TEMP_IMAGE_POSFIX);
            OutputStream os = new BufferedOutputStream(new FileOutputStream(tmpFile), bufferSize);
            boolean savedSuccessfully = false;
            try {
                savedSuccessfully = bitmap.compress(compressFormat, compressQuality, os);//copressQuality=100表示不压缩
            } finally {
                IoUtils.closeSilently(os);
                if (savedSuccessfully && !tmpFile.renameTo(imageFile)) {
                    savedSuccessfully = false;
                }
                if (!savedSuccessfully) {
                    tmpFile.delete();
                }
            }
            bitmap.recycle();
            return savedSuccessfully;
    }

    @Override
    public boolean save(String imageUri, InputStream imageStream, IoUtils.CopyListener listener) throws IOException {
        File imageFile = getFile(imageUri);
        File tmpFile = new File(imageFile.getAbsolutePath() + TEMP_IMAGE_POSFIX);
        boolean isLoaded = false;
        try {
            OutputStream os = new BufferedOutputStream(new FileOutputStream(tmpFile),bufferSize);
            try {
                isLoaded = IoUtils.copyStream(imageStream,os,listener,bufferSize);
            }finally {
                IoUtils.closeSilently(os);
            }
        }finally {
            if (isLoaded && !tmpFile.renameTo(imageFile)) isLoaded = false;
            if (!isLoaded) tmpFile.delete();
        }

        return isLoaded;
    }

    @Override
    public boolean remove(String imageUri) {
        return getFile(imageUri).delete();
    }

    @Override
    public void clear() {
        File[] files = cacheDir.listFiles();
        if (files != null){
            for (File f: files){
                f.delete();
            }
        }
    }

    @Override
    public void close() {
        //no to do
    }

    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    public void setCompressFormat(Bitmap.CompressFormat compressFormat) {
        this.compressFormat = compressFormat;
    }

    public void setCompressQuality(int compressQuality) {
        this.compressQuality = compressQuality;
    }

    /**通过图像uri得到缓存中的文件**/
    protected File getFile(String imageUri){
        String filename = fileNameGenerator.gernerate(imageUri);
        File dir = cacheDir;

        if (!cacheDir.exists() && !cacheDir.mkdir()){
            if (reserveCacheDir != null && (reserveCacheDir.exists() || reserveCacheDir.mkdir())){
                dir = reserveCacheDir;
            }
        }
        return new File(dir,filename);
    }
}
