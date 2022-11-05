package com.upsun.quizz.Adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.res.ColorStateList;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.upsun.quizz.Model.JoinedQuizModel;
import com.upsun.quizz.Model.QuizModel;
import com.upsun.quizz.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {
    ArrayList<JoinedQuizModel> j_q_list;
    ArrayList<QuizModel> quiz_list;
    Activity activity;
    DatabaseReference quizRef;
    ProgressDialog loadingBar ;

    public TransactionAdapter(ArrayList<JoinedQuizModel> j_q_list, ArrayList<QuizModel> quiz_list, Activity activity)
    {
        this.j_q_list = j_q_list;
        this.quiz_list = quiz_list;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.row_w_transaction,null);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        JoinedQuizModel model = j_q_list.get(position);
        String j_id = model.getJoin_id();
        String j_d = j_id.substring(2,10);
        String j_t = j_id.substring(10,j_id.length());
        String date =j_d.substring(0,2)+":"+j_d.subSequence(2,4)+":"+j_d.subSequence(4,j_d.length());
        String time = j_t.substring(0,2)+":"+j_t.subSequence(2,4)+":"+j_t.subSequence(4,6);
        SimpleDateFormat format_t = new SimpleDateFormat("hh:mm:ss");
        SimpleDateFormat f = new SimpleDateFormat("hh:mm a");
        try {
            Date t  = format_t.parse(time);
            String tt = f.format(t);
            holder.txt_time.setText(date + "\n"+tt);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        for (int i = 0 ; i<quiz_list.size();i++)
        {
            if( model.getQuiz_id().equals(quiz_list.get(i).getQuiz_id()))
            {
                holder.txt_name.setText(quiz_list.get(i).getTitle());
                holder.txt_ini.setText(quiz_list.get(i).getTitle().subSequence(0,1));
                holder.txt_amt.setText("- "+quiz_list.get(i).getEntry_fee());
            }
//            else
//            {
//                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(1, 1);
//                holder.lyt.setLayoutParams(layoutParams);
//            }
        }

        switch (position%4)
        {
            case 0:

                holder.rel_bcl.setBackgroundTintList(ColorStateList.valueOf(activity.getResources().getColor(R.color.rc_1)));
                holder.rel_fcl.setBackgroundTintList(ColorStateList.valueOf(activity.getResources().getColor(R.color.rc_1)));
                break;
            case 1:

                holder.rel_bcl.setBackgroundTintList(ColorStateList.valueOf(activity.getResources().getColor(R.color.rc_2)));
                holder.rel_fcl.setBackgroundTintList(ColorStateList.valueOf(activity.getResources().getColor(R.color.rc_2)));
                break;
            case 2:

                holder.rel_bcl.setBackgroundTintList(ColorStateList.valueOf(activity.getResources().getColor(R.color.rc_3)));
                holder.rel_fcl.setBackgroundTintList(ColorStateList.valueOf(activity.getResources().getColor(R.color.rc_3)));
                break;
            case 3:

                holder.rel_bcl.setBackgroundTintList(ColorStateList.valueOf(activity.getResources().getColor(R.color.rc_4)));
                holder.rel_fcl.setBackgroundTintList(ColorStateList.valueOf(activity.getResources().getColor(R.color.rc_4)));
                break;

            default :

                holder.rel_bcl.getBackground().setTint(activity.getResources().getColor(R.color.rc_1));
                holder.rel_fcl.getBackground().setTint(activity.getResources().getColor(R.color.rc_1));
                break;


        }

    }

    @Override
    public int getItemCount() {
        return j_q_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txt_name, txt_time, txt_amt, txt_ini;
        RelativeLayout rel_bcl, rel_fcl, lyt;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            rel_bcl = (RelativeLayout) itemView.findViewById(R.id.rel_bcl);
            rel_fcl = (RelativeLayout) itemView.findViewById(R.id.rel_fcl);
            txt_amt = itemView.findViewById(R.id.quiz_amt);
            txt_name = itemView.findViewById(R.id.quiz_name);
            txt_ini = itemView.findViewById(R.id.tv_sm);
            txt_time = itemView.findViewById(R.id.quiz_time);
            lyt = itemView.findViewById(R.id.lyt);
            loadingBar=new ProgressDialog(activity);
            loadingBar.setMessage("Loading...");
            loadingBar.setCanceledOnTouchOutside(false);
        }
    }

    public void getQuizData(final String quiz_id , final TextView name , final TextView amt , final TextView ini) {
        quiz_list = new ArrayList<>();
//        quiz_list.clear();

//        loadingBar.show();
        quizRef = FirebaseDatabase.getInstance().getReference();
        quizRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                loadingBar.dismiss();
                if (dataSnapshot.hasChildren()) {
                    for (DataSnapshot data : dataSnapshot.child("quiz").getChildren()) {
                        QuizModel model = data.getValue(QuizModel.class);
                        try {
                            if (model.getQuiz_id().equals(quiz_id)) {
//                                quiz_list.add(model);
                                name.setText(model.getTitle());
                                ini.setText(model.getTitle().subSequence(0,1));
                                amt.setText(activity.getResources().getString(R.string.rupee)+""+model.getEntry_fee());
//                                loadingBar.dismiss();

                            }

                        } catch (Exception e) {
                            e.printStackTrace();
//                            loadingBar.dismiss();
                        }
                    }


                } else {
//                    loadingBar.dismiss();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
//                loadingBar.dismiss();
                Toast.makeText(activity, "" + databaseError.getMessage(), Toast.LENGTH_LONG).show();

            }
        });

    }
}
