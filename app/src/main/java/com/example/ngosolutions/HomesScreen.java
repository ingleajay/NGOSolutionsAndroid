package com.example.ngosolutions;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.ngosolutions.AddPost.PostAdd;

import com.example.ngosolutions.GoogleMap.Google_Map;
import com.example.ngosolutions.GoogleMap.TrackLocation;
import com.example.ngosolutions.LoginActivity.FirstFragment;
import com.example.ngosolutions.LoginActivity.FourthFragment;
import com.example.ngosolutions.LoginActivity.MainScreenClass;
import com.example.ngosolutions.LoginActivity.Ngo_post_frag;
import com.example.ngosolutions.LoginActivity.ProfileClass;
import com.example.ngosolutions.LoginActivity.SecondFragment;
import com.example.ngosolutions.LoginActivity.SigninClass;
import com.example.ngosolutions.LoginActivity.ThirdFragment;
import com.example.ngosolutions.notifications.NotificationFragment;
import com.example.ngosolutions.notifications.Token;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

public class HomesScreen extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener {
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    FloatingActionButton fab;
    ImageView menuIcon,addpost;
    TextView title;
    LinearLayout contentView;
    ChipNavigationBar chipNavigationBar;
    static final float END_SCALE = 0.7f;
    FirebaseAuth firebaseAuth;
    String mUID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_homes_screen);
        chipNavigationBar = findViewById(R.id.bottom_nav_menu);
        chipNavigationBar.setItemSelected(R.id.frag_first,true);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new FirstFragment()).commit();
        bottomMenu();
        //Menu Hooks
        fab = findViewById(R.id.fab);
        title = findViewById(R.id.set_text);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        menuIcon = findViewById(R.id.menu_icon);
        addpost = findViewById(R.id.addpost);
        contentView = findViewById(R.id.content);
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_home);
        fab.setOnClickListener(new View.OnClickListener() {
            boolean visible = true;
            @Override
            public void onClick(View view) {
                if(visible){
                chipNavigationBar.setVisibility(View.VISIBLE);
                visible = false;
                }
                else {
                    chipNavigationBar.setVisibility(View.INVISIBLE);
                    visible = true;
                }
            }

        });

        naviagtionDrawer();
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user= firebaseAuth.getCurrentUser();
        checkUserStatus();
        //update Token
        updateToken(FirebaseInstanceId.getInstance().getToken());


//       addpost.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(getApplicationContext(), PostAdd.class);
//                startActivity(intent);
//
//            }
//        });
        Menu menu = navigationView.getMenu();
        if(user != null) {
            menu.findItem(R.id.nav_reg).setVisible(false);
            menu.findItem(R.id.nav_login).setVisible(false);
            menu.findItem(R.id.nav_profile).setVisible(true);
            menu.findItem(R.id.nav_logout).setVisible(true);
        }
        else {
            menu.findItem(R.id.nav_reg).setVisible(true);
            menu.findItem(R.id.nav_login).setVisible(true);
            menu.findItem(R.id.nav_profile).setVisible(false);
        }

    }
    protected  void onResume() {
        checkUserStatus();
        super.onResume();
    }
    public  void updateToken(String token){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Tokens");
        Token mtoken = new Token(token);
        ref.child(mUID).setValue(mtoken);
    }
    public void addpost(View view){
        startActivity(new Intent( this, PostAdd.class));
        finish();
    }
    private void bottomMenu() {

        chipNavigationBar.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int i) {
                Fragment fragment = null;
                ActionBar actionBar = getSupportActionBar();
                switch (i) {
                    case R.id.frag_first:
                        title.setText("Home");
                        fragment = new FirstFragment();
                        break;
                    case R.id.frag_ngo_post:
                        title.setText("NGO Event");
                        fragment = new Ngo_post_frag();
                        break;
                    case R.id.frag_second:
                        title.setText("Chat");
                        fragment = new SecondFragment();
                        break;
                    case R.id.frag_third:
                        title.setText("NGO List");
                        fragment = new ThirdFragment();
                        break;
                  case R.id.frag_fourth:
                      title.setText("Users");
                        fragment = new FourthFragment();
                        break;
                  case R.id.frag_notify:
                        title.setText("Notification");
                        fragment = new NotificationFragment();
                        break;
               }
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment).commit();
            }
        });

    }
    private void naviagtionDrawer(){

        //Naviagtion Drawer
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_home);

        menuIcon.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if(drawerLayout.isDrawerVisible(GravityCompat.START))
                    drawerLayout.closeDrawer(GravityCompat.START);
                else drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        animateNavigationDrawer();
    }
    public void onBackPressed() {
        if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else
            super.onBackPressed();
    }
    private void animateNavigationDrawer() {
drawerLayout.setScrimColor(getResources().getColor(R.color.colorAccent));
        drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

                // Scale the View based on current slide offset
                final float diffScaledOffset = slideOffset * (1 - END_SCALE);
                final float offsetScale = 1 - diffScaledOffset;
                contentView.setScaleX(offsetScale);
                contentView.setScaleY(offsetScale);

                // Translate the View, accounting for the scaled width
                final float xOffset = drawerView.getWidth() * slideOffset;
                final float xOffsetDiff = contentView.getWidth() * diffScaledOffset / 2;
                final float xTranslation = xOffset - xOffsetDiff;
                contentView.setTranslationX(xTranslation);
            }
        });

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()) {
                    case R.id.nav_home:

                        break;
//                    case R.id.nav_chat:
//                        break;
                    case R.id.nav_addpost:
                        startActivity(new Intent(HomesScreen.this, PostAdd.class));
                        break;
                    case R.id.nav_location:
                        startActivity(new Intent(HomesScreen.this,TrackLocation.class));
                        break;
                    case R.id.nav_profile:
                            startActivity(new Intent(HomesScreen.this, ProfileClass.class));
                        break;
                    case R.id.nav_logout:
                                firebaseAuth.signOut();
                                startActivity(new Intent(HomesScreen.this, SigninClass.class));
                                finish();

                        break;
    }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void checkUserStatus(){
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user != null){
            mUID = user.getUid();
            SharedPreferences sp = getSharedPreferences("SP_USER" , MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("Current_USERID" , mUID);
            editor.apply();
        }
        else {
            // user not signin in ,  go to main activity
            startActivity(new Intent(HomesScreen.this , MainScreenClass.class));
            finish();
        }
    }

    @Override
    protected void onStart() {
        checkUserStatus();
        super.onStart();
    }


}