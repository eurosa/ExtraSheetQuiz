package com.upsun.quizz.admin.activities;

import static com.upsun.quizz.Config.Constants.JOINED_QUIZ_REF;
import static com.upsun.quizz.Config.Constants.RANK_REWARD_REF;
import static com.upsun.quizz.Config.Constants.USER_REF;

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
import com.upsun.quizz.Adapter.AdminParticipantAdapter;
import com.upsun.quizz.Adapter.AdminRankRewardAdapter;
import com.upsun.quizz.Config.Module;
import com.upsun.quizz.Model.JoinedQuizModel;
import com.upsun.quizz.Model.QuizModel;
import com.upsun.quizz.Model.QuizRankRewardModel;
import com.upsun.quizz.Model.UserModel;
import com.upsun.quizz.R;
import com.upsun.quizz.networkconnectivity.NetworkConnection;
import com.upsun.quizz.utils.ToastMsg;

import java.util.ArrayList;

public class ViewParticipentsActvity extends AppCompatActivity {
    ImageView iv_back;
    TextView tv_title;
    RecyclerView rv_view;
    RelativeLayout rel_no_items;
    Activity ctx=ViewParticipentsActvity.this;
    AdminParticipantAdapter viewAdapter;
    AdminRankRewardAdapter rankAdapter;
    Module module;
    ToastMsg toastMsg;
    ProgressDialog loadingBar;
    String quiz_id="",title="",start_time="",end_time="",date="",type="";
    DatabaseReference join_ref,rank_ref,user_ref;
    ArrayList<JoinedQuizModel> jList;
    ArrayList<QuizModel> qList;
    ArrayList<UserModel> uList;
    ArrayList<QuizRankRewardModel> rwList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_participents_actvity);
        initViews();
        if(NetworkConnection.connectionChecking(ctx))
        {


        if(type.equalsIgnoreCase("pcnt"))
        {
            getAllParticipants(quiz_id);
        }
        else if(type.equalsIgnoreCase("rank"))
        {
            getAllRanks(quiz_id);
        }
        }
        else
        {
module.noConnectionActivity();
        }
    }

    private void initViews() {
        iv_back=(ImageView)findViewById(R.id.iv_back);
        tv_title=(TextView)findViewById(R.id.tv_title);
        rv_view=(RecyclerView)findViewById(R.id.rv_view);
        rel_no_items=(RelativeLayout)findViewById(R.id.rel_no_items);
        quiz_id=getIntent().getStringExtra("quiz_id");
        title=getIntent().getStringExtra("title");
        date=getIntent().getStringExtra("date");
        start_time=getIntent().getStringExtra("start_time");
        end_time=getIntent().getStringExtra("end_time");
        type=getIntent().getStringExtra("type");
        tv_title.setText(quiz_id.toUpperCase()+" Participants");
        join_ref= FirebaseDatabase.getInstance().getReference();
        rank_ref= FirebaseDatabase.getInstance().getReference().child(RANK_REWARD_REF);
        user_ref= FirebaseDatabase.getInstance().getReference().child(USER_REF);
        module =new Module(ctx);
        toastMsg=new ToastMsg(ctx);
        loadingBar=new ProgressDialog(ctx);
        loadingBar.setMessage("Loading...");
        loadingBar.setCanceledOnTouchOutside(false);
        qList=new ArrayList<>();
        jList=new ArrayList<>();
        rwList=new ArrayList<>();
        uList=new ArrayList<>();

    }


    private void getAllRanks(final String quiz_id) {
        loadingBar.show();
        rwList.clear();
        uList.clear();
        join_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                loadingBar.dismiss();
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0)
                {
                    rel_no_items.setVisibility(View.GONE);
                    rv_view.setVisibility(View.VISIBLE);
                    //joined quiz
                    for(DataSnapshot snap_join:dataSnapshot.child(RANK_REWARD_REF).getChildren())
                    {
                        QuizRankRewardModel model=snap_join.getValue(QuizRankRewardModel.class);
                        if(model.getQuiz_id().equalsIgnoreCase(quiz_id))
                        {
                            rwList.add(model);
                        }
                    }
                    //User List
                    for(DataSnapshot snap_user:dataSnapshot.child(USER_REF).getChildren())
                    {
                        UserModel model=snap_user.getValue(UserModel.class);
                        uList.add(model);
                    }

                    if(rwList.size()>0)
                    {


                    rv_view.setLayoutManager(new LinearLayoutManager(ctx));
                    rankAdapter=new AdminRankRewardAdapter(ctx,rwList,uList);
                    rv_view.setAdapter(rankAdapter);
                        rankAdapter.notifyDataSetChanged();
                    }
                    else
                    {
                        rel_no_items.setVisibility(View.VISIBLE);
                        rv_view.setVisibility(View.GONE);
                    }
                }
                else
                {
                    rel_no_items.setVisibility(View.VISIBLE);
                    rv_view.setVisibility(View.GONE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                loadingBar.dismiss();
                module.showToast(""+databaseError.getMessage().toString());
            }
        });
    }

    private void getAllParticipants(final String quiz_id) {
        loadingBar.show();
        jList.clear();
        uList.clear();
        join_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                loadingBar.dismiss();
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0)
                {
                    rel_no_items.setVisibility(View.GONE);
                    rv_view.setVisibility(View.VISIBLE);
                    //joined quiz
                    for(DataSnapshot snap_join:dataSnapshot.child(JOINED_QUIZ_REF).getChildren())
                    {
                        JoinedQuizModel model=snap_join.getValue(JoinedQuizModel.class);
                        if(model.getQuiz_id().equalsIgnoreCase(quiz_id))
                        {
                            jList.add(model);
                        }
                    }
                    //User List
                    for(DataSnapshot snap_user:dataSnapshot.child(USER_REF).getChildren())
                    {
                        UserModel model=snap_user.getValue(UserModel.class);
                        uList.add(model);
                    }

                    if(jList.size()>0) {
                        rv_view.setLayoutManager(new LinearLayoutManager(ctx));
                        viewAdapter = new AdminParticipantAdapter(ctx, jList, uList);
                        rv_view.setAdapter(viewAdapter);
                        viewAdapter.notifyDataSetChanged();
                    }
                    else
                    {
                        rel_no_items.setVisibility(View.VISIBLE);
                        rv_view.setVisibility(View.GONE);
                    }
                }
                else
                {
                    rel_no_items.setVisibility(View.VISIBLE);
                    rv_view.setVisibility(View.GONE);
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
