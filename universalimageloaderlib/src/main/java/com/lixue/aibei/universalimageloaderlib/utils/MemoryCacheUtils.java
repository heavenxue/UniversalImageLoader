package com.lixue.aibei.universalimageloaderlib.utils;

import android.graphics.Bitmap;

import com.lixue.aibei.universalimageloaderlib.cache.memory.MemoryCache;
import com.lixue.aibei.universalimageloaderlib.core.assist.ImageSize;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 用于内存缓存器的key的生成，key的比较以及存储缓存的公共类
 * Created by Administrator on 2016/3/25.
 */
public class MemoryCacheUtils {
    private static final String URI_AND_SIZE_SEPARATOR = "_";//uri和大小之间的分隔符
    private static final String WIDTH_AND_HEIGHT_SEPARATOR = "*";//宽和高之间的分隔符

    public MemoryCacheUtils(){}
    /**为图像生成缓存的key
     * 以这样的形式：uri_width*height
     * **/
    public static String generatekey(String imageUri,ImageSize targetSize){
        return new StringBuilder().append(imageUri).append(URI_AND_SIZE_SEPARATOR).append(targetSize.getWidth()).append(WIDTH_AND_HEIGHT_SEPARATOR).append(targetSize.getHeight()).toString();
    }

    /**创建key的模糊比较器**/
    public static Comparator<String> createFuzzkeyComparator(){
        return new Comparator<String>() {
            @Override
            public int compare(String key1, String key2) {
                String imageUri1 = key1.substring(0, key1.lastIndexOf(URI_AND_SIZE_SEPARATOR));
                String imageUri2 = key2.substring(0, key2.lastIndexOf(URI_AND_SIZE_SEPARATOR));
                return imageUri1.compareTo(imageUri2);
            }
        };
    }
    /**通过图像uri找到缓存的图像**/
    public static List<Bitmap> findCachedBitmapsForImageUri(String imageUri,MemoryCache memoryCache){
        List<Bitmap> cachedBitmaps = new ArrayList<>();
        for (String key : memoryCache.keys()){
            if (key.startsWith(imageUri)){
                cachedBitmaps.add(memoryCache.get(key));
            }
        }
        return cachedBitmaps;
    }
    /**通过图像uri找到缓存的图像的键**/
    public static List<String> findCachedKeysForImageUri(String imageUri,MemoryCache memoryCache){
        List<String> cachedKeys = new ArrayList<>();
        for (String key : memoryCache.keys()){
            if (key.startsWith(imageUri)){
                cachedKeys.add(key);
            }
        }
        return cachedKeys;
    }
    /**从缓存中移除与此imageuri键相同的缓存**/
    public static void removeFromCache(String imageUri,MemoryCache memoryCache){
        List<String> keysToRemove = new ArrayList<String>();
        for (String key : memoryCache.keys()){
            if (key.startsWith(imageUri)){
                keysToRemove.add(key);
            }
        }
        for (String keyToRemove : keysToRemove) {
            memoryCache.remove(keyToRemove);
        }
    }
}
