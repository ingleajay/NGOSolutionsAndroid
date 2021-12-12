package com.example.ngosolutions.GooglePay;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ngosolutions.HomesScreen;
import com.example.ngosolutions.MainActivity;
import com.example.ngosolutions.R;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class GooglePay_ngo extends AppCompatActivity {
    public static final String GPAY_PACKAGE_NAME = "com.google.android.apps.nbu.paisa.user";
    TextInputLayout name , upi_id , amount , note;
    TextView msg;
    ImageView passback;
    Button pay;
    Uri uri;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    DatabaseReference donationRef;
    FirebaseAuth firebaseAuth;
    public static String payerName, UpiId, msgNote, sendAmount, status;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_google_pay_ngo);
        passback= findViewById(R.id.pass_back);
        name = findViewById(R.id.Gpay_user);
        upi_id = findViewById(R.id.Gpay_upi_id);
        amount = findViewById(R.id.Gpay_amount);
        note = findViewById(R.id.Gpay_transaction_note);
        msg =  findViewById(R.id.status);
        pay  = findViewById(R.id.pay);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        donationRef = firebaseDatabase.getReference().child("Donation");
        passback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), HomesScreen.class);
                startActivity(intent);

            }
        });
        databaseReference = firebaseDatabase.getReference("Web_admin");
        Intent intent = getIntent();
        String uid  =intent.getStringExtra("userId");
//        Toast.makeText(this, ""+uid, Toast.LENGTH_SHORT).show();
        Query query = databaseReference.orderByChild("userId").equalTo(uid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                for (DataSnapshot ds : datasnapshot.getChildren()) {
                    String orgname = "" + ds.child("orgname").getValue();
                    String upi = "" + ds.child("upi_id").getValue();
                    upi_id.getEditText().setText(upi);
                    name.getEditText().setText(orgname);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                payerName= name.getEditText().getText().toString().trim();
                UpiId = upi_id.getEditText().getText().toString().trim();
                msgNote= note.getEditText().getText().toString().trim();
                sendAmount = amount.getEditText().getText().toString().trim();
                if (!validatepayerName() | !validateupiId() | !validatemsgNote() | !validatesendAmount()) {
                    return;
                }
                else {
                    uri = getUpiPaymentUri(payerName, UpiId, msgNote, sendAmount);
                    payWithGpay(GPAY_PACKAGE_NAME);
                }
            }
        });
    }

    private void payus() {
        databaseReference = firebaseDatabase.getReference("Web_admin");
        Intent intent = getIntent();
        String ngo_adminid =intent.getStringExtra("userId");
        final String app_userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        SimpleDateFormat s = new SimpleDateFormat("dd/MM/YYYY hh:mm:ss",Locale.getDefault());
        String Date_Time=s.format(new Date());
//        String Date_Time= ""+System.currentTimeMillis();
        payerName= name.getEditText().getText().toString().trim();
        UpiId = upi_id.getEditText().getText().toString().trim();
        msgNote= note.getEditText().getText().toString().trim();
        sendAmount = amount.getEditText().getText().toString().trim();
        Donation donation = new Donation(payerName,UpiId,msgNote,sendAmount,Date_Time,ngo_adminid,app_userid);
        donationRef.push().setValue(donation);
        Toast.makeText(GooglePay_ngo.this , "Data inserted",Toast.LENGTH_SHORT).show();

    }

    private static Uri getUpiPaymentUri(String name, String upiId, String note, String amount){
        return  new Uri.Builder()
                .scheme("upi")
                .authority("pay")
                .appendQueryParameter("pa",upiId)
                .appendQueryParameter("pn",name)
                .appendQueryParameter("mc", "")
                .appendQueryParameter("tn",note)
                .appendQueryParameter("am",amount)
                .appendQueryParameter("tr", "25584584")
                .appendQueryParameter("cu","INR")
                .build();
    }

    private void payWithGpay(String packageName){

        if(isAppInstalled(this,packageName)){

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(uri);
            intent.setPackage(packageName);
            startActivityForResult(intent,0);

        }
        else{
            Toast.makeText(GooglePay_ngo.this,"Google pay is not installed. Please istall and try again.", Toast.LENGTH_SHORT).show();
        }

    }
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null) {
            status = data.getStringExtra("Status").toLowerCase();
        }
        if ((RESULT_OK == resultCode) && status.equals("success")) {
            payus();
            Toast.makeText(GooglePay_ngo.this, "Transaction successful. ", Toast.LENGTH_SHORT).show();
            msg.setText("Transaction successful of ₹" + sendAmount);
            msg.setTextColor(Color.GREEN);

        }

        else{
            Toast.makeText(GooglePay_ngo.this, "Transaction cancelled or failed please try again.", Toast.LENGTH_SHORT).show();
            msg.setText("Transaction Failed of ₹" + sendAmount);
            msg.setTextColor(Color.RED);
        }

    }

    public static boolean isAppInstalled(Context context, String packageName){
        try{
            context.getPackageManager().getApplicationInfo(packageName,0);
            return true;
        }catch (PackageManager.NameNotFoundException e){
            return false;
        }
    }

    private boolean validatesendAmount() {
        String val = amount.getEditText().getText().toString().trim();
        if (val.isEmpty()) {
            amount.setError("Field can not be empty");
            return false;
        } else {
            amount.setError(null);
            amount.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validatemsgNote() {
        String val = note.getEditText().getText().toString().trim();
        String checkspaces = "Aw{1,20}z";

        if (val.isEmpty()) {
            note.setError("Field can not be empty");
            return false;
        }
        else {
            note.setError(null);
            note.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validateupiId() {
        String val = upi_id.getEditText().getText().toString().trim();

        if (val.isEmpty()) {
            upi_id.setError("Field can not be empty");
            return false;
        }  else {
            upi_id.setError(null);
            upi_id.setErrorEnabled(false);
            return true;
        }
    }

    private boolean validatepayerName() {
        String val = name.getEditText().getText().toString().trim();
        String checkspaces = "Aw{1,20}z";

        if (val.isEmpty()) {
            name.setError("Field can not be empty");
            return false;
        } else if (val.length() > 20) {
            name.setError("Payesr name  is too large!");
            return false;
        }  else {
            name.setError(null);
            name.setErrorEnabled(false);
            return true;
        }
    }



}