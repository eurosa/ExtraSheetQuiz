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
import com.google.gson.Gson;
import com.upsun.quizz.Config.Constants;
import com.upsun.quizz.Config.Module;
import com.upsun.quizz.Model.HistoryModel;
import com.upsun.quizz.NewAnswerActivity;
import com.upsun.quizz.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
    private final String TAG = HistoryAdapter.class.getSimpleName();
    ArrayList<HistoryModel> hList;

    Activity activity;
    Module module;
    ProgressDialog loadingBar;
    InterstitialAd mInterstitialAd;
    String quiz_date, end_time;

    public interface OnItemClickListener {
        void onItemClick(View view, int position, HistoryModel model);
    }

    public OnItemClickListener onItemClickListener;

    public HistoryAdapter(ArrayList<HistoryModel> hList, Activity activity) {
        this.activity = activity;
        this.hList = hList;
    }

    public HistoryAdapter(ArrayList<HistoryModel> hList, Activity activity, OnItemClickListener onItemClickListener) {
        this.hList = hList;
        this.activity = activity;
        this.onItemClickListener = onItemClickListener;
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

        HistoryModel model = hList.get(position);
        holder.bind(position, model, onItemClickListener);
        end_time = model.getEnd_time();
        quiz_date = model.getQuiz_date();
        if (Boolean.parseBoolean(activity.getString(R.string.show_adds))) loadISNTAd(position);

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
//            holder.txt_date.setText(date+ "\n Start : "+model.getStart_time()+" End : "+model.getEnd_time());
//            holder.txt_date.setText(date);
            holder.txt_date.setText(model.getDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (model.getQuiz_name() != null) {
            holder.txt_name.setText(model.getQuiz_name().toString());
            holder.txt_entry.setText(activity.getResources().getString(R.string.rupee) + "" + model.getEntry_fees().toString());
        }

        if (module.checkNull(model.getAnswer()) || model.getAnswer().equals("0")) {
            holder.txt_score.setText("");
        } else {
            holder.txt_score.setText(model.getAnswer());
        }
        if (module.checkNull(model.getRank()) || model.getRank().equals("0")) {
            holder.txt_rank.setText("");
        } else {
            holder.txt_rank.setText(model.getRank());
        }

        if (module.checkNull(model.getReward()) || model.getReward().equals("0")) {
            holder.txt_rewards.setText("");
        } else {
            holder.txt_rewards.setText(model.getReward());
        }
//        if (!module.checkNull(end_time)) {
//            if (!getStatus()) {
//                holder.txt_answers.setVisibility(View.GONE);
//            }
//        }
//        holder.txt_answers.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.e(TAG, "onClick: "+model.toString()+" :: "+position );
//                try{
//                    if (mInterstitialAd.isLoaded()) {
//                        mInterstitialAd.show();
//                    } else {
//                        goToAnswerActivity(model);
//                    }
//
//                }catch (Exception ex){
////                    ex.printStackTrace();
////                    Log.e(TAG, "catchs: "+ex.getMessage() );
//                }
//
//                notifyDataSetChanged();
////                Log.e(TAG, "onClick: "+section_id +" - "+model.getQuiz_id().toString() );
//
//
//            }
//        });


    }

    private void goToAnswerActivity(HistoryModel model) {
        Log.e(TAG, "goToAnswerActivity: " + model.getSection_id());
        Intent intent = new Intent(activity, NewAnswerActivity.class);
        intent.putExtra("model", new Gson().toJson(model));
        activity.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return hList.size();
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
            module = new Module(activity);
        }

        public void bind(int position, HistoryModel historyModel, OnItemClickListener listener) {
            txt_answers.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(v, position, historyModel);
                }
            });


        }
    }

    private boolean getStatus() {

        DateFormat outputformat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Date curr = new Date();
        String current_date = outputformat.format(curr);
        SimpleDateFormat dtm_Format = new SimpleDateFormat("hh:mm a");
        String tm = dtm_Format.format(curr);
        SimpleDateFormat format24 = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat parseFormat = new SimpleDateFormat("hh:mm a");
        Date e_time = null;
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

    public void loadISNTAd(int pos) {
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
                Log.e(TAG, "onAdClosed: " + hList.get(pos).getSection_id());
                goToAnswerActivity(hList.get(pos));
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
