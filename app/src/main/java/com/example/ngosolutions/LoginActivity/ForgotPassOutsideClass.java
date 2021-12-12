package com.example.ngosolutions.LoginActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.ngosolutions.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ForgotPassOutsideClass extends AppCompatActivity {
    TextInputLayout log_email;
    ProgressDialog progressDialog;
    ImageView passback;
    private FirebaseAuth firebaseAuth;
    Button calllogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_forgot_pass_outside_class);
        log_email = findViewById(R.id.log_email);
        passback= findViewById(R.id.pass_back);
        progressDialog = new ProgressDialog(this , R.style.MyAlertDialogStyle);
        progressDialog.setMessage("Sending Email....");
        passback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SigninClass.class);
                startActivity(intent);
                finish();
            }
        });
        calllogin = findViewById(R.id.login_screen_forgot);
        calllogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SigninClass.class);
                startActivity(intent);
                finish();
            }
        });
        firebaseAuth = FirebaseAuth.getInstance();
    }

    public  void SendEmail(View view){
        final FirebaseUser user = firebaseAuth.getCurrentUser();
        if (!validateEmail()  ) {
            return ;
        }
        else {

            final String email = log_email.getEditText().getText().toString().trim();
            progressDialog.show();
            if (user == null ) {
                firebaseAuth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                progressDialog.dismiss();
                                if (task.isSuccessful()) {
                                    Toast.makeText(ForgotPassOutsideClass.this, "Email Sent", Toast.LENGTH_SHORT).show();
                                } else {
                                    progressDialog.dismiss();
                                    Toast.makeText(ForgotPassOutsideClass.this, "Email Sent Failed", Toast.LENGTH_SHORT).show();

                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(ForgotPassOutsideClass.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
            else{
                progressDialog.dismiss();
                Toast.makeText(ForgotPassOutsideClass.this, "Email Not Send", Toast.LENGTH_SHORT).show();

            }
        }
    }


    private boolean validateEmail() {
        String email =log_email.getEditText().getText().toString().trim();
        String checkEmail = "[a-zA-Z0-9._-]+@[a-z]+.+[a-z]+";

        if (email.isEmpty()) {
            log_email.setError("Field can not be empty");
            return false;
        } else if (!email.matches(checkEmail)) {
            log_email.setError("Invalid Email!");
            return false;
        } else {
            log_email.setError(null);
            log_email.setErrorEnabled(false);
            return true;
        }
    }

}