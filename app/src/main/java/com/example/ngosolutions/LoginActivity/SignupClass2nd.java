package com.example.ngosolutions.LoginActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.example.ngosolutions.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Calendar;

public class SignupClass2nd extends AppCompatActivity {
    Button calllogin;
    Button next;
    RadioButton selectdGender;
    RadioGroup radioGroup;
    DatePicker datePicker;
    ImageView img;
    TextView reg_head;
    private FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_signup_class2nd);
        calllogin = findViewById(R.id.login_screen);
        calllogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SigninClass.class);
                startActivity(intent);
            }
        });
        next = findViewById(R.id.next_reg);
        img = findViewById(R.id.logo_image1);
        reg_head = findViewById(R.id.slogan_name);
        radioGroup = findViewById(R.id.radio_group);
        datePicker = findViewById(R.id.age_picker);

    }

    public void RegisterUser(View view){
        if (!validateGender() | !validateAge() ) {
            return ;
        }
        else {

            selectdGender = findViewById(radioGroup.getCheckedRadioButtonId());
            String _gender = selectdGender.getText().toString();
            String _reguser = getIntent().getStringExtra("reguser");
            String _reg_email = getIntent().getStringExtra("reg_email");
            String _password = getIntent().getStringExtra("password");
            String _city = getIntent().getStringExtra("city");
            firebaseAuth = FirebaseAuth.getInstance();
            FirebaseUser user = firebaseAuth.getCurrentUser();
            String _uid = user.getUid();

            int day  = datePicker.getDayOfMonth();
            int month = datePicker.getMonth();
            int year = datePicker.getYear();

            String _date = day+"/"+month+"/"+year;
            Intent intent = new Intent(getApplicationContext(), SignupClass3rd.class);
            intent.putExtra("reguser", _reguser);
            intent.putExtra("user", _uid);
            intent.putExtra("reg_email", _reg_email);
            intent.putExtra("password", _password);
            intent.putExtra("city", _city);
            intent.putExtra("date",_date);
            intent.putExtra("gender", _gender);
            intent.putExtra("whatToDo","createNewUser");
            //Add Shared Animation
            Pair[] pairs = new Pair[4];
            pairs[0] = new Pair<View, String>(img, "transition_reg_image");
            pairs[1] = new Pair<View, String>(next, "transition_next_btn");
            pairs[2] = new Pair<View, String>(calllogin, "transition_loguser_btn");
            pairs[3] = new Pair<View, String>(reg_head, "transition_reg_text");

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SignupClass2nd.this, pairs);
                startActivity(intent, options.toBundle());
            } else {
                startActivity(intent);
                finish();
            }
        }
    }
    private boolean validateGender() {
        if (radioGroup.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Please Select Gender", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    private boolean validateAge() {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        int userAge = datePicker.getYear();
        int isAgeValid = currentYear - userAge;

        if (isAgeValid < 14) {
            Toast.makeText(this, "You are not eligible to apply", Toast.LENGTH_SHORT).show();
            return false;
        } else
            return true;
    }

}