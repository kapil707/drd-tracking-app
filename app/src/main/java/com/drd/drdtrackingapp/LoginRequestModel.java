package com.drd.drdtrackingapp;

public class LoginRequestModel {
    private String username;
    private String password;
    private String submit;

    public LoginRequestModel(String username, String password, String submit) {
        this.username = username;
        this.password = password;
        this.submit = submit;
    }
}

