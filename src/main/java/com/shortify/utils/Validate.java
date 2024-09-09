package com.shortify.utils;

import java.util.regex.Pattern;

import com.shortify.models.User;

public class Validate {
    private static Pattern httpRegex = Pattern.compile("^https?:\\/\\/[\\w\\-]+(\\.[\\w\\-]+)+[/#?]?.*$");
    private static Pattern passwordRegex = Pattern.compile("^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*\\W)(?!.*\\s).{8,16}$");
    private static Pattern emailRegex = Pattern.compile("^[a-zA-Z\\d-_]+@[a-zA-Z0-9.]+.[a-z]+$");
    private static Pattern usernameRegex = Pattern.compile("^\\w[\\w.]{0,28}\\w$");

    public static boolean validateHttpAddress(String httpAddress) {
        boolean isCorrect = false;
        if (httpRegex.matcher(httpAddress).find()) {
            isCorrect = true;
        }
        return isCorrect;
    }

    public static boolean isValidUser(User user) {
        boolean result = false;
        if (user.getPassword() != null && passwordRegex.matcher(user.getPassword()).find() ) {
            if (user.getUsername() != null && usernameRegex.matcher(user.getUsername()).find()
            || user.getEmail() != null && emailRegex.matcher(user.getEmail()).find()) {
                result = true;
            }
        }
        return result;
    }
}
