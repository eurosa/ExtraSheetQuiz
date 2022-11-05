package com.upsun.quizz;

import static com.upsun.quizz.Config.Constants.POINT_REF;

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
import com.upsun.quizz.Adapter.PointsAdapter;
import com.upsun.quizz.Config.Module;
import com.upsun.quizz.Model.PointsModel;
import com.upsun.quizz.networkconnectivity.NetworkConnection;
import com.upsun.quizz.utils.ToastMsg;

import java.util.ArrayList;

public class PointsActivity extends AppCompatActivity {
    ImageView iv_back;
    TextView tv_title;
    ProgressDialog loadingBar;
    ToastMsg toastMsg;
    Module module;
    Activity ctx= PointsActivity.this;
    DatabaseReference points_ref;
    RecyclerView rv_points;
    RelativeLayout rel_no_items;
    ArrayList<PointsModel> list;
    PointsAdapter pointsAdapter ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_points);
        initViews();
        if(NetworkConnection.connectionChecking(this))
        {
            getAllPointsData();
        }
        else {
            module.noConnectionActivity();
        }

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
        points_ref= FirebaseDatabase.getInstance().getReference().child(POINT_REF);
        rv_points=(RecyclerView)findViewById(R.id.rv_points);
        rel_no_items=(RelativeLayout) findViewById(R.id.rel_no_items);
        tv_title.setText("Add Points");
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

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

                    rv_points.setLayoutManager(new LinearLayoutManager(ctx));
                pointsAdapter = new PointsAdapter(ctx,list);
                    rv_points.setAdapter(pointsAdapter);
                   pointsAdapter.notifyDataSetChanged();

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
}
