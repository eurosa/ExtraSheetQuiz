package com.upsun.quizz.admin.activities;

import static com.upsun.quizz.Config.Constants.ADD_QUESTION_REF;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.upsun.quizz.Adapter.AddQuestionAdapter;
import com.upsun.quizz.Config.Module;
import com.upsun.quizz.Model.AddQuestionModel;
import com.upsun.quizz.R;
import com.upsun.quizz.networkconnectivity.NetworkConnection;
import com.upsun.quizz.utils.ToastMsg;

import java.util.ArrayList;
import java.util.HashMap;

public class AddQuestionsActivity extends AppCompatActivity implements View.OnClickListener {
    RecyclerView rv_ques;
    ImageView iv_back;
    TextView tv_title;
    Button btn_add;
    ProgressDialog loadingBar;
    ToastMsg toastMsg;
    String quiz_id="";
    int no_of_ques=0;
    DatabaseReference add_ques_ref;
    AddQuestionAdapter adapter;
    Activity ctx=AddQuestionsActivity.this;
    ArrayList<AddQuestionModel> ques_list;
    Module module;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_questions);
        initViews();

    }

    private void initViews() {
        iv_back=(ImageView)findViewById(R.id.iv_back);
        tv_title=(TextView)findViewById(R.id.tv_title);
        btn_add=(Button) findViewById(R.id.btn_add);
        rv_ques=(RecyclerView)findViewById(R.id.rv_ques);
        quiz_id=getIntent().getStringExtra("quiz_id");
        no_of_ques=Integer.parseInt(getIntent().getStringExtra("no_of_ques"));
        ques_list=new ArrayList<>();
        loadingBar=new ProgressDialog(ctx);
        loadingBar.setMessage("Loading...");
        loadingBar.setCanceledOnTouchOutside(false);
        toastMsg=new ToastMsg(ctx);
        iv_back.setOnClickListener(this);
        btn_add.setOnClickListener(this);
        module=new Module(this);
        add_ques_ref= FirebaseDatabase.getInstance().getReference().child(ADD_QUESTION_REF);
        adapter=new AddQuestionAdapter(ctx,no_of_ques);
        rv_ques.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false ) );
        rv_ques.setAdapter(adapter);


    }

    @Override
    public void onClick(View v) {
        int id=v.getId();
        if(id == R.id.iv_back){
          finish();
        }else if(id == R.id.btn_add){
            try {
                if(NetworkConnection.connectionChecking(ctx))
                {
                    addQuestions();
                }
                else
                {
                    module.noConnectionActivity();
                }

            }
            catch (Exception ex)
            {
                Log.e("errr",""+ex.getMessage());
                toastMsg.toastIconError(""+ex.getMessage());
            }

        }

    }

    private void addQuestions() {
        for(int i=0;i<adapter.getItemCount();i++)
        {
            AddQuestionModel model=new AddQuestionModel();
            AddQuestionAdapter.ViewHolder vh= (AddQuestionAdapter.ViewHolder) rv_ques.findViewHolderForAdapterPosition(i);
            EditText et_ques=(EditText) vh.et_option_a;
            EditText et_option_a=vh.et_option_a;
            EditText et_option_b=vh.et_option_b;
            EditText et_option_c=vh.et_option_c;
            EditText et_option_d=vh.et_option_d;
            EditText et_ans=vh.et_ans;
            TextView ques_no=vh.ques_no;

//            String qs=et_ques.getText().toString();
//            String opt_a=et_option_a.getText().toString();
//            String opt_b=et_option_b.getText().toString();
//            String opt_c=et_option_c.getText().toString();
//            String opt_d=et_option_d.getText().toString();
//            String ans=et_ans.getText().toString();

//            Log.e("dddddd",qs+"\n"+opt_a+"\n"+opt_b+"\n"+opt_c+"\n"+opt_d+"\n"+ans);
//            toastMsg.toastIconSuccess(""+qs);
            model.setQues(et_ques.getText().toString());
            model.setOption_a(et_option_a.getText().toString());
            model.setOption_b(et_option_b.getText().toString());
            model.setOption_c(et_option_c.getText().toString());
            model.setOption_d(et_option_d.getText().toString());
            model.setAns(et_ans.getText().toString());
//            model.setQuiz_id(quiz_id);
            model.setAns(et_ans.getText().toString());


            if(model.getQues().isEmpty() || model.getOption_a().isEmpty() || model.getOption_b().isEmpty() || model.getOption_c().isEmpty() || model.getOption_d().isEmpty() || model.getAns().isEmpty()){
                toastMsg.toastIconError("Fill All Question details..");
            }
            else {
                ques_list.add(model);
            }
        }

        if(ques_list.size()<=0)
        {
            toastMsg.toastIconError("Fill All details..");
        }
        else
        {
            new AddQuestions(ques_list).execute();
        }

    }

    public class AddQuestions extends AsyncTask<Void,Void,Void>
    {
        ArrayList<AddQuestionModel> list=new ArrayList<>();
        public AddQuestions(ArrayList<AddQuestionModel> list) {
            this.list=list;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingBar.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            if(list.size()<=0)
            {
                toastMsg.toastIconError("Fill All details..");
            }
            else
            {
                for(int i=0; i<list.size();i++)
                {
                    AddQuestionModel model=new AddQuestionModel();
                    model=list.get(i);
                    String ques_id="QUES"+(i+1);
                    HashMap<String,Object> params=new HashMap<>();
                    //params.put("quiz_id",model.getQuiz_id());
                    params.put("ques_no",model.getQues_no());
                    params.put("ques_id",ques_id);
                    params.put("title",model.getQues());
                    params.put("opt_1",model.getOption_a());
                    params.put("opt_2",model.getOption_b());
                    params.put("opt_3",model.getOption_c());
                    params.put("opt_4",model.getOption_d());
                    params.put("answer",model.getAns());

                    add_ques_ref.child(ques_id).updateChildren(params).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                toastMsg.toastIconSuccess("Questions Add Successfully..");
                                Intent intent=new Intent(ctx,HomeActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);

                            }
                            else
                            {
                                toastMsg.toastIconError(""+task.getException().getMessage());
                            }

                        }
                    });


                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
    loadingBar.dismiss();
        }
    }
}
