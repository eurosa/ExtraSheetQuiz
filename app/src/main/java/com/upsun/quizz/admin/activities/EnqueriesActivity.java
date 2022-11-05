package com.upsun.quizz.admin.activities;

import static com.upsun.quizz.Config.Constants.CONTACT_REF;

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
import com.google.firebase.database.ValueEventListener;
import com.upsun.quizz.Adapter.EnquiryAdapter;
import com.upsun.quizz.Config.Module;
import com.upsun.quizz.Model.ContactUsModel;
import com.upsun.quizz.R;
import com.upsun.quizz.networkconnectivity.NetworkConnection;
import com.upsun.quizz.utils.ToastMsg;

import java.util.ArrayList;
import java.util.Collections;

public class EnqueriesActivity extends AppCompatActivity {
    RelativeLayout rel_no_items;
    ImageView iv_back;
    TextView tv_title;
    ProgressDialog loadingBar;
    ToastMsg toastMsg;
    Module module;
    RecyclerView rv_contact;
    ArrayList<ContactUsModel> list;
    Activity ctx=EnqueriesActivity.this;
    DatabaseReference enq_ref;
    EnquiryAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enquries);
        initViews();

        if(NetworkConnection.connectionChecking(ctx))
        {
            getAllEnquries();
        }
        else
        {
            module.noConnectionActivity();
        }
    }

    private void getAllEnquries() {
        loadingBar.show();
        list.clear();
        enq_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                loadingBar.dismiss();
           if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0)
           {
               for(DataSnapshot snap:dataSnapshot.getChildren())
               {
                   ContactUsModel usModel=snap.getValue(ContactUsModel.class);
                   list.add(usModel);
               }
               if(list.size()>0)
               {
                   rel_no_items.setVisibility(View.GONE);
                   rv_contact.setVisibility(View.VISIBLE);
                   Collections.sort(list,ContactUsModel.max);
                   rv_contact.setLayoutManager(new LinearLayoutManager(ctx));
                   adapter=new EnquiryAdapter(ctx,list);
                   rv_contact.setAdapter(adapter);
                   adapter.notifyDataSetChanged();
               }
               else {
                   rel_no_items.setVisibility(View.VISIBLE);
                   rv_contact.setVisibility(View.GONE);
               }

           }
           else {
               rel_no_items.setVisibility(View.VISIBLE);
               rv_contact.setVisibility(View.GONE);
           }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            loadingBar.dismiss();
            module.showToast(""+databaseError.getMessage().toString());
            }
        });

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            finish();
            }
        });
    }

    private void initViews() {
        iv_back=(ImageView)findViewById(R.id.iv_back);
        tv_title=(TextView)findViewById(R.id.tv_title);
        loadingBar=new ProgressDialog(ctx);
        loadingBar.setMessage("Loading...");
        loadingBar.setCanceledOnTouchOutside(false);
        toastMsg=new ToastMsg(ctx);
        module=new Module(ctx);
        list=new ArrayList<>();
        rel_no_items=(RelativeLayout)findViewById(R.id.rel_no_items);
        rv_contact=(RecyclerView)findViewById(R.id.rv_contact);
        enq_ref= FirebaseDatabase.getInstance().getReference().child(CONTACT_REF);
        tv_title.setText("All Enquiries");
    }
}
