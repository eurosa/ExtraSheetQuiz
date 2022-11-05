package com.upsun.quizz.Adapter;

import android.app.Activity;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.upsun.quizz.Config.Module;
import com.upsun.quizz.Model.ManualRankModel;
import com.upsun.quizz.R;

import java.util.ArrayList;

/**
 * Developed by Binplus Technologies pvt. ltd.  on 02,June,2020
 */
public class RankAdapter extends RecyclerView.Adapter<RankAdapter.ViewHolder> {
    Activity activity;
    ArrayList<ManualRankModel> list;
    Module module;
    public RankAdapter(Activity activity, ArrayList<ManualRankModel> list) {
        this.activity = activity;
        this.list = list;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(activity).inflate(R.layout.row_user_ranks,null);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final ManualRankModel model=list.get(position);
//        Log.e("usernames", "onBindViewHolder: "+model.getUsername() );
        holder.tv_sm.setText(model.getRank());
        if(module.checkNull(model.getUser_id())){
//            holder.tv_rew.setText(module.checkNullString(model.get).toUpperCase());
//            holder.itemView.setVisibility(View.GONE);

        }else{
            holder.tv_rew.setText(module.checkNullString(model.getUsername()).toUpperCase());

        }
        holder.tv_name.setVisibility(View.VISIBLE);
        switch (position%5)
        {
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
            default :
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
        RelativeLayout rel_bcl,rel_fcl;
        TextView tv_sm,tv_name;
        TextView tv_rew;
        public ViewHolder(@NonNull View v) {
            super(v);
            rel_bcl=v.findViewById(R.id.rel_bcl);
            rel_fcl=v.findViewById(R.id.rel_fcl);
            tv_sm=v.findViewById(R.id.tv_sm);
            tv_rew=v.findViewById(R.id.tv_rew);
            tv_name=v.findViewById(R.id.tv_name);
            module=new Module(activity);
        }
    }
}
