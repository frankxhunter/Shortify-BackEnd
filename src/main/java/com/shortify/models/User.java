package com.shortify.models;

import com.google.gson.annotations.Expose;

public class User {

    private int id;
    @Expose
    private String username;
    private String email;
    // El password solo debe ponerse al momento de crear el user y luego
    // eliminarse,por seguridad
    private String password;

    public User() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        if (username != null)
            this.username = username.trim();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email != null)
            this.email = email.trim();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        if (password != null)
            this.password = password.trim();
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = false;
        if (obj instanceof User) {
            User user = (User) obj;
            if ((user.getUsername() != null && user.getUsername().equals(this.getUsername()))
                    || (user.getEmail() != null && user.getEmail().equals(this.email))) {
                result = true;
            }
        }
        return result;
    }

}
