package com.easylive.base.untils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5 加密工具类
 */
public class MD5Util {

    // 盐值（自定义，增加加密复杂度）
    private static final String SALT = "easypan@2026";

    /**
     * MD5加密（带盐值）
     */
    public static String encrypt(String password) {
        if (StringTools.isEmpty(password)) {
            return "";
        }
        try {
            // 拼接盐值
            String raw = password + SALT;
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest(raw.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                String hex = Integer.toHexString(b & 0xff);
                if (hex.length() == 1) {
                    sb.append("0").append(hex);
                } else {
                    sb.append(hex);
                }
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5加密失败", e);
        }
    }

    /**
     * 验证密码（原密码 + 加密后密码）
     */
    public static boolean verify(String rawPassword, String encryptedPassword) {
        if (StringTools.isEmpty(rawPassword) || StringTools.isEmpty(encryptedPassword)) {
            return false;
        }
        return encrypt(rawPassword).equals(encryptedPassword);
    }
}