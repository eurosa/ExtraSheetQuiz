package com.upsun.quizz;

import static com.upsun.quizz.Config.Constants.KEY_ID;
import static com.upsun.quizz.Config.Constants.WITHDRAW_REF;
import static com.upsun.quizz.Model.WithdrawModel.camp_withd;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.upsun.quizz.Adapter.WithdrawUserHistoryAdapter;
import com.upsun.quizz.Config.Module;
import com.upsun.quizz.Model.WithdrawModel;
import com.upsun.quizz.networkconnectivity.NetworkConnection;
import com.upsun.quizz.utils.SessionManagment;
import com.upsun.quizz.utils.ToastMsg;

import java.util.ArrayList;
import java.util.Collections;

public class WithdrwHistry extends AppCompatActivity {
  ImageView iv_back;
    RelativeLayout rel_no_items;
    TextView tv_title;
    ProgressDialog loadingBar;
    ToastMsg toastMsg;
    Module module;
    Activity ctx= WithdrwHistry.this;
    SessionManagment sessionManagment;
    RecyclerView rv_withdrw;
   DatabaseReference withdrawRef;
   String user_id="";
   WithdrawUserHistoryAdapter adapter;
   ArrayList<WithdrawModel> wList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdrw_histry);
        initViews();
        if(NetworkConnection.connectionChecking(ctx))
        {
            getAllWithdrawRequest(user_id);
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
        sessionManagment=new SessionManagment(ctx);
        user_id=sessionManagment.getUserDetails().get(KEY_ID);
        iv_back=(ImageView)findViewById(R.id.iv_back);
        tv_title=(TextView)findViewById(R.id.tv_title);
        rel_no_items=(RelativeLayout)findViewById(R.id.rel_no_items);
        rv_withdrw=(RecyclerView)findViewById(R.id.rv_withdrw);
        wList=new ArrayList<>();
        withdrawRef= FirebaseDatabase.getInstance().getReference().child(WITHDRAW_REF);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void getAllWithdrawRequest(final String user_id) {
     wList.clear();
     loadingBar.show();
        Query query=withdrawRef.orderByChild("user_id").equalTo(user_id);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
         @Override
         public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
             loadingBar.dismiss();
            if(dataSnapshot.getChildrenCount()>0 && dataSnapshot.exists())
            {
                for(DataSnapshot snapshot:dataSnapshot.getChildren())
                {
                    WithdrawModel model=snapshot.getValue(WithdrawModel.class);
                        wList.add(model);
                }
                for(int i=0; i<wList.size();i++)
                {
                    String q_id = wList.get(i).getId();
                    String j_d = q_id.substring(4,12);
                    String dtstr=j_d.substring(0,2)+"-"+j_d.subSequence(2,4)+"-"+j_d.subSequence(4,j_d.length());
                    int days= module.getDateDiff(dtstr.toString());
                    wList.get(i).setDays(String.valueOf(days));
                }
                Collections.sort(wList,camp_withd);
                if(wList.size()>0)
                {
                    rel_no_items.setVisibility(View.GONE);
                    rv_withdrw.setVisibility(View.VISIBLE);
                    rv_withdrw.setLayoutManager(new LinearLayoutManager(ctx));
                    adapter=new WithdrawUserHistoryAdapter(ctx,wList);
                    rv_withdrw.setAdapter(adapter);
                    adapter.notifyDataSetChanged();


                }
                else {
                    rel_no_items.setVisibility(View.VISIBLE);
                    rv_withdrw.setVisibility(View.GONE);
                }

            }
            else
            {
                rel_no_items.setVisibility(View.VISIBLE);
                rv_withdrw.setVisibility(View.GONE);
            }
         }

         @Override
         public void onCancelled(@NonNull DatabaseError databaseError) {
         loadingBar.dismiss();
         module.showToast(databaseError.getMessage().toString());
         }
     });
    }
}
