package com.upsun.quizz.Adapter;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.upsun.quizz.Model.SectionQuestionModel;
import com.upsun.quizz.R;

import java.util.ArrayList;

/**
 * Developed by Binplus Technologies pvt. ltd.  on 10,April,2020
 */
public class NewAnswerAdapter extends RecyclerView.Adapter<NewAnswerAdapter.ViewHolder> {
    Activity activity;
    ArrayList<SectionQuestionModel> queslist;

    public NewAnswerAdapter(Activity activity, ArrayList<SectionQuestionModel> queslist) {
        this.activity = activity;
        this.queslist = queslist;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(activity).inflate(R.layout.row_ques_ans,null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SectionQuestionModel model = queslist.get(position);
        int quesnum = position+1;
        if (model.getLanguage()!=null) {
            if (!model.getLanguage().equals("")) {
                if (model.getLanguage().equalsIgnoreCase("english")) {
                    holder.ques.setText("Ques " + quesnum + ": " + model.getQues());
                    holder.ans1.setText(model.getOption_a());
                    holder.ans2.setText(model.getOption_b());
                    holder.ans3.setText(model.getOption_c());
                    holder.ans4.setText(model.getOption_d());
                    switch (model.getAns()) {
                        case "a":
                            setSelectedAnswer(1, holder.ans1, holder.ans2, holder.ans3, holder.ans4);
                            break;
                        case "b":
                            setSelectedAnswer(2, holder.ans1, holder.ans2, holder.ans3, holder.ans4);
                            break;
                        case "c":
                            setSelectedAnswer(3, holder.ans1, holder.ans2, holder.ans3, holder.ans4);
                            break;
                        case "d":
                            setSelectedAnswer(4, holder.ans1, holder.ans2, holder.ans3, holder.ans4);
                            break;
                    }
                } else {
                    holder.ques.setText("Ques " + quesnum + ": " + model.getHindi_ques());
                    holder.ans1.setText(model.getHindi_option_a());
                    holder.ans2.setText(model.getHindi_option_b());
                    holder.ans3.setText(model.getHindi_option_c());
                    holder.ans4.setText(model.getHindi_option_d());
                    switch (model.getHindi_ans()) {
                        case "a":
                            setSelectedAnswer(1, holder.ans1, holder.ans2, holder.ans3, holder.ans4);
                            break;
                        case "b":
                            setSelectedAnswer(2, holder.ans1, holder.ans2, holder.ans3, holder.ans4);
                            break;
                        case "c":
                            setSelectedAnswer(3, holder.ans1, holder.ans2, holder.ans3, holder.ans4);
                            break;
                        case "d":
                            setSelectedAnswer(4, holder.ans1, holder.ans2, holder.ans3, holder.ans4);
                            break;

                    }
                }
            }
        }
        else {
            holder.ques.setText("Ques " + quesnum + ": " + model.getHindi_ques());
            holder.ans1.setText(model.getHindi_option_a());
            holder.ans2.setText(model.getHindi_option_b());
            holder.ans3.setText(model.getHindi_option_c());
            holder.ans4.setText(model.getHindi_option_d());
            if (model.getHindi_ans()!=null){
                switch (model.getHindi_ans()) {
                    case "a":
                        setSelectedAnswer(1, holder.ans1, holder.ans2, holder.ans3, holder.ans4);
                        break;
                    case "b":
                        setSelectedAnswer(2, holder.ans1, holder.ans2, holder.ans3, holder.ans4);
                        break;
                    case "c":
                        setSelectedAnswer(3, holder.ans1, holder.ans2, holder.ans3, holder.ans4);
                        break;
                    case "d":
                        setSelectedAnswer(4, holder.ans1, holder.ans2, holder.ans3, holder.ans4);
                        break;

                }
            }
            else if (model.getAns()!=null){
                switch (model.getAns()) {
                    case "a":
                        setSelectedAnswer(1, holder.ans1, holder.ans2, holder.ans3, holder.ans4);
                        break;
                    case "b":
                        setSelectedAnswer(2, holder.ans1, holder.ans2, holder.ans3, holder.ans4);
                        break;
                    case "c":
                        setSelectedAnswer(3, holder.ans1, holder.ans2, holder.ans3, holder.ans4);
                        break;
                    case "d":
                        setSelectedAnswer(4, holder.ans1, holder.ans2, holder.ans3, holder.ans4);
                        break;

                }
            }

        }

    }

    @Override
    public int getItemCount() {
        return queslist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView ques,ans1,ans2,ans3,ans4;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ques=itemView.findViewById(R.id.question);
            ans1 = itemView.findViewById(R.id.ans1);
            ans2 = itemView.findViewById(R.id.ans2);
            ans3 = itemView.findViewById(R.id.ans3);
            ans4 = itemView.findViewById(R.id.ans4);
        }
    }
    public void setSelectedAnswer(int option,TextView tv1,TextView tv2,TextView tv3,TextView tv4){
        switch (option){
            case 1:
                selectedAnswer(tv1);
                unSelectedAnswer(tv2);
                unSelectedAnswer(tv3);
                unSelectedAnswer(tv4);
                break;
            case 2:
                unSelectedAnswer(tv1);
                selectedAnswer(tv2);
                unSelectedAnswer(tv3);
                unSelectedAnswer(tv4);
                break;
            case 3:
                unSelectedAnswer(tv1);
                unSelectedAnswer(tv2);
                selectedAnswer(tv3);
                unSelectedAnswer(tv4);
                break;
            case 4:
                unSelectedAnswer(tv1);
                unSelectedAnswer(tv2);
                unSelectedAnswer(tv3);
                selectedAnswer(tv4);
                break;


        }
    }
    public void unSelectedAnswer(TextView tv){
        tv.setBackgroundColor(activity.getResources().getColor(R.color.white));
        tv.setTextColor(Color.BLACK);
    }
    public void selectedAnswer(TextView tv){
        tv.setBackgroundColor(activity.getResources().getColor(R.color.tot));
        tv.setTextColor(Color.WHITE);
    }
}
