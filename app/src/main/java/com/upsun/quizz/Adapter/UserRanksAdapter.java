package com.upsun.quizz.Adapter;

import android.app.Activity;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.upsun.quizz.Model.ViewRankModel;
import com.upsun.quizz.R;

import java.util.ArrayList;

/**
 * Developed by Binplus Technologies pvt. ltd.  on 03,May,2020
 */
public class UserRanksAdapter extends RecyclerView.Adapter<UserRanksAdapter.ViewHolder> {
    Activity activity;
    ArrayList<ViewRankModel> list;

    public UserRanksAdapter(Activity activity, ArrayList<ViewRankModel> list) {
        this.activity = activity;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(activity).inflate(R.layout.row_user_ranks,null);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
   ViewRankModel model=list.get(position);
   holder.tv_sm.setText(model.getRank());
   holder.tv_rew.setText(activity.getResources().getString(R.string.rupee)+model.getRewards());
   holder.tv_rew.setEnabled(false);

        switch (position%5)
        {
            case 0:
                holder.tv_rew.setTextColor(activity.getResources().getColor(R.color.rc_1));
                holder.rel_bcl.getBackground().setTint(activity.getResources().getColor(R.color.rc_1));
                holder.rel_fcl.getBackground().setTint(activity.getResources().getColor(R.color.rc_1));
                break;
            case 1:
                holder.tv_rew.setTextColor(activity.getResources().getColor(R.color.rc_2));
                holder.rel_bcl.getBackground().setTint(activity.getResources().getColor(R.color.rc_2));
                holder.rel_fcl.getBackground().setTint(activity.getResources().getColor(R.color.rc_2));
                break;
            case 2:
                holder.tv_rew.setTextColor(activity.getResources().getColor(R.color.rc_3));
                holder.rel_bcl.getBackground().setTint(activity.getResources().getColor(R.color.rc_3));
                holder.rel_fcl.getBackground().setTint(activity.getResources().getColor(R.color.rc_3));
                break;
            case 3:
                holder.tv_rew.setTextColor(activity.getResources().getColor(R.color.rc_4));
                holder.rel_bcl.getBackground().setTint(activity.getResources().getColor(R.color.rc_4));
                holder.rel_fcl.getBackground().setTint(activity.getResources().getColor(R.color.rc_4));
                break;
            case 4:
                holder.tv_rew.setTextColor(activity.getResources().getColor(R.color.rc_5));
                holder.rel_bcl.getBackground().setTint(activity.getResources().getColor(R.color.rc_5));
                holder.rel_fcl.getBackground().setTint(activity.getResources().getColor(R.color.rc_5));
                break;
            default :
                holder.tv_rew.setTextColor(activity.getResources().getColor(R.color.rc_1));
                holder.rel_bcl.getBackground().setTint(activity.getResources().getColor(R.color.rc_1));
                holder.rel_fcl.getBackground().setTint(activity.getResources().getColor(R.color.rc_1));
                break;


        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout rel_bcl,rel_fcl;
        TextView tv_sm,tv_rew;
        public ViewHolder(@NonNull View v) {
            super(v);
            rel_bcl=(RelativeLayout)v.findViewById(R.id.rel_bcl);
            rel_fcl=(RelativeLayout)v.findViewById(R.id.rel_fcl);
            tv_sm=(TextView) v.findViewById(R.id.tv_sm);
            tv_rew=(TextView) v.findViewById(R.id.tv_rew);
        }
    }
}
