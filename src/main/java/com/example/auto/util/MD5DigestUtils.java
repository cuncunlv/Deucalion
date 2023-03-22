package com.example.auto.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import java.util.Optional;

public class MD5DigestUtils {
    public static String digestMD5(String var) {
        if (var == null || "".equals(var)) {
            return null;
        }
        StringBuilder md5Str = null;
        try {
            MessageDigest messagedigest = MessageDigest.getInstance("MD5");
            messagedigest.update(var.getBytes());
            byte[] digestBytes = messagedigest.digest();
            BigInteger bigInteger = new BigInteger(1, digestBytes);
            md5Str = new StringBuilder(bigInteger.toString(16));
            while (md5Str.length() < 32) {
                md5Str.insert(0, "0");
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return Optional
                .ofNullable(md5Str)
                .map(Objects::toString)
                .orElse(null);
    }
}
