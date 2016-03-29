package com.lixue.aibei.universalimageloaderlib.core.assist;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Administrator on 2016/3/24.
 */
public class ContentLengthInputStream extends InputStream {
    private InputStream inputStream;
    private final int length;

    public ContentLengthInputStream(InputStream inputStream,int length){
        this.inputStream = inputStream;
        this.length = length;
    }

    @Override
    public int available() throws IOException {
        return length;
    }

    @Override
    public int read() throws IOException {
        return inputStream.read();
    }

    @Override
    public void close() throws IOException {
        inputStream.close();
    }

    @Override
    public void mark(int readlimit) {
        inputStream.mark(readlimit);
    }

    @Override
    public int read(byte[] buffer) throws IOException {
        return inputStream.read(buffer);
    }

    @Override
    public int read(byte[] buffer, int byteOffset, int byteCount) throws IOException {
        return inputStream.read(buffer, byteOffset, byteCount);
    }

    @Override
    public void reset() throws IOException {
        inputStream.reset();
    }

    @Override
    public long skip(long byteCount) throws IOException {
        return inputStream.skip(byteCount);
    }

    @Override
    public boolean markSupported() {
        return inputStream.markSupported();
    }
}
