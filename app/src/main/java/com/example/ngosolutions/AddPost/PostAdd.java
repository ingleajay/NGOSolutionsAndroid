package com.example.ngosolutions.AddPost;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ngosolutions.HomesScreen;
import com.example.ngosolutions.LoginActivity.AdapterUsers;
import com.example.ngosolutions.LoginActivity.MainScreenClass;
import com.example.ngosolutions.LoginActivity.ModalUsers;
import com.example.ngosolutions.LoginActivity.ProfileClass;
import com.example.ngosolutions.LoginActivity.SigninClass;
import com.example.ngosolutions.R;
import com.example.ngosolutions.adapter.AdapterPost;
import com.example.ngosolutions.models.ModelPost;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
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
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;

public class PostAdd extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    ProgressDialog pd;
    StorageReference storageReference;
    Uri image_uri = null;
    private static  final  int CAMERA_REQUEST_CLICK =100;
    private static  final  int STORAGE_REQUEST_CLICK =200;
    private static  final  int IMAGE_PICK_GALLERY_REQUEST_CODE = 300;
    private static  final  int IMAGE_PICK_CAMERA_REQUEST_CODE = 400;
    String[] cameraPermissions ;
    String[] storagePermission;
    TextView set_text;
    ImageView passback , imageTv;
    TextInputLayout titleEt , descriptionEt;
    Button uploadBtn;

    String _reg_user , _reg_email , _uid,_dp;
    String editTitle , editdescription , editImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_post_add);
        passback = findViewById(R.id.pass_back);
        titleEt = findViewById(R.id.pTitleEt);
        set_text = findViewById(R.id.set_text);
        descriptionEt = findViewById(R.id.pDescriptionEt);
        imageTv = findViewById(R.id.pImageTv);
        uploadBtn = findViewById(R.id.upload);
        firebaseAuth  = FirebaseAuth.getInstance();
        checkUserStatus();
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if(Intent.ACTION_SEND.equals(action) && type!=null){
            if("text/plain".equals(type)){
                handleSendText(intent);
            }
            else if(type.startsWith("image")){
                handleSendImage(intent);
            }
        }

        final String isUpdatekey = ""+intent.getStringExtra("key");
       final String editPostId = ""+intent.getStringExtra("editPostId");
        if(isUpdatekey.equals("editpost")){
            set_text.setText("Edit Post");
            uploadBtn.setText("Update");
            loadPostData(editPostId);

        }
        else {
            set_text.setText("Add Post");
            uploadBtn.setText("Upload");
        }
        imageTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImagePickDialog();
            }
        });
        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String title= titleEt.getEditText().getText().toString().trim();
                String description = descriptionEt.getEditText().getText().toString().trim();
                Intent intent = getIntent();
                String isUpdatekey = ""+intent.getStringExtra("key");
                String editPostId = ""+intent.getStringExtra("editPostId");

                if (!validatedesc() | !validatetitle()) {
                    return ;
                }
                if(isUpdatekey.equals("editpost")){
                    beginUpdate(title,description,editPostId);
                }
                else{
                    uploadData(title,description);
                }
            }
        });
        cameraPermissions = new String[]{Manifest.permission.CAMERA , Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE};
        pd = new ProgressDialog(this , R.style.MyAlertDialogStyle);

        user =  firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("App_Users");
        Query query = databaseReference.orderByChild("_reg_email").equalTo(user.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                for(DataSnapshot ds : datasnapshot.getChildren() ){
                    _reg_user = ""+ds.child("_reguser").getValue();
                    _reg_email = ""+ds.child("_reg_email").getValue();
                    _dp = ""+ds.child("image").getValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        passback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), HomesScreen.class);
                startActivity(intent);

            }
        });

    }

    private void handleSendImage(Intent intent) {
    Uri imageuri = (Uri)intent.getParcelableExtra(Intent.EXTRA_STREAM);
    if(imageuri != null){
        image_uri = imageuri;
        imageTv.setImageURI(image_uri);
    }
    }

    private void handleSendText(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if(sharedText!=null){
            descriptionEt.getEditText().setText(sharedText);
        }
    }

    private void loadPostData(String editPostId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("App_posts");
        Query query = reference.orderByChild("pId").equalTo(editPostId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds: snapshot.getChildren()){
                    editTitle= ""+ds.child("pTitle").getValue();
                    editdescription = ""+ds.child("pDesec").getValue();
                    editImage = ""+ds.child("pImage").getValue();

                    titleEt.getEditText().setText(editTitle);
                    descriptionEt.getEditText().setText(editdescription);

                    if(!editImage.equals("noImage")){
                        try{
                            Picasso.get().load(editImage).into(imageTv);

                        }catch (Exception e){

                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void uploadData(final String title, final String description) {
        pd.setMessage("Publishing Post...");
        pd.show();

        final String timestamp = String.valueOf(System.currentTimeMillis());
        final String pLikes = "0";
        final String pComments = "0";
        String filePathAndName = "App_posts/" + "post" + timestamp;
        if(imageTv.getDrawable() != null){

            Bitmap bitmap = ((BitmapDrawable)imageTv.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG , 100,baos);
            byte[] data = baos.toByteArray();


            StorageReference ref = FirebaseStorage.getInstance().getReference().child(filePathAndName);
            ref.putBytes(data)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isSuccessful());
                            String downloadUri = uriTask.getResult().toString();
                            if(uriTask.isSuccessful()){
                                HashMap<Object , String> hashmap = new HashMap<>();
                                hashmap.put("uid",_uid);
                                hashmap.put("uName",_reg_user);
                                hashmap.put("uEmail",_reg_email);
                                hashmap.put("uDp",_dp);
                                hashmap.put("pId",timestamp);
                                hashmap.put("pTitle",title);
                                hashmap.put("pDesec",description);
                                hashmap.put("pImage",downloadUri);
                                hashmap.put("pTime",timestamp);
                                hashmap.put("pLikes",pLikes);
                                hashmap.put("pComments",pComments);
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("App_posts");
                                ref.child(timestamp).setValue(hashmap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                pd.dismiss();
                                                Toast.makeText(PostAdd.this, "Post Published", Toast.LENGTH_SHORT).show();
                                                titleEt.getEditText().setText("");
                                                descriptionEt.getEditText().setText("");
                                                imageTv.setImageURI(null);
                                                image_uri =null;
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                pd.dismiss();
                                                Toast.makeText(PostAdd.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    pd.dismiss();
                    Toast.makeText(PostAdd.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        else{
            HashMap<Object , String> hashmap = new HashMap<>();
            hashmap.put("uid",_uid);
            hashmap.put("uName",_reg_user);
            hashmap.put("uEmail",_reg_email);
            hashmap.put("uDp",_dp);
            hashmap.put("pId",timestamp);
            hashmap.put("pTitle",title);
            hashmap.put("pDesec",description);
            hashmap.put("pImage","noImage");
            hashmap.put("pTime",timestamp);
            hashmap.put("pLikes",pLikes);
            hashmap.put("pComments",pComments);
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("App_posts");
            ref.child(timestamp).setValue(hashmap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            pd.dismiss();
                            Toast.makeText(PostAdd.this, "Post Published", Toast.LENGTH_SHORT).show();
                            titleEt.getEditText().setText("");
                            descriptionEt.getEditText().setText("");
                            imageTv.setImageURI(null);
                            image_uri =null;
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pd.dismiss();
                            Toast.makeText(PostAdd.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void showImagePickDialog() {
        String[] options = {"Camera","Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Image from");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(i==0){
                    if(!checkCameraPermission()){
                        requestCameraPermission();
                    }
                    else{
                        pickFromCamera();
                    }
                }
                if(i==1){
                    // edit cover
                    if(!checkStoragePermission()){
                        requestStoragePermission();
                    }
                    else{
                        pickFromGallery();
                    }
                }
            }
        });
        builder.create().show();
    }
    private void pickFromCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,"Temp Pic");
        values.put(MediaStore.Images.Media.DESCRIPTION,"Temp Desciption");
        image_uri = this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(cameraIntent,IMAGE_PICK_CAMERA_REQUEST_CODE);
    }
    private  boolean checkStoragePermission(){
        boolean result = ContextCompat.checkSelfPermission(this , Manifest.permission.WRITE_EXTERNAL_STORAGE)
                ==(PackageManager.PERMISSION_GRANTED);
        return  result;
    }
    private  void requestStoragePermission(){
        // request runntime storage permission
        ActivityCompat.requestPermissions(this,storagePermission,STORAGE_REQUEST_CLICK);

    }
    private  boolean checkCameraPermission(){
        boolean result = ContextCompat.checkSelfPermission(this , Manifest.permission.CAMERA)
                ==(PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this , Manifest.permission.WRITE_EXTERNAL_STORAGE)
                ==(PackageManager.PERMISSION_GRANTED);
        return  result && result1 ;
    }
    private  void requestCameraPermission(){
        // request runntime storage permission
        ActivityCompat.requestPermissions(this,cameraPermissions , CAMERA_REQUEST_CLICK);

    }
    private void pickFromGallery() {
        Intent galleryIntent =  new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,IMAGE_PICK_GALLERY_REQUEST_CODE);
    }
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case  CAMERA_REQUEST_CLICK: {
                if(grantResults.length > 0){
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if(cameraAccepted && writeStorageAccepted){
                        pickFromCamera();
                    }
                    else{
                        Toast.makeText(this, "Please enable camera & storage Permission", Toast.LENGTH_SHORT).show();
                    }
                }

            }
            break;
            case  STORAGE_REQUEST_CLICK:{
                if(grantResults.length > 0){
                    boolean writeStorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if(writeStorageAccepted){
                        pickFromGallery();
                    }
                    else{
                        Toast.makeText(this, "Please enable storage Permission", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
        }



    }
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == RESULT_OK){
            if(requestCode == IMAGE_PICK_GALLERY_REQUEST_CODE){

                image_uri = data.getData();

                imageTv.setImageURI(image_uri);

            }
            if(requestCode == IMAGE_PICK_CAMERA_REQUEST_CODE){

                imageTv.setImageURI(image_uri);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }



    private void checkUserStatus(){
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user != null){
            // user is sign in
            _reg_email = user.getEmail();
            _uid = user.getUid();
        }
        else {
            // user not signin in ,  go to main activity
            startActivity(new Intent(this , MainScreenClass.class));
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkUserStatus();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkUserStatus();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

//    public void uploadBtn(View view){
//
//
////        if(image_uri==null){
////            uploadData(title,description,"noImage");
////        }
////        else {
////            uploadData(title,description,String.valueOf(image_uri));
////        }
//    }

    private void beginUpdate(String title, String description, String editPostId) {
        pd.setMessage("Updating Post");
        pd.show();
        if(!editImage.equals("noImage")){
            updateWasWithImage(title,description,editPostId);

        }
        if(imageTv.getDrawable() != null) {
            updateWithNowImage(title,description,editPostId);
        }
        else {
            updateWithoutImage(title,description,editPostId);
        }

    }

    private void updateWithoutImage(String title, String description, String editPostId) {

        HashMap<String , Object> hashMap = new HashMap<>();
        hashMap.put("uid",_uid);
        hashMap.put("uName",_reg_user);
        hashMap.put("uEmail",_reg_email);
        hashMap.put("uDp",_dp);
        hashMap.put("pTitle",title);
        hashMap.put("pDesec" , description);
        hashMap.put("pImage","noImage");


        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("App_posts");
        ref.child(editPostId)
                .updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                pd.dismiss();
                Toast.makeText(PostAdd.this, "Updated", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(PostAdd.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateWasWithImage(final String title, final String description, final String editPostId) {

        StorageReference mPictureRef = FirebaseStorage.getInstance().getReference(editImage);
        mPictureRef.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        String timeStamp = String.valueOf(System.currentTimeMillis());
                        String filePathAndName = "App_posts/"+"post_"+timeStamp;

                        Bitmap bitmap = ((BitmapDrawable)imageTv.getDrawable()).getBitmap();
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG , 100,baos);
                        byte[] data = baos.toByteArray();

                        StorageReference ref = FirebaseStorage.getInstance().getReference().child(filePathAndName);
                        ref.putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                                while (!uriTask.isSuccessful());

                                String downloadUri = uriTask.getResult().toString();
                                if(uriTask.isSuccessful()){
                                    HashMap<String , Object> hashMap = new HashMap<>();
                                    hashMap.put("uid",_uid);
                                    hashMap.put("uName",_reg_user);
                                    hashMap.put("uEmail",_reg_email);
                                    hashMap.put("uDp",_dp);
                                    hashMap.put("pTitle",title);
                                    hashMap.put("pDesec" , description);
                                    hashMap.put("pImage",downloadUri);

                                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("App_posts");
                                    ref.child(editPostId)
                                            .updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                                pd.dismiss();
                                            Toast.makeText(PostAdd.this, "Updated", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            pd.dismiss();
                                            Toast.makeText(PostAdd.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                pd.dismiss();
                                Toast.makeText(PostAdd.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(PostAdd.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateWithNowImage(final String title, final String description, final String editPostId) {
        String timeStamp = String.valueOf(System.currentTimeMillis());
        String filePathAndName = "App_posts/"+"post_"+timeStamp;

        Bitmap bitmap = ((BitmapDrawable)imageTv.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG , 100,baos);
        byte[] data = baos.toByteArray();

        StorageReference ref = FirebaseStorage.getInstance().getReference().child(filePathAndName);
        ref.putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isSuccessful());

                String downloadUri = uriTask.getResult().toString();
                if(uriTask.isSuccessful()){
                    HashMap<String , Object> hashMap = new HashMap<>();
                    hashMap.put("uid",_uid);
                    hashMap.put("uName",_reg_user);
                    hashMap.put("uEmail",_reg_email);
                    hashMap.put("uDp",_dp);
                    hashMap.put("pTitle",title);
                    hashMap.put("pDesec" , description);
                    hashMap.put("pImage",downloadUri);

                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("App_posts");
                    ref.child(editPostId)
                            .updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            pd.dismiss();
                            Toast.makeText(PostAdd.this, "Updated", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pd.dismiss();
                            Toast.makeText(PostAdd.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(PostAdd.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    private boolean validatetitle() {
        String title =titleEt.getEditText().getText().toString().trim();

        if (title.isEmpty()) {
            titleEt.setError("Field can not be empty");
            return false;
        } else {
            titleEt.setError(null);
            titleEt.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validatedesc() {
        String desc =descriptionEt.getEditText().getText().toString().trim();


        if (desc.isEmpty()) {
            titleEt.setError("Field can not be empty");
            return false;
        } else {
            titleEt.setError(null);
            titleEt.setErrorEnabled(false);
            return true;
        }
    }



}