//package com.example.ngosolutions.LoginActivity;
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
//import android.widget.ImageView;
//import android.widget.ProgressBar;
//import android.widget.Toast;
//
//import com.example.ngosolutions.R;
//import com.google.android.gms.tasks.OnFailureListener;
//import com.google.android.gms.tasks.OnSuccessListener;
//import com.google.android.material.textfield.TextInputLayout;
//import com.google.firebase.auth.AuthCredential;
//import com.google.firebase.auth.EmailAuthCredential;
//import com.google.firebase.auth.EmailAuthProvider;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//
//public class SetNewPassClass extends AppCompatActivity {
//    Button setupdate;
//    ImageView passback;
//    ProgressDialog progressDialog;
//    TextInputLayout _newPassword , _confirmpassword, _currentpassword;
//    FirebaseAuth firebaseAuth;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        setContentView(R.layout.activity_set_new_pass_class);
//        _newPassword = findViewById(R.id.new_password);
//        _confirmpassword = findViewById(R.id.confirm_password);
//        _currentpassword = findViewById(R.id.current_password);
//
//        passback= findViewById(R.id.pass_back);
//        setupdate = findViewById(R.id.set_new_password_btn);
//        progressDialog = new ProgressDialog(this , R.style.MyAlertDialogStyle);
//        progressDialog.setMessage("Password Updating...");
//        passback.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(getApplicationContext(), SigninClass.class);
//                startActivity(intent);
//
//            }
//        });
//        firebaseAuth = FirebaseAuth.getInstance();
//    }
//
//    public  void SetNewPass(View view){
//        if (!validateconfirmPassword() | !validatenewPassword() |!validatencurrentPassword() ) {
//            return ;
//        }
//        else {
//            final String newpassword= _newPassword.getEditText().getText().toString().trim();
//            String currentpassword= _currentpassword.getEditText().getText().toString().trim();
//
//            final FirebaseUser user = firebaseAuth.getCurrentUser();
//
//            progressDialog.show();
//
//            final AuthCredential authCredential = EmailAuthProvider.getCredential(user.getEmail() ,currentpassword);
//            user.reauthenticate(authCredential).addOnSuccessListener(new OnSuccessListener<Void>() {
//                @Override
//                public void onSuccess(Void aVoid) {
//                        user.reauthenticate(authCredential).addOnSuccessListener(new OnSuccessListener<Void>() {
//                            @Override
//                            public void onSuccess(Void aVoid) {
////                                String _phoneNo = getIntent().getStringExtra("phoneNo");
////                                progressDialog.dismiss();
////                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("App_Users");
////                                reference.child(_phoneNo).child("_password").setValue(newpassword);
////                                startActivity(new Intent(SetNewPassClass.this, ForgotPassSucessClass.class));
////                                finish();
//                                user.updatePassword(newpassword).addOnSuccessListener(new OnSuccessListener<Void>() {
//                                    @Override
//                                    public void onSuccess(Void aVoid) {
//                                        progressDialog.dismiss();
//                                        startActivity(new Intent(SetNewPassClass.this, ForgotPassSucessClass.class));
//                                        finish();
//                                    }
//                                }).addOnFailureListener(new OnFailureListener() {
//                                    @Override
//                                    public void onFailure(@NonNull Exception e) {
//
//                                    }
//                                });
//                            }
//                        }).addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                                Toast.makeText(SetNewPassClass.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
//
//                            }
//                        });
//                }
//            }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception e) {
//                progressDialog.dismiss();
//                    Toast.makeText(SetNewPassClass.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
//                }
//            });
//
//        }
//    }
//
//
//
//    private boolean validatenewPassword() {
//        String val = _newPassword.getEditText().getText().toString().trim();
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
//        if (val.isEmpty()  ) {
//            _newPassword.setError("Field can not be empty");
//            return false;
//        } else if (!val.matches(checkPassword)) {
//            _newPassword.setError("Password should contain 6 characters!");
//            return false;
//        } else {
//            _newPassword.setError(null);
//            _newPassword.setErrorEnabled(false);
//            return true;
//        }
//    }
//    private boolean validatencurrentPassword() {
//        String val = _currentpassword.getEditText().getText().toString().trim();
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
//        if (val.isEmpty()  ) {
//            _currentpassword.setError("Field can not be empty");
//            return false;
//        } else if (!val.matches(checkPassword)) {
//            _currentpassword.setError("Password should contain 6 characters!");
//            return false;
//        } else {
//            _currentpassword.setError(null);
//            _currentpassword.setErrorEnabled(false);
//            return true;
//        }
//    }
//
//    private boolean validateconfirmPassword() {
//        String val1 = _newPassword.getEditText().getText().toString().trim();
//
//        String val = _confirmpassword.getEditText().getText().toString().trim();
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
//        if (val.isEmpty()  ) {
//            _confirmpassword.setError("Field can not be empty");
//            return false;
//        } else if (!val.matches(checkPassword)) {
//            _confirmpassword.setError("Password should contain 6 characters!");
//            return false;
//        }
//        else if (!val.equals(val1)) {
//            _confirmpassword.setError("Password is Not Match!");
//            return false;
//        }
//        else {
//            _confirmpassword.setError(null);
//            _confirmpassword.setErrorEnabled(false);
//            return true;
//        }
//    }
//}