package com.upsun.quizz.admin.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import com.upsun.quizz.Config.Module;
import com.upsun.quizz.Model.UserModel;
import com.upsun.quizz.R;
import com.upsun.quizz.networkconnectivity.NetworkConnection;
import com.upsun.quizz.utils.ToastMsg;

import static com.upsun.quizz.Config.Constants.USER_REF;
import static com.upsun.quizz.Config.Constants.WITHDRAW_REF;

public class ApproveRequestActivity extends AppCompatActivity implements View.OnClickListener {
    ImageView iv_back;
    TextView tv_title;
    ProgressDialog loadingBar;
    ToastMsg toastMsg;
    Module module;
    Activity ctx= ApproveRequestActivity.this;
    String user_id="",id,pay_id,request_amount,status,acc_no,bank_name,h_name,type,upi,ifsc,rewards;
    TextView tv_name,tv_mobile,tv_date,tv_time,tv_amt,tv_type,txt_name,tv_status,tv_upi,
            tv_bank_name,tv_acc_no,tv_ifsc,tv_holder;
    LinearLayout lin_upi,lin_bank;
    DatabaseReference userRef,withRef;
    Button btn_approve,btn_decline;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approve_request);
        initViews();
        if(NetworkConnection.connectionChecking(ctx))
        {
            getUserData(user_id);
        }
        else
        {
            module.noConnectionActivity();
        }


    }


    private void initViews() {
        loadingBar=new ProgressDialog(ctx);
        loadingBar.setMessage("Loading...");
        loadingBar.setCanceledOnTouchOutside(false);
        toastMsg=new ToastMsg(ctx);
        module=new Module(ctx);
        tv_title=(TextView)findViewById(R.id.tv_title);
        iv_back=(ImageView)findViewById(R.id.iv_back);
        tv_name=(TextView)findViewById(R.id.tv_name);
        tv_mobile=(TextView)findViewById(R.id.tv_mobile);
        tv_date=(TextView)findViewById(R.id.tv_date);
        tv_time=(TextView)findViewById(R.id.tv_time);
        tv_amt=(TextView)findViewById(R.id.tv_amt);
        tv_type=(TextView)findViewById(R.id.tv_type);
        txt_name=(TextView)findViewById(R.id.txt_name);
        tv_status=(TextView)findViewById(R.id.tv_status);
        tv_upi=(TextView)findViewById(R.id.tv_upi);
        tv_bank_name=(TextView)findViewById(R.id.tv_bank_name);
        tv_acc_no=(TextView)findViewById(R.id.tv_acc_no);
        tv_ifsc=(TextView)findViewById(R.id.tv_ifsc);
        tv_holder=(TextView)findViewById(R.id.tv_holder);
        lin_upi=(LinearLayout) findViewById(R.id.lin_upi);
        lin_bank=(LinearLayout) findViewById(R.id.lin_bank);
        btn_approve=(Button) findViewById(R.id.btn_approve);
        btn_decline=(Button) findViewById(R.id.btn_decline);
        userRef= FirebaseDatabase.getInstance().getReference().child(USER_REF);
        withRef= FirebaseDatabase.getInstance().getReference().child(WITHDRAW_REF);
        user_id=getIntent().getStringExtra("user_id");
        id=getIntent().getStringExtra("id");
        pay_id=getIntent().getStringExtra("pay_id");
        request_amount=getIntent().getStringExtra("request_amount");
        status=getIntent().getStringExtra("status");
        acc_no=getIntent().getStringExtra("acc_no");
        bank_name=getIntent().getStringExtra("bank_name");
        h_name=getIntent().getStringExtra("h_name");
        type=getIntent().getStringExtra("type");
        upi=getIntent().getStringExtra("upi");
        ifsc=getIntent().getStringExtra("ifsc");
        btn_approve.setOnClickListener(this);
        btn_decline.setOnClickListener(this);
        iv_back.setOnClickListener(this);
        tv_title.setText("Approve/Cancel Request");
       if(status.equalsIgnoreCase("pending"))
       {
           tv_status.setTextColor(getResources().getColor(R.color.rec_dahlia));

       }
       else if(status.equalsIgnoreCase("approved"))
       {
           tv_status.setTextColor(getResources().getColor(R.color.green_500));
           btn_decline.setVisibility(View.GONE);
           btn_approve.setVisibility(View.GONE);
       }
       else
       {
           tv_status.setTextColor(getResources().getColor(R.color.red_600));
           btn_decline.setVisibility(View.GONE);
           btn_approve.setVisibility(View.GONE);
       }
    }

    @Override
    public void onClick(View v) {
        if(v.getId()== R.id.iv_back)
        {
            finish();
        }
        else if(v.getId() == R.id.btn_approve)
        {
            updateWithdrawRequest(user_id,id,"approved",rewards,request_amount);
        }
        else  if(v.getId() == R.id.btn_decline)
        {
            updateWithdrawRequest(user_id,id,"rejected",rewards,request_amount);
        }
    }

    private void updateWithdrawRequest(final String user_id, String id, final String approve, final String rew, final String req_amt) {
        loadingBar.show();
        HashMap<String,Object> params = new HashMap<>(  );
        params.put("status",approve);
        withRef.child(id).updateChildren(params).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                loadingBar.dismiss();
                if(task.isSuccessful())
                {
                    if(approve.equalsIgnoreCase("approved"))
                    {
                        toastMsg.toastIconSuccess("Request Approved");
                        tv_status.setText("APPROVED");
                        tv_status.setTextColor(getResources().getColor(R.color.green_500));
                        btn_decline.setVisibility(View.GONE);
                        btn_approve.setVisibility(View.GONE);
                    }
                    else
                    {
                        String updatedRewards=String.valueOf(Integer.parseInt(rew)+Integer.parseInt(req_amt));
//                        toastMsg.toastIconSuccess(""+updatedRewards+" - "+rew+" - "+req_amt);
                        module.updateDbRewards(user_id,updatedRewards);
                        tv_status.setText("REJECTED");
                        tv_status.setTextColor(getResources().getColor(R.color.red_600));
                        toastMsg.toastIconSuccess("Request Rejected");
                        btn_decline.setVisibility(View.GONE);
                        btn_approve.setVisibility(View.GONE);
                    }
                }
                else
                {
                    module.showToast(""+task.getException().getMessage().toString());
                }

            }
        });

    }

    private void getUserData(String user_id) {
     loadingBar.show();
     userRef.child(user_id).addListenerForSingleValueEvent(new ValueEventListener() {
         @Override
         public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
             loadingBar.dismiss();
             if(dataSnapshot.getChildrenCount()>0 && dataSnapshot.exists())
             {
                 UserModel model=dataSnapshot.getValue(UserModel.class);
                 tv_name.setText(model.getName());
                 rewards=model.getRewards();
                 tv_mobile.setText(model.getId());
                 tv_date.setText(module.getDateFromID(id,"wr"));
                 tv_time.setText(module.getTimeFromID(id,"wr"));
                 tv_status.setText(status.toUpperCase());
                 tv_amt.setText(getResources().getString(R.string.rupee)+request_amount);
                 if(type.equalsIgnoreCase("upi"))
                 {
                     lin_upi.setVisibility(View.VISIBLE);
                     tv_type.setText("UPI");
                     tv_upi.setText(upi);
                 }
                 else
                 {
                     lin_bank.setVisibility(View.VISIBLE);
                     tv_type.setText("Bank Account");
                     tv_bank_name.setText(bank_name);
                     tv_acc_no.setText(acc_no);
                     tv_ifsc.setText(ifsc);
                     tv_holder.setText(h_name);
                 }
             }

         }

         @Override
         public void onCancelled(@NonNull DatabaseError databaseError) {
   loadingBar.dismiss();
   module.showToast(""+databaseError.getMessage().toString());
         }
     });
    }

}
