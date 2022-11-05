package com.upsun.quizz.Fragments;

import static com.upsun.quizz.Config.Constants.KEY_ID;
import static com.upsun.quizz.Model.JoinedQuizModel.camp_jquiz;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.upsun.quizz.Adapter.MyQuizAdapter;
import com.upsun.quizz.Config.Module;
import com.upsun.quizz.Database.AppDatabase;
import com.upsun.quizz.Model.JoinedQuizModel;
import com.upsun.quizz.Model.QuizModel;
import com.upsun.quizz.Model.QuizRankRewardModel;
import com.upsun.quizz.Model.QuizResultModel;
import com.upsun.quizz.QuizListActivity;
import com.upsun.quizz.R;
import com.upsun.quizz.Worker.HistoryWorker;
import com.upsun.quizz.networkconnectivity.NetworkConnection;
import com.upsun.quizz.utils.SessionManagment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Developed by Binplus Technologies pvt. ltd.  on 12,June,2021
 */
public class NewMyQuizsFragment extends Fragment implements View.OnClickListener {
private final String TAG=NewMyQuizsFragment.class.getSimpleName();
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

        public NewMyQuizsFragment() {
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
        if(NetworkConnection.connectionChecking(getActivity())){
            Log.e(TAG, "JC : "+AppDatabase.getInstance(getActivity()).getJoinedQuizDao().getJoinQuizCount()+
                    "\n REWC : "+AppDatabase.getInstance(getActivity()).getRankRewardsDao().getRankRewardsCount()+
                    "\n RESULTC : "+AppDatabase.getInstance(getActivity()).getQuizResultDao().getQuizResultCount());
           getAllData();

        module.enqueuePeriodicRequest(HistoryWorker.class);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getAllData();
            }
          },1000);
        }
        else{
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
        List<QuizResultModel> rTempList=AppDatabase.getInstance(getActivity()).getQuizResultDao().getAllQuizResult();
        if (rTempList!=null ||  rTempList.size()>0) {
        for (QuizResultModel model : rTempList) {

        if (q_r_List.contains(model.getQuiz_id())) {

        } else {
        q_r_List.add(model);
        per_List.add(model.getPercentage());
        }
        }
        for(int i=0; i<j_List.size();i++){
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

        for (int i = 0;i<per_List.size();i++){
        int sum = 0;
        sum= sum+Integer.parseInt(per_List.get(i));
        txt_per.setText(String.valueOf(average(sum,per_List.size())));
        }
        for (int i = 0;i<rank_List.size();i++) {
        int sum = 0;
        sum= sum+Integer.parseInt(rank_List.get(i));
        txt_rank.setText(String.valueOf(average(sum,j_List.size())));
        }
        txt_all.setText(String.valueOf(j_List.size()));
        }
        else
        {
        //rel_no_item.setVisibility(View.VISIBLE);
        rv_trans.setVisibility(View.GONE);
        }
        }

        public int average(int m ,int t)
        {
        int avg = m/t;
        return avg;
        }

        public void getJoinedUserQuizList(final String user_id)
        {
        j_List.clear();
        try
        {
        List<JoinedQuizModel> jTempList= AppDatabase.getInstance(getActivity()).getJoinedQuizDao().getAllJoinedQuiz();
        if(jTempList==null || jTempList.size()<=0){
            module.showToast("No History Available");
        }else{


        for(JoinedQuizModel model:jTempList)
        {
           j_List.add(model);
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

        public void getQuizRankRewards(final String user_id)
        {
        rewardList.clear();
        rank_List.clear();
        try
        {
            List<QuizRankRewardModel> rewTempList=AppDatabase.getInstance(getActivity()).getRankRewardsDao().getAllQuizRnkRewards();
        if(rewTempList !=null || rewTempList.size()>0)
        {

        for(QuizRankRewardModel model:rewTempList)
        {
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

      private void getAllData(){
          getJoinedUserQuizList(user_id);
          getQuizRankRewards(user_id);
          getQuizResults(user_id);

      }

}
