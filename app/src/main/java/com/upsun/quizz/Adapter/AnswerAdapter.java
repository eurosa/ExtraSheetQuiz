package com.upsun.quizz.Adapter;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.upsun.quizz.Model.AddQuestionModel;
import com.upsun.quizz.R;

import java.util.ArrayList;

/**
 * Developed by Binplus Technologies pvt. ltd.  on 10,April,2020
 */
public class AnswerAdapter extends RecyclerView.Adapter<AnswerAdapter.ViewHolder> {
    Activity activity;
    ArrayList<AddQuestionModel> queslist;

    public AnswerAdapter(Activity activity, ArrayList<AddQuestionModel> queslist) {
        this.activity = activity;
        this.queslist = queslist;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.row_ques_ans, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AddQuestionModel model = queslist.get(position);
        int quesnum = position + 1;
        if (model.getLanguage()!=null) {
            if (model.getLanguage().equalsIgnoreCase("english")) {
                holder.ques.setText("Ques " + quesnum + ": " + model.getQues());
                holder.ans1.setText(model.getOption_a());
                holder.ans2.setText(model.getOption_b());
                holder.ans3.setText(model.getOption_c());
                holder.ans4.setText(model.getOption_d());
                switch (model.getAns()) {
                    case "a":
                        holder.ans1.setBackgroundColor(activity.getResources().getColor(R.color.tot));
                        holder.ans1.setTextColor(Color.WHITE);
                        break;
                    case "b":
                        holder.ans2.setBackgroundColor(activity.getResources().getColor(R.color.tot));
                        holder.ans2.setTextColor(Color.WHITE);
                        break;
                    case "c":
                        holder.ans3.setBackgroundColor(activity.getResources().getColor(R.color.tot));
                        holder.ans3.setTextColor(Color.WHITE);
                        break;
                    case "d":
                        holder.ans4.setBackgroundColor(activity.getResources().getColor(R.color.tot));
                        holder.ans4.setTextColor(Color.WHITE);
                        break;
                    default:
                        holder.ans1.setBackgroundColor(activity.getResources().getColor(R.color.white));
                        holder.ans1.setTextColor(Color.BLACK);
                }
            } else {
                holder.ques.setText("Ques " + quesnum + ": " + model.getHindi_ques());
                holder.ans1.setText(model.getHindi_option_a());
                holder.ans2.setText(model.getHindi_option_b());
                holder.ans3.setText(model.getHindi_option_c());
                holder.ans4.setText(model.getHindi_option_d());
                switch (model.getHindi_ans()) {
                    case "a":
                        holder.ans1.setBackgroundColor(activity.getResources().getColor(R.color.tot));
                        holder.ans1.setTextColor(Color.WHITE);
                        break;
                    case "b":
                        holder.ans2.setBackgroundColor(activity.getResources().getColor(R.color.tot));
                        holder.ans2.setTextColor(Color.WHITE);
                        break;
                    case "c":
                        holder.ans3.setBackgroundColor(activity.getResources().getColor(R.color.tot));
                        holder.ans3.setTextColor(Color.WHITE);
                        break;
                    case "d":
                        holder.ans4.setBackgroundColor(activity.getResources().getColor(R.color.tot));
                        holder.ans4.setTextColor(Color.WHITE);
                        break;
                    default:
                        holder.ans1.setBackgroundColor(activity.getResources().getColor(R.color.white));
                        holder.ans1.setTextColor(Color.BLACK);
                }
            }
        }


    }

    @Override
    public int getItemCount() {
        return queslist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView ques, ans1, ans2, ans3, ans4;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ques = itemView.findViewById(R.id.question);
            ans1 = itemView.findViewById(R.id.ans1);
            ans2 = itemView.findViewById(R.id.ans2);
            ans3 = itemView.findViewById(R.id.ans3);
            ans4 = itemView.findViewById(R.id.ans4);
        }
    }
}
