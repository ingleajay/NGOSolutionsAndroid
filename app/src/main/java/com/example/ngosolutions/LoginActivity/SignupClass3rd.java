package com.example.ngosolutions.LoginActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import com.example.ngosolutions.R;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.hbb20.CountryCodePicker;

public class SignupClass3rd extends AppCompatActivity {
    Button calllogin;
    TextInputLayout phoneNumber;
    CountryCodePicker countryCodePicker;
    ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_signup_class3rd);
        calllogin = findViewById(R.id.login_screen);
        calllogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SigninClass.class);
                startActivity(intent);
            }
        });

        phoneNumber = findViewById(R.id.signup_phone_number);
        countryCodePicker = findViewById(R.id.country_code_picker);
        progressDialog = new ProgressDialog(this , R.style.MyAlertDialogStyle);
        progressDialog.setMessage("Register...");
    }

    public void RegisterUser(View view) {
        if (!validatePhoneNumber()) {
            return ;
        }
        else {
            progressDialog.show();
            String _reguser = getIntent().getStringExtra("reguser");
            String _reg_email = getIntent().getStringExtra("reg_email");
            String _password = getIntent().getStringExtra("password");
            String _city = getIntent().getStringExtra("city");
            String _date = getIntent().getStringExtra("date");
            String _gender = getIntent().getStringExtra("gender");
            firebaseAuth = FirebaseAuth.getInstance();
            FirebaseUser user = firebaseAuth.getCurrentUser();
            String _uid = user.getUid();
            String _getUserEnterPhoneNo = phoneNumber.getEditText().getText().toString().trim();
            String _phoneno = "+" + countryCodePicker.getFullNumber()+_getUserEnterPhoneNo;
            Intent intent = new Intent(getApplicationContext(),OTP_VerifyClass.class);

            // pass all fields to the next activity
            intent.putExtra("user", _uid);
            intent.putExtra("reguser", _reguser);
            intent.putExtra("reg_email", _reg_email);
            intent.putExtra("password", _password);
            intent.putExtra("city", _city);
            intent.putExtra("date", _date);
            intent.putExtra("gender", _gender);
            intent.putExtra("phoneNo", _phoneno);
            intent.putExtra("whatToDo","createNewUser");
            startActivity(intent);
            finish();
        }
    }

    private boolean validatePhoneNumber() {
        String val = phoneNumber.getEditText().getText().toString().trim();
        String checkspaces = "{1,10}";
        if (val.isEmpty()) {
            phoneNumber.setError("Enter valid phone number");
            return false;
        }else {
            phoneNumber.setError(null);
            phoneNumber.setErrorEnabled(false);
            return true;
        }
    }
}