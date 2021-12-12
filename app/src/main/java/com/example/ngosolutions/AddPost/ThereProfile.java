package com.example.ngosolutions.AddPost;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ngosolutions.HomesScreen;
import com.example.ngosolutions.LoginActivity.MainScreenClass;
import com.example.ngosolutions.LoginActivity.ProfileClass;
import com.example.ngosolutions.R;
import com.example.ngosolutions.adapter.AdapterPost;
import com.example.ngosolutions.models.ModelPost;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ThereProfile extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    RecyclerView postsRecyclerview;
    List<ModelPost> postList;
    AdapterPost adapterPost;
    ImageView passback;
    TextView user_name , user_phone, user_email;
    ImageView profileimage ;
    EditText visible_edit;
    LinearLayout showcontainer;
    Button visible_search;
    String uid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_there_profile);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        postsRecyclerview = findViewById(R.id.recyclerview_posts);
        passback= findViewById(R.id.pass_back);
        passback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), HomesScreen.class);
                startActivity(intent);

            }
        });
        visible_edit =findViewById(R.id.searchvisible);
        visible_search = findViewById(R.id.visiblesearch);
        showcontainer = (LinearLayout) findViewById(R.id.linear);
        visible_search.setOnClickListener(new View.OnClickListener() {
            boolean visible;
            @Override
            public void onClick(View view) {

                visible_search.setVisibility(View.VISIBLE);
                showcontainer.setVisibility(View.VISIBLE);

            }
        });
        visible_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                SearchHisPosts(editable.toString());
            }
        });
        firebaseAuth = FirebaseAuth.getInstance();
        profileimage = findViewById(R.id.profile_image);
        user_email = findViewById(R.id.user_email);
        user_name = findViewById(R.id.user_name);
        user_phone = findViewById(R.id.user_phone);
        Intent intent = getIntent();
        uid = intent.getStringExtra("uid");
        firebaseDatabase = FirebaseDatabase.getInstance();
        user =  firebaseAuth.getCurrentUser();
        databaseReference = firebaseDatabase.getReference("App_Users");
        Query query = databaseReference.orderByChild("_uid").equalTo(uid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                for(DataSnapshot ds : datasnapshot.getChildren() ){
                    String  name = ""+ds.child("_reguser").getValue();
                    String  phone = ""+ds.child("_phoneNo").getValue();
                    String  email = ""+ds.child("_reg_email").getValue();
                    String  user_profile = ""+ds.child("image").getValue();


                    user_email.setText(email);
                    user_phone.setText(phone);
                    user_name.setText(name);


                    try {
                        Picasso.get().load(user_profile).into(profileimage);
                    }
                    catch (Exception e){
                        Picasso.get().load(R.drawable.add_photo).into(profileimage);
                    }
//                    try {
//                        Picasso.get().load(coverimage).into(cover);
//                    }
//                    catch (Exception e){
//                        Picasso.get().load(R.drawable.add_photo).into(cover);
//                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        postList = new ArrayList<>();
        checkUserStatus();
        loadHisPosts();
    }

    private void loadHisPosts() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        postsRecyclerview.setLayoutManager(layoutManager);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("App_posts");
        Query query = ref.orderByChild("uid").equalTo(uid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                postList.clear();
                for(DataSnapshot ds: datasnapshot.getChildren()){
                    ModelPost myPost = ds.getValue(ModelPost.class);
                    postList.add(myPost);
                    adapterPost = new AdapterPost(ThereProfile.this,postList);
                    postsRecyclerview.setAdapter(adapterPost);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseerror) {
                Toast.makeText(ThereProfile.this, ""+databaseerror.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });


    }

    private void SearchHisPosts(final String searchQuery) {

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        postsRecyclerview.setLayoutManager(layoutManager);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("App_posts");
        Query query = ref.orderByChild("uid").equalTo(uid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                postList.clear();
                for(DataSnapshot ds: datasnapshot.getChildren()){
                    ModelPost myPost = ds.getValue(ModelPost.class);
                    if(myPost.getpTitle().toLowerCase().contains(searchQuery.toLowerCase()) ||
                            myPost.getpDesec().toLowerCase().contains(searchQuery.toLowerCase())){
                        postList.add(myPost);
                    }
                    adapterPost = new AdapterPost(ThereProfile.this,postList);
                    postsRecyclerview.setAdapter(adapterPost);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseerror) {
                Toast.makeText(ThereProfile.this, ""+databaseerror.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void checkUserStatus(){
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user != null){

        }
        else {
            // user not signin in ,  go to main activity
            startActivity(new Intent(this , MainScreenClass.class));
            finish();
        }
    }
}