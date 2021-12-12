package com.example.ngosolutions.ChatPlatform;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.Volley;
import com.example.ngosolutions.LoginActivity.AdapterUsers;
import com.example.ngosolutions.LoginActivity.MainScreenClass;
import com.example.ngosolutions.LoginActivity.ModalUsers;
import com.example.ngosolutions.LoginActivity.ProfileClass;
import com.example.ngosolutions.R;
import com.example.ngosolutions.adapter.AdapterChat;
import com.example.ngosolutions.models.ModalChat;
import com.example.ngosolutions.notifications.Client;
import com.example.ngosolutions.notifications.Data;
import com.example.ngosolutions.notifications.Sender;
import com.example.ngosolutions.notifications.Token;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.internal.$Gson$Preconditions;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Request;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;

public class ChatActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    ImageView profileTv,blocktv;
    TextView nameTv , userStatusTv;
    EditText messageEt;
    ProgressDialog pd;
    Uri image_uri = null;
    ImageButton sendBtn , attachBtn;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    ValueEventListener seenListener;
    DatabaseReference userRefForSeen;
    List<ModalChat> chatList;
    AdapterChat adapterChat;
    DatabaseReference userDbref;
    String hisuid;
    String myuid ;
    String hisImage;
    boolean isBlocked = false;
    private RequestQueue requestQueue;

    private boolean notify = false;

    private static  final  int CAMERA_REQUEST_CLICK =100;
    private static  final  int STORAGE_REQUEST_CLICK =200;
    private static  final  int IMAGE_PICK_GALLERY_REQUEST_CODE = 300;
    private static  final  int IMAGE_PICK_CAMERA_REQUEST_CODE = 400;
    String[] cameraPermissions ;
    String[] storagePermission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_chat);
        recyclerView = findViewById(R.id.chat_recyclerView);
        profileTv = findViewById(R.id.profileTv);
        nameTv  = findViewById(R.id.nameTv);
        userStatusTv = findViewById(R.id.userStatusTv);
        messageEt = findViewById(R.id.messageEt);
        attachBtn = findViewById(R.id.attachBtn);
        sendBtn = findViewById(R.id.sendBtn);
        firebaseAuth = FirebaseAuth.getInstance();
        blocktv = findViewById(R.id.blockTv);
        pd = new ProgressDialog(this , R.style.MyAlertDialogStyle);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        cameraPermissions = new String[]{Manifest.permission.CAMERA , Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE};
        requestQueue = Volley.newRequestQueue(getApplicationContext());

        //create api service


        Intent intent = getIntent();
        hisuid = intent.getStringExtra("hisuid");

        firebaseDatabase = FirebaseDatabase.getInstance();
        userDbref = firebaseDatabase.getReference("App_Users");

        Query userQuery = userDbref.orderByChild("_uid").equalTo(hisuid);
        userQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds: snapshot.getChildren()){
                    String name = ""+ds.child("_reguser").getValue();
                    hisImage = ""+ds.child("image").getValue();
                    String  typingstatus= ""+ds.child("typingTo").getValue();

                    if(typingstatus.equals(myuid)){
                        userStatusTv.setText("typing...");
                    }
                    else{
                        String onlineStatus  = ""+ds.child("onlineStatus").getValue();
                        if(onlineStatus.equals("online")){
                            userStatusTv.setText(onlineStatus);
                        }
                        else{
                            Calendar calender = Calendar.getInstance(Locale.ENGLISH);
                            calender.setTimeInMillis(Long.parseLong(onlineStatus));
                            String dateTime = DateFormat.format("dd/MM/yyyy hh:mm aa" , calender).toString();
                            userStatusTv.setText("Last seen at :"+dateTime);
                        }
                    }
                    nameTv.setText(name);
//
                    try {
                        Picasso.get().load(hisImage).placeholder(R.drawable.icon_face).into(profileTv);
                    }catch (Exception e){
                        Picasso.get().load(R.drawable.icon_face).into(profileTv);

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notify = true;
                String message = messageEt.getText().toString().trim();
                if(TextUtils.isEmpty(message)){
                    Toast.makeText(ChatActivity.this, "Can not send the empty message ", Toast.LENGTH_SHORT).show();
                }
                else{
                    sendMessage(message);
                }
                messageEt.setText("");
            }
        });

        attachBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImagePickDialog();
            }
        });

        messageEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if(charSequence.toString().trim().length() == 0){

                    }else{
                        checkTypingToStatus(hisuid);
                    }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        blocktv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isBlocked){

                    unBlockuser();
                }
                else {
                    blockUser();
                }
            }
        });
        readMessage();
        checkBlocked();
        seenMessage();

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
                try {
                    sendImageMessage(image_uri);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            if(requestCode == IMAGE_PICK_CAMERA_REQUEST_CODE){
                try {
                    sendImageMessage(image_uri);
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    private void sendMessage(final String message) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        final HashMap<String , Object> hashMap = new HashMap<>();
        hashMap.put("sender" , myuid);
        hashMap.put("receiver",hisuid);
        hashMap.put("message",message);
        hashMap.put("timestamp",timestamp);
        hashMap.put("isSeen",false);
        hashMap.put("type","text");
        databaseReference.child("Chats").push().setValue(hashMap);


        final DatabaseReference database = FirebaseDatabase.getInstance().getReference("App_Users").child(myuid);
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ModalUsers user = snapshot.getValue(ModalUsers.class);
                if(notify){
                    sendNotification(hisuid,user.get_reguser(),message);
                }
                notify = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        final DatabaseReference chatRef1 = FirebaseDatabase.getInstance().getReference("ChatList")
                .child(myuid).child(hisuid);

        chatRef1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()){
                    chatRef1.child("id").setValue(hisuid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        final DatabaseReference chatRef2 = FirebaseDatabase.getInstance().getReference("ChatList")
                .child(hisuid).child(myuid);

        chatRef2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()){
                    chatRef2.child("id").setValue(myuid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void sendImageMessage(Uri image_uri) throws IOException {
            notify = true;
            pd = new ProgressDialog(this , R.style.MyAlertDialogStyle);
            pd.setMessage("Sending Image ...");
            pd.show();

            final String timeStamp = ""+System.currentTimeMillis();
            String fileNameAndPath = "ChatImages/"+"post_"+timeStamp;

            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),image_uri);
            ByteArrayOutputStream boas = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG,100,boas);
            final byte[] data = boas.toByteArray();
                StorageReference ref = FirebaseStorage.getInstance().getReference().child(fileNameAndPath);
                ref.putBytes(data)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                pd.dismiss();
                                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                                while(!uriTask.isSuccessful());
                                String downloaduri = uriTask.getResult().toString();
                                if(uriTask.isSuccessful()){
                                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                                    HashMap<String ,Object> hashMap = new HashMap<>();

                                    hashMap.put("sender",myuid);
                                    hashMap.put("receiver",hisuid);
                                    hashMap.put("message",downloaduri);
                                    hashMap.put("timestamp",timeStamp);
                                    hashMap.put("type","image");
                                    hashMap.put("isSeen",false);

                                    databaseReference.child("Chats").push().setValue(hashMap);

                                    DatabaseReference database = FirebaseDatabase.getInstance().getReference("App_Users").child(myuid);
                                    database.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            ModalUsers modalUsers = snapshot.getValue(ModalUsers.class);
                                            if(notify){
                                                sendNotification(hisuid, modalUsers.get_reguser(),"Sent you a photo..");
                                            }
                                            notify = false;
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                    final DatabaseReference chatRef1 = FirebaseDatabase.getInstance().getReference("ChatList")
                                            .child(myuid).child(hisuid);

                                    chatRef1.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if(!snapshot.exists()){
                                                chatRef1.child("id").setValue(hisuid);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });

                                    final DatabaseReference chatRef2 = FirebaseDatabase.getInstance().getReference("ChatList")
                                            .child(hisuid).child(myuid);

                                    chatRef2.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if(!snapshot.exists()){
                                                chatRef2.child("id").setValue(myuid);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });

                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                    }
                });

    }


    private void checkBlocked() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("App_Users");
        ref.child(firebaseAuth.getUid()).child("BlockUsers").orderByChild("_uid").equalTo(hisuid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds: snapshot.getChildren()){
                            if(ds.exists()){
                                blocktv.setImageResource(R.drawable.icon_block);
                               isBlocked = true;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
    private void unBlockuser() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("App_Users");
        ref.child(firebaseAuth.getUid()).child("BlockUsers").orderByChild("_uid").equalTo(hisuid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot ds: snapshot.getChildren()){
                            if(ds.exists()){
                                ds.getRef().removeValue()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(ChatActivity.this, "unBlocked Successfully...", Toast.LENGTH_SHORT).show();
                                                blocktv.setImageResource(R.drawable.icon_check);

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(ChatActivity.this, "unBlocked Failed..."+e.getMessage(), Toast.LENGTH_SHORT).show();

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
    private void blockUser() {
        HashMap<String ,String> hashMap = new HashMap<>();
        hashMap.put("_uid",hisuid);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("App_Users");
        ref.child(myuid).child("BlockUsers").child(hisuid).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(ChatActivity.this, "Blocked Successfully..", Toast.LENGTH_SHORT).show();
                        blocktv.setImageResource(R.drawable.icon_block);

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ChatActivity.this, "Blocked Failed.."+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void seenMessage() {
        userRefForSeen = FirebaseDatabase.getInstance().getReference("Chats");
        seenListener = userRefForSeen.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren()){
                    ModalChat chat = ds.getValue(ModalChat.class);
                    if(chat.getReceiver().equals(myuid) && chat.getSender().equals(hisuid)){
                        HashMap<String ,Object> hashSeenMap = new HashMap<>();
                        hashSeenMap.put("isSeen",true);
                        ds.getRef().updateChildren(hashSeenMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readMessage() {
        chatList = new ArrayList<>();
        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("Chats");
        dbref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatList.clear();
                for (DataSnapshot ds : snapshot.getChildren()){
                    ModalChat chat = ds.getValue(ModalChat.class);
                    if(chat.getReceiver().equals(myuid) &&
                    chat.getSender().equals(hisuid) || chat.getReceiver().equals(hisuid) &&
                            chat.getSender().equals(myuid)){
                        chatList.add(chat);

                    }
                    adapterChat = new AdapterChat(ChatActivity.this , chatList , hisImage);
                    adapterChat.notifyDataSetChanged();
                    recyclerView.setAdapter(adapterChat);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



    private void sendNotification(final String hisuid, final String reguser, final String message) {
        DatabaseReference allToken = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = allToken.orderByKey().equalTo(hisuid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds: snapshot.getChildren()){
                    Token token = ds.getValue(Token.class);
                    Data data = new Data(myuid,reguser+":"+message,"New Message",hisuid,R.drawable.icon_face);
                    Sender sender = new Sender(data , token.getToken());

                    try{
                        JSONObject senderJsonObj = new JSONObject(new Gson().toJson(sender));
                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("https://fcm.googleapis.com/fcm/send", senderJsonObj,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        //response of the request
                                        Log.d("JSON_RESPONSE","onResponse:"+response.toString());
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d("JSON_RESPONSE","onResponse:"+error.toString());

                            }
                        }){
                            @Override
                            public Map<String, String> getHeaders() throws AuthFailureError{
                                Map<String ,String > headers = new HashMap<>();
                                headers.put("Content-Type","application/json");
                                headers.put("Authorization","key=AAAAp9Pt4vU:APA91bE8ss6ii_I9e7mFnY2SlwHD_2MKGcs073yUuVBPbYvDmddaM_3jVJiJT_HNMsOOnblN6VuohZ1wNHhrn2jYt9YZBmTB6ddb3BRLKa4x_9wZtpK471zfOcXE4B7TLgceyfEF_q8U");

                                return headers;
                            }
                        };
                        requestQueue.add(jsonObjectRequest);

                    }catch (JSONException e){
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkUserStatus(){
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user != null){
            // user is sign in
            myuid = user.getUid();
        }
        else {
            // user not signin in ,  go to main activity
            startActivity(new Intent(ChatActivity.this , MainScreenClass.class));
            finish();
        }
    }

    private void checkOnlineStatus(String status){
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("App_Users").child(myuid);
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("onlineStatus" , status);
        dbRef.updateChildren(hashMap);
    }

    private void checkTypingToStatus(String typing){
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("App_Users").child(myuid);
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("typingTo" , typing);
        dbRef.updateChildren(hashMap);
    }

    @Override
    protected void onStart() {
        checkUserStatus();
        checkOnlineStatus("online");
        super.onStart();
    }
    @Override
    protected void onPause() {
        super.onPause();
        String timestamp = String.valueOf(System.currentTimeMillis());
        checkOnlineStatus(timestamp);
        checkTypingToStatus("noOne");
        userRefForSeen.removeEventListener(seenListener);
    }

    @Override
    protected void onResume() {
        checkOnlineStatus("online");
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}