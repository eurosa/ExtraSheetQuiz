package com.upsun.quizz.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.upsun.quizz.Model.InstructionModel;
import com.upsun.quizz.R;
import com.upsun.quizz.admin.activities.AddInstuctionActivity;

import java.util.ArrayList;

/**
 * Developed by Binplus Technologies pvt. ltd.  on 15,April,2020
 */
public class InstructionAdapter extends RecyclerView.Adapter<InstructionAdapter.ViewHolder> {
    Activity activity;
    ArrayList<InstructionModel> list;

    public InstructionAdapter(Activity activity, ArrayList<InstructionModel> list) {
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
        final InstructionModel model=list.get(position);
        holder.tv_name.setText(model.getTitle());
        holder.tv_desc.setText(model.getDesc());

        holder.lin_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(activity, AddInstuctionActivity.class);
                intent.putExtra("is_edit","true");
                     intent.putExtra("id",model.getId().toString());
                     intent.putExtra("title",model.getTitle().toString());
                     intent.putExtra("desc",model.getDesc().toString());
                     activity.startActivity(intent);
            }
        });




    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name,tv_desc;
        ImageView iv_delete;
        LinearLayout lin_main;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name=(TextView)itemView.findViewById(R.id.tv_name);
            tv_desc=(TextView)itemView.findViewById(R.id.tv_desc);
            iv_delete=(ImageView) itemView.findViewById(R.id.iv_delete);
            lin_main=(LinearLayout) itemView.findViewById(R.id.lin_main);

        }
    }
}
