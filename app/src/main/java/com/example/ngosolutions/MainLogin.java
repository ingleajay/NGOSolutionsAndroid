//package com.example.ngosolutions;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//
//import android.app.ProgressDialog;
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.View;
//import android.view.WindowManager;
//import android.widget.Button;
//import android.widget.Toast;
//
//import com.example.ngosolutions.HelperClass.SessionManager;
//import com.google.android.material.textfield.TextInputEditText;
//import com.google.android.material.textfield.TextInputLayout;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.Query;
//import com.google.firebase.database.ValueEventListener;
//import com.hbb20.CountryCodePicker;
//
//public class MainLogin extends AppCompatActivity {
//Button callSignup;
//Button callforgotup;
//Button calllogin;
//TextInputLayout phoneNumber;
//CountryCodePicker countryCodePicker;
//TextInputLayout password;
//TextInputEditText phoneNumberEditText;
//TextInputEditText passwordEditText;
//ProgressDialog progressDialog;
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        setContentView(R.layout.activity_main_screen);
//
//        callSignup = findViewById(R.id.signup_screen);
//        callSignup.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(getApplicationContext(),SignUp.class);
//                startActivity(intent);
//
//            }
//        });
//
//        callforgotup = findViewById(R.id.forgot_screen);
//        callforgotup.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(getApplicationContext(),ForgotPass.class);
//                startActivity(intent);
//
//            }
//        });
//
//        calllogin = findViewById(R.id.login);
//        phoneNumber = findViewById(R.id.login_phone_number);
//        phoneNumberEditText  = findViewById(R.id.phoneNumberEditText);
//        passwordEditText  = findViewById(R.id.passwordEditText);
//        countryCodePicker = findViewById(R.id.login_country_code_picker);
//        password = findViewById(R.id.password);
//        progressDialog = new ProgressDialog(this);
//        progressDialog.setMessage("Login...");
//
////        SessionManager sessionManager = new SessionManager(MainScreen.this , SessionManager.SESSION_REMEMBERME);
////        if(sessionManager.checkRememberMe()){
////            HashMap<String, String> rememberMeDetails = sessionManager.getRememberMeDetailsFromSession();
////            phoneNumberEditText.setText(rememberMeDetails.get(SessionManager.KEY_SESSIONPHONE));
////            passwordEditText.setText(rememberMeDetails.get(SessionManager.KEY_SESSIONPASSWORD));
////
////        }
//
//    }
//    public void LoginUser(View view){
//
//
//
//        if (!validatePhoneNumber() | !validatePassword()) {
//            return ;
//        }
//        progressDialog.show();
//            String _phoneNumber = phoneNumber.getEditText().getText().toString().trim();
//          final String _password =password.getEditText().getText().toString().trim();
//
//
//            if(_phoneNumber.charAt(0)=='0'){
//                _phoneNumber = _phoneNumber.substring(1);
//            }
//             final String _completePhoneNumber = "+" + countryCodePicker.getFullNumber()+_phoneNumber;
//
//            //query
////            if(rememberMe.isChecked()){
////                SessionManager sessionManager = new SessionManager(MainScreen.this , SessionManager.SESSION_REMEMBERME);
////                sessionManager.createRememberMeSession(_phoneNumber,_password);
////
////
////            }
//            Query checkUser = FirebaseDatabase.getInstance().getReference("App_Users").orderByChild("_phoneNo").equalTo(_completePhoneNumber);
//
//            checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                    if(dataSnapshot.exists()){
//                        phoneNumber.setError(null);
//                        phoneNumber.setErrorEnabled(false);
//
//                        String systemPass = dataSnapshot.child(_completePhoneNumber).child("_password").getValue(String.class);
//                        if(systemPass.equals(_password)){
//
//                            password.setError(null);
//                            password.setErrorEnabled(false);
//                            String _reguser = dataSnapshot.child(_completePhoneNumber).child("_reguser").getValue(String.class);
//                            String _reg_email = dataSnapshot.child(_completePhoneNumber).child("_reg_email").getValue(String.class);
//                            String _city = dataSnapshot.child(_completePhoneNumber).child("_city").getValue(String.class);
//                            String _date = dataSnapshot.child(_completePhoneNumber).child("_date").getValue(String.class);
//                            String _gender = dataSnapshot.child(_completePhoneNumber).child("_gender").getValue(String.class);
//                            String _password = dataSnapshot.child(_completePhoneNumber).child("_password").getValue(String.class);
//                            String _phoneNo = dataSnapshot.child(_completePhoneNumber).child("_phoneNo").getValue(String.class);
//
//                            // create a session
//                            SessionManager sessionManager = new SessionManager(MainLogin.this, SessionManager.SESSION_USERSESSION);
//                            sessionManager.createLoginSession(_reguser,_reg_email,_city,_password,_date,_gender,_phoneNo);
//
//                            startActivity(new Intent(MainLogin.this , UserProfile.class));
//                            finish();
////                            Toast.makeText(MainScreen.this, _reguser+"\n"+_reg_email+"\n"+_city+"\n"+_date, Toast.LENGTH_SHORT).show();
//
//                        }
//                        else{
//                            progressDialog.dismiss();
//                            Toast.makeText(MainLogin.this, "Password does Not match!", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                    else{
//                        progressDialog.dismiss();
//                        Toast.makeText(MainLogin.this, "No such user exists!", Toast.LENGTH_SHORT).show();
//                    }
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//                    progressDialog.dismiss();
//                    Toast.makeText(MainLogin.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
//                }
//            });
//
//
//
//    }
//
//
//
//
//    private boolean validatePassword() {
//        String val = password.getEditText().getText().toString().trim();
//        String checkPassword = "^" +
//                "(?=.*[0-9])" +         //at least 1 digit
//                "(?=.*[a-z])" +         //at least 1 lower case letter
//                "(?=.*[A-Z])" +         //at least 1 upper case letter
//                "(?=.*[a-zA-Z])" +      //any letter
//                "(?=.*[@#$%^&+=])" +    //at least 1 special character
////                "(?=S+$)" +           //no white spaces
//                ".{6,}" +               //at least 6 characters
//                "$";
//
//        if (val.isEmpty()) {
//            password.setError("Field can not be empty");
//            return false;
//        } else if (!val.matches(checkPassword)) {
//            password.setError("Password should contain 6 characters!");
//            return false;
//        } else {
//            password.setError(null);
//            password.setErrorEnabled(false);
//            return true;
//        }
//    }
//
//    private boolean validatePhoneNumber() {
//        String val = phoneNumber.getEditText().getText().toString().trim();
//        String checkspaces = "{1,10}";
//        if (val.isEmpty()) {
//            phoneNumber.setError("Enter valid phone number");
//            return false;
//        }else {
//            phoneNumber.setError(null);
//            phoneNumber.setErrorEnabled(false);
//            return true;
//        }
//    }
//}