package com.example.ngosolutions.HelperClass;

public class UserHelp {
    String _reguser , _reg_email, _city , _date ,  _gender , _phoneNo, _uid;
    public UserHelp(){

    }
    public UserHelp(String _reguser, String _reg_email, String _city, String _date, String _gender, String _phoneNo , String _uid) {
        this._reguser = _reguser;
        this._reg_email = _reg_email;
        this._city = _city;
        this._uid = _uid;
//        this._password = _password;
        this._date = _date;
        this._gender = _gender;
        this._phoneNo = _phoneNo;
    }

    public String get_reguser() {
        return _reguser;
    }

    public void set_reguser(String _reguser) {
        this._reguser = _reguser;
    }
    public String get_uid() {
        return _uid;
    }
    public void set_uid(String _uid) {
        this._uid = _uid;
    }

    public String get_reg_email() {
        return _reg_email;
    }

    public void set_reg_email(String _reg_email) {
        this._reg_email = _reg_email;
    }

    public String get_city() {
        return _city;
    }

    public void set_city(String _city) {
        this._city = _city;
    }

//    public String get_password() {
//        return _password;
//    }

//    public void set_password(String _password) {
//        this._password = _password;
//    }

    public String get_date() {
        return _date;
    }

    public void set_date(String _date) {
        this._date = _date;
    }

    public String get_gender() {
        return _gender;
    }

    public void set_gender(String _gender) {
        this._gender = _gender;
    }

    public String get_phoneNo() {
        return _phoneNo;
    }

    public void set_phoneNo(String _phoneNo) {
        this._phoneNo = _phoneNo;
    }
}
