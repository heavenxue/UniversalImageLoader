package com.lixue.aibei.universalimageloaderlib.core.assist;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 网络慢而引起的异常
 * <a href="http://code.google.com/p/android/issues/detail?id=6066">this problem</a>.
 * Created by Administrator on 2016/3/25.
 */
public class FlushedInputStream extends FilterInputStream{
    public FlushedInputStream(InputStream in) {
        super(in);
    }

    /**
     * 跳过和丢弃此输入流中数据的 byteCount 个字节。出于各种原因，
     * skip 方法结束时跳过的字节数可能小于该数，也可能为 0。
     * 导致这种情况的原因很多，跳过 byteCount 个字节之前已到达文件末尾只是其中一种可能。返回跳过的实际字节数。
     * 如果 byteCount 为负，则不跳过任何字节。
     *此类的 skip 方法创建一个 byte 数组，然后重复将字节读入其中，直到读够byteCount个字节或已到达流末尾为止。
     * 建议子类提供此方法更为有效的实现。例如，可依赖搜索能力的实现。
     * **/
    @Override
    public long skip(long byteCount) throws IOException {
        long totalBytesSkipped = 0L;
        while (totalBytesSkipped < byteCount) {
            long bytesSkipped = in.skip(byteCount - totalBytesSkipped);
            if (bytesSkipped == 0L) {
                int by_te = read();
                if (by_te < 0) {
                    break; // we reached EOF
                } else {
                    bytesSkipped = 1; // we read one byte
                }
            }
            totalBytesSkipped += bytesSkipped;
        }
        return totalBytesSkipped;
    }
}
