package com.example.ngosolutions.AddPost;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.example.ngosolutions.HomesScreen;
import com.example.ngosolutions.LoginActivity.AdapterUsers;
import com.example.ngosolutions.LoginActivity.ModalUsers;
import com.example.ngosolutions.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PostLikedByActivity extends AppCompatActivity {
    String  postId;
    ImageView passback;
    private RecyclerView recyclerView;
    private List<ModalUsers> usersList;
    private AdapterUsers adapterUsers;
    private  FirebaseAuth firebaseAuth;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_post_liked_by);
        passback= findViewById(R.id.pass_back);
        recyclerView = findViewById(R.id.recyclerView);
        Intent intent = getIntent();
        postId = intent.getStringExtra("postId");
        firebaseAuth = FirebaseAuth.getInstance();

        passback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), HomesScreen.class);
                startActivity(intent);

            }
        });
        usersList =  new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Likes");
        ref.child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usersList.clear();
                for(DataSnapshot ds: snapshot.getChildren()){
                    String hisuid = ""+ds.getRef().getKey();
                    getUsers(hisuid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getUsers(String hisuid) {
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("App_Users");
        reference.orderByChild("_uid").equalTo(hisuid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot){
                for(DataSnapshot ds: snapshot.getChildren()){
                    ModalUsers modalUsers = ds.getValue(ModalUsers.class);
                    usersList.add(modalUsers);
                }

                adapterUsers = new AdapterUsers(PostLikedByActivity.this , usersList);
                recyclerView.setAdapter(adapterUsers);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}