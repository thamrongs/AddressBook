package com.buu.se.addressbook;

/**
 * Created by thamrongs on 5/13/15 AD.
 */
public class Addresses {

    int con_id;
    String con_name;
    String con_company;
    String con_tel;
    String con_email;
    String con_image;

    public int getCon_id() {
        return con_id;
    }

    public void setCon_id(int con_id) {
        this.con_id = con_id;
    }

    public String getCon_name() {
        return con_name;
    }

    public void setCon_name(String con_name) {
        this.con_name = con_name;
    }

    public String getCon_tel() {
        return con_tel;
    }

    public void setCon_tel(String con_tel) {
        this.con_tel = con_tel;
    }

    public String getCon_email() {
        return con_email;
    }

    public void setCon_email(String con_email) {
        this.con_email = con_email;
    }

    public String getCon_image() {
        return con_image;
    }

    public void setCon_image(String con_image) {
        this.con_image = con_image;
    }

    public String getCon_company() {
        return con_company;
    }

    public void setCon_company(String con_company) {
        this.con_company = con_company;
    }
}
