package com.drd.drdtrackingapp;

import java.util.ArrayList;

public class Delivery_order_list_tagno_get_or_set {
    private String gstvno,mydate,chemist_code,chemist_name,amount,medicine_items,intid;
    public Delivery_order_list_tagno_get_or_set() {
    }

    public Delivery_order_list_tagno_get_or_set(String gstvno,
                                                String mydate, String chemist_code,
                                                String chemist_name, String amount,
                                                String medicine_items, String intid,
                                                ArrayList<String> genre) {
        this.gstvno = gstvno;
        this.mydate = mydate;
        this.chemist_code = chemist_code;
        this.chemist_name = chemist_name;
        this.amount = amount;
        this.medicine_items = medicine_items;
        this.intid = intid;
    }

    public String gstvno() {
        return gstvno;
    }

    public void gstvno(String gstvno) {
        this.gstvno = gstvno;
    }

    public String mydate() {
        return mydate;
    }

    public void mydate(String mydate) {
        this.mydate = mydate;
    }

    public String chemist_code() {
        return chemist_code;
    }

    public void chemist_code(String chemist_code) {
        this.chemist_code = chemist_code;
    }
    public String chemist_name() {
        return chemist_name;
    }

    public void chemist_name(String chemist_name) {
        this.chemist_name = chemist_name;
    }
    public String amount() {
        return amount;
    }

    public void amount(String amount) {
        this.amount = amount;
    }

    public String medicine_items() {
        return medicine_items;
    }

    public void medicine_items(String medicine_items) {
        this.medicine_items = medicine_items;
    }

    public String intid() {
        return intid;
    }

    public void intid(String intid) {
        this.intid = intid;
    }

}
