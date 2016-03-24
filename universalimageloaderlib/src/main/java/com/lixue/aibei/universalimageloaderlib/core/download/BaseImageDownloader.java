package com.lixue.aibei.universalimageloaderlib.core.download;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.webkit.MimeTypeMap;

import com.lixue.aibei.universalimageloaderlib.core.assist.ContentLengthInputStream;
import com.lixue.aibei.universalimageloaderlib.utils.IoUtils;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 图像下载器
 * Created by Administrator on 2016/3/24.
 */
public class BaseImageDownloader implements ImageDownloader {
    public static final int DEFULT_HTTP_CONNECT_TIMEOUT = 5 * 1000;//milliseconds
    public static final int DEFULT_HTTP_READ_TIMEOUT = 20 * 1000;

    protected static final int BUFFER_SIZE = 32 * 1024;//32KB
    protected static final String ALLOWED_URI_CHARS = "@#&=*+-_.,:!?()/~'%";//URI可以包含的字符

    protected static final int MAX_REDIRECT_COUNT = 5;//可以重定向的url最大数量
    protected static final String CONECT_CONTACTS_URI_PREFIX = "content://com.android.contacts/";//content contacts前缀
    protected static final String ERROR_UNSUPPORTED_SCHEME = "UIL doesn't support scheme(protocol) by default [%s]. \" + \"You should implement this support yourself (BaseImageDownloader.getStreamFromOtherSource(...))";

    protected final Context context;
    protected final int connectTimeout;
    protected final int readTimeout;

    public BaseImageDownloader(Context context){
        this(context,DEFULT_HTTP_CONNECT_TIMEOUT,DEFULT_HTTP_READ_TIMEOUT);
    }

    public BaseImageDownloader(Context context,int connectTimeout,int readTimeout){
        this.context = context;
        this.connectTimeout = connectTimeout;
        this.readTimeout = readTimeout;
    }

    @Override
    public InputStream getStream(String imageUri, Object extra) throws IOException {
        switch (Scheme.ofUri(imageUri)){
            case HTTP:
            case HTTPS:
                return getStreamFromNetwork(imageUri, extra);
            case FILE:
                return getStreamFromFile(imageUri,extra);
            case CONTENT:
                return getStreamFromContent(imageUri, extra);
            case ASSETS:
                return getStreamFromAssets(imageUri, extra);
            case DRAWABLE:
                return getStreamFromDrawable(imageUri, extra);
            case UNKNOWN:
                default:
                    return getStreamFromOtherSource(imageUri, extra);
        }
    }

    /**从网络下载图片**/
    protected InputStream getStreamFromNetwork(String imageUri,Object extra) throws IOException {
        HttpURLConnection connection = createConnection(imageUri);
        int redirectCount = 0;
        while (connection.getResponseCode() / 100 == 3 && redirectCount < MAX_REDIRECT_COUNT){
            connection = createConnection(connection.getHeaderField("Location"));
            redirectCount ++;
        }
        InputStream imageInputStream;

        try {
            imageInputStream = connection.getInputStream();
        }catch (IOException e){
            IoUtils.closeSilently(connection.getErrorStream());
            throw e;
        }

        if (!shouldBeProcessed(connection)){
            IoUtils.closeSilently(imageInputStream);
            throw new IOException("Image request failed with response code " + connection.getResponseCode());
        }
        return new ContentLengthInputStream(new BufferedInputStream(imageInputStream,BUFFER_SIZE),connection.getContentLength());
    }

    /**通过从文件系统或sd卡中取图片**/
    protected InputStream getStreamFromFile(String imageUri,Object extra) throws IOException {
        String filePath = Scheme.FILE.crop(imageUri);
        if (isVideoFileUri(imageUri)){
            return getVideoThumbnailStream(filePath);
        }else{
            BufferedInputStream stream = new BufferedInputStream(new FileInputStream(filePath),BUFFER_SIZE);
            return new ContentLengthInputStream(stream,(int)new File(filePath).length());
        }
    }

    /**从ContentResover中获得图片**/
    protected InputStream getStreamFromContent(String imageUri,Object extra) throws FileNotFoundException {
        ContentResolver res = context.getContentResolver();
        Uri uri = Uri.parse(imageUri);
        if (isVideoContentUri(uri)){//如果是视频文件
            Long origId = Long.valueOf(uri.getLastPathSegment());//最后路径
            Bitmap bitmap = MediaStore.Video.Thumbnails.getThumbnail(res, origId, MediaStore.Images.Thumbnails.MINI_KIND, null);
            if (bitmap != null){
                ByteArrayOutputStream bom = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG,0,bom);//图像压缩
                return new ByteArrayInputStream(bom.toByteArray());
            }
        }else if(imageUri.startsWith(CONECT_CONTACTS_URI_PREFIX)){//contants系统 photo
            return getContactPhotoStream(uri);
        }
        return res.openInputStream(uri);
    }

    /**从assets文件夹中获取图片**/
    protected InputStream getStreamFromAssets(String imageUri,Object extra) throws IOException {
        String filePath = Scheme.ASSETS.crop(imageUri);
        return context.getAssets().open(filePath);
    }

    /**从drawable文件夹中获取图片**/
    protected InputStream getStreamFromDrawable(String imageUri,Object extra){
        String drawableIdString = Scheme.DRAWABLE.crop(imageUri);
        int drawableId = Integer.parseInt(drawableIdString);
        return context.getResources().openRawResource(drawableId);
    }

    /**不符合格式的抛出异常**/
    protected InputStream getStreamFromOtherSource(String imageUri, Object extra) throws IOException {
        throw new UnsupportedOperationException(String.format(ERROR_UNSUPPORTED_SCHEME, imageUri));
    }

    private InputStream getContactPhotoStream(Uri uri){
        ContentResolver res = context.getContentResolver();
        //android系统版本大于android4.0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH){
            return ContactsContract.Contacts.openContactPhotoInputStream(res, uri, true);
        }else{
            return ContactsContract.Contacts.openContactPhotoInputStream(res, uri);
        }
    }

    /**获得视频缩略图流**/
    private InputStream getVideoThumbnailStream(String filePath){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(filePath, MediaStore.Images.Thumbnails.FULL_SCREEN_KIND);
            if (bitmap != null) {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos);
                return new ByteArrayInputStream(bos.toByteArray());
            }
        }
        return null;
    }

    /**是否是媒体文件**/
    private boolean isVideoContentUri(Uri uri){
        String mimeType = context.getContentResolver().getType(uri);
        return mimeType != null && mimeType.startsWith("video/");
    }

    /**是否是媒体文件**/
    private boolean isVideoFileUri(String uri){
        String extension = MimeTypeMap.getFileExtensionFromUrl(uri);
        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        return mimeType != null && mimeType.startsWith("video/");
    }

    /**通过uri创建连接**/
    private HttpURLConnection createConnection(String imageUri) throws IOException {
        String encodeUri = Uri.encode(imageUri, ALLOWED_URI_CHARS);
        HttpURLConnection conn = (HttpURLConnection) new URL(encodeUri).openConnection();
        conn.setConnectTimeout(connectTimeout);
        conn.setReadTimeout(readTimeout);
        return conn;
    }

    /**是否应该处理下载连接**/
    private boolean shouldBeProcessed(HttpURLConnection connection) throws IOException {
        return connection.getResponseCode() == 200;
    }
}
