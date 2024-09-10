package com.shortify.models;

public class Url {
    private int id; 
    private String shortUrl;
    private String orginalUrl;
    private int user_id;

    

    public Url() {
    }
    public Url(int id, String shorUrl, String orginalUrl) {
        this.id = id;
        this.shortUrl = shorUrl;
        this.orginalUrl = orginalUrl;
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
    public String getOrginalUrl() {
        return orginalUrl;
    }
    public void setOrginalUrl(String orginalUrl) {
        this.orginalUrl = orginalUrl;
    }
    public int getUser_id() {
        return user_id;
    }
    public void setUser_id(int id_user) {
        this.user_id = id_user;
    }
    

    
}
