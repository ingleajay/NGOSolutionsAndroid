//package com.example.ngosolutions;
//
//import android.os.Bundle;
//
//import androidx.annotation.NonNull;
//import androidx.fragment.app.Fragment;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//
//import java.util.ArrayList;
//import java.util.List;
//
///** * A simple {@link Fragment} subclass.*/
//public class FourthFragment extends Fragment {
//RecyclerView recyclerView;
//AdapterUsers adapterUsers;
//List<ModalUsers> usersList;
//    public FourthFragment() {
//        // Required empty public constructor
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        View view = inflater.inflate(R.layout.fragment_fourth, container, false);
//
//        recyclerView = view.findViewById(R.id.users_recyclerview);
//        recyclerView.setHasFixedSize(true);
//        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
//        usersList = new ArrayList<>();
//        getAllUsers();
//        return  view;
//    }
//
//    private void getAllUsers() {
//        final FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
//        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("App_Users");
//
//        ref.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
//                for (DataSnapshot ds: datasnapshot.getChildren()){
//                    ModalUsers modalUsers = ds.getValue(ModalUsers.class);
//                    if(!modalUsers.getUid().equals(fuser.getUid())){
//                        usersList.add(modalUsers);
//                    }
//                    adapterUsers = new AdapterUsers(getActivity() , usersList);
//                    recyclerView.setAdapter(adapterUsers);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }
//}