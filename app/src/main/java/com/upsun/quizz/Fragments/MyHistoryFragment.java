package com.upsun.quizz.Fragments;

import static com.upsun.quizz.Config.Constants.HISTORY_REF;
import static com.upsun.quizz.Config.Constants.KEY_ID;
import static com.upsun.quizz.Model.HistoryModel.camp_history;

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
import com.google.gson.Gson;
import com.upsun.quizz.Adapter.HistoryAdapter;
import com.upsun.quizz.Config.Constants;
import com.upsun.quizz.Config.Module;
import com.upsun.quizz.Model.HistoryModel;
import com.upsun.quizz.Model.JoinedQuizModel;
import com.upsun.quizz.Model.QuizModel;
import com.upsun.quizz.Model.QuizRankRewardModel;
import com.upsun.quizz.Model.QuizResultModel;
import com.upsun.quizz.NewAnswerActivity;
import com.upsun.quizz.R;
import com.upsun.quizz.networkconnectivity.NetworkConnection;
import com.upsun.quizz.utils.SessionManagment;
import com.upsun.quizz.utils.ToastMsg;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

/**
 * Developed by Binplus Technologies pvt. ltd.  on 14,June,2021
 */
public class MyHistoryFragment extends Fragment {

    private final String TAG = MyQuizsFragmemts.class.getSimpleName();
    CardView card_all, card_won, card_lost;
    TextView txt_all, txt_rank, txt_per, tv_count;
    RelativeLayout rel_no_item;
    SessionManagment sessionManagment;
    ArrayList<QuizResultModel> q_r_List;
    ArrayList<JoinedQuizModel> j_List;
    ArrayList<QuizModel> quiz_List;
    ArrayList<QuizRankRewardModel> rewardList;
    ArrayList<String> per_List;
    ArrayList<String> rank_List;
    RecyclerView rv_trans;
    HistoryAdapter quizAdapter;
    String user_id;
    Module module;
    ProgressDialog loadingBar;
    Date currDate, preThirdDate;
    ArrayList<HistoryModel> hList;
    InterstitialAd mInterstitialAd;
    int mPosition = 0;

    public MyHistoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_quizs_fragmemts, container, false);
        card_all = view.findViewById(R.id.card_all);
        card_lost = view.findViewById(R.id.card_lost);
        card_won = view.findViewById(R.id.card_won);
        rel_no_item = view.findViewById(R.id.rel_no_items);
        txt_all = view.findViewById(R.id.total_quiz);
        txt_per = view.findViewById(R.id.avg_per);
        txt_rank = view.findViewById(R.id.avg_rank);
        tv_count = view.findViewById(R.id.tv_count);
        sessionManagment = new SessionManagment(getContext());
        j_List = new ArrayList<>();
        hList = new ArrayList<>();
        module = new Module(getActivity());
        q_r_List = new ArrayList<>();
        quiz_List = new ArrayList<>();
        per_List = new ArrayList<>();
        rewardList = new ArrayList<>();
        rank_List = new ArrayList<>();
        loadingBar = new ProgressDialog(getActivity());
        loadingBar.setMessage("Loading...");
        loadingBar.setCanceledOnTouchOutside(false);
        rv_trans = view.findViewById(R.id.rv_trans);
        rv_trans.setNestedScrollingEnabled(false);
        user_id = sessionManagment.getUserDetails().get(KEY_ID);
        if (NetworkConnection.connectionChecking(getActivity())) {
            currDate = new Date();
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -2);
            preThirdDate = cal.getTime();
            getHistory();
            Log.e(TAG, "onCreateView: " + user_id);
        } else {
            module.noConnectionActivity();
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (Boolean.parseBoolean(getString(R.string.show_adds))) {
            loadISNTAd();
        }
    }

    private void getHistory() {
        loadingBar.show();

        hList.clear();

        DatabaseReference hisRef = FirebaseDatabase.getInstance().getReference().child(HISTORY_REF);
        Query query = hisRef.orderByChild("user_id").equalTo(user_id);//.orderByChild("date").startAt(preThirdDate).endAt(currDate);//
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                loadingBar.dismiss();
                try {
                    if (snapshot.getValue() != null || snapshot.hasChildren()) {
                        rel_no_item.setVisibility(View.GONE);
                        rv_trans.setVisibility(View.VISIBLE);
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            HistoryModel model = ds.getValue(HistoryModel.class);

                           Date date = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).parse(model.getDate());

                            Log.d(TAG, "onDataChange: " + model.getSection_id());
                            Log.d(TAG, "onDataChange: " + model.getId());

                            if (model.getUser_id().equalsIgnoreCase(user_id)) {
                                if (model.getSection_id() != null && !model.getSection_id().equals("")) {
                                    long endDiff = new Module(getActivity()).getDiffInLong(model.getQuiz_date(), "00:00:00");
//                                    if (endDiff > 0) {
                                   if (date.after(preThirdDate) && date.before(currDate)) {
                                        hList.add(model);
                                   }
//                                    }
                                }
                            }

                        }

                        if (hList.isEmpty()) {
                            rel_no_item.setVisibility(View.VISIBLE);
                            rv_trans.setVisibility(View.GONE);
                        }
                        tv_count.setText("" + hList.size());
                        for (int i = 0; i < hList.size(); i++) {
                            String dtstr = hList.get(i).getDate();
                            int days = module.getDateDiff(dtstr.toString());
                            hList.get(i).setDays(String.valueOf(days));
                            Log.e(TAG, "dayesssss " + days);
                        }
                        Collections.sort(hList, camp_history);
                        rv_trans.setLayoutManager(new LinearLayoutManager(getActivity()));
                        quizAdapter = new HistoryAdapter(hList, getActivity(), (view, position, model) -> {
                            switch (view.getId()) {
                                case R.id.txt_answers:
                                    mPosition = position;
                                    long endDiff = module.getTimeDiffInLong(model.getDate(), "00:00:00");
                                    if (module.getTimeDiffInbol(model.getDate()) ) {
                                        try {
                                            if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
                                                mInterstitialAd.show();
                                            } else {
                                                goToAnswerActivity(model);
                                            }

                                        } catch (Exception ex) {
                                        }
                                    }
                                    else {
                                        new ToastMsg(getActivity()).toastIconError("Result is not announced yet");
                                    }
                                    break;
                            }
                        });
                        rv_trans.setAdapter(quizAdapter);
                        quizAdapter.notifyDataSetChanged();
                    } else {
                        rel_no_item.setVisibility(View.VISIBLE);
                        rv_trans.setVisibility(View.GONE);
                    }
                } catch (Exception ex) {
                    ex.getMessage();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                loadingBar.dismiss();
                new ToastMsg(getActivity()).toastIconError("" + error.getMessage());
            }
        });
    }

    public void loadISNTAd() {
        mInterstitialAd = new InterstitialAd(getActivity());

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
            }

            @Override
            public void onAdClosed() {
                super.onAdClosed();
                Log.e(TAG + mPosition, "onAdClosed: " + hList.get(mPosition).getSection_id());
                goToAnswerActivity(hList.get(mPosition));
            }

            @Override
            public void onAdFailedToLoad(LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
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

    private void goToAnswerActivity(HistoryModel model) {
        Log.e(TAG, "goToAnswerActivity: " + model.getSection_id());
        Intent intent = new Intent(getActivity(), NewAnswerActivity.class);
        intent.putExtra("model", new Gson().toJson(model).toString());
        getActivity().startActivity(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}

