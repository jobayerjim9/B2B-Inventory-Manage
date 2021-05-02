package com.nmadpl.pitstop.models;

public class LoginModel {
    private String email,password;

    public LoginModel() {
        email="";
        password="";
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
