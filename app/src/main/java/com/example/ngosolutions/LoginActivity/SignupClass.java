package com.example.ngosolutions.LoginActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ngosolutions.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignupClass extends AppCompatActivity {

    Button calllogin;
    Button next;
    ImageView img;
    TextView reg_head;
    TextInputLayout reguser;
    TextInputLayout reg_email;
    TextInputLayout password;
    TextInputLayout city;
    private FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_signup_class);
        calllogin = findViewById(R.id.login_screen);
        calllogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),SigninClass.class);
                startActivity(intent);
            }
        });
        next = findViewById(R.id.next_reg);
        img = findViewById(R.id.logo_image1);
        reg_head = findViewById(R.id.slogan_name);

        reguser = findViewById(R.id.reguser);
        reg_email = findViewById(R.id.reg_email);
        password = findViewById(R.id.password);
        city = findViewById(R.id.city);

        progressDialog = new ProgressDialog(this , R.style.MyAlertDialogStyle);
        progressDialog.setMessage("Register...");
        firebaseAuth = FirebaseAuth.getInstance();
    }
    public void RegisterUser(View view){

        if (!validateEmail() | !validatePassword() | !validateUsername() | !validateCity()) {
            return ;
        }
        else {
            progressDialog.show();
            String _reguser = reguser.getEditText().getText().toString().trim();
            String _reg_email =reg_email.getEditText().getText().toString().trim();
            String _password = password.getEditText().getText().toString().trim();
            String _city = city.getEditText().getText().toString().trim();

            firebaseAuth.createUserWithEmailAndPassword(_reg_email, _password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success,
                                FirebaseUser user = firebaseAuth.getCurrentUser();
                                String uid = user.getUid();
                                Toast.makeText(SignupClass.this, "Register...", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), ProfileClass.class);
                                intent.putExtra("user",uid);
                                startActivity(intent);
                                finish();

                            } else {
                                progressDialog.dismiss();
                                // If sign in fails, display a message to the user.
                                Toast.makeText(SignupClass.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(SignupClass.this, ""+e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            });

            Intent intent = new Intent(getApplicationContext(), SignupClass2nd.class);

            // pass all fields to the next activity

            intent.putExtra("reguser", _reguser);
            intent.putExtra("reg_email", _reg_email);
            intent.putExtra("password", _password);
            intent.putExtra("city",_city);
            intent.putExtra("whatToDo","createNewUser");

            //Add Shared Animation
            Pair[] pairs = new Pair[4];
            pairs[0] = new Pair<View, String>(img, "transition_reg_image");
            pairs[1] = new Pair<View, String>(next, "transition_next_btn");
            pairs[2] = new Pair<View, String>(calllogin, "transition_loguser_btn");
            pairs[3] = new Pair<View, String>(reg_head, "transition_reg_text");

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SignupClass.this, pairs);
                startActivity(intent, options.toBundle());
            } else {
                startActivity(intent);
                finish();
            }
        }
    }

    private boolean validatePassword() {
        String pass = password.getEditText().getText().toString().trim();
        String checkPassword = "^" +
                "(?=.*[0-9])" +         //at least 1 digit
                "(?=.*[a-z])" +         //at least 1 lower case letter
                "(?=.*[A-Z])" +         //at least 1 upper case letter
                "(?=.*[a-zA-Z])" +      //any letter
                "(?=.*[@#$%^&+=])" +    //at least 1 special character
//                "(?=S+$)" +           //no white spaces
                ".{6,}" +               //at least 6 characters
                "$";

        if (pass.isEmpty()) {
            password.setError("Field can not be empty");
            return false;
        } else if (!pass.matches(checkPassword)) {
            password.setError("Password should contain 6 characters!");
            return false;
        } else {
            password.setError(null);
            password.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateEmail() {
        String email =reg_email.getEditText().getText().toString().trim();
        String checkEmail = "[a-zA-Z0-9._-]+@[a-z]+.+[a-z]+";

        if (email.isEmpty()) {
            reg_email.setError("Field can not be empty");
            return false;
        } else if (!email.matches(checkEmail)) {
            reg_email.setError("Invalid Email!");
            return false;
        } else {
            reg_email.setError(null);
            reg_email.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateUsername() {
        String val = reguser.getEditText().getText().toString().trim();
        String checkspaces = "Aw{1,20}z";

        if (val.isEmpty()) {
            reguser.setError("Field can not be empty");
            return false;
        } else if (val.length() > 20) {
            reguser.setError("Username is too large!");
            return false;
        }  else {
            reguser.setError(null);
            reguser.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateCity() {
        String val = city.getEditText().getText().toString().trim();
        if (val.isEmpty()) {
            city.setError("Field can not be empty");
            return false;
        } else {
            city.setError(null);
            city.setErrorEnabled(false);
            return true;
        }
    }


}