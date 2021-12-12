package com.example.ngosolutions.LoginActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;

import com.example.ngosolutions.OnBoard;
import com.example.ngosolutions.R;

public class MainScreenClass extends AppCompatActivity {
    SharedPreferences onLoginScreen;
//    private static  int SPLASH_SCREEN = 1000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main_screen_class);

//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//
//                onLoginScreen = getSharedPreferences("onLoginScreen",MODE_PRIVATE);
//                boolean isFirstTime = onLoginScreen.getBoolean("firstTime",true);
//                if(isFirstTime){
//                    SharedPreferences.Editor editor = onLoginScreen.edit();
//                    editor.putBoolean("firstTime",false);
//                    editor.commit();
//                    Intent intent = new Intent(getApplicationContext(), SignupClass.class);
//                    startActivity(intent);
//                    finish();
//                }
//                else{
//                    Intent intent = new Intent(getApplicationContext(), SigninClass.class);
//                    startActivity(intent);
//                    finish();
//                }
//            }
//        },SPLASH_SCREEN);
    }

    public void call_login(View view) {
        Intent intent = new Intent(getApplicationContext(), SigninClass.class);
        Pair[] pairs = new Pair[1];
        pairs[0] = new Pair<View, String>(findViewById(R.id.loginbtn), "transition_login");
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainScreenClass.this, pairs);
            startActivity(intent, options.toBundle());
        } else {
            startActivity(intent);
        }
    }
    public void call_signup(View view) {

        Intent intent = new Intent(getApplicationContext(), SignupClass.class);
        Pair[] pairs = new Pair[1];
        pairs[0] = new Pair<View, String>(findViewById(R.id.registerbtn), "transition_signup");

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainScreenClass.this, pairs);
            startActivity(intent, options.toBundle());
        } else {
            startActivity(intent);
        }


    }
}