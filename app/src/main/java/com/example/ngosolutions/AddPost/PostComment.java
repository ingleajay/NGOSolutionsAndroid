package com.example.ngosolutions.AddPost;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ngosolutions.HomesScreen;
import com.example.ngosolutions.LoginActivity.MainScreenClass;
import com.example.ngosolutions.LoginActivity.ProfileClass;
import com.example.ngosolutions.R;
import com.example.ngosolutions.adapter.AdapterComment;
import com.example.ngosolutions.models.ModalComments;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class PostComment extends AppCompatActivity {
ImageView uPictureTv ,pImageTv;
TextView uNameTv, pTimeTv,pTitleTv,pDescriptionTv,pLikeTv,subtitle,pCommentsTv;
ImageButton moreBtn;
ImageView passback;
LinearLayout profileLayout;
RecyclerView recyclerView;
List<ModalComments> commentsList;
AdapterComment adapterComment;
Button likeBtn , shareBtn;
EditText commentEt;
ImageButton sendBtn;
ImageView cAvatarTv;
ProgressDialog pd;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
boolean mProcessComment = false;
boolean mProcessLike = false;
String myUid , myEmail , myName , myDp , postId , pLikes,hisDp, hisName,hisUid,pImage ,pComments;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_post_comment);
        Intent intent = getIntent();
        postId = intent.getStringExtra("postId");
        uPictureTv = findViewById(R.id.uPictureTv);
        pImageTv = findViewById(R.id.pImageTv);
        uNameTv = findViewById(R.id.uNameTv);
        pTimeTv = findViewById(R.id.pTimeTv);
        pTitleTv = findViewById(R.id.pTitleTv);
        pDescriptionTv = findViewById(R.id.pDescriptionTv);
        pLikeTv= findViewById(R.id.pLikesTv);
        moreBtn = findViewById(R.id.moreBtn);
        likeBtn = findViewById(R.id.likebtn);
        passback= findViewById(R.id.pass_back);
        shareBtn = findViewById(R.id.shareBtn);
        profileLayout = findViewById(R.id.profileLayout);
        recyclerView = findViewById(R.id.recyclerView);
        commentEt = findViewById(R.id.commentEt);
        cAvatarTv =findViewById(R.id.cAvatarTv);
        pCommentsTv=findViewById(R.id.pCommentsTv);
        firebaseAuth  = FirebaseAuth.getInstance();
        user =  firebaseAuth.getCurrentUser();
        sendBtn = findViewById(R.id.sendBtn);
        checkUserStatus();
        loadPostInfo();
        loadUserInfo();
        setLikes();
        loadComments();

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postComment();
            }
        });
        likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               likePost();
            }
        });

        moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMoreOptions();
            }
        });
        passback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), HomesScreen.class);
                startActivity(intent);

            }
        });

        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pTitle = pTitleTv.getText().toString().trim();
                String  pDescription = pDescriptionTv.getText().toString().trim();

                BitmapDrawable bitmapDrawable = (BitmapDrawable)pImageTv.getDrawable();
                if(bitmapDrawable == null){
                    shareTextonly(pTitle , pDescription);
                }
                else{
                    Bitmap bitmap = bitmapDrawable.getBitmap();
                    shareImageAndText(pTitle, pDescription,bitmap);

                }
            }
        });

       pLikeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PostComment.this, PostLikedByActivity.class);
                intent.putExtra("postId",postId);
                startActivity(intent);            }
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
        startActivity(Intent.createChooser(sIntent , "Share Via"));
    }

    private void shareImageAndText(String pTitle, String pDescription, Bitmap bitmap) {
        String sharebody = pTitle + "\n" + pDescription;
        Uri uri = saveImageToShare(bitmap);

        Intent sIntent = new Intent(Intent.ACTION_SEND);
        sIntent.putExtra(Intent.EXTRA_STREAM ,uri);
        sIntent.putExtra(Intent.EXTRA_TEXT , sharebody);
        sIntent.putExtra(Intent.EXTRA_SUBJECT , "Subject Here");
        sIntent.setType("image/png");
        startActivity(Intent.createChooser(sIntent,"Share Via"));
    }

    private Uri saveImageToShare(Bitmap bitmap) {
        File imageFolder = new File(getCacheDir() , "images");
        Uri uri = null;
        try{
            imageFolder.mkdirs();
            File file = new File(imageFolder  , "shared_image.png");
            FileOutputStream stream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG , 90,stream);
            stream.flush();
            stream.close();
            uri = FileProvider.getUriForFile(this, "com.example.ngosolutions.fileprovider", file);

        }
        catch (Exception e){
            Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return uri;
    }
    private void loadComments() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        commentsList =new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("App_posts").child(postId).child("Comments");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                commentsList.clear();
                for (DataSnapshot ds: snapshot.getChildren()){
                    ModalComments modalComments = ds.getValue(ModalComments.class);
                    commentsList.add(modalComments);
                    adapterComment =new AdapterComment(getApplicationContext() ,commentsList,myUid,postId);
                    recyclerView.setAdapter(adapterComment);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showMoreOptions() {
        PopupMenu popupMenu = new PopupMenu(this , moreBtn , Gravity.END);
        if(hisUid.equals(myUid)){
            popupMenu.getMenu().add(Menu.NONE , 0 , 0 , "Delete");
            popupMenu.getMenu().add(Menu.NONE , 1 , 0 , "Edit");
        }

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                int id= menuItem.getItemId();
                if(id==0){
                    beginDelete();
                }
                else if(id==1){
                    Intent intent = new Intent(PostComment.this , PostAdd.class);
                    intent.putExtra("key","editpost");
                    intent.putExtra("editPostId",postId);
                    startActivity(intent);
                }


                return false;
            }
        });
        popupMenu.show();
    }

    private void beginDelete() {
        if(pImage.equals("noImage")){
            deleteWithoutImage();
        }
        else{
            deleteWithImage();
        }
    }

    private void deleteWithImage() {
        pd= new ProgressDialog(PostComment.this, R.style.MyAlertDialogStyle);
        pd.setMessage("Delete...");

        StorageReference picRef = FirebaseStorage.getInstance().getReferenceFromUrl(pImage);
        picRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Query query = FirebaseDatabase.getInstance().getReference("App_posts").orderByChild("pId").equalTo(postId);

                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds: snapshot.getChildren()){
                            ds.getRef().removeValue();
                        }
                        Toast.makeText(PostComment.this, "Deleted sucessfully", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(PostComment.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteWithoutImage() {
        pd= new ProgressDialog(PostComment.this, R.style.MyAlertDialogStyle);
        pd.setMessage("Delete...");
        Query query = FirebaseDatabase.getInstance().getReference("App_posts").orderByChild("pId").equalTo(postId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds: snapshot.getChildren()){
                    ds.getRef().removeValue();
                }
                Toast.makeText(PostComment.this, "Deleted sucessfully", Toast.LENGTH_SHORT).show();
                pd.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setLikes() {
        final DatabaseReference likeRef = FirebaseDatabase.getInstance().getReference().child("Likes");
        likeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(postId).hasChild(myUid)){
                    likeBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_like_pink , 0 , 0 , 0);
                   likeBtn.setText("Liked");
                }
                else{
                    likeBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_likes , 0 , 0 , 0);
                    likeBtn.setText("Like");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void likePost() {
        mProcessLike =true;
        final DatabaseReference likeRef = FirebaseDatabase.getInstance().getReference().child("Likes");
        final DatabaseReference postRef = FirebaseDatabase.getInstance().getReference().child("App_posts");
        likeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(mProcessLike) {
                    if (snapshot.child(postId).hasChild(myUid)) {
                        postRef.child(postId).child("pLikes").setValue("" + (Integer.parseInt(pLikes)-1));
                        likeRef.child(postId).child(myUid).removeValue();
                        mProcessLike = false;
                        likeBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_likes , 0,0,0);
                        likeBtn.setText("Like");
                    }
                    else{
                        postRef.child(postId).child("pLikes").setValue(""+(Integer.parseInt(pLikes)+1));
                        likeRef.child(postId).child(myUid).setValue("Liked");
                        mProcessLike = false;
                        likeBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_like_pink , 0,0,0);
                        likeBtn.setText("Liked");
                        addToHisNotification(""+hisUid,""+postId,"Liked your post");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void postComment() {
        pd = new ProgressDialog(this , R.style.MyAlertDialogStyle);
        pd.setMessage("Adding Comment...");

        String comment = commentEt.getText().toString().trim();
        if(TextUtils.isEmpty(comment)){
            Toast.makeText(this, "Comment is Empty", Toast.LENGTH_SHORT).show();
            return;
        }
        String timeStamp =String.valueOf(System.currentTimeMillis());
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("App_posts").child(postId).child("Comments");

        HashMap<String ,Object> hashMap = new HashMap<>();
        hashMap.put("cId",timeStamp);
        hashMap.put("comment",comment);
        hashMap.put("timestamp",timeStamp);
        hashMap.put("uid",myUid);
        hashMap.put("uEmail",myEmail);
        hashMap.put("uDp",myDp);
        hashMap.put("uName",myName);

        ref.child(timeStamp).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                pd.dismiss();
                Toast.makeText(PostComment.this, "Comment Added...", Toast.LENGTH_SHORT).show();
                commentEt.setText("");
                updateCommentCount();
                addToHisNotification(""+hisUid,""+postId,"Commented on your post");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(PostComment.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void updateCommentCount() {
        mProcessComment= true;
        final DatabaseReference ref= FirebaseDatabase.getInstance().getReference("App_posts").child(postId);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot snapshot) {
               if(mProcessComment) {

                       String comments = ""+snapshot.child("pComments").getValue();
                       int newCommentVal  = Integer.parseInt(comments) + 1;
                       ref.child("pComments").setValue("" + newCommentVal);
                       mProcessComment = false;

               }

           }

           @Override
           public void onCancelled(@NonNull DatabaseError error) {

           }
       });
    }

    private void loadUserInfo() {
        Query query = FirebaseDatabase.getInstance().getReference("App_Users");
        query.orderByChild("_uid").equalTo(myUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds: snapshot.getChildren()){
                    myName =""+ds.child("_reguser").getValue();
                    myDp = ""+ds.child("image").getValue();
                    myUid =""+ds.child("_uid").getValue();
                    try {
                        Picasso.get().load(myDp).placeholder(R.drawable.icon_face).into(cAvatarTv);
                    }
                    catch (Exception e){
                        Picasso.get().load(R.drawable.icon_face).into(cAvatarTv);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadPostInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("App_posts");
        Query query= ref.orderByChild("pId").equalTo(postId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds: snapshot.getChildren()){
                    String pTitle = ""+ds.child("pTitle").getValue();
                    String pDesec = ""+ds.child("pDesec").getValue();
                    pLikes = ""+ds.child("pLikes").getValue();
                    String pTimeStamp= ""+ds.child("pTime").getValue();
                    pImage= ""+ds.child("pImage").getValue();
                    hisDp= ""+ds.child("uDp").getValue();
                    hisUid = ""+ds.child("uid").getValue();
                    String uEmail = ""+ds.child("uEmail").getValue();
                    hisName = ""+ds.child("uName").getValue();
                    String commentscount = ""+ds.child("pComments").getValue();

                    Calendar calender = Calendar.getInstance(Locale.getDefault());
                    calender.setTimeInMillis(Long.parseLong(pTimeStamp));
                    String pTime = DateFormat.format("dd/MM/yyyy hh:mm aa" , calender).toString();

                    pTitleTv.setText(pTitle);
                    pDescriptionTv.setText(pDesec);
                    pLikeTv.setText(pLikes +"\tLikes");
                    pTimeTv.setText(pTime);
                    uNameTv.setText(hisName);
                    pCommentsTv.setText(commentscount + "\tComment");

                    if(pImage.equals("noImage")){
                       pImageTv.setVisibility(View.GONE);
                    }
                    else {
                        pImageTv.setVisibility(View.VISIBLE);
                        try {
                            Picasso.get().load(pImage).into(pImageTv);
                        } catch (Exception e) {

                        }
                    }

                    try {
                        Picasso.get().load(hisDp).placeholder(R.drawable.icon_face).into(uPictureTv);
                    } catch (Exception e) {
                            Picasso.get().load(R.drawable.icon_face).into(uPictureTv);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void checkUserStatus(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            // user is sign in
            myEmail = user.getEmail();
            myUid = user.getUid();
        }
        else {
            // user not signin in ,  go to main activity
            startActivity(new Intent(PostComment.this , MainScreenClass.class));
            finish();
        }
    }
    @Override
    protected void onStart() {
        checkUserStatus();
        super.onStart();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}