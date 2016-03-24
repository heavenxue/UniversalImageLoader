package com.lixue.aibei.universalimageloaderlib.core.download;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

/**
 * 图像下载器接口
 * Created by Administrator on 2016/3/24.
 */
public interface ImageDownloader {
    /**@param extra 在DisplayImageOptions.extraForDownloader(Object)的选项，可以为空**/
    InputStream getStream(String imageUri,Object extra) throws IOException;

    /**所支持的所有uri类型**/
    public enum Scheme{
        HTTP("http"),
        HTTPS("https"),
        FILE("file"),
        CONTENT("content"),
        ASSETS("assets"),
        DRAWABLE("drawable"),
        UNKNOWN("unknown");

        private String scheme;
        private String uriPrefix;

        Scheme(String scheme){
            this.scheme = scheme;
            this.uriPrefix = scheme + "://";
        }

        public static Scheme ofUri(String uri){
            if (uri != null){
                for (Scheme s : values()){
                    if (s.belongsTo(uri)){
                        return s;
                    }
                }
            }
            return UNKNOWN;
        }

        /**uri是否有效，如果以诸如http://开头的即可**/
        private boolean belongsTo(String uri){
            return uri.toLowerCase(Locale.US).startsWith(uriPrefix);
        }

        /** 前缀追加链接*/
        public String wrap(String path) {
            return uriPrefix + path;
        }

        /**去掉前缀诸如part ("scheme://")*/
        public String crop(String uri) {
            if (!belongsTo(uri)) {
                throw new IllegalArgumentException(String.format("URI [%1$s] doesn't have expected scheme [%2$s]", uri, scheme));
            }
            return uri.substring(uriPrefix.length());
        }
    }
}
