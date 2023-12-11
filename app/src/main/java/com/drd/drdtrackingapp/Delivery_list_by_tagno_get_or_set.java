package com.drd.drdtrackingapp;

import java.util.ArrayList;

public class Delivery_list_by_tagno_get_or_set {
    private String mytagno,mydate,mytime,intid;
    public Delivery_list_by_tagno_get_or_set() {
    }

    public Delivery_list_by_tagno_get_or_set(String mytagno, String mydate, String mytime, String intid,
                                             ArrayList<String> genre) {
        this.mytagno = mytagno;
        this.mydate = mydate;
        this.mytime = mytime;
        this.intid = intid;
    }

    public String mytagno() {
        return mytagno;
    }

    public void mytagno(String mytagno) {
        this.mytagno = mytagno;
    }

    public String mydate() {
        return mydate;
    }

    public void mydate(String mydate) {
        this.mydate = mydate;
    }

    public String mytime() {
        return mytime;
    }

    public void mytime(String mytime) {
        this.mytime = mytime;
    }
    
    public String intid() {
        return intid;
    }

    public void intid(String intid) {
        this.intid = intid;
    }

}
