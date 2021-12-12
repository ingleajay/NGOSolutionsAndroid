package com.example.ngosolutions.LoginActivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.ngosolutions.HomesScreen;
import com.example.ngosolutions.R;
import com.example.ngosolutions.adapter.AdapterPost;
import com.example.ngosolutions.models.ModelPost;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
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

import java.security.Key;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ProfileClass extends AppCompatActivity {
FirebaseAuth firebaseAuth;
FirebaseUser user;
FirebaseDatabase firebaseDatabase;
DatabaseReference databaseReference;
TextView user_name , user_phone, user_email;
ImageView profileimage , cover;
ImageView passback,set_pass;
FloatingActionButton fab;
ProgressDialog pd;
StorageReference storageReference;
String storagepath = "Users_Profile_Cover_Imgs/";
RecyclerView postsRecyclerview;
Button logout;
EditText visible_edit;
LinearLayout showcontainer;
Button visible_search;
// permission constants
    private static  final  int CAMERA_REQUEST_CODE = 100;
    private static  final  int STORAGE_REQUEST_CODE = 200;
    private static  final  int IMAGE_PICK_GALLERY_REQUEST_CODE = 300;
    private static  final  int IMAGE_PICK_CAMERA_REQUEST_CODE = 400;

    String camerapermission[];
    String storagepermission[];

    List<ModelPost> postList;
    AdapterPost adapterPost;
    String uid;

    Uri image_uri;
    String profileOrCoverPhoto;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_profile_class);
        firebaseAuth  = FirebaseAuth.getInstance();
        logout = findViewById(R.id.logoutbtn);
        visible_edit =findViewById(R.id.searchvisible);
        visible_search = findViewById(R.id.visiblesearch);
        showcontainer = (LinearLayout) findViewById(R.id.linear);
        visible_search.setOnClickListener(new View.OnClickListener() {
            boolean visible;
            @Override
            public void onClick(View view) {

                visible_search.setVisibility(View.VISIBLE);
                showcontainer.setVisibility(View.VISIBLE);

            }
        });
        visible_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                SearchMyPost(editable.toString());
            }
        });
        // init array of permission
        camerapermission = new String[]{Manifest.permission.CAMERA ,  Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagepermission = new String[]{  Manifest.permission.WRITE_EXTERNAL_STORAGE};



        profileimage = findViewById(R.id.profile_image);
        user_email = findViewById(R.id.user_email);
        user_name = findViewById(R.id.user_name);
        passback= findViewById(R.id.pass_back);
//        set_pass= findViewById(R.id.set_pass);
        user_phone = findViewById(R.id.user_phone);
        fab = findViewById(R.id.fab);
        postsRecyclerview = findViewById(R.id.recyclerview_posts);
       user =  firebaseAuth.getCurrentUser();
       firebaseDatabase = FirebaseDatabase.getInstance();
       databaseReference = firebaseDatabase.getReference("App_Users");
        storageReference = FirebaseStorage.getInstance().getReference();
        passback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), HomesScreen.class);
                startActivity(intent);

            }
        });
//        set_pass.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                showChangePaswsword();
//            }
//        });
        pd = new ProgressDialog(this , R.style.MyAlertDialogStyle);

        Query query = databaseReference.orderByChild("_reg_email").equalTo(user.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                for(DataSnapshot ds : datasnapshot.getChildren() ){
                    String  name = ""+ds.child("_reguser").getValue();
                    String  phone = ""+ds.child("_phoneNo").getValue();
                    String  email = ""+ds.child("_reg_email").getValue();
                    String  user_profile = ""+ds.child("image").getValue();
                    String  coverimage = ""+ds.child("cover").getValue();

                    user_email.setText(email);
                    user_phone.setText(phone);
                    user_name.setText(name);


                    try {
                        Picasso.get().load(user_profile).into(profileimage);
                    }
                    catch (Exception e){
                        Picasso.get().load(R.drawable.add_photo).into(profileimage);
                    }
//                    try {
//                        Picasso.get().load(coverimage).into(cover);
//                    }
//                    catch (Exception e){
//                        Picasso.get().load(R.drawable.add_photo).into(cover);
//                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        fab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                showEditProfileDialog();
            }
        });
        postList = new ArrayList<>();
        checkUserStatus();
        loadMyPost();
    }

    private void loadMyPost() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        postsRecyclerview.setLayoutManager(layoutManager);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("App_posts");
        Query query = ref.orderByChild("uid").equalTo(uid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                postList.clear();
                for(DataSnapshot ds: datasnapshot.getChildren()){
                    ModelPost myPost = ds.getValue(ModelPost.class);
                    postList.add(myPost);
                    adapterPost = new AdapterPost(ProfileClass.this,postList);
                    postsRecyclerview.setAdapter(adapterPost);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseerror) {
                Toast.makeText(ProfileClass.this, ""+databaseerror.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

    }
    private void SearchMyPost(final String searchQuery) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        postsRecyclerview.setLayoutManager(layoutManager);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("App_posts");
        Query query = ref.orderByChild("uid").equalTo(uid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                postList.clear();
                for(DataSnapshot ds: datasnapshot.getChildren()){
                    ModelPost myPost = ds.getValue(ModelPost.class);
                    if(myPost.getpTitle().toLowerCase().contains(searchQuery.toLowerCase()) ||
                            myPost.getpDesec().toLowerCase().contains(searchQuery.toLowerCase())){
                        postList.add(myPost);
                    }
                    adapterPost = new AdapterPost(ProfileClass.this,postList);
                    postsRecyclerview.setAdapter(adapterPost);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseerror) {
                Toast.makeText(ProfileClass.this, ""+databaseerror.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

    }
    private  boolean checkStoragePermission(){
            boolean result = ContextCompat.checkSelfPermission(this , Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    ==(PackageManager.PERMISSION_GRANTED);
            return  result;
    }
    private  void requestStoragePermission(){
        // request runntime storage permission
       requestPermissions(storagepermission , STORAGE_REQUEST_CODE);

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
       requestPermissions(camerapermission , CAMERA_REQUEST_CODE);

    }

    private void showEditProfileDialog() {

        String options[] = {"Edit Profile Picture" , "Edit Name","Edit Phone"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Action..");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(i == 0){
                    // edit profile
                    pd.setMessage("Updating Profile Picture...");
                    profileOrCoverPhoto = "image";
                    showImagePicDialog();
                }
//                else if(i == 1){
//                    // edit cover
//                    pd.setMessage("Updating Cover Picture...");
//                    profileOrCoverPhoto = "cover";
//                    showImagePicDialog();
//                }
                else if(i == 1){
                    // edit name
                    pd.setMessage("Updating Name...");
                    showNamePhoneUpdateDialog("_reguser");
                }
                else if(i == 2){
                    // edt phone
                    pd.setMessage("Updating Phone no ...");
                    showNamePhoneUpdateDialog("_phoneNo");
                }
//                else if(i == 3){
//                    // edt phone
//                    pd.setMessage("Changing password ...");
//
//                }
            }
        });
        builder.create().show();
    }

//    private void showChangePaswsword() {
//        View mview = getLayoutInflater().inflate(R.layout.activity_set_new_pass_class, null);
//        final TextInputLayout  passwordEt = (TextInputLayout) mview.findViewById(R.id.passwordEt);
//        final TextInputLayout  cpasswordEt = (TextInputLayout) mview.findViewById(R.id.cpasswordEt);
//        Button updatePass = (Button) mview.findViewById(R.id.set_new_password_btn);
//        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setView(mview);
//        final AlertDialog dialog = builder.create();
//        dialog.show();
//        builder.create().show();
//        updatePass.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String currentpass= passwordEt.getEditText().getText().toString().trim();
//                String newpass= cpasswordEt.getEditText().getText().toString().trim();
//
//                if (!validatenewPassword(currentpass) |!validatencurrentPassword(newpass) ) {
//                       return ;
//                }
//                else {
//                    dialog.dismiss();
//                    updatepassword(currentpass,newpass);
//                }
//            }
//        });
//    }
//
//    private void updatepassword(String passwordEt, final String cpasswordEt) {
//        pd.show();
//        final FirebaseUser user = firebaseAuth.getCurrentUser();
//        final AuthCredential authCredential = EmailAuthProvider.getCredential(user.getUid() ,passwordEt);
//        user.reauthenticate(authCredential).addOnSuccessListener(new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void aVoid) {
//                user.updatePassword(cpasswordEt).addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        pd.dismiss();
//                        Toast.makeText(ProfileClass.this, "Password Updated...", Toast.LENGTH_SHORT).show();
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        pd.dismiss();
//                        Toast.makeText(ProfileClass.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                });
//
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                pd.dismiss();
//                Toast.makeText(ProfileClass.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

    private boolean validatenewPassword(String currentpass) {
        final TextInputLayout  passwordEt = (TextInputLayout) findViewById(R.id.passwordEt);
        String val =passwordEt.getEditText().getText().toString().trim();
        String checkPassword = "^" +
                "(?=.*[0-9])" +         //at least 1 digit
                "(?=.*[a-z])" +         //at least 1 lower case letter
                "(?=.*[A-Z])" +         //at least 1 upper case letter
                "(?=.*[a-zA-Z])" +      //any letter
                "(?=.*[@#$%^&+=])" +    //at least 1 special character
//                "(?=S+$)" +           //no white spaces
                ".{6,}" +               //at least 6 characters
                "$";

        if (val.isEmpty()  ) {
            passwordEt.setError("Field can not be empty");
            return false;
        } else if (!val.matches(currentpass)) {
            passwordEt.setError("Password should contain 6 characters!");
            return false;
        } else {
            passwordEt.setError(null);
            passwordEt.setErrorEnabled(false);
            return true;
        }
    }
    private boolean validatencurrentPassword(String newpass) {
        final TextInputLayout  cpasswordEt = (TextInputLayout) findViewById(R.id.cpasswordEt);
        String val =cpasswordEt.getEditText().getText().toString().trim();
        String checkPassword = "^" +
                "(?=.*[0-9])" +         //at least 1 digit
                "(?=.*[a-z])" +         //at least 1 lower case letter
                "(?=.*[A-Z])" +         //at least 1 upper case letter
                "(?=.*[a-zA-Z])" +      //any letter
                "(?=.*[@#$%^&+=])" +    //at least 1 special character
//                "(?=S+$)" +           //no white spaces
                ".{6,}" +               //at least 6 characters
                "$";

        if (val.isEmpty()  ) {
            cpasswordEt.setError("Field can not be empty");
            return false;
        } else if (!val.matches(newpass)) {
            cpasswordEt.setError("Password should contain 6 characters!");
            return false;
        } else {
            cpasswordEt.setError(null);
            cpasswordEt.setErrorEnabled(false);
            return true;
        }
    }


    private void showNamePhoneUpdateDialog(final String key) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update " + key);
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);;
        linearLayout.setPadding(10, 10,10,10);
        final EditText editText  = new EditText(this);
        editText.setHint("Enter"+key);
        linearLayout.addView(editText);

        builder.setView(linearLayout);
        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                final String value = editText.getText().toString().trim();
                if(!TextUtils.isEmpty(value)){
                    pd.show();
                    HashMap<String, Object> result = new HashMap<>();
                    result.put(key ,value);

                    databaseReference.child(user.getUid()).updateChildren(result)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    pd.dismiss();
                                    Toast.makeText(ProfileClass.this, "Updating...", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                                pd.dismiss();
                            Toast.makeText(ProfileClass.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                    if(key.equals("_reguser")){
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("App_posts");
                        Query query = ref.orderByChild("uid").equalTo(uid);
                        query.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for(DataSnapshot ds: snapshot.getChildren()){
                                    String child = ds.getKey();
                                    snapshot.getRef().child(child).child("uName").setValue(value);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                       ref.addListenerForSingleValueEvent(new ValueEventListener() {
                           @Override
                           public void onDataChange(@NonNull DataSnapshot snapshot) {
                               for(DataSnapshot ds:snapshot.getChildren()){
                                   String child = ds.getKey();
                                   if(snapshot.child(child).hasChild("Comments")){
                                       String child1 = ""+snapshot.child(child).getKey() ;
                                       Query child2 = FirebaseDatabase.getInstance().getReference("App_posts")
                                               .child(child1).child("Comments").orderByChild("uid").equalTo(uid);
                                       child2.addValueEventListener(new ValueEventListener() {
                                           @Override
                                           public void onDataChange(@NonNull DataSnapshot snapshot) {
                                               for(DataSnapshot ds:snapshot.getChildren()){
                                                   String child = ds.getKey();
                                                   snapshot.getRef().child(child).child("uName").setValue(value);

                                               }
                                           }

                                           @Override
                                           public void onCancelled(@NonNull DatabaseError error) {

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
                }
                else{
                    Toast.makeText(ProfileClass.this, " Please Enter"+key+"", Toast.LENGTH_SHORT).show();
                }
            }
        });
       builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
           @Override
           public void onClick(DialogInterface dialog, int i) {
            dialog.dismiss();
           }
       });
        builder.create().show();
    }

    private void showImagePicDialog() {
        String options[] = {"Camera" , "Gallery" };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick Image From..");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(i == 0){
                    //camera click
                    if(!checkCameraPermission()){
                        requestCameraPermission();
                    }
                    else{
                        pickFromCamera();
                    }

                }
                else if(i == 1){
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

    private void checkUserStatus(){
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user != null){
            // user is sign in
            user_email.setText(user.getEmail());
            uid = user.getUid();
        }
        else {
            // user not signin in ,  go to main activity
            startActivity(new Intent(ProfileClass.this ,MainScreenClass.class));
            finish();
        }
    }

    @Override
    protected void onStart() {
        checkUserStatus();
        logoutStatus();
        super.onStart();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void logoutStatus() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user != null) {
            logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    firebaseAuth.signOut();
                    startActivity(new Intent(ProfileClass.this , SigninClass.class));
                    finish();
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case  CAMERA_REQUEST_CODE: {
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
            case  STORAGE_REQUEST_CODE:{
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == RESULT_OK){
            if(requestCode == IMAGE_PICK_GALLERY_REQUEST_CODE){

                image_uri = data.getData();
                uploadProfileCoverPhoto(image_uri);
            }
            if(requestCode == IMAGE_PICK_CAMERA_REQUEST_CODE){
                uploadProfileCoverPhoto(image_uri);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadProfileCoverPhoto(Uri uri) {
        pd.show();
        String filePathAndName = storagepath+""+profileOrCoverPhoto+"_"+user.getUid();
        StorageReference storageReference2nd = storageReference.child(filePathAndName);
        storageReference2nd.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isSuccessful());
                final Uri downloadUri = uriTask.getResult();
                if(uriTask.isSuccessful()){
                    HashMap<String , Object> results = new HashMap<>();
                    results.put(profileOrCoverPhoto , downloadUri.toString());

                    databaseReference.child(user.getUid()).updateChildren(results)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    pd.dismiss();
                                    Toast.makeText(ProfileClass.this, "Image Updated", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pd.dismiss();
                            Toast.makeText(ProfileClass.this, "Error Updating Image", Toast.LENGTH_SHORT).show();

                        }
                    });
                    if(profileOrCoverPhoto.equals("image")){
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("App_posts");
                        Query query = ref.orderByChild("uid").equalTo(uid);
                        query.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for(DataSnapshot ds: snapshot.getChildren()){
                                    String child = ds.getKey();
                                    snapshot.getRef().child(child).child("uDp").setValue(downloadUri.toString());
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                      ref.addListenerForSingleValueEvent(new ValueEventListener() {
                          @Override
                          public void onDataChange(@NonNull DataSnapshot snapshot) {
                              for(DataSnapshot ds:snapshot.getChildren()){
                                  String child = ds.getKey();
                                  if(snapshot.child(child).hasChild("Comments")){
                                      String child1 = ""+snapshot.child(child).getKey() ;
                                      Query child2 = FirebaseDatabase.getInstance().getReference("App_posts")
                                              .child(child1).child("Comments").orderByChild("uid").equalTo(uid);
                                      child2.addValueEventListener(new ValueEventListener() {
                                          @Override
                                          public void onDataChange(@NonNull DataSnapshot snapshot) {
                                              for(DataSnapshot ds:snapshot.getChildren()){
                                                  String child = ds.getKey();
                                                  snapshot.getRef().child(child).child("uDp").setValue(downloadUri.toString());

                                              }
                                          }

                                          @Override
                                          public void onCancelled(@NonNull DatabaseError error) {

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
                }
                else{
                    pd.dismiss();
                    Toast.makeText(ProfileClass.this, "Some error occured", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(ProfileClass.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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

    private void pickFromGallery() {
        Intent galleryIntent =  new Intent(Intent.ACTION_PICK);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,IMAGE_PICK_GALLERY_REQUEST_CODE);
    }
}