package com.shortify.models;

import jakarta.servlet.http.HttpServletRequest;

public class InfoRequest {
    private int idUrl;
    private String ip;
    private String browser;
    private String os;
    private String architecture;

    public InfoRequest() {
    }

    public InfoRequest(HttpServletRequest req, int url_id) {

        idUrl = url_id;

        // Obtener la IP
        ip = getClientIp(req);

        // Obtener el User-Agent
        String userAgent = req.getHeader("User-Agent");

        // Detectar el sistema operativo
        if (userAgent.contains("Windows NT")) {
            os = "Windows";
            architecture = userAgent.contains("Win64") || userAgent.contains("x64") ? "64-bit" : "32-bit";
        } else if (userAgent.contains("Mac OS X") && !userAgent.contains("Mobile")) {
            os = "macOS";
            architecture = userAgent.contains("Intel") ? "Intel" : "Unknown";
        } else if (userAgent.contains("Android")) {
            os = "Android";
            architecture = "Mobile";
        } else if (userAgent.contains("iPhone")) {
            os = "iOS";
            architecture = "Mobile";
        } else if (userAgent.contains("Linux")) {
            os = "Linux";
            architecture = userAgent.contains("x86_64") ? "64-bit" : "32-bit";
        }

        // Detectar el navegador
        if (userAgent.contains("Chrome")) {
            browser = "Chrome";
        } else if (userAgent.contains("Firefox")) {
            browser = "Firefox";
        } else if (userAgent.contains("Safari") && !userAgent.contains("Chrome")) {
            browser = "Safari";
        } else if (userAgent.contains("Edge")) {
            browser = "Edge";
        } else if (userAgent.contains("MSIE") || userAgent.contains("Trident")) {
            browser = "Internet Explorer";
        }

    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getBrowser() {
        return browser;
    }

    public void setBrowser(String browser) {
        this.browser = browser;
    }

    public String getOs() {
        return os;
    }

    public int getIdUrl() {
        return idUrl;
    }

    public void setIdUrl(int idUser) {
        this.idUrl = idUser;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getArchitecture() {
        return architecture;
    }

    public void setArchitecture(String architecture) {
        this.architecture = architecture;
    }

    @Override
    public String toString() {
        return "IP: " + ip + "\n" +
                "Browser: " + browser + "\n" +
                "OS: " + os + "\n" +
                "Architecture: " + architecture;
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
