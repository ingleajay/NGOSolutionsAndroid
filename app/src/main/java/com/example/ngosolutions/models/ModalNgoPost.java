package com.example.ngosolutions.models;

public class ModalNgoPost {
    String adminId , pComments , pDesec ,  pImage , pLikes ,  pTitle , uEmail ,pLike_Time;
    long pId, pTime;
    public  ModalNgoPost(){}

    public ModalNgoPost(String adminId, String pComments, String pDesec, long pId,  String pImage, String pLikes, long pTime, String pTitle, String uEmail) {
        this.adminId = adminId;
        this.pComments = pComments;
        this.pDesec = pDesec;
        this.pId = pId;
        this.pImage = pImage;
        this.pLikes = pLikes;
        this.pTime = pTime;
        this.pTitle = pTitle;
        this.uEmail = uEmail;


    }

    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public String getpComments() {
        return pComments;
    }

    public void setpComments(String pComments) {
        this.pComments = pComments;
    }

    public String getpDesec() {
        return pDesec;
    }

    public void setpDesec(String pDesec) {
        this.pDesec = pDesec;
    }

    public String getpImage() {
        return pImage;
    }

    public void setpImage(String pImage) {
        this.pImage = pImage;
    }

    public String getpLikes() {
        return pLikes;
    }

    public void setpLikes(String pLikes) {
        this.pLikes = pLikes;
    }

    public String getpTitle() {
        return pTitle;
    }

    public void setpTitle(String pTitle) {
        this.pTitle = pTitle;
    }

    public String getuEmail() {
        return uEmail;
    }

    public void setuEmail(String uEmail) {
        this.uEmail = uEmail;
    }

    public long getpId() {
        return pId;
    }

    public void setpId(long pId) {
        this.pId = pId;
    }


    public long getpTime() {
        return pTime;
    }

    public void setpTime(long pTime) {
        this.pTime = pTime;
    }

}
