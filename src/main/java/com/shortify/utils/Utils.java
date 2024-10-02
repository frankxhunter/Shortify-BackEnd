package com.shortify.utils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.shortify.models.User;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class Utils {
    public static String generateHash(String text, int user_id) {
        String encode = null;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            byte[] hash = digest.digest((text + user_id).getBytes(StandardCharsets.UTF_8));

            encode = Base64.getUrlEncoder().withoutPadding().encodeToString(hash);

            return encode.substring(0, 8);

        } catch (NoSuchAlgorithmException e) {

        }
        return encode;
    }

    public static User getUserFromSession(HttpServletRequest req) {
        User user = (User) req.getSession().getAttribute("user");
        return user;
    }

    public static String convertObjectToJson(Object obj) {
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        String result = gson.toJson(obj);
        return result;
    }

    public static void sendErrorJson(HttpServletResponse resp, int statusError, String msg) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.setStatus(statusError);
        String jsonError = String.format("{\"error\": \"%s\"}", msg);
        resp.getWriter().write(jsonError);
    }

    public static void sendRespJson(HttpServletResponse resp, Object obj) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.setStatus(200);
        if (obj != null) {
            String respJson = Utils.convertObjectToJson(obj);
            resp.getWriter().write(respJson);
        }
    }

    public static void setMaxAgeTimeSession(HttpServletRequest req, HttpServletResponse resp, HttpSession session) {
        Cookie[] cookies = req.getCookies();

        session.getMaxInactiveInterval();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("JSESSIONID".equals(cookie.getName())) {
                    // Modificar los atributos de la cookie existente
                    cookie.setHttpOnly(true);
                    cookie.setSecure(true); // Solo HTTPS
                    cookie.setPath("/");

                    // Añadir SameSite=None a la cookie existente
                    resp.setHeader("Set-Cookie", cookie.getName() + "=" + cookie.getValue()
                            + "; Path=" + cookie.getPath()
                            + "; HttpOnly; Secure; SameSite=None");

                    // No uses response.addCookie(), ya que esto agregaría una nueva cookie
                }
            }
        }
    }


}
