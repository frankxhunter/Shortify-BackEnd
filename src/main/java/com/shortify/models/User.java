package com.shortify.models;

public class User {

    private int id;
    private String username;
    private String email;
    // El password solo debe ponerse al momento de crear el user y luego eliminarse,por seguridad
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
        this.username = username.trim();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email.trim();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password.trim();
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = false;
        if (obj instanceof User) {
            User user = (User) obj;
            if (user.getPassword().equals(this.password)
                    && (user.getUsername().equals(this.getUsername())
                            || user.getEmail().equals(this.email))) {
                                result = true;
            }
        }
        return result;
    }

}
