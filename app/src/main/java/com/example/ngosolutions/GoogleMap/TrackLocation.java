package com.example.ngosolutions.GoogleMap;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.example.ngosolutions.HomesScreen;
import com.example.ngosolutions.R;
import com.example.ngosolutions.GoogleMap.Google_Map;
public class TrackLocation extends AppCompatActivity {
    ImageView passback;
    Button track, place;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_location);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        passback= findViewById(R.id.pass_back);
        track = findViewById(R.id.btn_track);
//        place = findViewById(R.id.btn_near_by);
        passback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), HomesScreen.class);
                startActivity(intent);

            }
        });
        track.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(TrackLocation.this, Google_Map.class));
            }
        });
//        place.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(TrackLocation.this, Nearby_Ngo.class));
//            }
//        });
    }
}