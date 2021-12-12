package com.example.ngosolutions.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ngosolutions.R;
import com.example.ngosolutions.models.ModalNgoPost;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AdapterNgoPost extends  RecyclerView.Adapter<AdapterNgoPost.Myholder> {
    Context context;
    List<ModalNgoPost> postList;
    String myUid;
    private DatabaseReference likeRef;
    private  DatabaseReference postRef;
    boolean  mProcessLike = false ;
    public AdapterNgoPost(Context context, List<ModalNgoPost> modalNgoPostList) {
        this.context = context;
        this.postList = modalNgoPostList;
        myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        likeRef = FirebaseDatabase.getInstance().getReference().child("Ngo_Likes");
        postRef = FirebaseDatabase.getInstance().getReference().child("post_app");
    }


    @NonNull
    @Override
    public Myholder onCreateViewHolder(@NonNull ViewGroup  viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_ngo_post , viewGroup, false);
        return new Myholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final Myholder myholder, final int i) {
        final String ngo_uid = postList.get(i).getAdminId();
        String uEmail = postList.get(i).getuEmail();
        final long pId = postList.get(i).getpId();
        final String pTitle = postList.get(i).getpTitle();
        final String pDescription = postList.get(i).getpDesec();
        final String pImage = postList.get(i).getpImage();
        long pTimeStamp = postList.get(i).getpTime();

        String pLikes = postList.get(i).getpLikes();
        Calendar calender = Calendar.getInstance(Locale.getDefault());
        calender.setTimeInMillis(pTimeStamp);
        String pTime = DateFormat.format("dd/MM/yyyy hh:mm aa" , calender).toString();

        final long ptstamp= postList.get(i).getpTime();
        Calendar calender1 = Calendar.getInstance(Locale.getDefault());
        calender.setTimeInMillis(ptstamp);
        final String pTime1 = DateFormat.format("dd/MM/yyyy hh:mm aa" , calender1).toString();



        myholder.uNameTv.setText("Email : "+uEmail);
        myholder.pTimeTv.setText(pTime);
        myholder.pTitleTv.setText(pTitle);
        myholder.pDescriptionTv.setText(pDescription);
        myholder.pLikesTv.setText(pLikes + "Likes");
        setLikes(myholder,pId);
        try {
                Picasso.get().load(pImage).into(myholder.pImageTv);
            } catch (Exception e) {

        }
        myholder.likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final int pLikes = Integer.parseInt(postList.get(i).getpLikes());

                mProcessLike =true;
                final long postIde = postList.get(i).getpId();


                likeRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(mProcessLike) {

                            if (snapshot.child(String.valueOf(postIde)).hasChild(myUid)) {

                                postRef.child(String.valueOf(postIde)).child("pLikes").setValue("" + (pLikes-1));
                                likeRef.child(String.valueOf(postIde)).child(myUid).removeValue();
                                mProcessLike = false;
                            }
                            else{

                                postRef.child(String.valueOf(postIde)).child("pLikes").setValue(""+(pLikes+1));
                                likeRef.child(String.valueOf(postIde)).child(myUid).setValue(""+pTime1);
                                mProcessLike = false;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        myholder.shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BitmapDrawable bitmapDrawable = (BitmapDrawable) myholder.pImageTv.getDrawable();
                if(bitmapDrawable == null){
                    shareTextonly(pTitle , pDescription);
                }
                else{
                    Bitmap bitmap = bitmapDrawable.getBitmap();
                    shareImageAndText(pTitle, pDescription,bitmap);

                }
            }
        });

    }
    private void shareTextonly(String pTitle, String pDescription) {
        String shareBody = pTitle + "\n" + pDescription;
        Intent sIntent = new Intent(Intent.ACTION_SEND);
        sIntent.setType("text/plain");
        sIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject Here");
        sIntent.putExtra(Intent.EXTRA_TEXT ,shareBody);
        context.startActivity(Intent.createChooser(sIntent , "Share Via"));
    }

    private void shareImageAndText(String pTitle, String pDescription, Bitmap bitmap) {
        String sharebody = pTitle + "\n" + pDescription;
        Uri uri = saveImageToShare(bitmap);

        Intent sIntent = new Intent(Intent.ACTION_SEND);
        sIntent.putExtra(Intent.EXTRA_STREAM ,uri);
        sIntent.putExtra(Intent.EXTRA_TEXT , sharebody);
        sIntent.putExtra(Intent.EXTRA_SUBJECT , "Subject Here");
        sIntent.setType("image/png");
        context.startActivity(Intent.createChooser(sIntent,"Share Via"));
    }

    private Uri saveImageToShare(Bitmap bitmap) {
        File imageFolder = new File(context.getCacheDir() , "images");
        Uri uri = null;
        try{
            imageFolder.mkdirs();
            File file = new File(imageFolder  , "shared_image.png");
            FileOutputStream stream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG , 90,stream);
            stream.flush();
            stream.close();
            uri = FileProvider.getUriForFile(context, "com.example.ngosolutions.fileprovider", file);

        }
        catch (Exception e){
            Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return uri;
    }


    private void setLikes(final AdapterNgoPost.Myholder holder, final long postkey) {
        likeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(String.valueOf(postkey)).hasChild(myUid)){
                    holder.likeBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_like_pink , 0 , 0 , 0);
                    holder.likeBtn.setText("Liked");
                }
                else{
                    holder.likeBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_likes , 0 , 0 , 0);
                    holder.likeBtn.setText("Like");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    @Override
    public int getItemCount() {
        return postList.size();
    }

    class Myholder extends RecyclerView.ViewHolder{
        ImageView pImageTv;
        TextView uNameTv, pTimeTv,pTitleTv,pDescriptionTv,admin,pLikesTv;
        Button likeBtn ,shareBtn;
        LinearLayout profileLayout;
        public Myholder(@NonNull View itemView){
            super(itemView);
            pImageTv = itemView.findViewById(R.id.pImageTv);
            uNameTv= itemView.findViewById(R.id.uNameTv);
            pTimeTv = itemView.findViewById(R.id.pTimeTv);
            pTitleTv = itemView.findViewById(R.id.pTitleTv);
            pLikesTv = itemView.findViewById(R.id.pLikesTv);
            pDescriptionTv = itemView.findViewById(R.id.pDescriptionTv);
            admin= itemView.findViewById(R.id.admin);
            likeBtn = itemView.findViewById(R.id.likebtn);
            shareBtn= itemView.findViewById(R.id.shareBtn);
            profileLayout= itemView.findViewById(R.id.profileLayout);

        }
    }
}
