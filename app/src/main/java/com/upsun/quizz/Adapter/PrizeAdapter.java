package com.upsun.quizz.Adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.res.ColorStateList;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.upsun.quizz.Model.RewardModel;
import com.upsun.quizz.R;

import java.util.ArrayList;

public class PrizeAdapter extends RecyclerView.Adapter<PrizeAdapter.ViewHolder> {
    ArrayList<RewardModel> r_list;
    Activity activity;
    ProgressDialog loadingBar ;
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.row_rewards,null);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        RewardModel model = r_list.get(position);
        Log.e("Reward",model.getRewards());
        holder.tv_sm.setText(model.getRank());
        holder.quiz_amt.setText(model.getRewards());

        switch (position%4) {
            case 0:

                holder.rel_bcl.setBackgroundTintList(ColorStateList.valueOf(activity.getResources().getColor(R.color.rc_1)));
                holder.rel_fcl.setBackgroundTintList(ColorStateList.valueOf(activity.getResources().getColor(R.color.rc_1)));
                break;
            case 1:

                holder.rel_bcl.setBackgroundTintList(ColorStateList.valueOf(activity.getResources().getColor(R.color.rc_2)));
                holder.rel_fcl.setBackgroundTintList(ColorStateList.valueOf(activity.getResources().getColor(R.color.rc_2)));
                break;
            case 2:

                holder.rel_bcl.setBackgroundTintList(ColorStateList.valueOf(activity.getResources().getColor(R.color.rc_3)));
                holder.rel_fcl.setBackgroundTintList(ColorStateList.valueOf(activity.getResources().getColor(R.color.rc_3)));
                break;
            case 3:

                holder.rel_bcl.setBackgroundTintList(ColorStateList.valueOf(activity.getResources().getColor(R.color.rc_4)));
                holder.rel_fcl.setBackgroundTintList(ColorStateList.valueOf(activity.getResources().getColor(R.color.rc_4)));
                break;

            default:

                holder.rel_bcl.getBackground().setTint(activity.getResources().getColor(R.color.rc_1));
                holder.rel_fcl.getBackground().setTint(activity.getResources().getColor(R.color.rc_1));
                break;
        }
    }
    public PrizeAdapter(ArrayList<RewardModel> r_list, Activity activity) {
        this.r_list = r_list;
        this.activity = activity;
    }

    @Override
    public int getItemCount() {
        return r_list.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_sm , quiz_amt;
        RelativeLayout rel_bcl, rel_fcl;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_sm = itemView.findViewById(R.id.tv_sm);
            quiz_amt = itemView.findViewById(R.id.quiz_amt);
            rel_bcl = (RelativeLayout) itemView.findViewById(R.id.rel_bcl);
            rel_fcl = (RelativeLayout) itemView.findViewById(R.id.rel_fcl);
            loadingBar=new ProgressDialog(activity);
            loadingBar.setMessage("Loading...");
            loadingBar.setCanceledOnTouchOutside(false);

        }
    }

}
