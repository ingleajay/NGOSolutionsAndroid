package com.example.ngosolutions.LoginActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.example.ngosolutions.HelperClass.SessionManager;
import com.example.ngosolutions.HelperClass.UserHelp;
import com.example.ngosolutions.HomesScreen;
import com.example.ngosolutions.OnBoard;
import com.example.ngosolutions.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class SigninClass extends AppCompatActivity {
    private static  int SPLASH_SCREEN = 1000;
    SharedPreferences onUserLoginScreen;
    private static final int RC_SIGN_IN = 100;
    GoogleSignInClient mGoogleSignInClient;
    Button callSignup;
    Button callforgotup;
    Button calllogin;
    TextInputLayout log_password;
    TextInputLayout log_email;
    ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    SignInButton mGoogleLoginbtn;
    String _reguser , _phoneNo , _reg_email , _date , _gender , _password, _city;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_signin_class);
        callSignup = findViewById(R.id.signup_screen);
        callSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SignupClass.class);
                startActivity(intent);

            }
        });

        callforgotup = findViewById(R.id.forgot_screen);
        callforgotup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ForgotPassOutsideClass.class);
                startActivity(intent);

            }
        });
        firebaseAuth = FirebaseAuth.getInstance();
        calllogin = findViewById(R.id.login);
        log_email = findViewById(R.id.log_email);
        log_password = findViewById(R.id.log_password);
        mGoogleLoginbtn = findViewById(R.id.google_log);
        progressDialog = new ProgressDialog(this , R.style.MyAlertDialogStyle);
        progressDialog.setMessage("Login...");
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this,gso);
        mGoogleLoginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                onUserLoginScreen = getSharedPreferences("onUserLoginScreen", MODE_PRIVATE);
                boolean isFirstTime = onUserLoginScreen.getBoolean("firstTime", true);
                if (isFirstTime) {
                    SharedPreferences.Editor editor = onUserLoginScreen.edit();
                    editor.putBoolean("firstTime", false);
                    editor.commit();
                } else {
                    if (user != null) {
                        Intent intent = new Intent(getApplicationContext(), HomesScreen.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }
        },SPLASH_SCREEN);

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            // user is signing first time then get and show info from google

                            if(task.getResult().getAdditionalUserInfo().isNewUser()){

                                String _reg_email = user.getEmail();
                                String _uid = user.getUid();
                                _phoneNo = getIntent().getStringExtra("phoneNo");
                                _reguser = getIntent().getStringExtra("reguser");


                                HashMap<Object, String> hashMap = new HashMap<>();
                                hashMap.put("_reg_email",_reg_email);
                                hashMap.put("_uid",_uid);
                                hashMap.put("onlineStatus","online");
                                hashMap.put("typingTo","noOne");
//                                hashMap.put("name",_reguser);
//                                hashMap.put("phone",_phoneNo);
//                                hashMap.put("image","");
//                                hashMap.put("cover","");
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference reference = database.getReference("App_Users");
                                reference.child(_uid).setValue(hashMap);
                            }
                            Toast.makeText(SigninClass.this, ""+user.getEmail(), Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SigninClass.this, HomesScreen.class));
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(SigninClass.this, "Login Failed..", Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SigninClass.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void LoginUser(View view){
        String email= log_email.getEditText().getText().toString().trim();
        String password= log_password.getEditText().getText().toString().trim();
        if (!validatePassword() | !validateEmail()) {
            return ;
        }
        else {
            progressDialog.show();
            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                FirebaseUser user = firebaseAuth.getCurrentUser();
                                startActivity(new Intent(SigninClass.this, HomesScreen.class));
                                finish();

                            } else {
                                // If sign in fails, display a message to the user.
                                progressDialog.dismiss();
                                Toast.makeText(SigninClass.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();

                            }

                            // ...
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(SigninClass.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private boolean validatePassword() {
        String pass = log_password.getEditText().getText().toString().trim();
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
            log_password.setError("Field can not be empty");
            return false;
        } else if (!pass.matches(checkPassword)) {
            log_password.setError("Password should contain 6 characters!");
            return false;
        } else {
            log_password.setError(null);
            log_password.setErrorEnabled(false);
            return true;
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