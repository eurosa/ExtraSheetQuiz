package com.upsun.quizz.Adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.upsun.quizz.Model.QuizModel;
import com.upsun.quizz.R;
import com.upsun.quizz.admin.activities.ViewParticipentsActvity;
import com.upsun.quizz.utils.ToastMsg;

import java.util.ArrayList;

/**
 * Developed by Binplus Technologies pvt. ltd.  on 17,April,2020
 */
public class AdminQuizViewAdapter extends RecyclerView.Adapter<AdminQuizViewAdapter.ViewHolder> {
    Activity activity;
    ArrayList<QuizModel> list;
    ProgressDialog loadingBar;
    ToastMsg toastMsg;

    public AdminQuizViewAdapter(Activity activity, ArrayList<QuizModel> list) {
        this.activity = activity;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(activity).inflate(R.layout.row_admin_quiz,null);
        ViewHolder viewHolder=new ViewHolder(view);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final QuizModel model=list.get(position);
        holder.tv_title.setText(model.getTitle());
        holder.tv_date.setText(model.getQuiz_date());
        holder.tv_start_time.setText(model.getQuiz_start_time());
        holder.tv_end_time.setText(model.getQuiz_end_time());
        holder.lin_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(activity, ViewParticipentsActvity.class);
                intent.putExtra("quiz_id",model.getQuiz_id());
                intent.putExtra("title",model.getTitle());
                intent.putExtra("date",model.getQuiz_id());
                intent.putExtra("start_time",model.getQuiz_id());
                intent.putExtra("end_time",model.getQuiz_id());
                intent.putExtra("type","pcnt");
                activity.startActivity(intent);
            }
        });

        holder.lin_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(activity, ViewParticipentsActvity.class);
                intent.putExtra("quiz_id",model.getQuiz_id());
                intent.putExtra("title",model.getTitle());
                intent.putExtra("date",model.getQuiz_id());
                intent.putExtra("start_time",model.getQuiz_id());
                intent.putExtra("end_time",model.getQuiz_id());
                intent.putExtra("type","pcnt");
                activity.startActivity(intent);

            }
        });

        holder.lin_rewards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(activity, ViewParticipentsActvity.class);
                intent.putExtra("quiz_id",model.getQuiz_id());
                intent.putExtra("title",model.getTitle());
                intent.putExtra("date",model.getQuiz_id());
                intent.putExtra("start_time",model.getQuiz_id());
                intent.putExtra("end_time",model.getQuiz_id());
                intent.putExtra("type","rank");
                activity.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_title,tv_date,tv_start_time,tv_end_time,tv_edit,tv_rew,tv_delete;
        LinearLayout lin_main,lin_edit,lin_delete,lin_rewards;
        ImageView iv_edit,iv_rew,iv_delete;


        public ViewHolder(@NonNull View v) {
            super(v);
            tv_title=(TextView)v.findViewById(R.id.tv_title);
            tv_date=(TextView)v.findViewById(R.id.tv_date);
            tv_start_time=(TextView)v.findViewById(R.id.tv_start_time);
            tv_end_time=(TextView)v.findViewById(R.id.tv_end_time);
            tv_edit=(TextView)v.findViewById(R.id.tv_edit);
            tv_rew=(TextView)v.findViewById(R.id.tv_rew);
            tv_delete=(TextView)v.findViewById(R.id.tv_delete);
            iv_edit=(ImageView) v.findViewById(R.id.iv_edit);
            iv_rew=(ImageView) v.findViewById(R.id.iv_rew);
            iv_delete=(ImageView) v.findViewById(R.id.iv_delete);
            lin_main=(LinearLayout)v.findViewById(R.id.lin_main);
            lin_edit=(LinearLayout) v.findViewById(R.id.lin_edit);
            lin_delete=(LinearLayout) v.findViewById(R.id.lin_delete);
            lin_rewards=(LinearLayout) v.findViewById(R.id.lin_rewards);
            toastMsg=new ToastMsg(activity);
            loadingBar=new ProgressDialog(activity);
            loadingBar.setMessage("Loading...");
            loadingBar.setCanceledOnTouchOutside(false);
            tv_edit.setText("View Participant");
            tv_rew.setText("View Rank/Rewards");
            lin_delete.setVisibility(View.GONE);
            iv_edit.setImageDrawable(activity.getResources().getDrawable(R.drawable.icons8_user_groups_100px));
            iv_rew.setImageDrawable(activity.getResources().getDrawable(R.drawable.icons8_leaderboard_96px));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                iv_edit.getBackground().setTint(activity.getResources().getColor(R.color.green_500));
                iv_edit.getBackground().setTint(activity.getResources().getColor(R.color.green_500));
            }

        }
    }
}
