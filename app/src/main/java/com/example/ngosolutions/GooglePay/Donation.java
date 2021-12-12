package com.example.ngosolutions.GooglePay;

public class Donation {
    String payerName;
    String UpiId;
    String msgNote;
    String sendAmount;
    String Date_Time;
    String ngo_adminid;
    String app_userid;

    public Donation(String payerName,String UpiId, String msgNote , String sendAmount,String Date_Time,String ngo_adminid , String app_userid){
        this.payerName=payerName;
        this.UpiId=UpiId;
        this.msgNote=msgNote;
        this.sendAmount=sendAmount;
        this.Date_Time=Date_Time;
        this.ngo_adminid=ngo_adminid;
        this.app_userid=app_userid;
    }

    public String getPayerName() {
        return payerName;
    }

    public String getApp_userid() {
        return app_userid;
    }

    public String getDate_Time() {
        return Date_Time;
    }

    public String getNgo_adminid() {
        return ngo_adminid;
    }

    public String getUpiId() {
        return UpiId;
    }

    public String getMsgNote() {
        return msgNote;
    }

    public String getSendAmount() {
        return sendAmount;
    }
}
