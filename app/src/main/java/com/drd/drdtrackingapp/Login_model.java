package com.drd.drdtrackingapp;

import com.google.gson.annotations.SerializedName;

public class Login_model {
    @SerializedName("user_session")
    private String user_session;

    @SerializedName("user_fname")
    private String user_fname;

    @SerializedName("user_code")
    private String user_code;

    @SerializedName("user_altercode")
    private String user_altercode;

    @SerializedName("user_password")
    private String user_password;

    @SerializedName("user_alert")
    private String user_alert;

    @SerializedName("user_return")
    private String user_return;

    private String submit;
    private String user_name;
    private String password;
    private String getfcmtoken;

    public String getUser_session() {
        return user_session;
    }

    public void setUser_session(String user_session) {
        this.user_session = user_session;
    }

    public String getUser_fname() {
        return user_fname;
    }

    public void setUser_fname(String user_fname) {
        this.user_fname = user_fname;
    }

    public String getUser_code() {
        return user_code;
    }

    public void setUser_code(String user_code) {
        this.user_code = user_code;
    }

    public String getUser_altercode() {
        return user_altercode;
    }

    public void setUser_altercode(String user_altercode) {
        this.user_altercode = user_altercode;
    }

    public String getUser_password() {
        return user_password;
    }

    public void setUser_password(String user_password) {
        this.user_password = user_password;
    }

    public String getUser_alert() {
        return user_alert;
    }

    public void setUser_alert(String user_alert) {
        this.user_alert = user_alert;
    }

    public String getUser_return() {
        return user_return;
    }

    public void setUser_return(String user_return) {
        this.user_return = user_return;
    }

    public String getSubmit() {
        return submit;
    }

    public void setSubmit(String submit) {
        this.submit = submit;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getGetfcmtoken() {
        return getfcmtoken;
    }

    public void setGetfcmtoken(String getfcmtoken) {
        this.getfcmtoken = getfcmtoken;
    }
}

