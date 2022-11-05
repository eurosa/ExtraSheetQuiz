package com.upsun.quizz.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.upsun.quizz.Model.QuizModel;
import com.upsun.quizz.R;
import com.upsun.quizz.admin.activities.UpdateRewardsActivity;

import java.util.ArrayList;

/**
 * Developed by Binplus Technologies pvt. ltd.  on 01,June,2020
 */
public class AdminManualRewardsAdapter extends RecyclerView.Adapter<AdminManualRewardsAdapter.ViewHolder> {
    Activity activity;
    ArrayList<QuizModel> list;

    public AdminManualRewardsAdapter(Activity activity, ArrayList<QuizModel> list) {
        this.activity = activity;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(activity).inflate(R.layout.row_quiz_rewards,null);
        ViewHolder viewHolder=new ViewHolder(view);
        return viewHolder;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final QuizModel model=list.get(position);
        final String ini = model.getTitle();
        holder.tv_sm.setText(String.valueOf(ini.charAt(0)).toUpperCase());
        holder.tv_name.setText(model.getTitle());
        holder.tv_date.setText(model.getQuiz_date());
//        holder.tv_on.setText(model.getQuiz_start_time()+" to "+model.getQuiz_end_time());
        holder.tv_mobile.setText(model.getDescription());
        holder.tv_mobile.setVisibility(View.GONE);
        switch (position%5)
        {
            case 0:
                holder.rel_bcl.getBackground().setTint(activity.getResources().getColor(R.color.rc_1));
                holder.rel_fcl.getBackground().setTint(activity.getResources().getColor(R.color.rc_1));
                break;
            case 1:
                holder.rel_bcl.getBackground().setTint(activity.getResources().getColor(R.color.rc_2));
                holder.rel_fcl.getBackground().setTint(activity.getResources().getColor(R.color.rc_2));
                break;
            case 2:
                holder.rel_bcl.getBackground().setTint(activity.getResources().getColor(R.color.rc_3));
                holder.rel_fcl.getBackground().setTint(activity.getResources().getColor(R.color.rc_3));
                break;
            case 3:
                holder.rel_bcl.getBackground().setTint(activity.getResources().getColor(R.color.rc_4));
                holder.rel_fcl.getBackground().setTint(activity.getResources().getColor(R.color.rc_4));
                break;
            case 4:
                holder.rel_bcl.getBackground().setTint(activity.getResources().getColor(R.color.rc_5));
                holder.rel_fcl.getBackground().setTint(activity.getResources().getColor(R.color.rc_5));
                break;
            default :
                holder.rel_bcl.getBackground().setTint(activity.getResources().getColor(R.color.rc_1));
                holder.rel_fcl.getBackground().setTint(activity.getResources().getColor(R.color.rc_1));
                break;
        }

        holder.lin_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(activity, UpdateRewardsActivity.class);
                intent.putExtra("quiz_id", model.getQuiz_id());
                activity.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout rel_bcl,rel_fcl;
        TextView tv_name,tv_mobile,tv_date,tv_on,tv_sm;
        LinearLayout lin_main;
        public ViewHolder(@NonNull View v) {
            super(v);
             rel_bcl=v.findViewById(R.id.rel_bcl);
            rel_fcl=v.findViewById(R.id.rel_fcl);
            tv_name=v.findViewById(R.id.tv_name);
            tv_mobile=v.findViewById(R.id.tv_mobile);
            tv_date=v.findViewById(R.id.tv_date);
            tv_on=v.findViewById(R.id.tv_on);
            tv_sm=v.findViewById(R.id.tv_sm);
            lin_main=v.findViewById(R.id.lin_main);
        }
    }
}
