package com.upsun.quizz.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.upsun.quizz.Model.RankModel;
import com.upsun.quizz.R;

import java.util.ArrayList;

/**
 * Developed by Binplus Technologies pvt. ltd.  on 12,April,2020
 */
public class AdminRankAdapter extends RecyclerView.Adapter<AdminRankAdapter.ViewHolder> {
    Activity activity;
    ArrayList<RankModel> list;

    public AdminRankAdapter(Activity activity, ArrayList<RankModel> list) {
        this.activity = activity;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(activity).inflate(R.layout.row_admin_rewards,null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RankModel model=list.get(position);
        holder.tv_rank.setText("Rank "+model.getRank());
        holder.tv_rewards.setText(activity.getResources().getString(R.string.rupee)+model.getRewards());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_rewards,tv_rank;
        public ViewHolder(@NonNull View v) {
            super(v);
            tv_rewards=(TextView)v.findViewById(R.id.tv_rewards);
            tv_rank=(TextView)v.findViewById(R.id.tv_rank);
        }
    }
}
