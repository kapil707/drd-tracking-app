package com.drd.drdtrackingapp;

import java.util.ArrayList;

public class Delivery_chemist_photo_get_or_set {
    private String id,image,datetime,intid;
    public Delivery_chemist_photo_get_or_set() {
    }

    public Delivery_chemist_photo_get_or_set(String id, String image, String datetime, String intid,
                                             ArrayList<String> genre) {
        this.id     = id;
        this.image  = image;
        this.datetime   = datetime;
        this.intid  = intid;
    }

    public String id() {
        return id;
    }

    public void id(String id) {
        this.id = id;
    }

    public String image() {
        return image;
    }

    public void image(String image) {
        this.image = image;
    }

    public String datetime() {
        return datetime;
    }

    public void datetime(String datetime) {
        this.datetime = datetime;
    }

    public String intid() {
        return intid;
    }

    public void intid(String intid) {
        this.intid = intid;
    }
}
