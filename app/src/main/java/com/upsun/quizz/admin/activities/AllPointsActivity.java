package com.upsun.quizz.admin.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import com.upsun.quizz.Adapter.AdminPointsAdapter;
import com.upsun.quizz.Config.Module;
import com.upsun.quizz.Model.PointsModel;
import com.upsun.quizz.R;
import com.upsun.quizz.networkconnectivity.NetworkConnection;
import com.upsun.quizz.utils.ToastMsg;

import static com.upsun.quizz.Config.Constants.POINT_REF;

public class AllPointsActivity extends AppCompatActivity implements View.OnClickListener {
    ImageView iv_back;
    public static TextView tv_title,no_of_points;
    ProgressDialog loadingBar;
    ToastMsg toastMsg;
    Module module;
    Button btn_add;
    Activity ctx=AllPointsActivity.this;
    DatabaseReference points_ref;
    RecyclerView rv_points;
    RelativeLayout rel_no_items;
    ArrayList<PointsModel> list;
    AdminPointsAdapter adminPointsAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_points);
        initViews();


    }

    @Override
    protected void onResume() {
        super.onResume();
//        getAllPointsData();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(NetworkConnection.connectionChecking(ctx))
        {
            getAllPointsData();
        }
        else
        {
            module.noConnectionActivity();
        }
    }

    private void getAllPointsData() {
        loadingBar.show();
        list.clear();
        points_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                loadingBar.dismiss();
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0)
                {
                    for(DataSnapshot snap_points:dataSnapshot.getChildren())
                    {
                        PointsModel model=snap_points.getValue(PointsModel.class);
                        list.add(model);
                    }

                    rel_no_items.setVisibility(View.GONE);
                    rv_points.setVisibility(View.VISIBLE);
                    no_of_points.setText("Total Points: "+String.valueOf(list.size()));
                    rv_points.setLayoutManager(new LinearLayoutManager(ctx));
                    adminPointsAdapter=new AdminPointsAdapter(ctx,list);
                    rv_points.setAdapter(adminPointsAdapter);
                    adminPointsAdapter.notifyDataSetChanged();

                }
                else
                {
                    rel_no_items.setVisibility(View.VISIBLE);
                    rv_points.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
          loadingBar.dismiss();
     module.showToast(""+databaseError.getMessage().toString());
            }
        });
    }

    private void initViews() {
        iv_back=(ImageView)findViewById(R.id.iv_back);
        tv_title=(TextView)findViewById(R.id.tv_title);
        no_of_points=(TextView)findViewById(R.id.no_of_points);
        loadingBar=new ProgressDialog(ctx);
        loadingBar.setMessage("Loading...");
        loadingBar.setCanceledOnTouchOutside(false);
        toastMsg=new ToastMsg(ctx);
        module=new Module(ctx);
        list=new ArrayList<>();
        points_ref= FirebaseDatabase.getInstance().getReference().child(POINT_REF);
        rv_points=(RecyclerView)findViewById(R.id.rv_points);
        rel_no_items=(RelativeLayout) findViewById(R.id.rel_no_items);
        btn_add=(Button)findViewById(R.id.btn_add);
        btn_add.setOnClickListener(this);
        iv_back.setOnClickListener(this);
        tv_title.setText("Add Points");
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btn_add)
        {
            Intent intent=new Intent(ctx,AddPointsActivity.class);
            intent.putExtra("is_edit","false");
            startActivity(intent);
        }
        else if(v.getId() == R.id.iv_back)
        {
            finish();
        }
    }
}
