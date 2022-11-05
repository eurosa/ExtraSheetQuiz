package com.upsun.quizz.Adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.LoadAdError;
import com.google.firebase.database.DatabaseReference;
import com.upsun.quizz.AnswersActivity;
import com.upsun.quizz.Config.Constants;
import com.upsun.quizz.Config.Module;
import com.upsun.quizz.Model.JoinedQuizModel;
import com.upsun.quizz.Model.QuizRankRewardModel;
import com.upsun.quizz.Model.QuizResultModel;
import com.upsun.quizz.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MyQuizAdapter extends RecyclerView.Adapter<MyQuizAdapter.ViewHolder> {
    private final String TAG = MyQuizAdapter.class.getSimpleName();
    ArrayList<QuizResultModel> r_list;
    ArrayList<JoinedQuizModel> j_q_list;
    ArrayList<QuizRankRewardModel> rewardList;
    Activity activity;
    Module module;
    ProgressDialog loadingBar;
    String quiz_date, end_time;
    InterstitialAd mInterstitialAd;
    boolean adShow = false;


    DatabaseReference quizRef;


    public MyQuizAdapter(ArrayList<QuizResultModel> r_list, ArrayList<JoinedQuizModel> j_q_list, ArrayList<QuizRankRewardModel> rewardList, Activity activity) {
        this.r_list = r_list;
        this.j_q_list = j_q_list;
        this.rewardList = rewardList;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.row_my_quiz, null);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        JoinedQuizModel model = j_q_list.get(position);
        String sec_id = "";
        for (int i = 0; i < r_list.size(); i++) {
            if (r_list.get(i).getQuiz_id().equals(model.getQuiz_id().toString())) {
                sec_id = r_list.get(i).getSection();
            }
        }

        if (Boolean.parseBoolean(activity.getString(R.string.show_adds))) {
            loadISNTAd(model, sec_id);
        }

        module = new Module(activity);
        end_time = model.getQuiz_end_time();
        quiz_date = model.getQuiz_date();
        String q_id = model.getQuiz_id();
        String j_d = q_id.substring(4, 12);
        String j_t = q_id.substring(12, q_id.length());
        String date = j_d.substring(0, 2) + ":" + j_d.subSequence(2, 4) + ":" + j_d.subSequence(4, j_d.length());
        String time = j_t.substring(0, 2) + ":" + j_t.subSequence(2, 4) + ":" + j_t.subSequence(4, 6);
        SimpleDateFormat format_t = new SimpleDateFormat("hh:mm:ss");
        SimpleDateFormat f = new SimpleDateFormat("hh:mm a");
        try {
            Date t = format_t.parse(time);
            String tt = f.format(t);
//            holder.txt_date.setText(date+ "\n Start : "+model.getQuiz_start_time()+" End : "+model.getQuiz_end_time());
            holder.txt_date.setText(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (model.getTitle() != null) {
            holder.txt_name.setText(model.getTitle().toString());
            holder.txt_entry.setText(activity.getResources().getString(R.string.rupee) + "" + model.getEntry_fee().toString());

        }


        for (int i = 0; i < r_list.size(); i++) {
            if (r_list.get(i).getQuiz_id().equals(model.getQuiz_id().toString())) {
                if (r_list.get(i).getStatus().equals("complete")) {
                    holder.txt_score.setText(r_list.get(i).getCorrect_ans());
                } else if (r_list.get(i).getStatus().equals("pending") || r_list.get(i).getStatus().equals("abort")) {
                    holder.txt_score.setText(r_list.get(i).getCorrect_ans());

                } else {

                }

            }

        }
        for (int i = 0; i < rewardList.size(); i++) {
            if (rewardList.get(i).getQuiz_id().equals(model.getQuiz_id().toString())) {
                holder.txt_rank.setText(rewardList.get(i).getRank());
                holder.txt_rewards.setText(rewardList.get(i).getRewards());
            }
//            else
//            {
//                holder.txt_rank.setText("0");
//                holder.txt_rewards.setText("0");
//
//            }
        }
//        if(!module.checkNull(end_time)){
        long endDiff = module.getTimeDiffInLong(quiz_date, "00:00:00");
//            if (getStatus()) {
        if (endDiff > 0) {
            holder.txt_answers.setVisibility(View.VISIBLE);
        } else
            holder.txt_answers.setVisibility(View.GONE);

//        }


        holder.txt_answers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String section_id = "";
                for (int i = 0; i < r_list.size(); i++) {
                    if (r_list.get(i).getQuiz_id().equals(model.getQuiz_id().toString())) {
                        section_id = r_list.get(i).getSection();
                    }
                }
                try {
                    if (mInterstitialAd.isLoaded()) {
                        mInterstitialAd.show();
                    } else {
                        Log.d(TAG, "onClick: "+section_id);
                        goToAnswerActivity(model, section_id);
                    }

                } catch (Exception ex) {
//                    ex.printStackTrace();
//                    Log.e(TAG, "catchs: "+ex.getMessage() );
                }

                notifyDataSetChanged();
//                Log.e(TAG, "onClick: "+section_id +" - "+model.getQuiz_id().toString() );


            }
        });


    }

    private void goToAnswerActivity(JoinedQuizModel model, String section_id) {
        Intent intent = new Intent(activity, AnswersActivity.class);
        intent.putExtra("quiz_id", model.getQuiz_id());
        intent.putExtra("section_id", section_id);
        activity.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return j_q_list.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txt_name, txt_date, txt_score, txt_entry, txt_rewards, txt_rank, not_played, txt_answers;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_name = itemView.findViewById(R.id.quiz_name);
            txt_date = itemView.findViewById(R.id.quiz_date);
            txt_entry = itemView.findViewById(R.id.entry_fee);
            txt_score = itemView.findViewById(R.id.quiz_score);
            txt_rewards = itemView.findViewById(R.id.rewards);
            txt_rank = itemView.findViewById(R.id.rank);
            txt_answers = itemView.findViewById(R.id.txt_answers);
            not_played = itemView.findViewById(R.id.not_played);
            loadingBar = new ProgressDialog(activity);
            loadingBar.setMessage("Loading...");
            loadingBar.setCanceledOnTouchOutside(false);
        }
    }

    private boolean getStatus() {

        DateFormat outputformat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Date curr = new Date();
        String current_date = outputformat.format(curr);
        SimpleDateFormat dtm_Format = new SimpleDateFormat("hh:mm a");
        String tm = dtm_Format.format(curr);
        try {
            SimpleDateFormat inFormat = new SimpleDateFormat("HH:mm:ss");
            SimpleDateFormat outFormat = new SimpleDateFormat("hh:mm a");
            end_time = module.getAccTimeFormat(tm, end_time);
            Date date = outFormat.parse(end_time);
            String endtime = inFormat.format(date);
            String quiz_end = quiz_date + " " + endtime;
            Log.e("TIME", current_date + "   " + quiz_end);
            if (quiz_end.compareTo(current_date) < 0) {
                return true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return false;
    }

    public void loadISNTAd(JoinedQuizModel model, String section_id) {
        mInterstitialAd = new InterstitialAd(activity);

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
                goToAnswerActivity(model, section_id);
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


}
