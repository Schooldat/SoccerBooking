package com.subi.soccerbooking.model;

import java.io.Serializable;

public class User implements Serializable {
    private String email, passworld;

    public User() {
    }

    public User(String email, String passworld) {
        this.email = email;
        this.passworld = passworld;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassworld() {
        return passworld;
    }

    public void setPassworld(String passworld) {
        this.passworld = passworld;
    }
}
