package com.upsun.quizz.Adapter;

import static com.upsun.quizz.Config.Constants.QUESTION_REF;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.upsun.quizz.Model.AddQuestionModel;
import com.upsun.quizz.R;
import com.upsun.quizz.admin.activities.AllQuestionActivity;
import com.upsun.quizz.admin.fragments.AddQuestionFragment;
import com.upsun.quizz.admin.fragments.ViewAllQuestionFragment;
import com.upsun.quizz.utils.ToastMsg;

import java.util.ArrayList;

/**
 * Developed by Binplus Technologies pvt. ltd.  on 08,April,2020
 */
public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.ViewHolder> {
    Activity activity;
    ProgressDialog loadingBar;
    ArrayList<AddQuestionModel> list;
    DatabaseReference ques_ref;
    ToastMsg toastMsg;

    public QuestionAdapter(Activity activity, ArrayList<AddQuestionModel> list) {
        this.activity = activity;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(activity).inflate(R.layout.row_all_question,null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
    final AddQuestionModel model=list.get(position);
//    if (model.getLanguage().equalsIgnoreCase("english"))
//    {
//        holder.tv_ques.setText(model.getQues());
//    }
//    else
    if (model.getLanguage().equalsIgnoreCase("hindi"))
    {
        holder.tv_ques.setText(model.getHindi_ques());
    }
    else
    {
        holder.tv_ques.setText(model.getQues());
    }
//    else if (model.getLanguage().equalsIgnoreCase("english,hindi"))
//    {
//        holder.tv_ques.setText(model.getQues()+"\n"+model.getHindi_ques());
//    }
    holder.iv_delete.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            createRemoveDialog(model.getQues_no(),position,model.getCat_id());

        }
    });
    holder.lin_main.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
//
            Bundle bundle = new Bundle();
            bundle.putString("cat_id",model.getCat_id());
            bundle.putString("question",model.getQues());
            bundle.putString("question_id",model.getQues_no());
            bundle.putString("option_a",model.getOption_a());
            bundle.putString("option_b",model.getOption_b());
            bundle.putString("option_c",model.getOption_c());
            bundle.putString("option_d",model.getOption_d());
            bundle.putString("answer",model.getAns());
            bundle.putString("h_question",model.getHindi_ques());
            bundle.putString("h_option_a",model.getHindi_option_a());
            bundle.putString("h_option_b",model.getHindi_option_b());
            bundle.putString("h_option_c",model.getHindi_option_c());
            bundle.putString("h_option_d",model.getHindi_option_d());
            bundle.putString("h_answer",model.getHindi_ans());
            bundle.putString("child_count", String.valueOf(position));
            bundle.putString("language",model.getLanguage());

            bundle.putString("is_edit","true");
            AddQuestionFragment f_add = new AddQuestionFragment();
            f_add.setArguments(bundle);
            ((AllQuestionActivity)v.getContext()).getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container_ques, f_add)
                    .addToBackStack(null)
                    .commit();


        }
    });

    }



    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_ques;
        LinearLayout lin_main;
        ImageView iv_delete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_ques=(TextView)itemView.findViewById(R.id.tv_ques);
            iv_delete=(ImageView) itemView.findViewById(R.id.iv_delete);
            lin_main=(LinearLayout) itemView.findViewById(R.id.lin_main);
            loadingBar=new ProgressDialog(activity);
            loadingBar.setMessage("Loading...");
            loadingBar.setCanceledOnTouchOutside(false);
            toastMsg=new ToastMsg(activity);

        }
    }
    private void createRemoveDialog(final String ques_no, final int pos, final String cId) {

        ques_ref= FirebaseDatabase.getInstance().getReference().child(QUESTION_REF);
        AlertDialog.Builder builder=new AlertDialog.Builder(activity);
        builder.setMessage(activity.getResources().getString(R.string.txt_delete_item));
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                loadingBar.show();
                ques_ref.child(cId).child(ques_no).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        loadingBar.dismiss();
                        if(task.isSuccessful())
                        {
                            list.remove(pos);
                            ViewAllQuestionFragment.no_of_ques.setText("No of Questions: "+list.size());
                            toastMsg.toastIconSuccess("Question Remove Successfully.");
                             notifyDataSetChanged();
                             dialog.dismiss();

                        }
                        else {
                            toastMsg.toastIconError(""+task.getException().getMessage());
                        }
                    }
                });

              }
        })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                      dialog.dismiss();
                    }
                });
       builder.create().show();
    }
}
