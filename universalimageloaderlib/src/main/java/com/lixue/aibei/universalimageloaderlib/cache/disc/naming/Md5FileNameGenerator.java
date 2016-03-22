package com.lixue.aibei.universalimageloaderlib.cache.disc.naming;

import com.lixue.aibei.universalimageloaderlib.utils.L;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Administrator on 2016/3/22.
 */
public class Md5FileNameGenerator implements FileNameGenerator {
    private static final String HASH_ALGORITHM = "MD5";
    private static final int radx = 10 + 26;//10位数字+26位字母

    @Override
    public String gernerate(String imageUri) {
        byte[] md5 = md5(imageUri.getBytes());
        BigInteger bi = new BigInteger(md5).abs();
        return bi.toString(radx);
    }

    private byte[] md5(byte[] bytes){
        byte[] hash = null;
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(HASH_ALGORITHM);
            messageDigest.update(bytes);
            hash = messageDigest.digest();

        } catch (NoSuchAlgorithmException e) {
            L.e(e);
        }
        return hash;
    }
}
