/*package in.binplus.extrasheetquiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import in.binplus.extrasheetquiz.Adapter.UserRanksAdapter;
import in.binplus.extrasheetquiz.Config.Module;
import in.binplus.extrasheetquiz.Model.ViewRankModel;
import in.binplus.extrasheetquiz.networkconnectivity.NetworkConnection;
import in.binplus.extrasheetquiz.utils.SessionManagment;
import in.binplus.extrasheetquiz.utils.ToastMsg;

import static in.binplus.extrasheetquiz.Config.Constants.RANK_REF;

public class RankActivity extends AppCompatActivity {

    RecyclerView rv_ranks ;
    SessionManagment sessionManagment;
    Activity ctx=RankActivity.this;
    DatabaseReference rankRef;
    ArrayList<ViewRankModel> rankList;
    Module module;
    ToastMsg toastMsg;
    ProgressDialog loadingBar ;
    String title ,desc,ques , start_time ,end_time ,tot_p ,q_date ,points,
            rewards,user_id ,quiz_id ,joined ,left ,q_status,lang="";
    int j_p=0 ,l_p=0 ,t_p=0 ;
    public TextView tv_title ,txt_ini,quiz_title;
    ImageView back;
    UserRanksAdapter userRanksAdapter ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank);
        sessionManagment = new SessionManagment(this);
        loadingBar=new ProgressDialog(this);
        loadingBar.setMessage("Loading...");
        loadingBar.setCanceledOnTouchOutside(false);
        module=new Module(ctx);
        toastMsg=new ToastMsg(ctx);
        rv_ranks = findViewById(R.id.rv_ranks);
        txt_ini = findViewById(R.id.quiz_initial);
        quiz_title = findViewById(R.id.quizname);
        back = findViewById(R.id.iv_back);
        rankList = new ArrayList<>();
        rankRef= FirebaseDatabase.getInstance().getReference().child(RANK_REF);
        title= getIntent().getStringExtra("title");
        desc = getIntent().getStringExtra("description");
        q_date=  getIntent().getStringExtra("quiz_date");
        quiz_id=   getIntent().getStringExtra("quiz_id");
        start_time=   getIntent().getStringExtra("start_time");
        end_time=   getIntent().getStringExtra("end_time");
        points=   getIntent().getStringExtra("entry_fee");
        rewards=  getIntent().getStringExtra("rewards");
        ques=   getIntent().getStringExtra("questions");
        tot_p=   getIntent().getStringExtra("participants");
        q_status = getIntent().getStringExtra("q_status");
        joined = getIntent().getStringExtra("joined");
        lang = getIntent().getStringExtra("lang");
        j_p = Integer.parseInt(getIntent().getStringExtra("cnt_prtcpnts"));
        t_p = Integer.parseInt(tot_p);
        l_p = t_p-j_p;
        txt_ini.setText(String.valueOf(title.charAt(0)));
        quiz_title.setText(title);

        if(NetworkConnection.connectionChecking(ctx))
        {
            getAllRanks(quiz_id);
        }
        else
        {
            module.noConnectionActivity();
        }
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
    private void getAllRanks(String quiz_id) {
        loadingBar.show();
        rankList.clear();
        rankRef.child(quiz_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                loadingBar.dismiss();
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0)
                {
                    for(DataSnapshot snap:dataSnapshot.getChildren())
                    {
                        ViewRankModel model=snap.getValue(ViewRankModel.class);
                        rankList.add(model);
                    }
                    if(rankList.size()>0)
                    {
                        rv_ranks.setLayoutManager(new GridLayoutManager(ctx,2));
                        userRanksAdapter=new UserRanksAdapter(ctx,rankList);
                        rv_ranks.setAdapter(userRanksAdapter);
                        userRanksAdapter.notifyDataSetChanged();
                    }
                    else {
                        toastMsg.toastIconError("No Ranks Available for this quiz");
                    }
                }
                else
                {

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                loadingBar.dismiss();
                module.showToast(""+databaseError.getMessage().toString());
            }
        });

    }
}*/
package com.upsun.quizz;

import static com.upsun.quizz.Config.Constants.KEY_ID;
import static com.upsun.quizz.Config.Constants.KEY_NAME;
import static com.upsun.quizz.Config.Constants.QUIZ_RESULTS_REF;
import static com.upsun.quizz.Config.Constants.USER_REF;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.LoadAdError;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.upsun.quizz.Adapter.RankAdapter;
import com.upsun.quizz.Config.Constants;
import com.upsun.quizz.Config.Module;
import com.upsun.quizz.Intefaces.OnRankListener;
import com.upsun.quizz.Model.ManualRankModel;
import com.upsun.quizz.Model.NewRankModel;
import com.upsun.quizz.Model.QuizResultModel;
import com.upsun.quizz.Model.RewardModel;
import com.upsun.quizz.Model.UserModel;
import com.upsun.quizz.networkconnectivity.NetworkConnection;
import com.upsun.quizz.utils.BannerAds;
import com.upsun.quizz.utils.SessionManagment;
import com.upsun.quizz.utils.ToastMsg;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class RankActivity extends AppCompatActivity implements View.OnClickListener {
    ImageView iv_back;
    TextView tv_title;
    ToastMsg toastMsg;
    ProgressDialog loadingBar;
    ProgressDialog dialog;
    RelativeLayout rel_no_items,rel_update;
    Module module;
    Activity ctx= RankActivity.this;
    DatabaseReference quizRef;
    String quiz_id="";
    RecyclerView rv_quiz;
    ArrayList<QuizResultModel> list;
    ArrayList<RewardModel> rewardList;
    ArrayList<ManualRankModel> rnkList;
    ArrayList<UserModel> userList;
    RankAdapter adapter;
    SessionManagment sessionManagment;
    DatabaseReference userRef=FirebaseDatabase.getInstance().getReference().child(USER_REF);
    DatabaseReference dRef;
    Button btn_info;
    RelativeLayout rel_ad_view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank);
        sessionManagment= new SessionManagment(this);
        initViews();
        if(NetworkConnection.connectionChecking(ctx))
        {
            loadISNTAd();
            getJoindUsers(quiz_id);
        }
        else
        {
            module.noConnectionActivity();
        }
        new BannerAds().showBannerAds(RankActivity.this,rel_ad_view);
    }
    private void initViews() {
        iv_back=(ImageView)findViewById(R.id.iv_back);
        tv_title=(TextView) findViewById(R.id.tv_title);
        rel_no_items=(RelativeLayout) findViewById(R.id.rel_no_items);
        rel_update=(RelativeLayout) findViewById(R.id.rel_update);
        rel_ad_view=(RelativeLayout) findViewById(R.id.adView);
        rel_ad_view.setVisibility(View.VISIBLE);
        btn_info=(Button) findViewById(R.id.btn_info);
        module=new Module(ctx);
        toastMsg=new ToastMsg(ctx);
        loadingBar=new ProgressDialog(ctx);
        loadingBar.setMessage("Loading...");
        loadingBar.setCanceledOnTouchOutside(false);
        tv_title.setText("Rank Details");
        list=new ArrayList<>();
        rewardList=new ArrayList<>();
        rnkList=new ArrayList<>();
        userList=new ArrayList<>();
        rv_quiz=findViewById(R.id.rv_quiz);
        iv_back.setOnClickListener(this);
        rel_update.setOnClickListener(this);
        btn_info.setOnClickListener(this);
        quiz_id=getIntent().getStringExtra("quiz_id");
        Log.e("Quiz",quiz_id);
        quizRef= FirebaseDatabase.getInstance().getReference().child(QUIZ_RESULTS_REF);
    }

    private void getJoindUsers(final String quiz_id) {
        loadingBar.show();
        list.clear();
        Query query=quizRef.orderByChild("quiz_id").equalTo(quiz_id);
        Format formatter = new SimpleDateFormat("dd-MM-yyyy");
        String today = formatter.format(new Date());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                loadingBar.dismiss();
                if (dataSnapshot.hasChildren()) {


                    for (DataSnapshot snap : dataSnapshot.getChildren()) {

                        if (snap.getKey().endsWith("2022")) {
                            QuizResultModel model = snap.getValue(QuizResultModel.class);
                            if (String.valueOf(snap.child("date").getValue()).equals(today)) {
                                list.add(model);
                            }
                        }


                    }
                    if (list.size() > 0) {
                        Collections.sort(list, QuizResultModel.score);
                        for (int j = 0; j < list.size(); j++) {
                            Log.e("Score_data", list.get(j).getCorrect_ans() + " - - " + list.get(j).getUsername());
                        }
                        final ArrayList<NewRankModel> calList = module.getNewAllRankUsers(list);
                        if (calList.size() > 0) {
                            Collections.sort(calList, NewRankModel.position);
                            for (NewRankModel model : calList) {
                                Log.e("rannkkkss", "" + model.getResult() + " - " + model.getUser_id() + "-" + model.getUsername() + "Ranks: " + model.getRank());
                            }
                        }

                        module.getRankRewards(quiz_id, loadingBar, calList, new OnRankListener() {
                            @Override
                            public void getRankRewards(ArrayList<RewardModel> lst) {

                                rewardList.clear();
                                rewardList.addAll(lst);
                                for (RewardModel model :
                                        rewardList) {
                                    Log.e("rewww", "" + model.getRank() + " - " + model.getRewards());
                                }


                            }

                            @Override
                            public void getCalListRewards(ArrayList<NewRankModel> callist) {

                                rnkList.clear();
//                            rnkList.add(new ManualRankModel(sessionManagment.getUserDetails().get(KEY_ID),sessionManagment.getUserDetails().get(KEY_NAME),String.valueOf(module.getRank(calList,sessionManagment.getUserDetails().get(KEY_ID))),rewardList.get(module.getRank(calList,sessionManagment.getUserDetails().get(KEY_ID))).getRewards()));
                                rnkList.add(new ManualRankModel(sessionManagment.getUserDetails().get(KEY_ID), sessionManagment.getUserDetails().get(KEY_NAME), String.valueOf(module.getRank(calList, sessionManagment.getUserDetails().get(KEY_ID))), module.getUserReward(rewardList, String.valueOf(module.getRank(calList, sessionManagment.getUserDetails().get(KEY_ID))))));
                                for (int i = 0; i < callist.size(); i++) {
//                                rnkList.add(new ManualRankModel(callist.get(i).getUser_id(),callist.get(i).getUsername(),calList.get(i).getRank(),rewardList.get(Integer.parseInt(calList.get(i).getRank())).getRewards()));
                                    rnkList.add(new ManualRankModel(callist.get(i).getUser_id().toString(), callist.get(i).getUsername(), calList.get(i).getRank(), module.getUserReward(rewardList, calList.get(i).getRank())));
                                }
                                for (int j = 1; j < rnkList.size(); j++) {
                                    if (rnkList.get(j).getUser_id().equalsIgnoreCase(sessionManagment.getUserDetails().get(KEY_ID))) {
                                        rnkList.remove(j);
                                    }
                                }

                                Log.e("dasd", "" + calList.size() + " -- " + rnkList.size());
                                if (rnkList.size() > 0) {
                                    rv_quiz.setLayoutManager(new LinearLayoutManager(ctx));
                                    adapter = new RankAdapter(ctx, rnkList);
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
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                loadingBar.dismiss();
                toastMsg.toastIconError(""+databaseError.getMessage().toString());
            }
        });

    }



    @Override
    public void onClick(final View v) {
        if(v.getId() == R.id.btn_info)
        {
            getAllUserInfomations();
        }
        else if(v.getId() == R.id.iv_back)
        {
            finish();
        }
    }

    private void getAllUserInfomations() {
        loadingBar.show();
        userList.clear();
        final ArrayList<UserModel> tempList=new ArrayList<>();
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                loadingBar.dismiss();
                try
                {
                    if(dataSnapshot.hasChildren())
                    {
                        for(DataSnapshot snap:dataSnapshot.getChildren())
                        {
                            UserModel model=snap.getValue(UserModel.class);
                            for(int i=0; i<rnkList.size();i++)
                            {
                                if(model.getId().equalsIgnoreCase(rnkList.get(i).getUser_id()))
                                {
                                    userList.add(model);
                                }
                            }

                        }
                        if(userList.size()>0)
                        {
                            btn_info.setVisibility(View.GONE);
                        }
                        for(int i=0; i<userList.size();i++)
                        {
                            Log.e("usersss",""+userList.get(i).getId());
                        }
                    }
                    else
                    {
                        module.showToast("No Users Exist for this quiz");
                    }
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                loadingBar.dismiss();
                module.showToast(""+databaseError.getMessage().toString());
            }
        });
    }



    InterstitialAd mInterstitialAd;
    public void  loadISNTAd(){
        mInterstitialAd = new InterstitialAd(this);

        // Insert the Ad Unit ID
        mInterstitialAd.setAdUnitId(Constants.ad_intertitial);

        // Create ad request.
        AdRequest adRequest = new AdRequest.Builder().build();

        // Begin loading your interstitial.
        mInterstitialAd.loadAd(adRequest);

        // Prepare an Interstitial Ad Listener
        mInterstitialAd.setAdListener(new AdListener() {
            public void onAdLoaded() {
                // Call displayInterstitial() function
                mInterstitialAd.show();
               // Toast.makeText(ctx, "loaded", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdClosed() {
                super.onAdClosed();

            }

            @Override
            public void onAdFailedToLoad(LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                //Toast.makeText(ctx, ""+loadAdError.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
            }

            @Override
            public void onAdClicked() {
                super.onAdClicked();
            }
        });
    }
}
