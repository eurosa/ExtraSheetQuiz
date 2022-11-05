package com.upsun.quizz.Adapter;

import static com.upsun.quizz.Config.Constants.QUIZ_REF;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.upsun.quizz.Model.QuizModel;
import com.upsun.quizz.R;
import com.upsun.quizz.admin.activities.AddAdminRewardsActivity;
import com.upsun.quizz.admin.activities.AdminAllQuizActivity;
import com.upsun.quizz.admin.activities.EditQuizActivity;
import com.upsun.quizz.admin.activities.SectionActivity;
import com.upsun.quizz.utils.ToastMsg;

import java.util.ArrayList;

/**
 * Developed by Binplus Technologies pvt. ltd.  on 11,April,2020
 */
public class AdminQuizAdapter extends RecyclerView.Adapter<AdminQuizAdapter.ViewHolder> {
    Activity activity;
    ArrayList<QuizModel> list;
    ProgressDialog loadingBar;
    ToastMsg toastMsg;

    public AdminQuizAdapter(Activity activity, ArrayList<QuizModel> list) {
        this.activity = activity;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(activity).inflate(R.layout.row_admin_quiz,null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final QuizModel model=list.get(position);
    holder.tv_title.setText(model.getTitle());
    holder.tv_date.setText(model.getQuiz_date());
    holder.tv_start_time.setText(model.getQuiz_start_time());
    holder.tv_end_time.setText(model.getQuiz_end_time());
    holder.lin_main.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Intent intent = new Intent(activity, SectionActivity.class);
            intent.putExtra("quiz_id",model.getQuiz_id().toString());
            activity.startActivity(intent);

        }
    });
    holder.lin_section.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Intent intent = new Intent(activity, SectionActivity.class);
            intent.putExtra("quiz_id",model.getQuiz_id().toString());
            activity.startActivity(intent);

        }
    });

    holder.lin_edit.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Log.d("TAG", "onClick: edit "+model.getQuestion_time());
            Intent intent=new Intent(activity, EditQuizActivity.class);
            intent.putExtra("is_edit","true");
            intent.putExtra("quiz_id",model.getQuiz_id());
            intent.putExtra("title",model.getTitle());
            intent.putExtra("description",model.getDescription());
            intent.putExtra("entry_fee",model.getEntry_fee());
            intent.putExtra("reward_fee",model.getReward_fee());
            intent.putExtra("participents",model.getParticipents());
            intent.putExtra("questions",model.getQuestions());
            intent.putExtra("questions_time",model.getQuestion_time());
            intent.putExtra("quiz_date",model.getQuiz_date());
            intent.putExtra("quiz_end_time",model.getQuiz_end_time());
            intent.putExtra("quiz_start_time",model.getQuiz_start_time());
            intent.putExtra("quiz_duration",model.getDuration());
            intent.putExtra("quizType",model.getQuizType());
            intent.putExtra("ins",model.getInstruction());
            intent.putExtra("lang",model.getLanguage());
            intent.putExtra("cat_id",model.getCat_id());
            intent.putExtra("cat_name",model.getCat_name());
            activity.startActivity(intent);
        }
    });
    holder.lin_rewards.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Intent intent=new Intent(activity, AddAdminRewardsActivity.class);
            intent.putExtra("quiz_id",model.getQuiz_id().toString());
            intent.putExtra("participents",model.getParticipents().toString());
            activity.startActivity(intent);

        }
    });
    holder.lin_delete.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            createRemoveDialog(position,model.getQuiz_id().toString());
        }
    });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_title,tv_date,tv_start_time,tv_end_time;
        LinearLayout lin_main,lin_edit,lin_delete,lin_rewards,lin_section;

        public ViewHolder(@NonNull View v) {
            super(v);
            tv_title=(TextView)v.findViewById(R.id.tv_title);
            tv_date=(TextView)v.findViewById(R.id.tv_date);
            tv_start_time=(TextView)v.findViewById(R.id.tv_start_time);
            tv_end_time=(TextView)v.findViewById(R.id.tv_end_time);
            lin_main=(LinearLayout)v.findViewById(R.id.lin_main);
            lin_edit=(LinearLayout) v.findViewById(R.id.lin_edit);
            lin_delete=(LinearLayout) v.findViewById(R.id.lin_delete);
            lin_rewards=(LinearLayout) v.findViewById(R.id.lin_rewards);
            lin_section=(LinearLayout) v.findViewById(R.id.lin_section);
            toastMsg=new ToastMsg(activity);
            loadingBar=new ProgressDialog(activity);
            loadingBar.setMessage("Loading...");
            loadingBar.setCanceledOnTouchOutside(false);

        }
    }

    private void createRemoveDialog(final int pos, final String cId) {

        final DatabaseReference cat_ref= FirebaseDatabase.getInstance().getReference().child(QUIZ_REF);
        AlertDialog.Builder builder=new AlertDialog.Builder(activity);
        builder.setMessage(activity.getResources().getString(R.string.txt_delete_item));
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                loadingBar.show();
                cat_ref.child(cId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        loadingBar.dismiss();
                        if(task.isSuccessful())
                        {
                            list.remove(pos);
                            toastMsg.toastIconSuccess("Quiz Remove Successfully.");
                            AdminAllQuizActivity.no_of_quiz.setText("No. Of Quiz: "+list.size());
                            notifyDataSetChanged();
                            dialog.dismiss();

                        }
                        else {
                            toastMsg.toastIconError(""+task.getException().getMessage());
                        }
                    }
                });

            }
        })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }

}
