package com.example.ngosolutions.models;

public class ModalNgo {
    String orgname , cause , email , shortdesc , country , city , pincode , state ,userId ,nFollow;
    String pTime;
    public  ModalNgo(){}

    public ModalNgo(String orgname, String cause, String email, String shortdesc,String pTime,String country, String city, String pincode, String state, String userId , String nFollow) {
        this.orgname = orgname;
        this.cause = cause;
        this.email = email;
        this.shortdesc = shortdesc;
        this.pTime = pTime;
        this.country = country;
        this.city = city;
        this.pincode = pincode;
        this.state = state;
        this.userId = userId;
        this.nFollow = nFollow;
    }

    public String getnFollow() {
        return nFollow;
    }

    public void setnFollow(String nFollow) {
        this.nFollow = nFollow;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getOrgname() {
        return orgname;
    }

    public void setOrgname(String orgname) {
        this.orgname = orgname;
    }

    public String getCause() {
        return cause;
    }

    public void setCause(String cause) {
        this.cause = cause;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getShortdesc() {
        return shortdesc;
    }

    public void setShortdesc(String shortdesc) {
        this.shortdesc = shortdesc;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getState() {
        return state;
    }

    public String getpTime() {
        return pTime;
    }

    public void setpTime(String  pTime) {
        this.pTime = pTime;
    }

    public void setState(String state) {
        this.state = state;
    }
}
