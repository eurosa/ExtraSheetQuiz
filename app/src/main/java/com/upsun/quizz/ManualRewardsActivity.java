package com.upsun.quizz;

import static com.upsun.quizz.Config.Constants.QUIZ_REF;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
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
import com.upsun.quizz.Adapter.AdminManualRewardsAdapter;
import com.upsun.quizz.Config.Module;
import com.upsun.quizz.Model.QuizModel;
import com.upsun.quizz.networkconnectivity.NetworkConnection;
import com.upsun.quizz.utils.ToastMsg;

import java.util.ArrayList;
import java.util.Collections;

public class ManualRewardsActivity extends AppCompatActivity implements View.OnClickListener {
    ImageView iv_back;
    TextView tv_title;
    ToastMsg toastMsg;
    ProgressDialog loadingBar;
    RelativeLayout rel_no_items;
    Module module;
    Activity ctx=ManualRewardsActivity.this;
    DatabaseReference quizRef;
    AdminManualRewardsAdapter adapter;
    ArrayList<QuizModel> list;
    RecyclerView rv_quiz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_rewards);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        initViews();
        if(NetworkConnection.connectionChecking(ctx))
        {
            getAllQuiz();
        }
        else
        {
            module.noConnectionActivity();
        }
    }

    private void getAllQuiz() {
        loadingBar.show();
        list.clear();
        quizRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                loadingBar.dismiss();
                 if(dataSnapshot.exists())
                {

                    for(DataSnapshot snap:dataSnapshot.getChildren())
                    {
                        Log.d("TAG", "onDataChange: "+snap.getValue());
                        QuizModel model=snap.getValue(QuizModel.class);
                         list.add(model);
                    }
                    for(int i=0; i<list.size();i++)
                    {
                        int days= module.getDateDiff(list.get(i).getQuiz_date().toString());
                        list.get(i).setDays(String.valueOf(days));
                    }
                    Collections.sort(list,QuizModel.camp_quiz);

                    if(list.size()>0)
                    {
                        rv_quiz.setLayoutManager(new LinearLayoutManager(ctx));
                        adapter=new AdminManualRewardsAdapter(ctx,list);
                        rv_quiz.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    }
                    else
                    {
                        rv_quiz.setVisibility(View.GONE);
                        rel_no_items.setVisibility(View.VISIBLE);
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

    private void initViews() {
        iv_back=(ImageView)findViewById(R.id.iv_back);
        tv_title=(TextView) findViewById(R.id.tv_title);
        rel_no_items=(RelativeLayout) findViewById(R.id.rel_no_items);
        module=new Module(ctx);
        toastMsg=new ToastMsg(ctx);
        loadingBar=new ProgressDialog(ctx);
        loadingBar.setMessage("Loading...");
        loadingBar.setCanceledOnTouchOutside(false);
        tv_title.setText("Send Rewards");
        list=new ArrayList<>();
        rv_quiz=findViewById(R.id.rv_quiz);
        quizRef= FirebaseDatabase.getInstance().getReference().child(QUIZ_REF);
        iv_back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId()== R.id.iv_back)
        {
            finish();
        }

    }
}
