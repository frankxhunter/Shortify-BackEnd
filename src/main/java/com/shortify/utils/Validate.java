package com.shortify.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.shortify.models.User;

public class Validate {
    private static String httpRegex = "^https?:\\/\\/[\\w\\-]+(\\.[\\w\\-]+)+[/#?]?.*$";

    public static boolean validateHttpAddress(String httpAddress) {
        boolean isCorrect = false;
        Pattern pt = Pattern.compile(httpRegex);
        Matcher mt = pt.matcher(httpAddress);
        if (mt.find()) {
            isCorrect = true;
        }
        return isCorrect;
    }

    public static boolean isValidUser(User user) {
        boolean result = false;
        if (user.getPassword() != null && !user.getPassword().equals("")) {
            if (user.getUsername() != null && !user.getUsername().equals("")
            || user.getEmail() != null && !user.getEmail().equals("")) {
                result = true;
            }
        }
        return result;
    }
}
