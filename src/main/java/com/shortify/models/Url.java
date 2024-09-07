package com.shortify.models;

public class Url {
    private int id; 
    private String shortUrl;
    private String orginalUrl;

    

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

    
}
