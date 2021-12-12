//package com.example.ngosolutions.LoginActivity;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.view.GravityCompat;
//import androidx.drawerlayout.widget.DrawerLayout;
//import androidx.fragment.app.Fragment;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.MenuItem;
//import android.view.View;
//import android.view.WindowManager;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.example.ngosolutions.FourthFragment;
//import com.example.ngosolutions.R;
//import com.example.ngosolutions.SecondFragment;
//import com.example.ngosolutions.ThirdFragment;
//import com.google.android.material.navigation.NavigationView;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.ismaeldivita.chipnavigation.ChipNavigationBar;
//
//public class UserDashClass extends AppCompatActivity {
//    DrawerLayout drawerLayout;
//    NavigationView navigationView;
//    ChipNavigationBar chipNavigationBar;
//    static final float END_SCALE = 0.7f;
//    ImageView menuIcon;
//    LinearLayout contentView;
//    TextView title;
//    Button logout;
//    FirebaseAuth firebaseAuth;
//    FirebaseUser user;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        setContentView(R.layout.activity_user_dash_class);
//        chipNavigationBar = findViewById(R.id.bottom_nav_menu);
//        chipNavigationBar.setItemSelected(R.id.frag_first,true);
//        menuIcon = findViewById(R.id.menu_icon);
//        contentView = findViewById(R.id.content);
//        logout = findViewById(R.id.logoutbtn);
//        title = findViewById(R.id.set_text);
//        navigationView.bringToFront();
//        navigationView.setNavigationItemSelectedListener((NavigationView.OnNavigationItemSelectedListener) this);
//        navigationView.setCheckedItem(R.id.nav_home);
//        naviagtionDrawer();
//        firebaseAuth  = FirebaseAuth.getInstance();
//        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new FirstFragment()).commit();
//        bottomMenu();
//
//
//
//
//
//    }
//        private void naviagtionDrawer(){
//
//        //Naviagtion Drawer
//        navigationView.bringToFront();
//        navigationView.setNavigationItemSelectedListener((NavigationView.OnNavigationItemSelectedListener) this);
//        navigationView.setCheckedItem(R.id.nav_home);
//
//        menuIcon.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View view){
//                if(drawerLayout.isDrawerVisible(GravityCompat.START))
//                    drawerLayout.closeDrawer(GravityCompat.START);
//                else drawerLayout.openDrawer(GravityCompat.START);
//            }
//        });
//
//        animateNavigationDrawer();
//    }
//
//    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
//
//                switch (menuItem.getItemId()) {
//                    case R.id.nav_home:
//                        break;
//                    case R.id.nav_chat:
//                        break;
//                    case R.id.nav_addpost:
//                        break;
//                    case R.id.nav_donate:
//                        break;
//                    case R.id.nav_profile:
//                            startActivity(new Intent(UserDashClass.this, ProfileClass.class));
//
//                        break;
//                    case R.id.nav_logout:
//                                firebaseAuth.signOut();
//                                startActivity(new Intent(UserDashClass.this, SigninClass.class));
//                                finish();
//
//                        break;
//    }
//        drawerLayout.closeDrawer(GravityCompat.START);
//        return true;
//    }
//    private void logoutStatus() {
//        FirebaseUser user = firebaseAuth.getCurrentUser();
//        if(user != null) {
//            logout.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    firebaseAuth.signOut();
//                    startActivity(new Intent(UserDashClass.this , SigninClass.class));
//                    finish();
//                }
//            });
//        }
//    }
//    private void checkUserStatus(){
//        FirebaseUser user = firebaseAuth.getCurrentUser();
//        if(user != null){
//            // user is sign i
//            Toast.makeText(this, user.getEmail(), Toast.LENGTH_SHORT).show();
//        }
//        else {
//            // user not signin in ,  go to main activity
//            startActivity(new Intent(UserDashClass.this ,MainScreenClass.class));
//
//        }
//    }
//    @Override
//    protected void onStart() {
//        checkUserStatus();
//        logoutStatus();
//        super.onStart();
//    }
//        private void animateNavigationDrawer() {
//        drawerLayout.setScrimColor(getResources().getColor(R.color.colorAccent));
//        drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
//            @Override
//            public void onDrawerSlide(View drawerView, float slideOffset) {
//
//                // Scale the View based on current slide offset
//                final float diffScaledOffset = slideOffset * (1 - END_SCALE);
//                final float offsetScale = 1 - diffScaledOffset;
//                contentView.setScaleX(offsetScale);
//                contentView.setScaleY(offsetScale);
//
//                // Translate the View, accounting for the scaled width
//                final float xOffset = drawerView.getWidth() * slideOffset;
//                final float xOffsetDiff = contentView.getWidth() * diffScaledOffset / 2;
//                final float xTranslation = xOffset - xOffsetDiff;
//                contentView.setTranslationX(xTranslation);
//            }
//        });
//
//    }
//    @Override
//    public void onBackPressed() {
//        if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
//            drawerLayout.closeDrawer(GravityCompat.START);
//        } else
//            super.onBackPressed();
//
//        super.onBackPressed();
//        finish();
//    }
//    private void bottomMenu() {
//
//        chipNavigationBar.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(int i) {
//                Fragment fragment = null;
//                switch (i) {
//                    case R.id.frag_first:
//                        title.setText("Home");
//                        fragment = new FirstFragment();
//                        break;
//                    case R.id.frag_second:
//                        title.setText("Chat");
//                        fragment = new SecondFragment();
//                        break;
//                    case R.id.frag_third:
//                        title.setText("Add Post");
//                        fragment = new ThirdFragment();
//                        break;
////                    case R.id.frag_fourth:
////                        title.setText("Profile");
////                        fragment = new FourthFragment();
////                        startActivity(new Intent(UserDashClass.this ,ProfileClass.class));
////                        finish();
////                        break;
//                }
//                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment).commit();
//            }
//        });
//
//    }
//}