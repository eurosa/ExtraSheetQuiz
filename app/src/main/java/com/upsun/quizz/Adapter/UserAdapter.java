package com.upsun.quizz.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.upsun.quizz.Model.UserModel;
import com.upsun.quizz.R;
import com.upsun.quizz.admin.activities.UserDetailsActivity;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Developed by Binplus Technologies pvt. ltd.  on 10,April,2020
 */
public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    Activity activity;
    ArrayList<UserModel> list;

    public UserAdapter(Activity activity, ArrayList<UserModel> list) {
        this.activity = activity;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.row_users, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final UserModel model = list.get(position);
        Gson g = new Gson();
//        Log.d("787028list",g.toJson(list));

        String img_url = model.getImg_url();
        if (false) {
            Log.d("787028if", "in");
            Glide.with(activity)
                    .load(img_url)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .centerCrop()
                    .placeholder(R.drawable.logo)
                    .into(holder.iv_profile);
        } else {
            Log.d("787028else", "in");
//            holder.iv_profile.setImageResource(R.drawable.icons8_user_100px_1);

        }
        holder.tv_name.setText(model.getName());
        holder.tv_number.setText(model.getId());
//     if(model.getStatus().equalsIgnoreCase("1"))
//     {
//         holder.card.setCardBackgroundColor(activity.getResources().getColor(R.color.red_trans));
//     }
        holder.lin_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, UserDetailsActivity.class);
                intent.putExtra("id", model.getId().toString());
                intent.putExtra("name", model.getName().toString());
                intent.putExtra("email", model.getEmail().toString());
                intent.putExtra("status", model.getStatus().toString());
                intent.putExtra("img_url", model.getImg_url().toString());
                activity.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView iv_profile;
        CardView card;
        TextView tv_name, tv_number;
        LinearLayout lin_main;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_profile = (CircleImageView) itemView.findViewById(R.id.iv_profile_new);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name_new);
            tv_number = (TextView) itemView.findViewById(R.id.tv_number);
            lin_main = (LinearLayout) itemView.findViewById(R.id.lin_main_new);
            card = (CardView) itemView.findViewById(R.id.card_new);
        }
    }
}
