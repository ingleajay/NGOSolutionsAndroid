package com.example.ngosolutions.adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ngosolutions.AddPost.ThereProfile;
import com.example.ngosolutions.GooglePay.GooglePay_ngo;
import com.example.ngosolutions.LoginActivity.AdapterUsers;
import com.example.ngosolutions.LoginActivity.PopUp;
import com.example.ngosolutions.R;
import com.example.ngosolutions.models.ModalNgo;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AdapterNgo extends RecyclerView.Adapter<AdapterNgo.MyHolder> {
    Context context;
    List<ModalNgo> modalNgoList;
    String adminId;
    private DatabaseReference FollowRef;
    private  DatabaseReference ngoRef;
    boolean mProcessFollow = false;
    public AdapterNgo(Context context, List<ModalNgo> modalNgoList) {
        this.context = context;
        this.modalNgoList = modalNgoList;
        adminId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FollowRef = FirebaseDatabase.getInstance().getReference().child("nFollow");
        ngoRef = FirebaseDatabase.getInstance().getReference().child("Web_admin");

    }



    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_ngos ,parent, false);
        return new MyHolder(view) ;
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder myholder, final int i) {
        String ngo_name = modalNgoList.get(i).getOrgname();
        final String nFollow = modalNgoList.get(i).getnFollow();
//        myholder.followBtn.setText(nFollow+ "Followed");

        final String timestamp = modalNgoList.get(i).getpTime();
        Calendar calender = Calendar.getInstance(Locale.getDefault());
        try {
            calender.setTimeInMillis(Long.parseLong(timestamp));
        } catch(Exception ex) {
            ex.printStackTrace();
        }
        final String pTime = DateFormat.format("dd/MM/yyyy hh:mm aa" , calender).toString();

        final String ngo_email = modalNgoList.get(i).getEmail();
        final String ngo_userId = modalNgoList.get(i).getUserId();
        myholder.ngo_name.setText(ngo_name);
        myholder.ngo_email.setText(ngo_email);
        myholder.followBtn.setText(nFollow + "Follow");
        setLikes(myholder,ngo_userId);
        myholder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setItems(new String[]{"Profile", "Donate"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(i == 0){
                            Intent intent = new Intent(context, PopUp.class);
                            intent.putExtra("userId",ngo_userId);
                            context.startActivity(intent);
                        }
                        if(i == 1){
                            Intent intent = new Intent(context, GooglePay_ngo.class);
                            intent.putExtra("userId",ngo_userId);
                            context.startActivity(intent);
                        }
                    }
                });
                builder.create().show();
            }
        });

        myholder.followBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mProcessFollow =true;
                final String ngoIde = modalNgoList.get(i).getUserId();
                FollowRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(mProcessFollow) {
                            if (snapshot.child(ngoIde).hasChild(adminId)) {
                                ngoRef.child(ngoIde).child("nFollow").setValue("" + (Integer.parseInt(nFollow)-1));
                                FollowRef.child(ngoIde).child(adminId).removeValue();
                                mProcessFollow = false;
                            }
                            else{
                                ngoRef.child(ngoIde).child("nFollow").setValue("" + (Integer.parseInt(nFollow)+1));
                                FollowRef.child(ngoIde).child(adminId).setValue(""+ pTime);
                                mProcessFollow = false;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });


    }

    private void setLikes(final MyHolder holder, final String ngo_userId) {
        FollowRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(ngo_userId).hasChild(adminId)){

                    holder.followBtn.setText("Followed");
                }
                else{
                    holder.followBtn.setText("Follow");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @Override
    public int getItemCount() {
        return modalNgoList.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder{
        TextView ngo_name , ngo_email , ngo_cause;
        Button followBtn;
        Dialog dialog;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            ngo_name = itemView.findViewById(R.id.ngo_name);
//            ngo_cause = itemView.findViewById(R.id.ngo_cause);
            ngo_email = itemView.findViewById(R.id.ngo_email);
            followBtn = itemView.findViewById(R.id.followBtn);

        }
    }
}
