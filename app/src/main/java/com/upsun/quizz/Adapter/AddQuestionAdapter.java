package com.upsun.quizz.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.upsun.quizz.R;

/**
 * Developed by Binplus Technologies pvt. ltd.  on 07,April,2020
 */
public class AddQuestionAdapter extends RecyclerView.Adapter<AddQuestionAdapter.ViewHolder> {
    Activity activity;
    int questions;

    public AddQuestionAdapter(Activity activity, int questions) {
        this.activity = activity;
        this.questions = questions;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(activity).inflate(R.layout.row_add_question,null);
        ViewHolder viewHolder=new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
     holder.ques_no.setText(""+(position+1));
    }

    @Override
    public int getItemCount() {
        return questions;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
       public TextView ques_no;
       public EditText et_ques;
       public EditText et_option_a;
       public EditText et_option_b,et_option_c,et_option_d,et_ans;

        public ViewHolder(@NonNull View v) {
            super(v);
//            et_ans=(EditText)v.findViewById(R.id.et_ans);
            et_ques=(EditText)v.findViewById(R.id.et_ques);
            et_option_a=v.findViewById(R.id.et_option_a);
            et_option_b=v.findViewById(R.id.et_option_b);
            et_option_c=v.findViewById(R.id.et_option_c);
            et_option_d=(EditText)v.findViewById(R.id.et_option_d);
            ques_no=(TextView)v.findViewById(R.id.ques_no);
        }
    }
}
