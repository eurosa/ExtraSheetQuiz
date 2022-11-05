package com.upsun.quizz;

import static com.upsun.quizz.Config.Constants.PRO_REF;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.upsun.quizz.utils.SessionManagment;
import com.upsun.quizz.utils.ToastMsg;

public class ProductDetailActivity extends AppCompatActivity {

    ImageView iv_pro_img,iv_back;
    TextView tv_pro_title,tv_pro_rew,tv_pro_detail;
    DatabaseReference productRef;
    EditText claimerName,claimerAddress,claimerNumber;
    String userName,userNumber,userAddress, status;
    Activity ctx= ProductDetailActivity.this;
    ProgressDialog loadingBar;
    Button btn_claim;
    ToastMsg toastMsg;
    String eTitle;
    String eDetail;
    String eImg;
    int rewardPrice;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_detail_activity);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        initViews();
    }

    private void initViews() {
        toastMsg=new ToastMsg(ctx);
        loadingBar=new ProgressDialog(ctx);
        intent = new Intent(ProductDetailActivity.this,fill_details_activity.class);
        loadingBar.setMessage("Loading...");
        loadingBar.setCanceledOnTouchOutside(false);
        productRef= FirebaseDatabase.getInstance().getReference().child(PRO_REF);
        iv_pro_img=(ImageView)findViewById(R.id.iv_pro_img);
        iv_back=(ImageView)findViewById(R.id.iv_back);
        tv_pro_title=(TextView)findViewById(R.id.tv_pro_title);
        tv_pro_rew=(TextView)findViewById(R.id.tv_pro_rew);
        tv_pro_detail=(TextView)findViewById(R.id.tv_pro_detail);
        btn_claim=(Button)findViewById(R.id.btn_claim);
        claimerName=(EditText) findViewById(R.id.claimerName);
        claimerNumber=(EditText) findViewById(R.id.claimerPhoneNumber);
        claimerAddress=(EditText) findViewById(R.id.claimerAddress);

        ///////////
        String eTitle =getIntent().getStringExtra("tv_pro_title");
        String eDetail=getIntent().getStringExtra("tv_pro_detail");
        String eRew   =getIntent().getStringExtra("tv_pro_rew");
        rewardPrice = Integer.parseInt(eRew);
        userName   =getIntent().getStringExtra("iv_pro_username");
        userNumber   =getIntent().getStringExtra("iv_pro_usernumber");
        userAddress   =getIntent().getStringExtra("iv_pro_useraddress");
        status   =getIntent().getStringExtra("status");

        intent.putExtra("tv_pro_title",getIntent().getStringExtra("tv_pro_title"));
        intent.putExtra("tv_pro_detail",getIntent().getStringExtra("tv_pro_detail"));
        intent.putExtra("tv_pro_rew",getIntent().getStringExtra("tv_pro_rew"));
        intent.putExtra("iv_pro_img",getIntent().getStringExtra("iv_pro_img"));







         eImg=getIntent().getStringExtra("iv_pro_img");
        tv_pro_title.setText(eTitle);
        tv_pro_detail.setText(eDetail);
        tv_pro_rew.setText(eRew);
        if(!eImg.isEmpty()){

            RequestOptions options = new RequestOptions()
                    .centerCrop()
                    .placeholder(R.mipmap.ic_launcher_round)
                    .error(R.mipmap.ic_launcher_round);

            Glide.with(ctx)
                    .load(eImg)

                    .into(iv_pro_img);
        }
        btn_claim.setOnClickListener(new View.OnClickListener(){
            @Override
            //On click function
            public void onClick(View view) {
//                toastMsg.toastIconSuccess("Product Claimed Successfully.");

//                DatabaseReference myRef =  FirebaseDatabase.getInstance().getReference().child("users").child("+91"+new SessionManagment(ctx).getNumber());
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
                                        startActivity(intent);
                                            finish();
                                        }


                                    }

                                }


                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });








            }
        });

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        if (getIntent().getIntExtra("comingFromHistory",0) == 1){

            claimerName.setEnabled(false);
            claimerAddress.setEnabled(false);
            claimerNumber.setEnabled(false);

            claimerName.setText(userName);
            claimerNumber.setText(userNumber);
            claimerAddress.setText(userAddress);

            TextView details = findViewById(R.id.fillDetails);
            TextView claimed = findViewById(R.id.tv_title);
            TextView status1 = findViewById(R.id.productStatus);
            claimed.setText("Claimed Product");
            details.setText("Details");

            details.setVisibility(View.VISIBLE);

            findViewById(R.id.f1).setVisibility(View.VISIBLE);
            findViewById(R.id.f2).setVisibility(View.VISIBLE);
            findViewById(R.id.f3).setVisibility(View.VISIBLE);


            findViewById(R.id.btn_claim).setVisibility(View.GONE);
            findViewById(R.id.statusTitle).setVisibility(View.VISIBLE);
            findViewById(R.id.productStatus).setVisibility(View.VISIBLE);


            status1.setText(status);

            //Toast.makeText(ctx, "called", Toast.LENGTH_SHORT).show();
        }




    }




}
