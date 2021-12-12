package com.example.ngosolutions.LoginActivity;

public class ModalUsers {
    String _reguser , _reg_email  ,search ,_phoneNo , image , _uid ,onlineStatus , typingTo;
    boolean isBlocked = false;
    public ModalUsers(){

    }

    public ModalUsers(String _reguser, String _reg_email, String search, String _phoneNo, String image, String _uid, String onlineStatus, String typingTo, boolean isBlocked) {
        this._reguser = _reguser;
        this._reg_email = _reg_email;
        this.search = search;
        this._phoneNo = _phoneNo;
        this.image = image;
        this._uid = _uid;
        this.onlineStatus = onlineStatus;
        this.typingTo = typingTo;
        this.isBlocked = isBlocked;
    }

    public String get_reguser() {
        return _reguser;
    }

    public void set_reguser(String _reguser) {
        this._reguser = _reguser;
    }

    public String get_reg_email() {
        return _reg_email;
    }

    public void set_reg_email(String _reg_email) {
        this._reg_email = _reg_email;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String get_phoneNo() {
        return _phoneNo;
    }

    public void set_phoneNo(String _phoneNo) {
        this._phoneNo = _phoneNo;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String get_uid() {
        return _uid;
    }

    public void set_uid(String _uid) {
        this._uid = _uid;
    }

    public String getOnlineStatus() {
        return onlineStatus;
    }

    public void setOnlineStatus(String onlineStatus) {
        this.onlineStatus = onlineStatus;
    }

    public String getTypingTo() {
        return typingTo;
    }

    public void setTypingTo(String typingTo) {
        this.typingTo = typingTo;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public void setBlocked(boolean blocked) {
        isBlocked = blocked;
    }
}
