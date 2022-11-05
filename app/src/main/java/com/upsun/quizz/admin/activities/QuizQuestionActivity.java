package com.upsun.quizz.admin.activities;

import static com.upsun.quizz.Config.Constants.CATEGORY_REF;
import static com.upsun.quizz.Config.Constants.QUESTION_REF;
import static com.upsun.quizz.Config.Constants.QUIZ_QUES_REF;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.upsun.quizz.Adapter.AddQuestionToQuizAdapter;
import com.upsun.quizz.Config.Module;
import com.upsun.quizz.Model.AddQuestionModel;
import com.upsun.quizz.Model.CategoriesModel;
import com.upsun.quizz.Model.QuizQuestionModel;
import com.upsun.quizz.R;
import com.upsun.quizz.networkconnectivity.NetworkConnection;
import com.upsun.quizz.utils.ToastMsg;

import java.util.ArrayList;
import java.util.HashMap;

public class QuizQuestionActivity extends AppCompatActivity {
    ImageView iv_back;
    TextView tv_title;
    Spinner spin_cat,spin_lang;
    RecyclerView rv_ques;
    RelativeLayout rel_no_items;
    ProgressDialog loadingBar;
    ToastMsg toastMsg;
    String sec_name="",sec_id="";
    public static String initCat_name="";
    Activity ctx=QuizQuestionActivity.this;
    DatabaseReference cat_ref,ques_ref;
    ArrayList<CategoriesModel> cat_list;
    ArrayList<CategoriesModel> cat_temp_list;
    ArrayList<AddQuestionModel> que_list,engList,hindiList;
    ArrayList<String> list_string,listLang;
    AddQuestionToQuizAdapter addQueAdapter;
    ArrayAdapter<String> arrayAdapter,langAdapter;
    ArrayList<QuizQuestionModel> qQList;
    String quiz_id="";
    Module module;
    Button btn_add_questions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_question);
        initViews();
        if(NetworkConnection.connectionChecking(ctx))
        {
            getAllCategories();

        }
        else
        {
            module.noConnectionActivity();
        }
        spin_cat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String cat_name=list_string.get(position).toString();
                getInitialQuestions(cat_name);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spin_lang.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(que_list.size()>0)
                {
                    Log.d("TAG", "onItemSelected: -->"+ que_list.size());
                   if(position==0)
                   {
                       if(engList.size()>0) {
                           rv_ques.setVisibility(View.VISIBLE);
                           rel_no_items.setVisibility(View.GONE);


                           addQueAdapter = new AddQuestionToQuizAdapter(ctx, engList, qQList, quiz_id,sec_id);
                           rv_ques.setAdapter(addQueAdapter);
                           addQueAdapter.notifyDataSetChanged();
                       }
                       else
                       {
                           rv_ques.setVisibility(View.GONE);
                           rel_no_items.setVisibility(View.VISIBLE);
                       }

                   }
                   else
                   {
                       if(hindiList.size()>0) {
                           rv_ques.setVisibility(View.VISIBLE);
                           rel_no_items.setVisibility(View.GONE);
                       addQueAdapter=new AddQuestionToQuizAdapter(ctx,hindiList,qQList,quiz_id,sec_id);
                       rv_ques.setAdapter(addQueAdapter);
                       addQueAdapter.notifyDataSetChanged();

                   }
                       else
                    {
                        rv_ques.setVisibility(View.GONE);
                        rel_no_items.setVisibility(View.VISIBLE);
                    }
                   }

                }
                else
                {
                    rv_ques.setVisibility(View.GONE);
                    rel_no_items.setVisibility(View.VISIBLE);
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void addQuestion(String cat_id, String ques_no, String q_id) {
        loadingBar.show();
        DatabaseReference quizQue_ref;
        String id=cat_id+"@"+ques_no;
        quizQue_ref= FirebaseDatabase.getInstance().getReference().child(QUIZ_QUES_REF);
        HashMap<String,Object> map=new HashMap<>();
        map.put("cat_id",cat_id);
        map.put("ques_no",ques_no);
        map.put("id",id);
        Log.d("TAG", "addQuestion: "+sec_id);
        quizQue_ref.child(q_id).child(sec_id).child(id).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    loadingBar.dismiss();
                    toastMsg.toastIconSuccess("Question Added succesfully.");
                }
                else
                {
                    loadingBar.dismiss();
                    toastMsg.toastIconError(""+task.getException().getMessage().toString());
                }

            }
        });

    }

    private void initViews() {
        cat_ref= FirebaseDatabase.getInstance().getReference().child(CATEGORY_REF);
        ques_ref= FirebaseDatabase.getInstance().getReference().child(QUESTION_REF);
        iv_back=(ImageView)findViewById(R.id.iv_back);
        tv_title=(TextView)findViewById(R.id.tv_title);
        tv_title.setText("Add Questions");
        spin_cat=(Spinner)findViewById(R.id.spin_cat);
        spin_lang=(Spinner)findViewById(R.id.spin_lang);
        rv_ques=(RecyclerView)findViewById(R.id.rv_ques);
        rel_no_items=(RelativeLayout)findViewById(R.id.rel_no_items);
        btn_add_questions=(Button) findViewById(R.id.btn_add_questions);
        quiz_id=getIntent().getStringExtra("quiz_id");
        sec_name = getIntent().getStringExtra("section_name");
        sec_id = getIntent().getStringExtra("section_id");
        loadingBar=new ProgressDialog(ctx);
        loadingBar.setMessage("Loading...");
        loadingBar.setCanceledOnTouchOutside(false);
        que_list=new ArrayList<>();
        module=new Module(ctx);
        qQList=new ArrayList<>();
        list_string=new ArrayList<>();
        listLang=new ArrayList<>();
        cat_list=new ArrayList<>();
        engList=new ArrayList<>();
        hindiList=new ArrayList<>();
        toastMsg=new ToastMsg(ctx);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                toastMsg.toastIconSuccess("QuizQ- "+qQList.size()+"all ques :- "+que_list.size());
                finish();
            }
        });

        btn_add_questions.setOnClickListener(v -> {
            if(que_list.size()>0) {
                for (AddQuestionModel model : que_list) {
                    addQuestion(model.getCat_id(), model.getQues_no(), quiz_id);
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    private void getAllCategories() {
        loadingBar.show();
        list_string.clear();
        cat_list.clear();
        cat_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0)
                {
                   for(DataSnapshot snapshot:dataSnapshot.getChildren())
                   {
                       CategoriesModel model=snapshot.getValue(CategoriesModel.class);
                       cat_list.add(model);
                       list_string.add(model.getCat_name().toString());
                   }
                    initCat_name=list_string.get(0).toString();
                    arrayAdapter=new ArrayAdapter<String>(ctx,android.R.layout.simple_spinner_dropdown_item,list_string);
                    spin_cat.setAdapter(arrayAdapter);

                }
                else
                {
                   toastMsg.toastIconError("No Categories Found");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
           loadingBar.dismiss();
           toastMsg.toastIconError(""+databaseError.getMessage().toString());
            }
        });
    }

    private void getInitialQuestions(String cat_name) {
        loadingBar.show();
        que_list.clear();
        engList.clear();
        hindiList.clear();
        listLang.clear();

        final String cat_id=getCatID(cat_list,cat_name);
        qQList.clear();

        DatabaseReference quizQue_ref= FirebaseDatabase.getInstance().getReference().child(QUIZ_QUES_REF);
        Query query=quizQue_ref.child(quiz_id).child(sec_id).orderByChild("cat_id").equalTo(cat_id);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Log.d("TAG", "onDataChange: child==> "+dataSnapshot.getChildrenCount());
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0)
                {
                    for(DataSnapshot snapshot:dataSnapshot.getChildren())
                    {
                        QuizQuestionModel model=snapshot.getValue(QuizQuestionModel.class);
                        qQList.add(model);

                    }
                    getListOfQuestions(qQList,cat_id);
                }
                else
                {
                    getListOfQuestions(qQList,cat_id);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                toastMsg.toastIconError(""+databaseError.getMessage().toString());
            }
        });


    }

    private void getListOfQuestions(final ArrayList<QuizQuestionModel> qQList,String cat_id) {
        ques_ref.child(cat_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0)
                {
                    rel_no_items.setVisibility(View.GONE);
                    rv_ques.setVisibility(View.VISIBLE);
                    for(DataSnapshot snapshot:dataSnapshot.getChildren())
                    {
                        AddQuestionModel model=snapshot.getValue(AddQuestionModel.class);
                        que_list.add(model);
                        if(model.getLanguage().equalsIgnoreCase("hindi"))
                        {
                            hindiList.add(model);
                        }
                        else if(model.getLanguage().equalsIgnoreCase("english"))
                        {
                            engList.add(model);
                        }
                    }
                    loadingBar.dismiss();
                    listLang.add("English");
                    listLang.add("Hindi");
                    langAdapter=new ArrayAdapter<String>(ctx,android.R.layout.simple_spinner_dropdown_item,listLang);
                    spin_lang.setAdapter(langAdapter);
//                    toastMsg.toastIconError("No "+qQList.get(0).getId().toString());
                    if(que_list.size()>0)
                    {rv_ques.setLayoutManager(new LinearLayoutManager(ctx));

                        rv_ques.setVisibility(View.VISIBLE);
                        rel_no_items.setVisibility(View.GONE);
                       }
                    else
                    {
                        rv_ques.setVisibility(View.GONE);
                        rel_no_items.setVisibility(View.VISIBLE);
                    }



                }
                else
                {
                    loadingBar.dismiss();

                    rel_no_items.setVisibility(View.VISIBLE);
                    rv_ques.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                loadingBar.dismiss();
                toastMsg.toastIconError(""+databaseError.getMessage().toString());
            }
        });
    }

    public String getCatID(ArrayList<CategoriesModel> list,String name)
    {
        String id="";
        for(int i=0;i<list.size();i++)
        {
            if(list.get(i).getCat_name().equalsIgnoreCase(name))
            {
                id=list.get(i).getCat_id().toString();
                break;
            }

        }
        return id;
    }


}
