package com.drd.drdtrackingapp.ui.yourlist;

import java.util.ArrayList;

public class YourList_get_or_set {
    private String chemist_id,name,amt,gstvno,intid;
    public YourList_get_or_set() {
    }

    public YourList_get_or_set(String chemist_id, String name, String amt, String gstvno, String intid,
                               ArrayList<String> genre) {
        this.chemist_id = chemist_id;
        this.name = name;
        this.amt = amt;
        this.gstvno = gstvno;
        this.intid = intid;
    }

    public String chemist_id() {
        return chemist_id;
    }

    public void chemist_id(String chemist_id) {
        this.chemist_id = chemist_id;
    }

    public String name() {
        return name;
    }

    public void name(String name) {
        this.name = name;
    }

    public String amt() {
        return amt;
    }

    public void amt(String amt) {
        this.amt = amt;
    }

    public String gstvno() {
        return gstvno;
    }

    public void gstvno(String gstvno) {
        this.gstvno = gstvno;
    }

    public String intid() {
        return intid;
    }

    public void intid(String intid) {
        this.intid = intid;
    }

}
