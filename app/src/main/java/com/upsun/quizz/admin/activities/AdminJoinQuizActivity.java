package com.upsun.quizz.admin.activities;

import static com.upsun.quizz.Config.Constants.QUIZ_REF;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
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
import com.upsun.quizz.Adapter.AdminQuizViewAdapter;
import com.upsun.quizz.Config.Module;
import com.upsun.quizz.Model.JoinedQuizModel;
import com.upsun.quizz.Model.QuizModel;
import com.upsun.quizz.Model.QuizRankRewardModel;
import com.upsun.quizz.R;
import com.upsun.quizz.networkconnectivity.NetworkConnection;
import com.upsun.quizz.utils.ToastMsg;

import java.util.ArrayList;

public class AdminJoinQuizActivity extends AppCompatActivity {
    ImageView iv_back;
    TextView tv_title;
    RecyclerView rv_join;
    RelativeLayout rel_no_items;
    Module module;
    ToastMsg toastMsg;
    ProgressDialog loadingBar;
    ArrayList<JoinedQuizModel> jList;
    ArrayList<QuizModel> qList;
    ArrayList<QuizRankRewardModel> rwList;
    Activity ctx=AdminJoinQuizActivity.this;
    AdminQuizViewAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_join_quiz);

        initViews();
        if(NetworkConnection.connectionChecking(ctx))
        {
            getAllData();
        }
        else
        {
           module.noConnectionActivity();
        }


    }


    private void initViews() {
        iv_back=(ImageView)findViewById(R.id.iv_back);
        tv_title=(TextView)findViewById(R.id.tv_title);
        rv_join=(RecyclerView)findViewById(R.id.rv_joinQuiz);
        rel_no_items=(RelativeLayout)findViewById(R.id.rel_no_items);
        module =new Module(ctx);
        toastMsg=new ToastMsg(ctx);
        loadingBar=new ProgressDialog(ctx);
        loadingBar.setMessage("Loading...");
        loadingBar.setCanceledOnTouchOutside(false);
        qList=new ArrayList<>();
        jList=new ArrayList<>();
        rwList=new ArrayList<>();
        tv_title.setText("All Quizs");
    }

    private void getAllData() {
        loadingBar.show();
        qList.clear();

        DatabaseReference db_ref= FirebaseDatabase.getInstance().getReference().child(QUIZ_REF);
        db_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                loadingBar.dismiss();
                //Quiz List
                for(DataSnapshot snap_quiz:dataSnapshot.getChildren())
                {
                    QuizModel quizModel=snap_quiz.getValue(QuizModel.class);
                    qList.add(quizModel);
                }
                rv_join.setLayoutManager(new LinearLayoutManager(ctx));
                adapter=new AdminQuizViewAdapter(ctx,qList);
                rv_join.setAdapter(adapter);

                //Toast.makeText(ctx, "called", Toast.LENGTH_SHORT).show();

                adapter.notifyDataSetChanged();
//                //joined List
//                for(DataSnapshot snap_join_quiz:dataSnapshot.child(JOINED_QUIZ_REF).getChildren())
//                {
//                    JoinedQuizModel quizJoinModel=snap_join_quiz.getValue(JoinedQuizModel.class);
//                    jList.add(quizJoinModel);
//                }
//                //Rank Rewards List
//                for(DataSnapshot snap_rank_reward:dataSnapshot.child(RANK_REWARD_REF).getChildren())
//                {
//                    QuizRankRewardModel quizRewModel=snap_rank_reward.getValue(QuizRankRewardModel.class);
//                    rwList.add(quizRewModel);
//                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            loadingBar.dismiss();
            module.showToast(databaseError.getMessage().toString());
            }
        });
    }

}
