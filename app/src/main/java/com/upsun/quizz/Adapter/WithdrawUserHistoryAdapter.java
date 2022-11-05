package com.upsun.quizz.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.upsun.quizz.Config.Module;
import com.upsun.quizz.Model.WithdrawModel;
import com.upsun.quizz.R;

import java.util.ArrayList;

/**
 * Developed by Binplus Technologies pvt. ltd.  on 26,April,2020
 */
public class WithdrawUserHistoryAdapter extends RecyclerView.Adapter<WithdrawUserHistoryAdapter.ViewHolder> {
    Activity activity;
    ArrayList<WithdrawModel> list;
    Module module;

    public WithdrawUserHistoryAdapter(Activity activity, ArrayList<WithdrawModel> list) {
        this.activity = activity;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(activity).inflate(R.layout.row_wthdrw_requests,null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    WithdrawModel model=list.get(position);
    holder.tv_title.setText(module.getDateFromID(model.getId(),"wr"));
    holder.tv_time.setText(module.getTimeFromID(model.getId(),"wr"));
    holder.lin_approve.setVisibility(View.GONE);
    holder.vw.setVisibility(View.GONE);
    if(model.getType().equalsIgnoreCase("upi"))
    {
        holder.txt_type.setText("UPI :");
        holder.tv_type.setText(model.getUpi());

    }
    else
    {
        holder.txt_type.setText("Bank Acc No :");
        holder.tv_type.setText(model.getAcc_no());
    }
    holder.tv_amt.setText(activity.getResources().getString(R.string.rupee)+" "+model.getRequest_amount());
        holder.tv_status.setText(model.getStatus().toUpperCase());

        if(model.getStatus().equalsIgnoreCase("pending") || model.getStatus().equalsIgnoreCase("success"))
    {
        holder.tv_status.setTextColor(activity.getResources().getColor(R.color.green_500));

    }
    else
    {
        holder.tv_status.setTextColor(activity.getResources().getColor(R.color.red_600));

    }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_title,tv_time,tv_amt,txt_type,tv_type,tv_status;
        LinearLayout lin_approve;
        View vw;
        public ViewHolder(@NonNull View v) {
            super(v);

            tv_title=(TextView)v.findViewById(R.id.tv_title);
            tv_time=(TextView)v.findViewById(R.id.tv_time);
            tv_amt=(TextView)v.findViewById(R.id.tv_amt);
            txt_type=(TextView)v.findViewById(R.id.txt_type);
            tv_type=(TextView)v.findViewById(R.id.tv_type);
            tv_status=(TextView)v.findViewById(R.id.tv_status);
            lin_approve=(LinearLayout) v.findViewById(R.id.lin_approve);
            vw=(View) v.findViewById(R.id.vw);
            module=new Module(activity);

        }
    }
}
