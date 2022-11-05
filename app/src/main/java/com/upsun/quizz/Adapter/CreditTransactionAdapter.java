package com.upsun.quizz.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.upsun.quizz.Config.Module;
import com.upsun.quizz.Model.CreditTransactionModel;
import com.upsun.quizz.R;

import java.util.ArrayList;

/**
 * Developed by Binplus Technologies pvt. ltd.  on 26,April,2020
 */
public class CreditTransactionAdapter extends RecyclerView.Adapter<CreditTransactionAdapter.ViewHolder> {
    Activity activity;
    ArrayList<CreditTransactionModel> list;
    Module module;

    public CreditTransactionAdapter(Activity activity, ArrayList<CreditTransactionModel> list) {
        this.activity = activity;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(activity).inflate(R.layout.row_w_transaction,null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CreditTransactionModel model=list.get(position);

        if (model.getReferredTo() != null){
            holder.ref_to.setVisibility(View.VISIBLE);
            holder.ref_to.setText("Referred to : "+model.getReferredTo());
        }

        holder.quiz_time.setText(module.getDateFromID(model.getTxn_id(),"txn") + "\n"+module.getTimeFromID(model.getTxn_id(),"txn"));
        holder.tv_sm.setText("C");
        holder.quiz_name.setText("Points Added :");
        holder.quiz_amt.setText("+ "+model.getPoints());
        holder.quiz_amt.setTextColor(activity.getResources().getColor(R.color.green_500));
     }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_sm,quiz_name,quiz_amt,quiz_time,ref_to;
        public ViewHolder(@NonNull View v) {
            super(v);
            tv_sm=(TextView)v.findViewById(R.id.tv_sm);
            quiz_name=(TextView)v.findViewById(R.id.quiz_name);
            quiz_amt=(TextView)v.findViewById(R.id.quiz_amt);
            quiz_time=(TextView)v.findViewById(R.id.quiz_time);
            ref_to=(TextView)v.findViewById(R.id.ref_to);
            module=new Module(activity);
        }
    }
}
