package com.upsun.quizz.Fragments;

import static com.upsun.quizz.Config.Constants.JOINED_QUIZ_REF;
import static com.upsun.quizz.Config.Constants.KEY_ID;
import static com.upsun.quizz.Model.JoinedQuizModel.camp_jquiz;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.upsun.quizz.Adapter.MyQuizAdapter;
import com.upsun.quizz.Config.Module;
import com.upsun.quizz.Model.JoinedQuizModel;
import com.upsun.quizz.Model.QuizModel;
import com.upsun.quizz.Model.QuizRankRewardModel;
import com.upsun.quizz.Model.QuizResultModel;
import com.upsun.quizz.QuizListActivity;
import com.upsun.quizz.R;
import com.upsun.quizz.networkconnectivity.NetworkConnection;
import com.upsun.quizz.utils.SessionManagment;
import com.upsun.quizz.utils.ToastMsg;

import java.util.ArrayList;
import java.util.Collections;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyQuizsFragmemts extends Fragment implements View.OnClickListener {
    private final String TAG=MyQuizsFragmemts.class.getSimpleName();
    CardView card_all ,card_won ,card_lost ;
    TextView txt_all ,txt_rank ,txt_per ;
    RelativeLayout rel_no_item ;
    SessionManagment sessionManagment ;
    DatabaseReference dref ;
    ArrayList<QuizResultModel> q_r_List;
    ArrayList<JoinedQuizModel> j_List;
    ArrayList<QuizModel> quiz_List;
    ArrayList<QuizRankRewardModel> rewardList;
    ArrayList<String> per_List;
    ArrayList<String> rank_List;
    RecyclerView rv_trans ;
    MyQuizAdapter quizAdapter;
    String user_id ;
    Module module;
    ProgressDialog loadingBar ;
    DatabaseReference quizResRef,joinedRef,quizRef,rankRef;

    public MyQuizsFragmemts() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View view =inflater.inflate(R.layout.fragment_my_quizs_fragmemts, container, false);
       card_all = view.findViewById(R.id.card_all);
       card_lost = view.findViewById(R.id.card_lost);
       card_won = view.findViewById(R.id.card_won);
       rel_no_item = view.findViewById(R.id.rel_no_items);
       txt_all = view.findViewById(R.id.total_quiz);
       txt_per = view.findViewById(R.id.avg_per);
       txt_rank = view.findViewById(R.id.avg_rank);
        sessionManagment = new SessionManagment(getContext());
        j_List = new ArrayList<>();
        module=new Module(getActivity());
        q_r_List = new ArrayList<>();
        quiz_List = new ArrayList<>();
        per_List = new ArrayList<>();
        rewardList = new ArrayList<>();
        rank_List = new ArrayList<>();
        loadingBar=new ProgressDialog(getActivity());
        loadingBar.setMessage("Loading...");
        loadingBar.setCanceledOnTouchOutside(false);
        rv_trans = view.findViewById(R.id.rv_trans);
        rv_trans.setNestedScrollingEnabled(false);
        user_id = sessionManagment.getUserDetails().get(KEY_ID);


        quizResRef=FirebaseDatabase.getInstance().getReference().child("quiz_results");
        joinedRef=FirebaseDatabase.getInstance().getReference().child("joined_quiz");
        rankRef=FirebaseDatabase.getInstance().getReference().child("quiz_rank_reward");
        if(NetworkConnection.connectionChecking(getActivity()))
        {

            getJoinedUserQuizList(user_id);
            getQuizRankRewards(user_id);
            getQuizResults(user_id);
        }
        else {
            module.noConnectionActivity();
        }

       card_won.setOnClickListener(this);
       card_all.setOnClickListener(this);
       card_lost.setOnClickListener(this);
       return  view ;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.card_won)
        {
            Intent intent = new Intent(getActivity(), QuizListActivity.class);
            intent.putExtra("type","won");
            startActivity(intent);
        }
        else if(id == R.id.card_all)
        {
            Intent intent = new Intent(getActivity(), QuizListActivity.class);
            intent.putExtra("type","all");
            startActivity(intent);
        }
        else if (id== R.id.card_lost)
        {
            Intent intent = new Intent(getActivity(), QuizListActivity.class);
            intent.putExtra("type","lost");
            startActivity(intent);
        }

    }
    public void getQuizResults(final String user_id)
    {
        loadingBar.show();
        q_r_List.clear();
        per_List.clear();
//        rewardList.clear();
        Query query1=quizResRef.orderByChild("user_id").equalTo(user_id);
        query1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    for (DataSnapshot snap : dataSnapshot.getChildren()) {
                        QuizResultModel model = snap.getValue(QuizResultModel.class);

                            if (q_r_List.contains(model.getQuiz_id())) {

                            } else {
                                q_r_List.add(model);
                                per_List.add(model.getPercentage());
                            }
                    }
                    for(int i=0; i<j_List.size();i++)
                    {
                        String q_id = j_List.get(i).getQuiz_id();
                        String j_d = q_id.substring(4,12);
                        String dtstr=j_d.substring(0,2)+"-"+j_d.subSequence(2,4)+"-"+j_d.subSequence(4,j_d.length());
                        int days= module.getDateDiff(dtstr.toString());
                        j_List.get(i).setDays(String.valueOf(days));
                    }

                    Collections.sort(j_List,camp_jquiz);
                    Log.e("sizzezzz",""+q_r_List.size()+" - "+j_List.size()+" - "+rewardList.size()+" - "+quiz_List.size());
                    rel_no_item.setVisibility(View.GONE);
                    rv_trans.setVisibility(View.VISIBLE);
                    rv_trans.setLayoutManager(new LinearLayoutManager(getActivity()));
                    quizAdapter = new MyQuizAdapter(q_r_List, j_List, rewardList, getActivity());
                    rv_trans.setAdapter(quizAdapter);
                    loadingBar.dismiss();

//                    quizAdapter.notifyDataSetChanged();

                    for (int i = 0;i<per_List.size();i++)
                    {
                        int sum = 0;
                        sum= sum+Integer.parseInt(per_List.get(i));
                        txt_per.setText(String.valueOf(average(sum,per_List.size())));
                    }
                    for (int i = 0;i<rank_List.size();i++)
                    {
                        int sum = 0;
                        sum= sum+Integer.parseInt(rank_List.get(i));
                        txt_rank.setText(String.valueOf(average(sum,j_List.size())));
                    }
                   txt_all.setText(String.valueOf(j_List.size()));
                }
                else
                {
                    rel_no_item.setVisibility(View.VISIBLE);
                    rv_trans.setVisibility(View.GONE);
                }
            }



            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                loadingBar.dismiss();
                new ToastMsg(getActivity()).toastIconError(databaseError.getMessage());

            }
        });
    }
    public int average(int m ,int t)
    {
        int avg = m/t;
        return avg;
    }

    public void getJoinedUserQuizList(final String user_id)
    {
        j_List.clear();
        DatabaseReference quizRef=FirebaseDatabase.getInstance().getReference().child(JOINED_QUIZ_REF);
        Query query=quizRef.orderByChild("user_id").equalTo(user_id);
//        Query query=quizRef.orderByChild("user_id").equalTo("+919252181919").orderByChild("date").startAt("08-06-2021").endAt("11-06-2021");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                loadingBar.dismiss();
                try
                {
                    Log.e(TAG, "joined_data "+dataSnapshot.toString());
                    if(dataSnapshot.hasChildren())
                    {
                        for(DataSnapshot snap:dataSnapshot.getChildren())
                        {
                            JoinedQuizModel model=snap.getValue(JoinedQuizModel.class);
                            long endDiff = module.getDiffInLong(model.getQuiz_date().toString(), "00:00:00");
                            if(endDiff>0) {
                                j_List.add(model);
                            }
                        }
                        for(int i=0; i<j_List.size();i++)
                        {
                            Log.e("j_listtt",""+j_List.get(i).getQuiz_id()+" - "+j_List.get(i).getTitle());
                        }

                    }
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                    module.showToast("Something went wrong");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                loadingBar.dismiss();
                module.showToast(""+databaseError.getMessage());
            }
        });
    }

    public void getQuizRankRewards(final String user_id)
    {
        rewardList.clear();
        rank_List.clear();
        DatabaseReference quizRef=FirebaseDatabase.getInstance().getReference().child("quiz_rank_reward");
        Query query=quizRef.orderByChild("user_id").equalTo(user_id);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                loadingBar.dismiss();
                try
                {
                    if(dataSnapshot.hasChildren())
                    {

                        for(DataSnapshot snap:dataSnapshot.getChildren())
                        {
                            QuizRankRewardModel model=snap.getValue(QuizRankRewardModel.class);

                            rewardList.add(model);
                                rank_List.add(model.getRank());

                        }

                        }
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                    module.showToast("Something went wrong");
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                loadingBar.dismiss();
                module.showToast(""+databaseError.getMessage());
            }
        });

    }


}
