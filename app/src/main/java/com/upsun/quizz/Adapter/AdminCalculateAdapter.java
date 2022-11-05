package com.upsun.quizz.Adapter;

import android.app.Activity;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.upsun.quizz.Config.Constants;
import com.upsun.quizz.Model.ManualRankModel;
import com.upsun.quizz.R;

import java.util.ArrayList;

/**
 * Developed by Binplus Technologies pvt. ltd.  on 02,June,2020
 */
public class AdminCalculateAdapter extends RecyclerView.Adapter<AdminCalculateAdapter.ViewHolder> {
    Activity activity;
    ArrayList<ManualRankModel> list;

    public AdminCalculateAdapter(Activity activity, ArrayList<ManualRankModel> list) {
        this.activity = activity;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.row_update_reward, null);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final ManualRankModel model = list.get(position);

        Log.d("TAG", "onBindViewHolder: " + model.getRewards());
        holder.tv_sm.setText(model.getRank());
        FirebaseDatabase.getInstance().getReference().child(Constants.USER_REF).child(model.getUser_id()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String rewards = snapshot.child("rewards").getValue().toString();
                Log.d("data", rewards + "  " + model.getUser_id());
                if (rewards.equals("0")&&model.getRewards().isEmpty()) {
                    holder.tv_rew.setText("2");
                } else if (model.getRewards().toString() != null && !model.getRewards().isEmpty()) {
                    holder.tv_rew.setText(model.getRewards().toString());
                } else {
                    holder.tv_rew.setText("0");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        if (holder.tv_rew.getText().toString().isEmpty()) {
            holder.tv_rew.setText("0");

        } else {
            holder.tv_rew.setText(model.getRewards());
        }
        holder.tv_rew.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                model.setRewards(String.valueOf(holder.tv_rew.getText()));
            }
        });
        holder.tv_name.setVisibility(View.VISIBLE);
        holder.tv_name.setText(model.getUser_id().toString());
        switch (position % 5) {
            case 0:
                holder.rel_bcl.getBackground().setTint(activity.getResources().getColor(R.color.rc_1));
                holder.rel_fcl.getBackground().setTint(activity.getResources().getColor(R.color.rc_1));
                break;
            case 1:
                holder.rel_bcl.getBackground().setTint(activity.getResources().getColor(R.color.rc_2));
                holder.rel_fcl.getBackground().setTint(activity.getResources().getColor(R.color.rc_2));
                break;
            case 2:
                holder.rel_bcl.getBackground().setTint(activity.getResources().getColor(R.color.rc_3));
                holder.rel_fcl.getBackground().setTint(activity.getResources().getColor(R.color.rc_3));
                break;
            case 3:
                holder.rel_bcl.getBackground().setTint(activity.getResources().getColor(R.color.rc_4));
                holder.rel_fcl.getBackground().setTint(activity.getResources().getColor(R.color.rc_4));
                break;
            case 4:
                holder.rel_bcl.getBackground().setTint(activity.getResources().getColor(R.color.rc_5));
                holder.rel_fcl.getBackground().setTint(activity.getResources().getColor(R.color.rc_5));
                break;
            default:
                holder.rel_bcl.getBackground().setTint(activity.getResources().getColor(R.color.rc_1));
                holder.rel_fcl.getBackground().setTint(activity.getResources().getColor(R.color.rc_1));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout rel_bcl, rel_fcl;
        TextView tv_sm, tv_name;
        EditText tv_rew;

        public ViewHolder(@NonNull View v) {
            super(v);
            rel_bcl = v.findViewById(R.id.rel_bcl);
            rel_fcl = v.findViewById(R.id.rel_fcl);
            tv_sm = v.findViewById(R.id.tv_sm);
            tv_rew = v.findViewById(R.id.tv_rew);
            tv_name = v.findViewById(R.id.tv_name);
        }
    }
}
