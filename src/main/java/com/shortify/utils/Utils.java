package com.shortify.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class Utils {
    public static String generateHash(String text){
        String encode = null;
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            byte[] hash = digest.digest(text.getBytes(StandardCharsets.UTF_8));

            encode = Base64.getUrlEncoder().withoutPadding().encodeToString(hash);

            return encode.substring(0,8);

        }catch(NoSuchAlgorithmException e){

        }
        return encode;
    }
}
