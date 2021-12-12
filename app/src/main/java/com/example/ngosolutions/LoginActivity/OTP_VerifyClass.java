package com.example.ngosolutions.LoginActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.chaos.view.PinView;
import com.example.ngosolutions.HelperClass.UserHelp;
import com.example.ngosolutions.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

public class OTP_VerifyClass extends AppCompatActivity {
    PinView pinFromUser;
    String  codebysystem;
    ImageView calllogin;
    ProgressDialog progressDialog;
    String _reguser , _phoneNo , _reg_email , _date , _gender , _password, _city, _whatToDo, _uid;
    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_o_t_p__verify_class);
        calllogin = findViewById(R.id.login_screen_otp);
        calllogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SigninClass.class);
                startActivity(intent);
                finish();
            }
        });
        pinFromUser = findViewById(R.id.pin_view);
        progressDialog = new ProgressDialog(this , R.style.MyAlertDialogStyle);
        progressDialog.setMessage("Verification...");
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        _uid = user.getUid();
        _phoneNo = getIntent().getStringExtra("phoneNo");
        _reguser = getIntent().getStringExtra("reguser");
        _reg_email = getIntent().getStringExtra("reg_email");
        _password = getIntent().getStringExtra("password");
        _date = getIntent().getStringExtra("date");
        _city = getIntent().getStringExtra("city");
        _gender = getIntent().getStringExtra("gender");
        _whatToDo = getIntent().getStringExtra("whatToDo");
        sendVerificationCodeToUser(_phoneNo);



    }
        private void sendVerificationCodeToUser(String phoneNo) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNo,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks

    }
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks =
            new PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
                @Override
                public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                    super.onCodeSent(s, forceResendingToken);
                    codebysystem = s;
                }

                @Override
                public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                    String code = phoneAuthCredential.getSmsCode();
                    if(code !=null){
                        pinFromUser.setText(code);
                        verifyCode(code);
                    }
                }

                @Override
                public void onVerificationFailed(@NonNull FirebaseException e) {
                    Toast.makeText(OTP_VerifyClass.this , e.getMessage() , Toast.LENGTH_LONG).show();
                }
            };

    private void verifyCode(String code ) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codebysystem,code);
        signInWithPhoneAuthCredential(credential);

    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            if(_whatToDo.equals("updateData")){
//                                updateOldUserdata();
                                FirebaseUser user = firebaseAuth.getCurrentUser();
                            }
                            else if (_whatToDo.equals("createNewUser")) {
                                storeNewUser();
                                FirebaseUser user = task.getResult().getUser();
                                startActivity(new Intent(OTP_VerifyClass.this, SignUpSucessClass.class));
                                finish();
                            }
                            // ...
                        } else {

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Toast.makeText(OTP_VerifyClass.this, "Verification not completed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }

//    private void updateOldUserdata() {
//        progressDialog.show();
//        Intent intent = new Intent(getApplicationContext(),SetNewPassClass.class);
//        intent.putExtra("phoneNo",_phoneNo);
//        startActivity(intent);
//        finish();
//    }

    private void storeNewUser() {
        progressDialog.show();
        FirebaseDatabase rootNode = FirebaseDatabase.getInstance();
        DatabaseReference reference = rootNode.getReference("App_Users");
        UserHelp addNewUser = new UserHelp(_reguser, _reg_email,_city,_date,_gender,_phoneNo,_uid);
        reference.child(_uid).setValue(addNewUser);

    }

    public  void  callNextScreenFromOTP(View view) {
        String code = pinFromUser.getText().toString();
        if (!code.isEmpty()) {
            verifyCode(code );

        }
    }


}