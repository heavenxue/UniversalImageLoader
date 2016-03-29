package com.lixue.aibei.universalimageloaderlib.utils;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 输入输出公共类
 * Created by Administrator on 2016/3/22.
 */
public class IoUtils {
    public static final int DEFAULT_BUFFER_SIZE = 32 * 1024;//缓存流大小32KB
    public static final int DEFAULT_IMAGE_TOTAL_SIZE = 500 * 1024;//缓存总大小500KB
    public static final int CONTINUE_LOADING_PRECENTAGE = 75;
    IoUtils(){}

    public static boolean copyStream(InputStream is, OutputStream os, CopyListener listener) throws IOException {
        return copyStream(is, os, listener, DEFAULT_BUFFER_SIZE);
    }

    /**复制流，通过侦听器发射事件查看进度，也可以被侦听器中断。**/
    public static boolean copyStream(InputStream is, OutputStream os, CopyListener listener, int bufferSize) throws IOException {
        int total = is.available();
        if (total <= 0) total = DEFAULT_IMAGE_TOTAL_SIZE;
        byte[] bytes = new byte[bufferSize];
        int current = 0;
        int count;
        if (shouldStopLoading(listener, current, total)) return false;
        while ((count = is.read(bytes,0,bufferSize)) != -1){
            os.write(bytes,0,count);
            current += count;
            if (shouldStopLoading(listener, current, total)) return false;
        }
        os.flush();
        return true;
    }

    /**是否应该中断加载**/
    public static boolean shouldStopLoading(CopyListener listener,int current,int total){
        if (listener != null){
            boolean shouldcontinue = listener.onBytesCopied(current,total);
            if (!shouldcontinue){
                if (100 * current / total < CONTINUE_LOADING_PRECENTAGE){
                    return true;//如果不继续了，那么进度小于75就中断加载
                }
            }
        }
        return false;
    }

    /**从数据流读出且关闭**/
    public static void readAndCloseStream(InputStream is) {
        if (is != null){
            int count;
            byte[] bytes = new byte[DEFAULT_BUFFER_SIZE];
            try {
                while ((count = is.read(bytes,0,DEFAULT_BUFFER_SIZE)) != -1);
            } catch (IOException e) {
            }finally {
                closeSilently(is);
            }
        }
    }

    public static void closeSilently(Closeable closeable) {
        if (closeable != null){
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**处理复制的监听控制器 */
    public static interface CopyListener {
        boolean onBytesCopied(int current, int total);
    }
}
