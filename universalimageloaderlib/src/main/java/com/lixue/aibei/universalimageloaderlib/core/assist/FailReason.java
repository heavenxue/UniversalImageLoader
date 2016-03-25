package com.lixue.aibei.universalimageloaderlib.core.assist;

/**
 * 失败原因
 * Created by Administrator on 2016/3/25.
 */
public class FailReason {
    private final FailType type;

    private final Throwable cause;

    public FailReason(FailType type, Throwable cause) {
        this.type = type;
        this.cause = cause;
    }

    /** @return {@linkplain FailType Fail type} */
    public FailType getType() {
        return type;
    }

    /** @return Thrown exception/error, can be <b>null</b> */
    public Throwable getCause() {
        return cause;
    }

    /** Presents type of fail while image loading */
    public static enum FailType {
        /** 文件错做错误. */
        IO_ERROR,
        /**
         * 解码错误
         */
        DECODING_ERROR,
        /**
         * 网络拒绝错误
         */
        NETWORK_DENIED,
        /** 没有内存错误 */
        OUT_OF_MEMORY,
        /**未知错误 */
        UNKNOWN
    }
}
