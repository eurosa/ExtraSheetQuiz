package com.upsun.quizz.admin.activities;

import static com.upsun.quizz.Config.Constants.INSTRUCTION_REF;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.upsun.quizz.Adapter.InstructionAdapter;
import com.upsun.quizz.Config.Module;
import com.upsun.quizz.Model.InstructionModel;
import com.upsun.quizz.R;
import com.upsun.quizz.networkconnectivity.NetworkConnection;
import com.upsun.quizz.utils.ToastMsg;

import java.util.ArrayList;

public class AllInsructionActivity extends AppCompatActivity {
    ImageView iv_back;
    TextView tv_title,no_of_ins;
    RecyclerView rv_ins;
    Button btn_add;
    RelativeLayout rel_no_items;
    ToastMsg toastMsg;
    ProgressDialog loadingBar;
    Activity ctx=AllInsructionActivity.this;
    DatabaseReference ins_ref;
    Module module;
    ArrayList<InstructionModel> list;
    InstructionAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_insruction);
        initViews();
        if(NetworkConnection.connectionChecking(ctx))
        {
            getAllInstructions();
        }
        else
        {
            module.noConnectionActivity();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    private void initViews() {
        ins_ref= FirebaseDatabase.getInstance().getReference().child(INSTRUCTION_REF);
        iv_back=(ImageView)findViewById(R.id.iv_back);
        tv_title=(TextView)findViewById(R.id.tv_title);
        rv_ins=(RecyclerView)findViewById(R.id.rv_ins);
        btn_add=(Button)findViewById(R.id.btn_add);
        rel_no_items=(RelativeLayout)findViewById(R.id.rel_no_items);
        no_of_ins=(TextView)findViewById(R.id.no_of_ins);
        toastMsg=new ToastMsg(ctx);
        loadingBar=new ProgressDialog(ctx);
        loadingBar.setMessage("Loading...");
        loadingBar.setCanceledOnTouchOutside(false);
        module=new Module(ctx);
        list=new ArrayList<>();
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(ctx,AddInstuctionActivity.class);
                intent.putExtra("is_edit","false");
                startActivity(intent);

            }
        });
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    private void getAllInstructions() {
        loadingBar.show();
        list.clear();
        ins_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                loadingBar.dismiss();
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0)
                {

                    for(DataSnapshot snapshot:dataSnapshot.getChildren())
                    {
                        InstructionModel model=snapshot.getValue(InstructionModel.class);
                        list.add(model);
                    }
                    rel_no_items.setVisibility(View.GONE);
                    rv_ins.setVisibility(View.VISIBLE);
                    no_of_ins.setText("No. Of Instructions : "+list.size());
                    rv_ins.setLayoutManager(new LinearLayoutManager(ctx));
                    adapter=new InstructionAdapter(ctx,list);
                    rv_ins.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
                else
                {
                    rel_no_items.setVisibility(View.VISIBLE);
                    rv_ins.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
              loadingBar.dismiss();
                Toast.makeText(ctx, ""+databaseError.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
