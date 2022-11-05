package com.upsun.quizz;

import static com.upsun.quizz.Config.Constants.HISTORY_REF;
import static com.upsun.quizz.Config.Constants.KEY_ID;
import static com.upsun.quizz.Config.Constants.KEY_NAME;
import static com.upsun.quizz.Config.Constants.KEY_REWARDS;
import static com.upsun.quizz.Config.Constants.QUIZ_RESULTS_REF;
import static com.upsun.quizz.Config.Constants.RANK_REWARD_REF;
import static com.upsun.quizz.Config.Constants.USER_REF;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.upsun.quizz.Config.Module;
import com.upsun.quizz.Model.HistoryModel;
import com.upsun.quizz.Model.NewRankModel;
import com.upsun.quizz.Model.QuizRankModel;
import com.upsun.quizz.Model.QuizResultModel;
import com.upsun.quizz.Model.RewardModel;
import com.upsun.quizz.networkconnectivity.NetworkConnection;
import com.upsun.quizz.utils.BannerAds;
import com.upsun.quizz.utils.MyService;
import com.upsun.quizz.utils.SessionManagment;
import com.upsun.quizz.utils.ToastMsg;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class QuizEnd extends AppCompatActivity implements View.OnClickListener {
    private final String TAG = QuizEnd.class.getSimpleName();
    TextView txt_percent, txt_correct, txt_timer, txt_rank, txt_rewards;
    ProgressBar progressBar;
    String quiz_id, quiz_name, tot_ques, c_ans, w_ans, r_time, section_id;
    DatabaseReference dRef, userRef;
    ProgressDialog loadingBar;
    CountDownTimer countDownTimer;
    ArrayList<QuizResultModel> quiz_list;
    ArrayList<QuizResultModel> sorted_list;
    ArrayList<QuizRankModel> rank_list;
    ArrayList<RewardModel> rank_rewrd_list;
    LinearLayout lin_timer, linear_rewards;
    ToastMsg toastMsg;
    RelativeLayout adView;
    Button btn_home, btn_review;
    int rank = 1;
    String user_id, reward_earned = "0", username;
    SessionManagment sessionManagment;
    Module module;
    HistoryModel historyModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_end);
        initViews();
        historyModel = new HistoryModel();
        if (NetworkConnection.connectionChecking(this)) {
            getQuizResults(quiz_id);
//            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child(USER_REF);
//            Query query = userRef.child(new SessionManagment(QuizEnd.this).getUserDetails().get(KEY_ID)).child("quiz_dates").child(quiz_id);
//            query.addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                    if (snapshot.exists()) {
//                        long endDiff = module.getTimeDiffInLong(String.valueOf(snapshot.getValue()), "00:00:00");
//                        startService(new Intent(QuizEnd.this, MyService.class));
//                        if (isMyServiceRunning(MyService.class)) {
//
//                            countDownTimer = new CountDownTimer(endDiff + 300L, 1000)
////            countDownTimer= new CountDownTimer(diff, 1000)
//                            {
//                                @Override
//                                public void onTick(long millisUntilFinished) {
//                                    String text = String.format(Locale.getDefault(), " %02d : %02d : %02d ",
//                                            TimeUnit.MILLISECONDS.toHours(millisUntilFinished) % 60, TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) % 60,
//                                            TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60);
//                                    txt_timer.setVisibility(View.VISIBLE);
//                                    txt_timer.setText(text);
//                                }
//
//                                @Override
//                                public void onFinish() {
////                                    getQuizResults(quiz_id);
//                                    linear_rewards.setVisibility(View.VISIBLE);
//                                    lin_timer.setVisibility(View.GONE);
//                                    txt_timer.setVisibility(View.GONE);

//                                }
//                            }.start();
//                        } else {
//                            startService(new Intent(QuizEnd.this, MyService.class));
//                        }
//                    }
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {
//
//                }
//            });
            progressBar.setProgress(module.getPercentage(Integer.parseInt(c_ans), Integer.parseInt(tot_ques)));
            txt_percent.setText(String.valueOf(module.getPercentage(Integer.parseInt(c_ans), Integer.parseInt(tot_ques))) + "%");
            txt_correct.setText(c_ans + " correct out of " + tot_ques);
            getHistoryQuizForUpdate(quiz_id);

        } else {
            module.noConnectionActivity();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(QuizEnd.this, MainActivity.class);
        startActivity(intent);
    }

    public void initViews() {
        adView = findViewById(R.id.adView);
        txt_correct = findViewById(R.id.correct);
        txt_percent = findViewById(R.id.percent);
        progressBar = findViewById(R.id.progressBar);
        txt_timer = findViewById(R.id.timer);
        lin_timer = findViewById(R.id.lin_timer);
        linear_rewards = findViewById(R.id.lin_rewards);
        btn_review = findViewById(R.id.results);
        btn_home = findViewById(R.id.home);
        txt_rank = findViewById(R.id.rank);
        txt_rewards = findViewById(R.id.rewards);
        btn_home.setOnClickListener(this);
        btn_review.setOnClickListener(this);
        quiz_list = new ArrayList<>();
        sorted_list = new ArrayList<>();
        rank_rewrd_list = new ArrayList<>();
        rank_list = new ArrayList<>();
        module = new Module(this);
        sessionManagment = new SessionManagment(QuizEnd.this);
        user_id = sessionManagment.getUserDetails().get(KEY_ID);
        username = sessionManagment.getUserDetails().get(KEY_NAME);
        c_ans = getIntent().getStringExtra("correct_ans");
        w_ans = getIntent().getStringExtra("wrong_ans");
        quiz_id = getIntent().getStringExtra("quiz_id");
        quiz_name = getIntent().getStringExtra("title");
        tot_ques = getIntent().getStringExtra("questions");
        r_time = getIntent().getStringExtra("remaining");
        section_id = getIntent().getStringExtra("section_id");

        loadingBar = new ProgressDialog(this);
        loadingBar.setMessage("Loading...");
        loadingBar.setCanceledOnTouchOutside(false);
        toastMsg = new ToastMsg(this);
        dRef = FirebaseDatabase.getInstance().getReference();
        userRef = FirebaseDatabase.getInstance().getReference().child(USER_REF);
    }


    void getQuizResults(final String quiz_id) {
        loadingBar.show();

        dRef.child(QUIZ_RESULTS_REF).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                quiz_list.clear();
//                Log.e("all_data", dataSnapshot.toString());
                if (dataSnapshot.hasChildren() || dataSnapshot.exists()) {
                    loadingBar.dismiss();
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        QuizResultModel model = data.getValue(QuizResultModel.class);
                        if (model.getQuiz_id().equals(quiz_id)) {
                            if (String.valueOf(data.child("date").getValue()).equals(module.getCurrentDate())) {
                                quiz_list.add(model);
                            }
                        }
                    }
                }

                Collections.sort(quiz_list, QuizResultModel.score);
                ArrayList<NewRankModel> calList = module.getNewAllRankUsers(quiz_list);
                if (calList.size() > 0) {
                    Collections.sort(calList, NewRankModel.position);
                    for (NewRankModel model : calList) {
                        Log.e("rannkkkss", "" + model.getResult() + " - " + model.getUser_id());
                    }
                    rank = module.getRank(calList, sessionManagment.getUserDetails().get(KEY_ID));

                    linear_rewards.setVisibility(View.VISIBLE);
                    lin_timer.setVisibility(View.GONE);
//                    getRewards(quiz_id, String.valueOf(rank), sessionManagment.getUserDetails().get(KEY_ID));

                }

            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                loadingBar.dismiss();
                toastMsg.toastIconError(databaseError.getMessage());
            }
        });
//
//

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.home) {
//            long diff = module.getDiffernceEC(getIntent().getStringExtra("end_time"));
//            if(diff<=0)
//            {
//             module.showToast("Please wait to update rank and rewards");
//            }
//            else
//            {
            Intent intent = new Intent(QuizEnd.this, MainActivity.class);
            startActivity(intent);

//            }

        } else if (id == R.id.results) {

        }

    }

    public void getRewards(final String quiz_id, final String rank, final String user_id) {
        loadingBar.show();
        rank_rewrd_list.clear();
        dRef.child("ranks").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists() || dataSnapshot.hasChildren()) {
                    loadingBar.dismiss();
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        if (data.getKey().equals(quiz_id)) {
                            for (DataSnapshot snap : data.getChildren()) {
                                RewardModel model = snap.getValue(RewardModel.class);
                                rank_rewrd_list.add(model);

                            }
                        }

                    }
                } else {
                    loadingBar.dismiss();
                }
                for (int i = 0; i < rank_rewrd_list.size(); i++) {
                    if (rank_rewrd_list.get(i).getRank().equals(rank)) {
                        reward_earned = rank_rewrd_list.get(i).getRewards();
                        txt_rewards.setText(QuizEnd.this.getResources().getString(R.string.rupee) + "" + reward_earned);
                        break;

                    }
//                    setQuizRewards(user_id,reward_earned,rank,quiz_id);
                }
                Log.e("rank-rewrd", "" + rank + " - " + reward_earned);

                setQuizRewards(user_id, username, reward_earned, rank, quiz_id);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                loadingBar.dismiss();
                toastMsg.toastIconError(databaseError.getMessage());

            }
        });
    }

    public void setQuizRewards(final String user_id, final String username, final String rewrds, String rank, String quiz_id) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("quiz_id", quiz_id);
        map.put("user_id", user_id);
        map.put("username", username);
        map.put("rank", rank);
        map.put("rewards", rewrds);
        map.put("id", module.getUniqueId("rank_rewards"));
        map.put("date", module.getCurrentDate());
        map.put("time", module.getCurrentTime());
        String unique_id = quiz_id + user_id + "_" + module.getCurrentDate();
        dRef.child(RANK_REWARD_REF).child(unique_id).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    toastMsg.toastIconSuccess("Quiz finished");

                    updateRewards(user_id, rewrds);
                    stopService(new Intent(QuizEnd.this, MyService.class));

                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                toastMsg.toastIconError(e.getMessage());
            }
        });
    }

    private void updateRewards(String user_id, String rewrds) {
        final String updatedRewards = String.valueOf(Integer.parseInt(sessionManagment.getUserDetails().get(KEY_REWARDS)) + Integer.parseInt(rewrds));

        HashMap<String, Object> map = new HashMap<>();
        map.put("rewards", updatedRewards);

        userRef.child(user_id).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    sessionManagment.updateRewards(updatedRewards);
                    Intent intent = new Intent(QuizEnd.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);

                }
            }
        });
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        new BannerAds().showBannerAds(QuizEnd.this, adView);
    }

    public void getHistoryQuizForUpdate(String quiz_id) {
        loadingBar.show();
        DatabaseReference hisRef = FirebaseDatabase.getInstance().getReference().child(HISTORY_REF);
        Query query = hisRef.orderByChild("quiz_id").equalTo(quiz_id);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                loadingBar.dismiss();
                try {
                    if (snapshot.getValue() != null || snapshot.hasChildren()) {
                        for (DataSnapshot s : snapshot.getChildren()) {
                            HistoryModel model = s.getValue(HistoryModel.class);
                            if (model.getUser_id().equalsIgnoreCase(user_id)) {
                                historyModel = model;
                            }

                        }
                        module.updateAnswer(historyModel.getId(), c_ans, section_id);
                    }
                } catch (Exception ex) {
                    Log.e(TAG, "onDataChange: " + ex.getMessage());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                loadingBar.dismiss();
                Log.e(TAG, "onCancelled: " + error.getMessage().toString());
            }
        });

    }
}
