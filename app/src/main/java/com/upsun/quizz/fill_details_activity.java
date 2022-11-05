package com.upsun.quizz;

import static com.upsun.quizz.Config.Constants.BROAD_REWARDS;
import static com.upsun.quizz.Config.Constants.BROAD_WALLET;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hbb20.CountryCodePicker;
import com.upsun.quizz.utils.SessionManagment;
import com.upsun.quizz.utils.ToastMsg;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class fill_details_activity extends AppCompatActivity {
    int rewardPrice;
    EditText claimerName,claimerAddress,claimerNumber,claimerCity,claimerCode;
    ToastMsg toastMsg;
    Activity ctx= fill_details_activity.this;
    String eTitle, eImg, eDetail;
   SessionManagment sessionManagment;
   CountryCodePicker countryCode_picker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fill_details);

        countryCode_picker=(CountryCodePicker) findViewById(R.id.countryCode_picker);
        claimerName=(EditText) findViewById(R.id.claimerName);
        claimerNumber=(EditText) findViewById(R.id.claimerPhoneNumber);
        claimerAddress=(EditText) findViewById(R.id.claimerAddress);
        claimerCity=(EditText) findViewById(R.id.claimerCity);
        claimerCode=(EditText) findViewById(R.id.claimerCode);
        toastMsg=new ToastMsg(ctx);
        sessionManagment = new SessionManagment(this);

        String eRew   =getIntent().getStringExtra("tv_pro_rew");
        rewardPrice = Integer.parseInt(eRew);

        eImg=getIntent().getStringExtra("iv_pro_img");
        eTitle =getIntent().getStringExtra("tv_pro_title");
        eDetail=getIntent().getStringExtra("tv_pro_detail");




        findViewById(R.id.orderSubmit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder=new AlertDialog.Builder(fill_details_activity.this);
                builder.setTitle("Confirmation");
                builder.setMessage("Are you sure want to submit?");
                builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

//                        DatabaseReference myRef =  FirebaseDatabase.getInstance().getReference().child("users").child("+91"+new SessionManagment(ctx).getNumber());
                        DatabaseReference myRef =  FirebaseDatabase.getInstance().getReference().child("users").child(new SessionManagment(ctx).getId());
                        DatabaseReference adminRef = FirebaseDatabase.getInstance().getReference().child("product_history");




                        myRef.child("rewards")
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()){
                                            int wallet = Integer.parseInt(snapshot.getValue(String.class));

                                            if (wallet < rewardPrice){
                                                Toast.makeText(ctx, "You don't have enough coins in your rewards", Toast.LENGTH_SHORT).show();
                                            }
                                            else{

                                                if (claimerName.getText().toString().equals("")){
                                                    toastMsg.toastIconError("Fill your Name");
                                                }
                                                else if(claimerNumber.getText().toString().equals("")){
                                                    toastMsg.toastIconError("Fill your Phone Number");
                                                }
                                                else if (claimerAddress.getText().toString().equals("")){
                                                    toastMsg.toastIconError("Fill your address");
                                                }
                                                else if (claimerCity.getText().toString().equals("")){
                                                    toastMsg.toastIconError("Fill City");
                                                }
                                                else if (claimerCode.getText().toString().equals("")){
                                                    toastMsg.toastIconError("Fill Pin code");
                                                }
                                                else{

                                                    HashMap<String,Object> map = new HashMap<>();
                                                    map.put("itemName",eTitle);
                                                    map.put("itemValue",String.valueOf(rewardPrice));
                                                    map.put("itemDesc",eDetail);
                                                    map.put("itemImageUrl",eImg);
                                                    map.put("claimerName",claimerName.getText().toString());
                                                    map.put("claimerNumber","+"+countryCode_picker.getSelectedCountryCode()+claimerNumber.getText().toString());
                                                    map.put("claimerAddress",claimerAddress.getText().toString() +", "+ claimerCity.getText().toString() +", "+ claimerCode.getText().toString());
                                                    map.put("claimDate", new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
//                                                    map.put("userId","+91"+new SessionManagment(ctx).getNumber());
                                                    map.put("userId",new SessionManagment(ctx).getId());
                                                    map.put("status","Your request is being processed");


                                                    adminRef.push().setValue(map);

                                                    myRef.child("rewards").setValue(String.valueOf(wallet - rewardPrice));

                                                    toastMsg.toastIconSuccess("Product Claimed Successfully.");
                                                    finish();
                                                }


                                            }

                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });


                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog=builder.create();
                dialog.show();
            }
        });

    }
    @Override
    public void onPause() {
        super.onPause();
        this.unregisterReceiver(bWallet);
        this.unregisterReceiver(bRewards);

    }


    @Override
    public void onResume() {
        super.onResume();
        this.registerReceiver(bWallet,new IntentFilter(BROAD_WALLET));
        this.registerReceiver(bRewards,new IntentFilter(BROAD_REWARDS));
    }


    public BroadcastReceiver bWallet=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String type=intent.getStringExtra("type");
            String amt=intent.getStringExtra("amount");
            if(type.contentEquals("update"))
            {
                updateWallet(amt);
            }

        }
    };


    public BroadcastReceiver bRewards=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String type=intent.getStringExtra("type");
            String amt=intent.getStringExtra("amount");
            if(type.contentEquals("update"))
            {
                updateReward(amt);
            }
        }
    };

    private void updateWallet(String amt) {
        sessionManagment.updateWallet(amt);
        new MainActivity().setRewardsCounter(amt);
    }
    private void updateReward(String amt) {
        sessionManagment.updateRewards(amt);
        new MainActivity().setRewardsCounter(amt);
    }
}