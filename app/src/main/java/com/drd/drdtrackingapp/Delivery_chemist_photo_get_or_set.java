package com.drd.drdtrackingapp;

import java.util.ArrayList;

public class Delivery_chemist_photo_get_or_set {
    private String id,image,time,intid;
    public Delivery_chemist_photo_get_or_set() {
    }

    public Delivery_chemist_photo_get_or_set(String id, String image, String time, String intid,
                                             ArrayList<String> genre) {
        this.id     = id;
        this.image  = image;
        this.time   = time;
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

    public String time() {
        return time;
    }

    public void time(String time) {
        this.time = time;
    }

    public String intid() {
        return intid;
    }

    public void intid(String intid) {
        this.intid = intid;
    }
}
