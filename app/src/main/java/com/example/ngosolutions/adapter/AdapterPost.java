package com.example.ngosolutions.adapter;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ngosolutions.AddPost.PostAdd;
import com.example.ngosolutions.AddPost.PostComment;
import com.example.ngosolutions.AddPost.PostLikedByActivity;
import com.example.ngosolutions.AddPost.ThereProfile;
import com.example.ngosolutions.R;
import com.example.ngosolutions.models.ModelPost;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;


import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class AdapterPost extends  RecyclerView.Adapter<AdapterPost.Myholder> {
    Context context;
    List<ModelPost> postList;
    String myUid;
    ProgressDialog pd;
    private DatabaseReference likeRef;
    private  DatabaseReference postRef;
    boolean  mProcessLike = false ;
    public AdapterPost(Context context, List<ModelPost> postList) {
        this.context = context;
        this.postList = postList;
        myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        likeRef = FirebaseDatabase.getInstance().getReference().child("Likes");
        postRef = FirebaseDatabase.getInstance().getReference().child("App_posts");

    }

    @NonNull
    @Override
    public Myholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_posts , viewGroup, false);
        return new Myholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final Myholder myholder, final int i) {
        final String uid = postList.get(i).getUid();
        String uEmail = postList.get(i).getuEmail();
        String uName = postList.get(i).getuName();
        String uDp = postList.get(i).getuDp();
        final String pId = postList.get(i).getpId();
        final String pTitle = postList.get(i).getpTitle();
        final String pDescription = postList.get(i).getpDesec();
        final String pImage = postList.get(i).getpImage();
        String pTimeStamp = postList.get(i).getpTime();
        String pLikes = postList.get(i).getpLikes();
        String pComments = postList.get(i).getpComments();

        Calendar calender = Calendar.getInstance(Locale.getDefault());
        calender.setTimeInMillis(Long.parseLong(pTimeStamp));
        String pTime = DateFormat.format("dd/MM/yyyy hh:mm aa" , calender).toString();

        myholder.uNameTv.setText(uName);
        myholder.pTimeTv.setText(pTime);
        myholder.pTitleTv.setText(pTitle);
        myholder.pDescriptionTv.setText(pDescription);
        myholder.pLikesTv.setText(pLikes + "Likes");
        myholder.pCommentsTv.setText(pComments + "Comments");
        setLikes(myholder,pId);

        try{
            Picasso.get().load(uDp).placeholder(R.drawable.add_photo).into(myholder.uPictureTv);
        }
        catch (Exception e){

        }
        if(pImage.equals("noImage")){
            myholder.pImageTv.setVisibility(View.GONE);
        }
        else {
            myholder.pImageTv.setVisibility(View.VISIBLE);
            try {
                Picasso.get().load(pImage).into(myholder.pImageTv);
            } catch (Exception e) {

            }
        }
        myholder.moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMoreOptions(myholder.moreBtn , uid , myUid , pId, pImage);
            }
        });

        myholder.likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               final int pLikes = Integer.parseInt(postList.get(i).getpLikes());
                mProcessLike =true;
                final String postIde = postList.get(i).getpId();
                likeRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(mProcessLike) {
                            if (snapshot.child(postIde).hasChild(myUid)) {
                                postRef.child(postIde).child("pLikes").setValue("" + (pLikes-1));
                                likeRef.child(postIde).child(myUid).removeValue();
                                mProcessLike = false;
                            }
                            else{
                                postRef.child(postIde).child("pLikes").setValue(""+(pLikes+1));
                                likeRef.child(postIde).child(myUid).setValue("Liked");
                                mProcessLike = false;

                                addToHisNotification(""+uid,""+pId,"Liked your post");
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        myholder.commentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context , PostComment.class);
                intent.putExtra("postId",pId);
                context.startActivity(intent);
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

        myholder.profileLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ThereProfile.class);
                intent.putExtra("uid",uid);
                context.startActivity(intent);
            }
        });


        myholder.pLikesTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PostLikedByActivity.class);
                intent.putExtra("postId",pId);
                context.startActivity(intent);            }
        });

    }

    private void addToHisNotification(String hisuid , String pId , String notification){
        String timestamp = ""+System.currentTimeMillis();
        HashMap<Object,String> hashMap = new HashMap<>();
        hashMap.put("pId", pId);
        hashMap.put("timestamp", timestamp);
        hashMap.put("pUid", hisuid);
        hashMap.put("notification", notification);
        hashMap.put("sUid", myUid);


        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("App_Users");
        ref.child(hisuid).child("Notification").child(timestamp).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

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


    private void setLikes(final Myholder holder, final String postkey) {
        likeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(postkey).hasChild(myUid)){
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

    private void showMoreOptions(ImageButton moreBtn, String uid, String myUid, final String pId, final String pImage) {

        PopupMenu popupMenu = new PopupMenu(context , moreBtn , Gravity.END);
        if(uid.equals(myUid)){
            popupMenu.getMenu().add(Menu.NONE , 0 , 0 , "Delete");
            popupMenu.getMenu().add(Menu.NONE , 1 , 0 , "Edit");
        }
        popupMenu.getMenu().add(Menu.NONE , 2, 0 , "View Detail");
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                int id= menuItem.getItemId();
                if(id==0){
                    beginDelete(pId,pImage);
                }
                else if(id==1){
                        Intent intent = new Intent(context , PostAdd.class);
                        intent.putExtra("key","editpost");
                        intent.putExtra("editPostId",pId);
                        context.startActivity(intent);
                    }
                else if(id == 2){
                    Intent intent = new Intent(context , PostComment.class);
                    intent.putExtra("postId",pId);
                    context.startActivity(intent);
                }

                return false;
            }
        });
        popupMenu.show();
    }

    private void beginDelete(String pId, String pImage) {
        if(pImage.equals("noImage")){
            deleteWithoutImage(pId);
        }
        else{
            deleteWithImage(pId,pImage);
        }
    }

    private void deleteWithImage(final String pId, String pImage) {
        pd= new ProgressDialog(context, R.style.MyAlertDialogStyle);
        pd.setMessage("Delete...");

        StorageReference picRef = FirebaseStorage.getInstance().getReferenceFromUrl(pImage);
        picRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Query query = FirebaseDatabase.getInstance().getReference("App_posts").orderByChild("pId").equalTo(pId);

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds: snapshot.getChildren()){
                            ds.getRef().removeValue();
                        }
                        Toast.makeText(context, "Deleted sucessfully", Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteWithoutImage(String pId) {
        pd= new ProgressDialog(context, R.style.MyAlertDialogStyle);
        pd.setMessage("Delete...");
        Query query = FirebaseDatabase.getInstance().getReference("App_posts").orderByChild("pId").equalTo(pId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds: snapshot.getChildren()){
                    ds.getRef().removeValue();
                }
                Toast.makeText(context, "Deleted sucessfully", Toast.LENGTH_SHORT).show();
                pd.dismiss();
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
        ImageView uPictureTv ,pImageTv;
        TextView uNameTv, pTimeTv,pTitleTv,pDescriptionTv,pLikesTv,pCommentsTv;
        ImageButton moreBtn;
        Button likeBtn , commentBtn, shareBtn;
        LinearLayout profileLayout;
        public Myholder(@NonNull View itemView){
            super(itemView);

            uPictureTv = itemView.findViewById(R.id.uPictureTv);
            pImageTv = itemView.findViewById(R.id.pImageTv);
            uNameTv= itemView.findViewById(R.id.uNameTv);
            pTimeTv = itemView.findViewById(R.id.pTimeTv);
            pTitleTv = itemView.findViewById(R.id.pTitleTv);
            pDescriptionTv = itemView.findViewById(R.id.pDescriptionTv);
            pLikesTv = itemView.findViewById(R.id.pLikesTv);
            moreBtn= itemView.findViewById(R.id.moreBtn);
            pCommentsTv = itemView.findViewById(R.id.pCommentsTv);
            likeBtn = itemView.findViewById(R.id.likebtn);
            commentBtn= itemView.findViewById(R.id.commentBtn);
            shareBtn= itemView.findViewById(R.id.shareBtn);
            profileLayout= itemView.findViewById(R.id.profileLayout);


        }
    }
}
