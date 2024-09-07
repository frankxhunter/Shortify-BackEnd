package com.shortify.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validate {
    private static String httpRegex = "^https?:\\/\\/[\\w\\-]+(\\.[\\w\\-]+)+[/#?]?.*$";

    public static boolean validateHttpAddress(String httpAddress){
        boolean isCorrect = false;
        Pattern pt = Pattern.compile(httpRegex);
        Matcher mt = pt.matcher(httpAddress);
        if(mt.find()){
            isCorrect = true;
        }
        return isCorrect;
    }
}
