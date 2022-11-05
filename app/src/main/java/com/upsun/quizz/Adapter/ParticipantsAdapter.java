package com.upsun.quizz.Adapter;

import static com.upsun.quizz.Config.Constants.USER_REF;

import android.app.Activity;
import android.app.ProgressDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.upsun.quizz.Model.JoinedQuizModel;
import com.upsun.quizz.Model.UserModel;
import com.upsun.quizz.R;

import java.util.ArrayList;

public class ParticipantsAdapter extends RecyclerView.Adapter<ParticipantsAdapter.ViewHolder> {
    ArrayList<JoinedQuizModel>p_list ;
    ArrayList<UserModel>u_list ;
    Activity activity;
    ProgressDialog loadingBar ;

    public ParticipantsAdapter(ArrayList<JoinedQuizModel> p_list, Activity activity ,ProgressDialog loadingBar) {
        this.p_list = p_list;
        this.loadingBar = loadingBar;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      View view = LayoutInflater.from(activity).inflate(R.layout.participants_layout,null);
      ViewHolder holder = new ViewHolder(view);
      return  holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        JoinedQuizModel model = p_list.get(position);
        int count = position+1;

        getUserInfo(model.getUser_id(),holder.txt_p_name ,count);
//        holder.txt_p_name.setText(String.valueOf(count)+""+model.getUser_id() + String.valueOf(u_list.size()));


    }

    @Override
    public int getItemCount() {
        return p_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txt_p_name;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
         txt_p_name = itemView.findViewById(R.id.txt_participants);

        }
    }

    private void getUserInfo(final String user_id , final TextView txt_name , final int count ) {
        u_list = new ArrayList<>();
        u_list.clear();
      DatabaseReference  user_ref= FirebaseDatabase.getInstance().getReference().child(USER_REF);

        Query query = user_ref.orderByChild("user_id");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.e("user",dataSnapshot.toString());
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0)
                {
                    for(DataSnapshot snapshot:dataSnapshot.getChildren())
                    {
                        if (snapshot.getKey().equals(user_id)) {
                            UserModel model = snapshot.getValue(UserModel.class);
                            u_list.add(model);
                            Log.e("u_snap", "true");
                            txt_name.setText(String.valueOf(count)+".\t \t"+u_list.get(0).getName());

                        }
                        else
                        { Log.e("u_snap", "false");}
                    }
                    Log.e("u_list", String.valueOf(u_list.size()));

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
//
    }
}
