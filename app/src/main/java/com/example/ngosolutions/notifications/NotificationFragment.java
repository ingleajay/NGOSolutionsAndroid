package com.example.ngosolutions.notifications;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ngosolutions.R;
import com.example.ngosolutions.adapter.AdapterNotification;
import com.example.ngosolutions.models.ModalNotification;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.

 */
public class NotificationFragment extends Fragment {

    RecyclerView notificationRv;
    private FirebaseAuth firebaseAuth;
    private ArrayList<ModalNotification> notificationList;
    private AdapterNotification adapterNotification;
    public NotificationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_notification, container, false);
        notificationRv = view.findViewById(R.id.notificationRv);
        firebaseAuth = FirebaseAuth.getInstance();
        getAllNotification();
        return  view;
    }

    private void getAllNotification() {
        notificationList =new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("App_Users");
        ref.child(firebaseAuth.getUid()).child("Notification")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        notificationList.clear();
                        for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                            ModalNotification modal = dataSnapshot.getValue(ModalNotification.class);

                            notificationList.add(modal);
                        }
                        adapterNotification = new AdapterNotification(getActivity(),notificationList);
                        notificationRv.setAdapter(adapterNotification);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}