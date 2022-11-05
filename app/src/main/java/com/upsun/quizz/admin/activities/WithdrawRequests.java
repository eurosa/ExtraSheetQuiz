package com.upsun.quizz.admin.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.upsun.quizz.Adapter.WithdrwRequestAdapter;
import com.upsun.quizz.Config.Module;
import com.upsun.quizz.Model.UserModel;
import com.upsun.quizz.Model.WithdrawModel;
import com.upsun.quizz.R;
import com.upsun.quizz.networkconnectivity.NetworkConnection;
import com.upsun.quizz.utils.ToastMsg;

import java.util.ArrayList;

public class WithdrawRequests extends AppCompatActivity implements View.OnClickListener {
    ImageView iv_back ;
    RecyclerView rv_withdraw ;
    TabLayout tablayout ;
    TabItem tab_p ,tab_a ,tab_all ;
    String type = "pending";
    ProgressDialog loadingBar;
    ToastMsg toastMsg ;
    Module module;
    RelativeLayout rel_no_item;
    ArrayList<WithdrawModel>all_list;
    ArrayList<WithdrawModel>pending_list;
    ArrayList<WithdrawModel>approved_list;
    ArrayList<UserModel>user_list;
    WithdrwRequestAdapter withdrwRequestAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw_requests);
        iv_back = findViewById(R.id.iv_back);
        rv_withdraw = findViewById(R.id.rv_withdrw);
        tablayout = findViewById(R.id.tablayout);
        tab_a = findViewById(R.id.aproved);
        tab_p = findViewById(R.id.pending);
        module=new Module(this);
        tab_all = findViewById(R.id.all);
        rel_no_item = findViewById(R.id.rel_no_items);

//        tab_a.setOnClickListener(this);
//        tab_p.setOnClickListener(this);
//        tab_all.setOnClickListener(this);
        iv_back.setOnClickListener(this);
        toastMsg = new ToastMsg(this);
        loadingBar=new ProgressDialog(WithdrawRequests.this);
        loadingBar.setMessage("Loading...");
        loadingBar.setCanceledOnTouchOutside(false);
        all_list = new ArrayList<>();
        pending_list= new ArrayList<>();
        approved_list = new ArrayList<>();
        user_list = new ArrayList<>();

        if(NetworkConnection.connectionChecking(this))
        {
            getWithdrawRequests(type);
        }
        else
        {
            module.noConnectionActivity();
        }

        tablayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                if( tab.getPosition()==0)
                {
                    type = "pending";
                    getWithdrawRequests(type);


                }
               else if( tab.getPosition()==1)
                {
                    type = "approved";
                    getWithdrawRequests(type);
                }
                if( tab.getPosition()==2)
                {
                    type = "all";
                    getWithdrawRequests(type);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id== R.id.iv_back)
        {
            finish();
        }

    }

       public void getWithdrawRequests(final String type)
        {
            all_list.clear();
            pending_list.clear();
            approved_list.clear();
            loadingBar.show();
            DatabaseReference dref = FirebaseDatabase.getInstance().getReference();
            dref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    loadingBar.dismiss();
                    for (DataSnapshot data: dataSnapshot.child("withdraw_request").getChildren())
                    {

                        WithdrawModel model = data.getValue(WithdrawModel.class);
                        all_list.add(model);
                       switch (model.getStatus())
                       {
                           case "pending": pending_list.add(model);
                           break;
                           case "approved" : approved_list.add(model);
                           break;
                           default: all_list.add(model);
                           break;
                       }


                    }
                    for (DataSnapshot data: dataSnapshot.child("users").getChildren())
                    {
                        UserModel model = data.getValue(UserModel.class);
                        user_list.add(model);

                    }

                    switch (type)
                    {
                        case "pending": withdrwRequestAdapter = new WithdrwRequestAdapter(pending_list, WithdrawRequests.this,user_list);
                            break;
                        case "approved" : withdrwRequestAdapter = new WithdrwRequestAdapter(approved_list, WithdrawRequests.this,user_list);
                            break ;
                        case "all": withdrwRequestAdapter = new WithdrwRequestAdapter(all_list, WithdrawRequests.this,user_list);
                            break;

                    }

                  if (pending_list.size()==0 || all_list.size()==0 || all_list.size()==0)
                  {
                      rel_no_item.setVisibility(View.VISIBLE);
                      rv_withdraw.setVisibility(View.GONE);
                  }
                  else {
                      rel_no_item.setVisibility(View.GONE);
                      rv_withdraw.setVisibility(View.VISIBLE);
                      rv_withdraw.setLayoutManager(new LinearLayoutManager(WithdrawRequests.this, RecyclerView.VERTICAL, false));
                      rv_withdraw.setAdapter(withdrwRequestAdapter);
                  }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    toastMsg.toastIconError(databaseError.getMessage());
                }
            });
        }

}
