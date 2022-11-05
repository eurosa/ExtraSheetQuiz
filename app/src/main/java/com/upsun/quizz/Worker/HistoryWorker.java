package com.upsun.quizz.Worker;

import static com.upsun.quizz.Config.Constants.JOINED_QUIZ_REF;
import static com.upsun.quizz.Config.Constants.KEY_ID;
import static com.upsun.quizz.Config.Constants.QUIZ_RESULTS_REF;
import static com.upsun.quizz.Config.Constants.RANK_REWARD_REF;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.upsun.quizz.Config.Module;
import com.upsun.quizz.Database.AppDatabase;
import com.upsun.quizz.Model.JoinedQuizModel;
import com.upsun.quizz.Model.QuizRankRewardModel;
import com.upsun.quizz.Model.QuizResultModel;
import com.upsun.quizz.utils.SessionManagment;
import com.upsun.quizz.utils.ToastMsg;

import java.util.ArrayList;
import java.util.List;

/**
 * Developed by Binplus Technologies pvt. ltd.  on 11,June,2021
 */
public class HistoryWorker extends Worker {
    private final String TAG=HistoryWorker.class.getSimpleName();
    SessionManagment sessionManagment;
    DatabaseReference quizResRef,joinedRef,rankRewardRef;
    Module module;
    public HistoryWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
       sessionManagment=new SessionManagment(context);
       module=new Module(context);

       joinedRef= FirebaseDatabase.getInstance().getReference().child(JOINED_QUIZ_REF);
       rankRewardRef= FirebaseDatabase.getInstance().getReference().child(RANK_REWARD_REF);
       quizResRef= FirebaseDatabase.getInstance().getReference().child(QUIZ_RESULTS_REF);
    }

    @NonNull
    @Override
    public Result doWork() {
        Boolean syncStatus=false;
        AppDatabase.getInstance(getApplicationContext()).clearAllTables();
        String user_id=sessionManagment.getUserDetails().get(KEY_ID);
        String currDate=module.getCurrentDate();
        String preThirdDate=module.getPrevThirdDate(module.getCurrentDate(),-3);
        getJoinedQuiz(user_id,currDate,preThirdDate);
        getQuizRankRewards(user_id,currDate,preThirdDate);
        getQuizResults(user_id,currDate,preThirdDate);

        return syncStatus?Result.success():Result.failure();
    }

    public void getJoinedQuiz(String user_id,String cdate,String preThirdDate){
        AppDatabase.getInstance(getApplicationContext()).getJoinedQuizDao().delete();
        Query query=joinedRef.orderByChild("date").startAt(preThirdDate).endAt(cdate);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<JoinedQuizModel> jList=new ArrayList<>();
                jList.clear();
                for(DataSnapshot s:snapshot.getChildren()){
                    JoinedQuizModel joinedQuizModel=s.getValue(JoinedQuizModel.class);
                    if(joinedQuizModel.getUser_id().equalsIgnoreCase(user_id)){
                        jList.add(joinedQuizModel);
                    }
                }
                AppDatabase.getInstance(getApplicationContext()).getJoinedQuizDao().insert(jList);
                Log.e(TAG, "Joined Count: "+AppDatabase.getInstance(getApplicationContext()).getJoinedQuizDao().getJoinQuizCount()+"\n "+jList.size());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void getQuizRankRewards(final String user_id,String cDate,String preThirdDate)
    {
        Query query=rankRewardRef.orderByChild("date").startAt(preThirdDate).endAt(cDate);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<QuizRankRewardModel> list=new ArrayList<>();
                try{
                    list.clear();
                        for(DataSnapshot snap:dataSnapshot.getChildren()){
                            QuizRankRewardModel model=snap.getValue(QuizRankRewardModel.class);
                            if(model.getUser_id().equalsIgnoreCase(user_id)){
                                list.add(model);
                            }

                        }
                   AppDatabase.getInstance(getApplicationContext()).getRankRewardsDao().insert(list);
                    Log.e(TAG, "Rewards Count: "+AppDatabase.getInstance(getApplicationContext()).getRankRewardsDao().getRankRewardsCount()+" \n "+list.size() );
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                    module.showToast("Something went wrong");
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                module.showToast(""+databaseError.getMessage());
            }
        });

    }
    public void getQuizResults(String user_id,String cDate,String preThirdDate){
        List<QuizResultModel> list=new ArrayList<>();
        Query query1=quizResRef.orderByChild("date").startAt(preThirdDate).endAt(cDate);
        query1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    list.clear();
                    for (DataSnapshot snap : dataSnapshot.getChildren()) {
                        QuizResultModel model = snap.getValue(QuizResultModel.class);
                        list.add(model);
                    }
                 AppDatabase.getInstance(getApplicationContext()).getQuizResultDao().insert(list);
                    Log.e(TAG, "Quiz Result Count: "+AppDatabase.getInstance(getApplicationContext()).getQuizResultDao().getQuizResultCount()+" \n "+list.size());

            }



            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                new ToastMsg(getApplicationContext()).toastIconError(databaseError.getMessage());

            }
        });
    }
}



