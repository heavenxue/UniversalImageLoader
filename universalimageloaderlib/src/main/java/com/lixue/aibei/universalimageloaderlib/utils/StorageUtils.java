package com.lixue.aibei.universalimageloaderlib.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;

import java.io.File;
import java.io.IOException;

/**
 * Created by Administrator on 2016/3/23.
 */
public class StorageUtils {

    private static final String EXTERNAL_STORAGE_PERMISSION = "android.permission.WRITE_EXTERNAL_STORAGE";
    private static final String INDIVIDUAL_DIR_NAME = "uil-images";//自定义目录名称

    /**得到缓存目录，优先在sd卡上存储，如果没有创建成功，就向内存中存**/
    public static File getCacheDirectory(Context context) {
        return getCacheDirectory(context, true);
    }

    /**创建自定义目录的缓存**/
    public static File getIndividualCacheDirectory(Context context) {
        return getIndividualCacheDirectory(context, INDIVIDUAL_DIR_NAME);
    }

    /**
     * 创建缓存目录，放在sd卡上
     * 数据目录“Adnroid/data/[包名]/cache”
     * 如果创建失败，就向内存中存储，目录：“/data/data/[包名]/cache”
     * @param preferExternal 是否外部存储（放在sd卡），否则就创建内存
     * **/
    public static File getCacheDirectory(Context context,boolean preferExternal){
        File cacheDir = null;
        String externalStorageState;
        try{
            externalStorageState = Environment.getExternalStorageState();
        }catch (NullPointerException e){
            externalStorageState = "";
        }catch (IncompatibleClassChangeError e){//不兼容类异常
            externalStorageState = "";
        }

        if (preferExternal && Environment.MEDIA_MOUNTED.equals(externalStorageState) && hasExternalStoragePermission(context)){
            cacheDir = getExternalCacheDir(context);
        }
        //如果sd卡存储失败，就创建系统存储
        if (cacheDir == null){
            String cacheDirPath = "/data/data/" + context.getPackageName() + "/cache/";
            L.w("Can't define system cache directory! '%s' will be used.", cacheDirPath);
            cacheDir = new File(cacheDirPath);
        }
        return cacheDir;
    }

    /**是否有写sd卡权限**/
    private static boolean hasExternalStoragePermission(Context context){
        int perm = context.checkCallingOrSelfPermission(EXTERNAL_STORAGE_PERMISSION);
        return perm == PackageManager.PERMISSION_GRANTED;
    }

    /**得到sd卡缓存目录**/
    private static File getExternalCacheDir(Context context){
        File dataFile = new File(new File(Environment.getExternalStorageDirectory(),"android"),"data");
        File appCacheFile = new File(new File(dataFile,context.getPackageName()),"cache");
        if (!appCacheFile.exists()){
            if (!appCacheFile.mkdirs()){
                L.w("Unable to create external cache directory");
                return null;
            }
            try {
                new File(appCacheFile, ".nomedia").createNewFile();
            } catch (IOException e) {
                L.i("Can't create \".nomedia\" file in application external cache directory");
            }
        }
        return appCacheFile;
    }

    public static File getIndividualCacheDirectory(Context context, String cacheDir) {
        File appCacheDir = getCacheDirectory(context);
        File individualCacheDir = new File(appCacheDir, cacheDir);
        if (!individualCacheDir.exists()) {
            if (!individualCacheDir.mkdir()) {
                individualCacheDir = appCacheDir;
            }
        }
        return individualCacheDir;
    }

    /**得到指定应用的缓存目录**/
    public static File getOwnCacheDirectory(Context context,String cacheDir){
        File appCacheDir = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) && hasExternalStoragePermission(context)){
            appCacheDir = new File(Environment.getExternalStorageDirectory(),cacheDir);
        }
        if (appCacheDir == null || (!appCacheDir.exists() && !appCacheDir.mkdirs())){
            appCacheDir = context.getCacheDir();
        }
        return appCacheDir;
    }

    public static File getOwnCacheDirectory(Context context, String cacheDir, boolean preferExternal) {
        File appCacheDir = null;
        if (preferExternal && Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) && hasExternalStoragePermission(context)) {
            appCacheDir = new File(Environment.getExternalStorageDirectory(), cacheDir);
        }
        if (appCacheDir == null || (!appCacheDir.exists() && !appCacheDir.mkdirs())) {
            appCacheDir = context.getCacheDir();
        }
        return appCacheDir;
    }
}
