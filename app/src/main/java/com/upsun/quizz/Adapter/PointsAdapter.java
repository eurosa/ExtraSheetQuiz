package com.upsun.quizz.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.upsun.quizz.Model.PointsModel;
import com.upsun.quizz.PaymentActivity;
import com.upsun.quizz.R;

import java.util.ArrayList;

/**
 * Developed by Binplus Technologies pvt. ltd.  on 21,April,2020
 */
public class PointsAdapter extends RecyclerView.Adapter<PointsAdapter.ViewHolder> {
    Activity activity;
    ArrayList<PointsModel> list;

    public PointsAdapter(Activity activity, ArrayList<PointsModel> list) {
        this.activity = activity;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(activity).inflate(R.layout.row_points_layout,null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final PointsModel model=list.get(position);
        holder.tv_points.setText(model.getPoints());
        holder.tv_amt.setText(model.getAmount());

        switch (position%5)
        {
            case 0:
                holder.card_main.setCardBackgroundColor(activity.getResources().getColor(R.color.rc_1));
                break;
            case 1:
                holder.card_main.setCardBackgroundColor(activity.getResources().getColor(R.color.rc_2));
                break;
            case 2:
                holder.card_main.setCardBackgroundColor(activity.getResources().getColor(R.color.rc_3));
                break;
            case 3:
                holder.card_main.setCardBackgroundColor(activity.getResources().getColor(R.color.rc_4));
                break;
            case 4:
                holder.card_main.setCardBackgroundColor(activity.getResources().getColor(R.color.rc_5));
                break;
            default:
                holder.card_main.setCardBackgroundColor(activity.getResources().getColor(R.color.rc_5));
                break;
        }

        holder.card_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//payment activity
                Intent intent = new Intent(activity, PaymentActivity.class);
                intent.putExtra("amt",model.getAmount());
                intent.putExtra("points",model.getPoints());
                activity.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_points,tv_amt;
        CardView card_main;
        public ViewHolder(View view) {
            super(view);
            tv_points=(TextView)view.findViewById(R.id.tv_points);
            tv_amt=(TextView)view.findViewById(R.id.tv_amt);
            card_main=(CardView)view.findViewById(R.id.card_main);
        }
    }
}
