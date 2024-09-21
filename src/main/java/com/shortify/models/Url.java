package com.shortify.models;

import com.google.gson.annotations.Expose;

public class Url {
    @Expose
    private int id;

    @Expose
    private String shortUrl;

    @Expose
    private String originalUrl;

    private int user_id;

    public Url() {
    }

    public Url(int id, String shorUrl, String originalUrl) {
        this.id = id;
        this.shortUrl = shorUrl;
        this.originalUrl = originalUrl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getShortUrl() {
        return shortUrl;
    }

    public void setShortUrl(String shorUrl) {
        this.shortUrl = shorUrl;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int id_user) {
        this.user_id = id_user;
    }

}
