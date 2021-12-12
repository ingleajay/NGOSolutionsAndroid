package com.example.ngosolutions.LoginActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ngosolutions.HomesScreen;
import com.example.ngosolutions.R;
import com.example.ngosolutions.adapter.AdapterNgo;
import com.example.ngosolutions.adapter.AdapterPost;
import com.example.ngosolutions.models.ModalNgo;
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

import java.util.List;

public class PopUp extends AppCompatActivity {
TextView ngo_email , ngo_cause , ngo_address, ngo_city,ngo_state, ngo_orgname, ngo_short,ngo_country,ngo_pincode;
Button followbtn;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    ImageView passback;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    List<ModalNgo> modalNgos;
    AdapterNgo adapterNgo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_pop_up);
        ngo_email = findViewById(R.id.ngo_email);
        ngo_address = findViewById(R.id.ngo_address);
        ngo_cause = findViewById(R.id.ngo_cause);
        ngo_city = findViewById(R.id.ngo_city);
        ngo_country = findViewById(R.id.ngo_country);
        ngo_state = findViewById(R.id.ngo_state);
        ngo_orgname = findViewById(R.id.ngo_name);
        ngo_short = findViewById(R.id.ngo_short);
        ngo_pincode = findViewById(R.id.ngo_pincode);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        passback= findViewById(R.id.pass_back);
        passback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), HomesScreen.class);
                startActivity(intent);

            }
        });
        databaseReference = firebaseDatabase.getReference("Web_admin");
        Intent intent = getIntent();
        String uid  =intent.getStringExtra("userId");
        Query query = databaseReference.orderByChild("userId").equalTo(uid);
        Toast.makeText(this, ""+uid, Toast.LENGTH_SHORT).show();
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                for(DataSnapshot ds : datasnapshot.getChildren() ) {
                        String orgname = "" + ds.child("orgname").getValue();
                        String address = "" + ds.child("address").getValue();
                        String city = "" + ds.child("city").getValue();
                        String country = "" + ds.child("country").getValue();
                        String state = "" + ds.child("state").getValue();
                        String email = "" + ds.child("email").getValue();
                        String pincode = "" + ds.child("pincode").getValue();
                        String shortdesc = "" + ds.child("shortdesc").getValue();
                        String cause = "" + ds.child("cause").getValue();

                        ngo_orgname.setText("Organization name : "+ orgname);
                        ngo_address.setText("Address : "+ address);
                        ngo_cause.setText("Cause : "+ cause);
                        ngo_city.setText("City : "+ city);
                        ngo_country.setText("Country : "+ country);
                        ngo_pincode.setText("Pincode : "+ pincode);
                        ngo_state.setText("State : "+state);
                        ngo_email.setText("Email"+email);
                        ngo_short.setText("Short Description : "+shortdesc);


                    }
                }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}