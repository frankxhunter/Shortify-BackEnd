package com.frank.shortiy.shortify.Utils;

import jakarta.servlet.http.HttpServletRequest;

public class UtilsRequest {

    private static String baseUrl = null;

    public static String getBaseUrl(HttpServletRequest request) {
        if (baseUrl == null) {
            baseUrl = request.getScheme() + "://"
                    + request.getServerName() + ""
                    + ":" + request.getServerPort();
        }
        return baseUrl;
    }

    public static String getClientIp(HttpServletRequest req) {
        String ip = req.getHeader("X-Forwarder-For");
        if (ip == null || ip.isEmpty() || ip.equalsIgnoreCase("unknown")) {
            ip = req.getRemoteAddr();
        }
        return ip;
    }

    private static String getUserAgent(HttpServletRequest req) {
        return req.getHeader("User-agent");
    }

    public static String getBrowser(HttpServletRequest req) {
        String userAgent = getUserAgent(req);
        if (userAgent.contains("Chrome")) return "Chrome";
        if (userAgent.contains("Firefox")) return "Firefox";
        if (userAgent.contains("Safari")) return "Safari";
        if (userAgent.contains("Edge")) return "Edge";
        return "Unknown";
    }

    public static String getOs(HttpServletRequest req) {
        String userAgent = getUserAgent(req);
        if (userAgent.contains("Windows")) return "Windows";
        if (userAgent.contains("Mac")) return "MacOS";
        if (userAgent.contains("X11")) return "Unix";
        if (userAgent.contains("Android")) return "Android";
        if (userAgent.contains("iPhone")) return "iOS";
        return "Unknown";
    }

    public static String getArchitecture(HttpServletRequest req) {
        String userAgent = getUserAgent(req);
        if (userAgent.contains("x64") || userAgent.contains("Win64")) return "64-bit";
        if (userAgent.contains("x86")) return "32-bit";
        return "Unknown";
    }
}
