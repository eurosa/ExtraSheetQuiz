package com.upsun.quizz.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.upsun.quizz.Model.ContactUsModel;
import com.upsun.quizz.R;

import java.util.ArrayList;

/**
 * Developed by Binplus Technologies pvt. ltd.  on 28,April,2020
 */
public class EnquiryAdapter extends RecyclerView.Adapter<EnquiryAdapter.ViewHolder> {
    Activity activity;
    ArrayList<ContactUsModel> list;

    public EnquiryAdapter(Activity activity, ArrayList<ContactUsModel> list) {
        this.activity = activity;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(activity).inflate(R.layout.row_instructions,null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    ContactUsModel model=list.get(position);
    holder.tv_name.setText("Name : "+model.getName()+"\nEmail : "+model.getEmail()+"\n Subject : "+model.getSubject());
     holder.tv_desc.setText(model.getMessage());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name,tv_desc;
        public ViewHolder(@NonNull View v) {
            super(v);
            tv_name=(TextView)v.findViewById(R.id.tv_name);
            tv_desc=(TextView)v.findViewById(R.id.tv_desc);
        }
    }
}
