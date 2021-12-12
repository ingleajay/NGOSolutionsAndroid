package com.example.ngosolutions.AddPost;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ngosolutions.ChatPlatform.ChatActivity;
import com.example.ngosolutions.LoginActivity.AdapterUsers;
import com.example.ngosolutions.LoginActivity.ModalUsers;
import com.example.ngosolutions.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

public class AdapterChatList extends  RecyclerView.Adapter<AdapterChatList.MyHolder> {

    Context context ;
    List<ModalUsers> usersList;
    FirebaseAuth firebaseAuth;
    String myuid;
    private HashMap<String , String> lastMessageMap;
    public AdapterChatList(Context context, List<ModalUsers> usersList) {
        this.context = context;
        this.usersList = usersList;
        lastMessageMap = new HashMap<>();
    }


    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_chatlist , viewGroup, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder myholder, final int i) {
        final String hisuid = usersList.get(i).get_uid();
        String userImage = usersList.get(i).getImage();
        String userName = usersList.get(i).get_reguser();
        String lastMessage = lastMessageMap.get(hisuid);
        firebaseAuth = FirebaseAuth.getInstance();
        myuid = firebaseAuth.getUid();
        myholder.nameTv.setText(userName);
        if(lastMessage == null || lastMessage.equals("default")){
            myholder.lastMessageTv.setVisibility(View.GONE);
        }
        else{
            myholder.lastMessageTv.setVisibility(View.VISIBLE);
            myholder.lastMessageTv.setText(lastMessage);
        }

        try{
            Picasso.get().load(userImage).placeholder(R.drawable.icon_face).into(myholder.profileTv);
        }catch (Exception e){
            Picasso.get().load(R.drawable.icon_face).into(myholder.profileTv);
        }

        if(usersList.get(i).getOnlineStatus().equals("online")){
            myholder.onlineStatustv.setImageResource(R.drawable.circle_online);
        }
        else{
            myholder.onlineStatustv.setImageResource(R.drawable.circle_offline);
        }

        myholder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                isBlockUser(hisuid);

            }
        });
        myholder.blocktv.setImageResource(R.drawable.icon_check);
        checkBlocked(hisuid,myholder,i);
        myholder.blocktv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(usersList.get(i).isBlocked()){
                    unBlockuser(hisuid);
                }
                else {
                    blockUser(hisuid);
                }
            }
        });
    }

    public  void setLastMessageMap(String userId , String lastMessage){
        lastMessageMap.put(userId,lastMessage);
    }
    @Override
    public int getItemCount() {
        return usersList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder{
        ImageView profileTv , onlineStatustv,blocktv;
        TextView nameTv , lastMessageTv;
        public MyHolder(@NonNull View itemView){
            super(itemView);
            profileTv = itemView.findViewById(R.id.profileTv);
            onlineStatustv = itemView.findViewById(R.id.onlineStatusTv);
            blocktv = itemView.findViewById(R.id.blockTv);
            nameTv = itemView.findViewById(R.id.nameTv);
            lastMessageTv = itemView.findViewById(R.id.lastMessageTv);

        }
    }

    private void isBlockUser(final String hisUid){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("App_Users");
        ref.child(hisUid).child("BlockUsers").orderByChild("_uid").equalTo(myuid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds: snapshot.getChildren()){
                            if(ds.exists()){
                                Toast.makeText(context, "You're blocked by that user , can't send message ", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }

                        Intent intent = new Intent(context, ChatActivity.class);
                        intent.putExtra("hisuid",hisUid);
                        context.startActivity(intent);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    private void checkBlocked(String hisUID, final MyHolder myholder, final int i) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("App_Users");
        ref.child(myuid).child("BlockUsers").orderByChild("_uid").equalTo(hisUID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds: snapshot.getChildren()){
                            if(ds.exists()){
                                myholder.blocktv.setImageResource(R.drawable.icon_block);
                                usersList.get(i).setBlocked(true);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    private void unBlockuser(String hisUID) {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("App_Users");
        ref.child(myuid).child("BlockUsers").orderByChild("_uid").equalTo(hisUID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds: snapshot.getChildren()){
                            if(ds.exists()){
                                ds.getRef().removeValue()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(context, "unBlocked Successfully...", Toast.LENGTH_SHORT).show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(context, "unBlocked Failed..."+e.getMessage(), Toast.LENGTH_SHORT).show();

                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    private void blockUser(String hisUID) {
        HashMap<String ,String> hashMap = new HashMap<>();
        hashMap.put("_uid",hisUID);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("App_Users");
        ref.child(myuid).child("BlockUsers").child(hisUID).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context, "Blocked Successfully..", Toast.LENGTH_SHORT).show();

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Blocked Failed.."+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
