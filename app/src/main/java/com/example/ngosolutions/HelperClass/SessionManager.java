package com.example.ngosolutions.HelperClass;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

public class SessionManager {
    SharedPreferences usersSession;
    SharedPreferences.Editor editor;
    Context context;

    public  static  final String SESSION_USERSESSION = "userLoginSession";
//    public  static  final String SESSION_REMEMBERME = "rememberMe";
    private static  final String IS_LOGIN = "IsLoggedIn";
// user session
    public static  final String KEY_REG_NAME = "_reguser";
    public static  final String KEY_REG_EMAIL= "_reg_email";
    public static  final String KEY_CITY= "_city";
    public static  final String KEY_PASSWORD= "_password";
    public static  final String KEY_DATE= "_date";
    public static  final String KEY_GENDER= "_gender";
    public static  final String KEY_PHONE= "_phoneNo";
// remember me  session

//    private static  final  String IS_REMEMBER = "IsRememberMe";
//    public static  final String KEY_SESSIONPHONE= "_phoneNo";
//    public static  final String KEY_SESSIONPASSWORD= "_password";
// constructor
    public SessionManager(Context _context , String sessionName){
        context = _context;
        usersSession = context.getSharedPreferences(sessionName,Context.MODE_PRIVATE);
        editor = usersSession.edit();
    }
    public void createLoginSession(String _reguser, String _reg_email, String _city,  String _date, String _gender, String _phoneNo){
        editor.putBoolean(IS_LOGIN,true);
        editor.putString(KEY_REG_NAME,_reguser);
        editor.putString(KEY_REG_EMAIL,_reg_email);
        editor.putString(KEY_CITY,_city);
//        editor.putString(KEY_PASSWORD,_password);
        editor.putString(KEY_DATE,_date);
        editor.putString(KEY_GENDER,_gender);
        editor.putString(KEY_PHONE,_phoneNo);


        editor.commit();

    }

    public HashMap<String,String> getUserDetailFromSession(){
        HashMap<String,String> userData = new HashMap<String, String>();

        userData.put(KEY_REG_NAME, usersSession.getString(KEY_REG_NAME,null));
        userData.put(KEY_REG_EMAIL, usersSession.getString(KEY_REG_EMAIL,null));
        userData.put(KEY_CITY, usersSession.getString(KEY_CITY,null));
//        userData.put(KEY_PASSWORD, usersSession.getString(KEY_PASSWORD,null));
        userData.put(KEY_DATE, usersSession.getString(KEY_DATE,null));
        userData.put(KEY_GENDER, usersSession.getString(KEY_GENDER,null));
        userData.put(KEY_PHONE, usersSession.getString(KEY_PHONE,null));

        return  userData;
    }

    public boolean checkLogin(){
        if(usersSession.getBoolean(IS_LOGIN, true)){
            return  true;
        }
        else
        {
            return  false;
        }
    }

    public void logout(){
        editor.clear();
        editor.commit();
    }

//    public void createRememberMeSession( String _phoneNo , String _password){
//        editor.putBoolean(IS_REMEMBER,true);
//        editor.putString(KEY_SESSIONPHONE,_phoneNo);
//        editor.putString(KEY_SESSIONPASSWORD,_password);
//        editor.commit();
//
//    }
//
//    public HashMap<String,String> getRememberMeDetailsFromSession(){
//        HashMap<String,String> userData = new HashMap<String, String>();
//        userData.put(KEY_SESSIONPHONE, usersSession.getString(KEY_SESSIONPHONE,null));
//        userData.put(KEY_SESSIONPASSWORD, usersSession.getString(KEY_SESSIONPASSWORD,null));
//        return  userData;
//    }
//
//    public boolean checkRememberMe(){
//        if(usersSession.getBoolean(IS_REMEMBER, false)){
//            return  true;
//        }
//        else
//        {
//            return  false;
//        }
//    }

}
