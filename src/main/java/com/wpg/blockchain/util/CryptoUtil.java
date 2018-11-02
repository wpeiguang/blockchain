package com.wpg.blockchain.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CryptoUtil {

    public static String getSHA256(String str){
        MessageDigest messageDigest;
        String encodeStr = "";
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(str.getBytes("UTF-8"));
            encodeStr = byte2Hex(messageDigest.digest());
        } catch (Exception e) {
            System.err.println("get SHA-256 is error, " + e.getMessage());
        }
        return encodeStr;
    }

    private static String byte2Hex(byte[] bytes){
        StringBuilder builder = new StringBuilder();
        String temp;
        for(int i = 0; i < bytes.length; i++){
            temp = Integer.toHexString(bytes[i] & 0xFF);
            if(temp.length() == 1){
                builder.append("0");
            }
            builder.append(temp);
        }
        return builder.toString();
    }
}
