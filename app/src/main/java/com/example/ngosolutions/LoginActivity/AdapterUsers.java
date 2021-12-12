package com.example.ngosolutions.LoginActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import androidx.recyclerview.widget.RecyclerView;

import com.example.ngosolutions.AddPost.ThereProfile;
import com.example.ngosolutions.ChatPlatform.ChatActivity;
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

public class AdapterUsers extends RecyclerView.Adapter<AdapterUsers.MyHolder> {

    Context context;
    List<ModalUsers> usersList;
    FirebaseAuth firebaseAuth;
    String myuid;
    public AdapterUsers(Context context, List<ModalUsers> usersList) {
        this.context = context;
        this.usersList = usersList;


    }



    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_users , viewGroup, false);
        return new MyHolder(view) ;
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder myholder, final int i) {
        firebaseAuth = FirebaseAuth.getInstance();
        myuid = firebaseAuth.getUid();
    final String hisUID = usersList.get(i).get_uid();
    String userImage = usersList.get(i).getImage();
    String userName = usersList.get(i).get_reguser();
    final String userEmail = usersList.get(i).get_reg_email();
    myholder.mName.setText(userName);
    myholder.mEmail.setText(userEmail);
    try{
        Picasso.get().load(userImage).placeholder(R.drawable.icon_face).into(myholder.mAvatar);
    }
    catch (Exception e){

    }
    myholder.blocktv.setImageResource(R.drawable.icon_check);
    checkBlocked(hisUID,myholder,i);
    myholder.itemView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Toast.makeText(context, ""+userEmail, Toast.LENGTH_SHORT).show();
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setItems(new String[]{"Profile", "Chat"}, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if(i == 0){

                        Intent intent = new Intent(context, ThereProfile.class);
                        intent.putExtra("uid",hisUID);
                        context.startActivity(intent);
                    }
                    if(i == 1){

                        isBlockUser(hisUID);

                    }
                }
            });
            builder.create().show();
        }
    });

    myholder.blocktv.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(usersList.get(i).isBlocked()){
                unBlockuser(hisUID);
            }
            else {
                blockUser(hisUID);
            }
        }
    });
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
    @Override
    public int getItemCount() {
        return usersList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder{
        ImageView mAvatar,blocktv;
        TextView mName ,  mEmail;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            mAvatar = itemView.findViewById(R.id.avatar);
            mName = itemView.findViewById(R.id.name);
            blocktv = itemView.findViewById(R.id.blockTv);
            mEmail = itemView.findViewById(R.id.email);

        }
    }
}
