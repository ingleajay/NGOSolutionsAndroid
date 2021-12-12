package com.example.ngosolutions.LoginActivity;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ngosolutions.R;
import com.example.ngosolutions.adapter.AdapterNgo;
import com.example.ngosolutions.models.ModalNgo;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class ThirdFragment extends Fragment {
    RecyclerView recyclerView;
    AdapterNgo adapterNgo;
    List<ModalNgo> modalNgoList;
    EditText editText;
    FirebaseAuth firebaseAuth;
    String myuid, adminId;

    public ThirdFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_third, container, false);

        recyclerView = view.findViewById(R.id.ngos_recyclerview);
        firebaseAuth = FirebaseAuth.getInstance();
        myuid = firebaseAuth.getUid();
        editText = view.findViewById(R.id.search_ngo);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        modalNgoList = new ArrayList<>();
        getAllNgos();
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                searchUser(editable.toString());
            }
        });
        return view;
    }

    private void getAllNgos() {
        final FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();


        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Web_admin");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                modalNgoList.clear();
                for (DataSnapshot ds: datasnapshot.getChildren()){
                    ModalNgo modalUsers = ds.getValue(ModalNgo.class);
                        modalNgoList.add(modalUsers);
                    adapterNgo = new AdapterNgo(getActivity() , modalNgoList);
                    recyclerView.setAdapter(adapterNgo);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void searchUser(final String query) {
        final FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Web_admin");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                modalNgoList.clear();
                for (DataSnapshot ds: datasnapshot.getChildren()){
                    ModalNgo modalUsers = ds.getValue(ModalNgo.class);
                    if(fuser != null) {
                        if (modalUsers.getOrgname().toLowerCase().contains(query.toLowerCase()) ||
                                modalUsers.getEmail().toLowerCase().contains(query.toLowerCase())) {
                            modalNgoList.add(modalUsers);
                        }
                    }
                   adapterNgo = new AdapterNgo(getActivity() , modalNgoList);
                    adapterNgo.notifyDataSetChanged();
                    recyclerView.setAdapter(adapterNgo);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}





