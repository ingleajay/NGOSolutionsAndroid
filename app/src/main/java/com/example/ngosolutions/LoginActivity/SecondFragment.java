package com.example.ngosolutions.LoginActivity;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ngosolutions.AddPost.AdapterChatList;
import com.example.ngosolutions.LoginActivity.MainScreenClass;
import com.example.ngosolutions.LoginActivity.ModalUsers;
import com.example.ngosolutions.LoginActivity.ProfileClass;
import com.example.ngosolutions.R;
import com.example.ngosolutions.adapter.AdapterChat;
import com.example.ngosolutions.models.ModalChat;
import com.example.ngosolutions.models.ModalChatList;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class SecondFragment extends Fragment {
    FirebaseAuth firebaseAuth;
    RecyclerView recyclerView;

    List<ModalChatList> chatListList;
    List<ModalUsers> usersList;
    DatabaseReference reference;
    FirebaseUser currentUser;
    AdapterChatList adapterChatList;
    public SecondFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_second, container, false);
        firebaseAuth = FirebaseAuth.getInstance();
        recyclerView = view.findViewById(R.id.recyclerView);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        chatListList = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("ChatList").child(currentUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatListList.clear();
                for(DataSnapshot ds:snapshot.getChildren()){
                    ModalChatList chatList = ds.getValue(ModalChatList.class);
                    chatListList.add(chatList);
                }
                loadChats();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return view;
    }

    private void loadChats(){
        usersList =  new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("App_Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usersList.clear();
                for(DataSnapshot ds : snapshot.getChildren()){
                    ModalUsers users = ds.getValue(ModalUsers.class);
                    for(ModalChatList chatList:chatListList){
                        if(users.get_uid() != null && users.get_uid().equals(chatList.getId())){
                            usersList.add(users);
                            break;
                        }
                    }
                    adapterChatList = new AdapterChatList(getContext(),usersList);
                    recyclerView.setAdapter(adapterChatList);
                    for (int i=0 ; i<usersList.size();i++){
                        lastMessage(usersList.get(i).get_uid());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void lastMessage(final String userId) {
        DatabaseReference reference  = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String thelastMessage = "default";
                for(DataSnapshot ds: snapshot.getChildren()){
                    ModalChat chat = ds.getValue(ModalChat.class);
                    if(chat == null){
                        continue;
                    }
                    String sender = chat.getSender();
                    String recevier =chat.getReceiver() ;
                    if(sender == null || recevier == null){
                        continue;
                    }
                    if(chat.getReceiver().equals(currentUser.getUid()) &&
                            chat.getSender().equals(userId) ||
                            chat.getReceiver().equals(userId) &&
                                    chat.getSender().equals(currentUser.getUid())){

                        if(chat.getType().equals("image")){
                            thelastMessage = "Sent a photo";
                        }
                        else{
                            thelastMessage = chat.getMessage();
                        }

                    }
                }
                adapterChatList.setLastMessageMap(userId ,thelastMessage);
                adapterChatList.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



}