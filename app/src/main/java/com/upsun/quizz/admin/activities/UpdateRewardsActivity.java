package com.upsun.quizz.admin.activities;

import static com.upsun.quizz.Config.Constants.HISTORY_REF;
import static com.upsun.quizz.Config.Constants.QUIZ_RESULTS_REF;
import static com.upsun.quizz.Config.Constants.RANK_REWARD_REF;
import static com.upsun.quizz.Config.Constants.USER_REF;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.upsun.quizz.Adapter.AdminCalculateAdapter;
import com.upsun.quizz.Config.Module;
import com.upsun.quizz.Intefaces.OnRankListener;
import com.upsun.quizz.Model.HistoryModel;
import com.upsun.quizz.Model.ManualRankModel;
import com.upsun.quizz.Model.NewRankModel;
import com.upsun.quizz.Model.QuizResultModel;
import com.upsun.quizz.Model.RewardModel;
import com.upsun.quizz.Model.UserModel;
import com.upsun.quizz.R;
import com.upsun.quizz.networkconnectivity.NetworkConnection;
import com.upsun.quizz.utils.ToastMsg;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UpdateRewardsActivity extends AppCompatActivity implements View.OnClickListener {
    private final String TAG = UpdateRewardsActivity.class.getSimpleName();
    ImageView iv_back;
    TextView tv_title;
    ToastMsg toastMsg;
    ProgressDialog loadingBar;
    ProgressDialog dialog;
    RelativeLayout rel_no_items, rel_update;
    Module module;
    Activity ctx = UpdateRewardsActivity.this;
    DatabaseReference quizRef;
    String quiz_id = "";
    RecyclerView rv_quiz;
    ArrayList<QuizResultModel> list;
    ArrayList<RewardModel> rewardList;
    ArrayList<ManualRankModel> rnkList;
    ArrayList<UserModel> userList;
    AdminCalculateAdapter adapter;
    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child(USER_REF);
    DatabaseReference dRef;
    Button btn_info, btn_update, btn_date;
    ArrayList<HistoryModel> hList;
    String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_rewards);
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        date = sdf.format(new Date());
        initViews();
//        date = "07-10-2022";
        if (NetworkConnection.connectionChecking(ctx)) {
            getJoindUsers(date);
            getHistoryQuizForUpdate(quiz_id);
        } else {
            module.noConnectionActivity();
        }
    }

    private void initViews() {
        iv_back = (ImageView) findViewById(R.id.iv_back);
        tv_title = (TextView) findViewById(R.id.tv_title);
        rel_no_items = (RelativeLayout) findViewById(R.id.rel_no_items);
        rel_update = (RelativeLayout) findViewById(R.id.rel_update);
        btn_date = (Button) findViewById(R.id.btn_date);
        btn_update = (Button) findViewById(R.id.btn_update);
        btn_info = (Button) findViewById(R.id.btn_info);
        module = new Module(ctx);
        hList = new ArrayList<>();
        toastMsg = new ToastMsg(ctx);
        loadingBar = new ProgressDialog(ctx);
        loadingBar.setMessage("Loading...");
        loadingBar.setCanceledOnTouchOutside(false);
        tv_title.setText("Send Rewards");
        list = new ArrayList<>();
        rewardList = new ArrayList<>();
        rnkList = new ArrayList<>();
        userList = new ArrayList<>();
        rv_quiz = findViewById(R.id.rv_quiz);
        btn_date.setOnClickListener(this);
        iv_back.setOnClickListener(this);
        rel_update.setOnClickListener(this);
        btn_info.setOnClickListener(this);
        btn_update.setOnClickListener(this);
        btn_update.setVisibility(View.GONE);
        quiz_id = getIntent().getStringExtra("quiz_id");

        Log.d(TAG, "initViews: " + quiz_id);
        btn_date.setText(date);

        quizRef = FirebaseDatabase.getInstance().getReference().child(QUIZ_RESULTS_REF);
    }

    private void getJoindUsers(String date) {
        loadingBar.show();
        list.clear();
        Query query = quizRef.orderByChild("quiz_id").equalTo(quiz_id);
        Log.d("111222", "" + date);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                loadingBar.dismiss();

                if (dataSnapshot.hasChildren()) {
                    for (DataSnapshot snap : dataSnapshot.getChildren()) {
                        Log.d("111222", "" + snap.getKey().endsWith("2022"));
                        if (snap.getKey().endsWith("2022")) {
                            QuizResultModel model = snap.getValue(QuizResultModel.class);
                            if (String.valueOf(snap.child("date").getValue()).equals(date)) {
                                list.add(model);
                            }
                        }
                        if (!list.isEmpty()) {
                            rel_no_items.setVisibility(View.GONE);
                            rv_quiz.setVisibility(View.VISIBLE);
                            Collections.sort(list, QuizResultModel.score);
                            final ArrayList<NewRankModel> calList = module.getNewAllRankUsers(list);
                            if (calList.size() > 0) {
                                Collections.sort(calList, NewRankModel.position);
                                for (NewRankModel model : calList) {
                                    Log.e("rannkkkss", "" + model.getResult() + " - " + model.getUser_id() + "Rnk:" + model.getRank());
                                }
                            }
//raw image
//                    if (!quiz_ids.isEmpty()) {
//                        for (int i=0;i<quiz_ids.size();i++){
//                            module.getRankRewards(quiz_ids.get(i), loadingBar, calList, new OnRankListener() {
                            module.getRankRewards(quiz_id, loadingBar, calList, new OnRankListener() {
                                @Override
                                public void getRankRewards(ArrayList<RewardModel> lst) {

                                    rewardList.clear();
                                    rewardList.addAll(lst);
                                    for (RewardModel model : rewardList) {
                                        Log.e("rewww", "" + model.getRank() + " - " + model.getRewards());
                                    }
                                }

                                @Override
                                public void getCalListRewards(ArrayList<NewRankModel> callist) {
                                    rnkList.clear();
                                    Log.e(TAG, "getCalListRewards: " + calList.size() + " - " + rewardList.size() + " - " + callist.size());
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                        Map<Object, List<NewRankModel>> clist = callist.stream().collect(Collectors.groupingBy(p -> p.getRank(), Collectors.toList()));
                                        for (Map.Entry<Object, List<NewRankModel>> entry : clist.entrySet()) {
                                            Log.d(TAG, "getCalListRewards:clist ==>" + entry.getKey() + "/" + entry.getValue().size());
                                            for (int i = 0; i < entry.getValue().size(); i++) {
                                                NewRankModel rankModel = entry.getValue().get(i);
                                                Log.d(TAG, "getCalListRewards: ==>" + rankModel);
                                                rnkList.add(new ManualRankModel(rankModel.getUser_id(), rankModel.getUsername(), rankModel.getRank(), module.getUserRewardResult(rewardList, rankModel.getRank(), entry)));
                                            }
                                        }
                                    }
//                          for(int i=0; i<callist.size();i++)
//                          {
//                              Log.d(TAG, "getCalListRewards: ==>"+calList.get(i));
////                              rnkList.add(new ManualRankModel(callist.get(i).getUser_id().toString(),callist.get(i).getUsername(),calList.get(i).getRank(),rewardList.get(Integer.parseInt(calList.get(i).getRank())).getRewards()));
//                              rnkList.add(new ManualRankModel(callist.get(i).getUser_id().toString(),callist.get(i).getUsername(),calList.get(i).getRank(),module.getUserReward(rewardList,calList.get(i).getRank())));
//                          }
                                    Log.e("dasd", "" + calList.size() + " -- " + rnkList.size());
                                    if (rnkList.size() > 0) {
                                        rv_quiz.setLayoutManager(new LinearLayoutManager(ctx));
                                        adapter = new AdminCalculateAdapter(ctx, rnkList);
                                        rv_quiz.setAdapter(adapter);
                                        adapter.notifyDataSetChanged();
                                    } else {
                                        rel_no_items.setVisibility(View.VISIBLE);
                                        rv_quiz.setVisibility(View.GONE);
                                    }

                                }
                            });
                        } else {
                            rel_no_items.setVisibility(View.VISIBLE);
                            rv_quiz.setVisibility(View.GONE);

                        }
//                    }
                    }
                } else {
                    rel_no_items.setVisibility(View.VISIBLE);
                    rv_quiz.setVisibility(View.GONE);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                loadingBar.dismiss();
                toastMsg.toastIconError("" + databaseError.getMessage().toString());
            }
        });
    }


    @Override
    public void onClick(final View v) {
        if (v.getId() == R.id.btn_update) {

            Log.e("dasdas", "" + rnkList.size());
            new UpdateRewards(rnkList, userList).execute();
        } else if (v.getId() == R.id.btn_info) {
            getAllUserInfomations();
        } else if (v.getId() == R.id.iv_back) {
            finish();
        } else if (v.getId() == R.id.btn_date) {
            showDatePicker();
        }
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int y = calendar.get(Calendar.YEAR);
        int m = calendar.get(Calendar.MONTH);
        int d = calendar.get(Calendar.DAY_OF_MONTH);

        final DatePickerDialog datePickerDialog = new DatePickerDialog(ctx, (view, year, mth, dayOfMonth) -> {
            int month = mth + 1;
            if (dayOfMonth < 10) {
                if (month < 10) {
                    date = "0" + dayOfMonth + "-0" + month + "-" + year;
                } else {
                    date = "0" + dayOfMonth + "-" + month + "-" + year;
                }
            } else {
                if (month < 10) {
                    date = dayOfMonth + "-0" + month + "-" + year;
                } else {
                    date = dayOfMonth + "-" + month + "-" + year;
                }
            }

            btn_date.setText(date);
            getJoindUsers(date);
        }, y, m, d);

//        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis()-1000);
        datePickerDialog.show();


    }

    private void getAllUserInfomations() {
        loadingBar.show();
        userList.clear();
        final ArrayList<UserModel> tempList = new ArrayList<>();
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                loadingBar.dismiss();
                try {
                    if (dataSnapshot.hasChildren()) {
                        for (DataSnapshot snap : dataSnapshot.getChildren()) {
                            UserModel model = snap.getValue(UserModel.class);
                            for (int i = 0; i < rnkList.size(); i++) {
                                if (model.getId() != null) {
                                    Log.d("data", model.getId() + "           " + rnkList.get(i).getUser_id());
                                    if (model.getId().toString().equalsIgnoreCase(rnkList.get(i).getUser_id().toString())) {
                                        userList.add(model);
                                    }
                                }
                            }

                        }
                        if (userList.size() > 0) {
                            btn_info.setVisibility(View.GONE);
                            btn_update.setVisibility(View.VISIBLE);
                        }
                        for (int i = 0; i < userList.size(); i++) {
                            Log.e("usersss", "" + userList.get(i).getId());
                        }
                    } else {
                        module.showToast("No Users Exist for this quiz");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    Toast.makeText(ctx, "" + ex.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                loadingBar.dismiss();
                module.showToast("" + databaseError.getMessage().toString());
            }
        });
    }

    int i;
    int count;
    int tot;

    public class UpdateRewards extends AsyncTask<Void, Void, Void> {
        ArrayList<ManualRankModel> list;
        ArrayList<UserModel> userList;
        ProgressDialog dialog;

        public UpdateRewards(ArrayList<ManualRankModel> list, ArrayList<UserModel> userList) {
            this.list = list;
            this.userList = userList;
        }

        @Override
        protected Void doInBackground(Void... voids) {
//            for (i = 0; i < rnkList.size(); i++) {
//                if (!rnkList.get(i).getRewards().isEmpty() && !rnkList.get(i).getRewards().equals("") && !getUserIDRewards(userList, rnkList.get(i).getUser_id()).isEmpty() && !getUserIDRewards(userList, rnkList.get(i).getUser_id()).equals("")) {
//                    tot = Integer.parseInt(rnkList.get(i).getRewards()) + Integer.parseInt(getUserIDRewards(userList, rnkList.get(i).getUser_id()));
//
//                    count = 0;
//                    FirebaseDatabase.getInstance().getReference().child(RANK_REWARD_REF);
//                    Query query = FirebaseDatabase.getInstance().getReference().child(RANK_REWARD_REF).orderByChild("user_id").equalTo(rnkList.get(i).getUser_id());
//                    query.addValueEventListener(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                            if (dataSnapshot.hasChildren()) {
//                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                                    Log.d("snapsjot", snapshot.getValue().toString());
//                                    count++;
//                                }
//                                if (count == 0) {
//                                    if (Integer.parseInt(getUserIDRewards(userList, rnkList.get(i).getUser_id())) == 0) {
//                                        tot = Integer.parseInt(rnkList.get(i).getRewards()) + 2;
//                                    }
//                                }
//                            }
//                            if(i<1) {
//                                String pRew = rnkList.get(i).getRewards().toString();
//                                String usr_id = rnkList.get(i).getUser_id().toString();
//                                String rew = String.valueOf(tot);
//                                String rnk = rnkList.get(i).getRank().toString();
//                                String qzId = quiz_id;
//                                Log.e("darara", "" + usr_id + " - " + rew + " - " + rnk + " - " + qzId);
//                                setQuizRewards(usr_id, pRew, rnk, qzId, rew, date);
//                                for (int j = 0; j < hList.size(); j++) {
//                                    if (rnkList.get(i).getUser_id().equalsIgnoreCase(hList.get(j).getUser_id())) {
//                                        module.updateRankRewards(hList.get(j).getId(), rnk, pRew);
//                                    }
//                                }
//                            }
//                            query.removeEventListener(this);
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError error) {
//
//                        }
//                    });
//
//
//                }
            int tot = 0;
            String pRew = "0";
            for (int i = 0; i < rnkList.size(); i++) {
                if (Integer.parseInt(getUserIDRewards(userList, rnkList.get(i).getUser_id())) == 0 && Integer.parseInt(rnkList.get(i).getRewards()) == 0) {
                    tot = 2;
                    pRew = "2";


                } else {
                    tot = Integer.parseInt(rnkList.get(i).getRewards()) + Integer.parseInt(getUserIDRewards(userList, rnkList.get(i).getUser_id()));
                    pRew = rnkList.get(i).getRewards().toString();

                }
                String usr_id = rnkList.get(i).getUser_id().toString();
                String rew = String.valueOf(tot);
                String rnk = rnkList.get(i).getRank().toString();
                String qzId = quiz_id;
                Log.e("darara", "" + usr_id + " - " + rew + " - " + rnk + " - " + qzId);
                setQuizRewards(usr_id, pRew, rnk, qzId, rew, date);
                for (int j = 0; j < hList.size(); j++) {
                    if (rnkList.get(i).getUser_id().equalsIgnoreCase(hList.get(j).getUser_id())) {
                        module.updateRankRewards(hList.get(j).getId(), rnk, pRew);
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(ctx);
            dialog.setMessage("Loading..");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dialog.dismiss();
            toastMsg.toastIconSuccess("Rewards Updated....");
            finish();
        }
    }

    private void getShouldAdd(String user_id) {

    }

    public void setQuizRewards(final String user_id, final String prewrds, String rank, String quiz_id, final String rewrds, String date) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("quiz_id", quiz_id);
        map.put("user_id", user_id);
        map.put("rank", rank);
        map.put("date", date);

        map.put("rewards", prewrds);
        map.put("id", module.getUniqueId("rank_rewards"));
        String unique_id = quiz_id + user_id + "_" + date;
        dRef = FirebaseDatabase.getInstance().getReference().child(RANK_REWARD_REF);
        dRef.child(unique_id).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    updateRew(user_id, rewrds);

                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                toastMsg.toastIconError(e.getMessage());
            }
        });
    }

    private void updateRew(String user_id, String rewrds) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("rewards", rewrds);
        userRef.child(user_id).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {
                    Log.e("dsdsdsdsdsd", "Successssssss");
                } else {
                    module.showToast("" + task.getException().getMessage().toString());
                }
            }
        });
    }

    public String getUserIDRewards(ArrayList<UserModel> userLst, String uStr) {
        String us_id = "";
        int ind = 0;
        for (int i = 0; i < userLst.size(); i++) {
            if (userLst.get(i).getId().equalsIgnoreCase(uStr)) {
                us_id = userLst.get(i).getId().toString();
                ind = i;
                break;
            }
        }

        return userLst.get(ind).getRewards().toString();
    }

    public void getHistoryQuizForUpdate(String quiz_id) {
        hList.clear();
        DatabaseReference hisRef = FirebaseDatabase.getInstance().getReference().child(HISTORY_REF);
        Log.e(TAG, "getHistoryQuizForUpdate: " + quiz_id);
        Query query = hisRef.orderByChild("quiz_id").equalTo(quiz_id);
//        Query query = hisRef.orderByChild("date").equalTo(date);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    if (snapshot.getValue() != null || snapshot.hasChildren()) {
                        for (DataSnapshot s : snapshot.getChildren()) {
                            HistoryModel model = s.getValue(HistoryModel.class);
                            hList.add(model);
                        }
                    }
                } catch (Exception ex) {
                    Log.e(TAG, "onDataChange: " + ex.getMessage());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "onCancelled: " + error.getMessage().toString());
            }
        });

    }


}
