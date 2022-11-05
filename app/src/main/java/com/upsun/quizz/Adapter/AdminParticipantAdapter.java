package com.upsun.quizz.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.upsun.quizz.Config.Module;
import com.upsun.quizz.Model.JoinedQuizModel;
import com.upsun.quizz.Model.UserModel;
import com.upsun.quizz.R;

import java.util.ArrayList;

/**
 * Developed by Binplus Technologies pvt. ltd.  on 17,April,2020
 */
public class AdminParticipantAdapter extends RecyclerView.Adapter<AdminParticipantAdapter.ViewHolder> {
    Activity activity;
    ArrayList<JoinedQuizModel> list;
    ArrayList<UserModel> uList;
    Module module;

    public AdminParticipantAdapter(Activity activity, ArrayList<JoinedQuizModel> list, ArrayList<UserModel> uList) {
        this.activity = activity;
        this.list = list;
        this.uList = uList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(activity).inflate(R.layout.row_joined_user,null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        JoinedQuizModel model=list.get(position);
        int userIndex=getUserDetails(uList,model.getUser_id().toString());
        UserModel userModel=uList.get(userIndex);
        holder.tv_sm.setText(userModel.getName().substring(0,1).toUpperCase());
        holder.tv_name.setText(userModel.getName().toString());
        holder.tv_mobile.setText(userModel.getId().toString());
        String dt=module.getDateFromID(model.getJoin_id().toString(),"jq");
        String tm=module.getTimeFromID(model.getJoin_id().toString(),"jq");
        holder.tv_on.setText(dt+"\n"+tm);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_sm,tv_name,tv_mobile,tv_on;

        public ViewHolder(@NonNull View v) {
            super(v);
            tv_sm=(TextView)v.findViewById(R.id.tv_sm);
            tv_name=(TextView)v.findViewById(R.id.tv_name);
            tv_mobile=(TextView)v.findViewById(R.id.tv_mobile);
            tv_on=(TextView)v.findViewById(R.id.tv_on);
            module=new Module(activity);
        }
    }
    public int getUserDetails(ArrayList<UserModel> uLists,String user_id)
    {
        int inex=-1;
        for(int i=0; i<uLists.size();i++)
        {
            if(uLists.get(i).getId().equalsIgnoreCase(user_id))
            {
                inex=i;
            }
        }
        return inex;
    }
}
