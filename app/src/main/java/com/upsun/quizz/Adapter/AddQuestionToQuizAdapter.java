package com.upsun.quizz.Adapter;

import static com.upsun.quizz.Config.Constants.QUIZ_QUES_REF;
import static com.upsun.quizz.Config.Constants.SEC_QUES_REF;

import android.app.Activity;
import android.app.ProgressDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.upsun.quizz.Config.Module;
import com.upsun.quizz.Model.AddQuestionModel;
import com.upsun.quizz.Model.QuizQuestionModel;
import com.upsun.quizz.R;
import com.upsun.quizz.utils.ToastMsg;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Developed by Binplus Technologies pvt. ltd.  on 11,April,2020
 */
public class AddQuestionToQuizAdapter extends RecyclerView.Adapter<AddQuestionToQuizAdapter.ViewHolder> {
    private final String TAG=AddQuestionToQuizAdapter.class.getSimpleName();
    Activity activity;
    ProgressDialog loadingBar;
    ToastMsg toastMsg;
    ArrayList<AddQuestionModel> list;
    ArrayList<QuizQuestionModel> listQues;
    DatabaseReference quizQue_ref;
    String quiz_id="";
    String section_id="";
    Module module;

    public AddQuestionToQuizAdapter(Activity activity, ArrayList<AddQuestionModel> list, ArrayList<QuizQuestionModel> listQues, String quiz_id,String section_id) {
        this.activity = activity;
        this.list = list;
        this.listQues = listQues;
        this.quiz_id = quiz_id;
        this.section_id = section_id;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(activity).inflate(R.layout.row_add_question,null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        final AddQuestionModel model=list.get(position);
        String s1=model.getCat_id().toString();
        String s2=model.getQues_no().toString();

        if(getQuesExist(listQues,s2,s1))
        {
            holder.iv_delete.setVisibility(View.VISIBLE);
            holder.iv_add.setVisibility(View.GONE);
                    }
        else
        {
            holder.iv_delete.setVisibility(View.GONE);
            holder.iv_add.setVisibility(View.VISIBLE);
        }

        if(model.getLanguage().equalsIgnoreCase("hindi"))
        {
            holder.tv_ques.setText(model.getHindi_ques().toString());
        }
        else {
            holder.tv_ques.setText(model.getQues().toString());
        }

        holder.iv_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addQuestion(model.getCat_id().toString(),model.getQues_no().toString(),quiz_id,holder.iv_add,holder.iv_delete);
                addSectionQuestion(model,quiz_id,section_id);
            }
        });

        holder.iv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                removeQuestion(model.getCat_id().toString(),model.getQues_no().toString(),quiz_id,holder.iv_add,holder.iv_delete);
                removeSectionQuestion(model,section_id);
            }
        });

    }

    private void removeSectionQuestion(AddQuestionModel model, String section_id) {
        DatabaseReference secQuesRef=FirebaseDatabase.getInstance().getReference().child(SEC_QUES_REF);
        secQuesRef.child(section_id).child(model.getQues_no()).removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                Log.e(TAG, "Section Ques "+model.getQues_no()+" :: "+section_id+" removed...dd" );
            }
        });

    }

    private void addSectionQuestion(AddQuestionModel model, String quiz_id, String section_id) {
        DatabaseReference secQuesRef=FirebaseDatabase.getInstance().getReference().child(SEC_QUES_REF);
        HashMap<String,Object> map=new HashMap<>();
        map.put("cat_id",module.checkNullString(model.getCat_id()));
        map.put("option_a",module.checkNullString(model.getOption_a()));
        map.put("option_b",module.checkNullString(model.getOption_b()));
        map.put("option_c",module.checkNullString(model.getOption_c()));
        map.put("option_d",module.checkNullString(model.getOption_d()));
        map.put("ans",module.checkNullString(model.getAns()));
        map.put("ques",module.checkNullString(model.getQues()));
        map.put("hindi_option_a",module.checkNullString(model.getHindi_option_a()));
        map.put("hindi_option_b",module.checkNullString(model.getHindi_option_a()));
        map.put("hindi_option_c",module.checkNullString(model.getHindi_option_a()));
        map.put("hindi_option_d",module.checkNullString(model.getHindi_option_a()));
        map.put("language",module.checkNullString(model.getLanguage()));
        map.put("ques_no",module.checkNullString(model.getQues_no()));
        map.put("hindi_ques",module.checkNullString(model.getHindi_ques()));
        map.put("hindi_ans",module.checkNullString(model.getHindi_ans()));
        map.put("quiz_id",module.checkNullString(quiz_id));
        map.put("section_id",section_id);
        map.put("date",module.getCurrentDate());
        map.put("time",module.getCurrentTime());

        secQuesRef.child(section_id).child(model.getQues_no()).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
               if(task.isSuccessful()){
                   Log.e(TAG, "Section Question Added " );
               }else{
                   Log.e(TAG, "Error "+task.getException().getMessage() );
               }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_ques;
        ImageView iv_add,iv_delete;
        RelativeLayout rel_view;
        LinearLayout lin_main;

        public ViewHolder(@NonNull View v) {
            super(v);
            tv_ques=(TextView)v.findViewById(R.id.tv_ques);
            iv_add=(ImageView)v.findViewById(R.id.iv_add);
            iv_delete=(ImageView)v.findViewById(R.id.iv_delete);
            rel_view=(RelativeLayout)v.findViewById(R.id.rel_view);
            lin_main=(LinearLayout) v.findViewById(R.id.lin_main);
            loadingBar=new ProgressDialog(activity);
            loadingBar.setMessage("Loading...");
            loadingBar.setCanceledOnTouchOutside(false);
            toastMsg=new ToastMsg(activity);
            module=new Module(activity);
        }
    }

    private void addQuestion(String cat_id, String ques_no, String q_id, final ImageView ivAdd, final ImageView ivDelete) {
        loadingBar.show();
        String id=cat_id+"@"+ques_no;
        quizQue_ref= FirebaseDatabase.getInstance().getReference().child(QUIZ_QUES_REF);
        HashMap<String,Object> map=new HashMap<>();
        map.put("cat_id",cat_id);
        map.put("ques_no",ques_no);
        map.put("id",id);
        quizQue_ref.child(q_id).child(section_id).child(id).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    loadingBar.dismiss();
                    toastMsg.toastIconSuccess("Question Added succesfully.");
                    ivAdd.setVisibility(View.GONE);
                    ivDelete.setVisibility(View.VISIBLE);

                }
                else
                {
                    loadingBar.show();
                    toastMsg.toastIconError(""+task.getException().getMessage().toString());
                }

            }
        });

    }
    private void removeQuestion(String cat_id, String ques_no, String q_id, final ImageView ivAdd, final ImageView ivDelete) {
        loadingBar.show();
        String id=cat_id+"@"+ques_no;
        quizQue_ref= FirebaseDatabase.getInstance().getReference().child(QUIZ_QUES_REF);

        quizQue_ref.child(q_id).child(section_id).child(id).removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                    loadingBar.dismiss();
                    toastMsg.toastIconSuccess("Question Remove succesfully.");
                    ivAdd.setVisibility(View.VISIBLE);
                    ivDelete.setVisibility(View.GONE);

            }
        });

    }

    public boolean getQuesExist(ArrayList<QuizQuestionModel> list,String ques_no,String cat_id)
    {
        boolean flag=false;
        String id=cat_id+"@"+ques_no;
        for(int i=0; i<list.size();i++)
        {
            Log.e("qwe",""+id+" - "+list.get(i).getId().toString());
            String s1=list.get(i).getId().toString();
            if(s1.equalsIgnoreCase(id))
            {
                flag=true;
                break;
            }
        }
        return flag;
    }
}
