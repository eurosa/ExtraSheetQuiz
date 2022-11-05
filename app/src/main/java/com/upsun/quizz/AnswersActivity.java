package com.upsun.quizz;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.upsun.quizz.Adapter.AnswerAdapter;
import com.upsun.quizz.Config.Module;
import com.upsun.quizz.Model.AddQuestionModel;
import com.upsun.quizz.Model.AnswerModel;
import com.upsun.quizz.Model.CatQuesModel;
import com.upsun.quizz.Model.QuesModel;
import com.upsun.quizz.Model.QuizQuesCategoryModel;
import com.upsun.quizz.networkconnectivity.NetworkConnection;
import com.upsun.quizz.utils.SessionManagment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AnswersActivity extends AppCompatActivity {

    ProgressDialog loadingBar;
    ImageView iv_back;
    TextView tv_title;
    private final String TAG = AnswersActivity.class.getSimpleName();
    String quiz_id;
    ArrayList<AnswerModel> ansList;
    ArrayList<QuizQuesCategoryModel> category_list;
    ArrayList<AddQuestionModel> question_list;
    DatabaseReference dRef;
    ArrayList<String> selectAnsList;
    SessionManagment sessionManagment;
    Module module;
    AnswerAdapter adapter;
    RecyclerView recyclerView;
    String section_id = "";
    ArrayList<QuesModel> qList;
    ArrayList<CatQuesModel> cList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answers);

        loadingBar = new ProgressDialog(AnswersActivity.this);
        loadingBar.setMessage("Loading...");
        loadingBar.setCanceledOnTouchOutside(false);

        quiz_id = getIntent().getStringExtra("quiz_id");
        section_id = getIntent().getStringExtra("section_id");

        category_list = new ArrayList<>();
        ansList = new ArrayList<>();
        question_list = new ArrayList<>();
        qList = new ArrayList<>();
        cList = new ArrayList<>();
        selectAnsList = new ArrayList<>();

        recyclerView = findViewById(R.id.rec_ans);
        tv_title = findViewById(R.id.tv_title);
        iv_back = findViewById(R.id.iv_back);

        tv_title.setText("Answer Keys");

        module = new Module(this);
        dRef = FirebaseDatabase.getInstance().getReference();
        sessionManagment = new SessionManagment(this);

        if (NetworkConnection.connectionChecking(this)) {
//            new DataTask(quiz_id).execute();
            allQuestions();
//           getQuizData(quiz_id,section_id);


        }
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    private void allQuestions() {
        loadingBar.show();
        qList.clear();
        cList.clear();
        dRef.child("questions").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                loadingBar.dismiss();
                for (DataSnapshot s : dataSnapshot.getChildren()) {
//                    Map<String, Object> map = (Map<String, Object>) s.getValue();
                    String key = s.getKey();
                    Log.d(TAG, "onDataChange: "+key);

                    CatQuesModel catM = new CatQuesModel();
                    catM.setCat_id(key);

                    ArrayList<QuesModel> tempList = new ArrayList<>();
                    tempList.clear();

                    for (DataSnapshot snapshot:s.child(key).getChildren()){
                        AddQuestionModel quesModel=snapshot.getValue(AddQuestionModel.class);
                        QuesModel model=new QuesModel();
                        model.setQue_id(snapshot.getKey());
                        model.setModel(quesModel);
                        tempList.add(model);
                    }
                    catM.setList(tempList);
                    cList.add(catM);

//                    for (String k : map.keySet()) {
//                        QuesModel m = new QuesModel();
//
//                        m.setQue_id(k);
//
//                        AddQuestionModel model = new AddQuestionModel();
//
//                        HashMap<String, Object> mm = (HashMap<String, Object>) map.get(k);
//                        model.setCat_id(String.valueOf(mm.get("cat_id")));
//                        model.setQues_no(String.valueOf(mm.get("ques_no")));
//                        model.setQues(String.valueOf(mm.get("ques")));
//                        model.setHindi_ques(String.valueOf(mm.get("hindi_ques")));
//                        model.setLanguage(String.valueOf(mm.get("language")));
//                        model.setOption_a(String.valueOf(mm.get("option_a")));
//                        model.setOption_b(String.valueOf(mm.get("option_b")));
//                        model.setOption_c(String.valueOf(mm.get("option_c")));
//                        model.setOption_d(String.valueOf(mm.get("option_d")));
//                        model.setAns(String.valueOf(mm.get("ans")));
//                        model.setHindi_ans(String.valueOf(mm.get("hindi_ans")));
//                        model.setHindi_option_a(String.valueOf(mm.get("hindi_option_a")));
//                        model.setHindi_option_b(String.valueOf(mm.get("hindi_option_b")));
//                        model.setHindi_option_c(String.valueOf(mm.get("hindi_option_c")));
//                        model.setHindi_option_d(String.valueOf(mm.get("hindi_option_d")));
//                        m.setModel(model);
//                        tempList.add(m);
//                    }


                }
                getQuizData(quiz_id, section_id);
//                getQuessList(cList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                loadingBar.dismiss();

                Log.e(TAG, "onqCancelled: " + databaseError.getMessage());
            }
        });

    }


    public void getQuizData(String quiz_id, String section_id) {
        category_list.clear();
        dRef.child("quiz_ques").child(quiz_id).child(section_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        QuizQuesCategoryModel model = data.getValue(QuizQuesCategoryModel.class);

                        category_list.add(model);
                    }
                    getQuessList(category_list);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Error", databaseError.toString());
            }
        });


    }

    public class DataTask extends AsyncTask<String, Void, ArrayList<AddQuestionModel>> {

        String quiz_id;

        @Override
        protected ArrayList<AddQuestionModel> doInBackground(String... strings) {
            getQuizData(quiz_id, section_id);
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loadingBar.show();
        }

        @Override
        protected void onPostExecute(ArrayList<AddQuestionModel> qList) {
            adapter = new AnswerAdapter(AnswersActivity.this, question_list);
            Log.e("qList", "onPostExecute: " + question_list.size());
            recyclerView.setLayoutManager(new GridLayoutManager(AnswersActivity.this, 1));
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            loadingBar.dismiss();
            Log.e("Done", "Yes");
        }

        public DataTask(String quiz_id) {
            this.quiz_id = quiz_id;

        }
    }

    public void getQuestion(final String cat_id, final String ques_no, final int count, final ArrayList<String> selAnsList) {
        question_list.clear();
        //loadingBar.show();
        dRef.child("questions").child(cat_id).child(ques_no).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //loadingBar.dismiss();
                if (dataSnapshot.hasChildren()) {
                    //clearViews();
                    AddQuestionModel questionModel = dataSnapshot.getValue(AddQuestionModel.class);
                    question_list.add(questionModel);
                    if (ansList.size() > 0) {
                        if (questionModel.getLanguage().equalsIgnoreCase("english")) {
                            ansList.get(count).setAns(questionModel.getAns().toString());
                        } else if (questionModel.getLanguage().equalsIgnoreCase("hindi")) {
                            ansList.get(count).setAns(questionModel.getHindi_ans().toString());
                        }

                    }


                }
                //txt_q_no.setText("Q.no." + String.valueOf(qIndex+1));

                /*if (question_list.get(0).getLanguage().equalsIgnoreCase("english"))
                {
                    /*txt_ques.setText(question_list.get(0).getQues());
                    txt_a.setText(question_list.get(0).getOption_a());
                    txt_b.setText(question_list.get(0).getOption_b());
                    txt_c.setText(question_list.get(0).getOption_c());
                    txt_d.setText(question_list.get(0).getOption_d());
                    c_ans = question_list.get(0).getAns();
                    Log.e("Ans",question_list.get(0).getAns());
                }
                else if (question_list.get(0).getLanguage().equalsIgnoreCase("hindi"))
                {
                    /*txt_ques.setText(question_list.get(0).getHindi_ques());
                    txt_a.setText(question_list.get(0).getHindi_option_a());
                    txt_b.setText(question_list.get(0).getHindi_option_b());
                    txt_c.setText(question_list.get(0).getHindi_option_c());
                    txt_d.setText(question_list.get(0).getHindi_option_d());
                    Log.e("Anshindi",question_list.get(0).getHindi_ans());
                }*/
                Log.e("Anshindi", ansList.get(count).getAns());
                Log.e("size", ansList.size() + " " + question_list.size());


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //loadingBar.dismiss();
                //toastMsg.toastIconError(databaseError.getMessage());
            }
        });
    }

    public void getAllQuestions(final String cat_id, final String ques_no, final int count, GetQuestionList getQuestionList) {
        question_list.clear();
        //loadingBar.show();
        dRef.child("questions").child(cat_id).child(ques_no).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //loadingBar.dismiss();
                if (dataSnapshot.hasChildren()) {
                    //clearViews();
                    AddQuestionModel questionModel = dataSnapshot.getValue(AddQuestionModel.class);
                    question_list.add(questionModel);
                    getQuestionList.getQuestionList(question_list);
                    if (ansList.size() > 0) {
                        if (questionModel.getLanguage().equalsIgnoreCase("english")) {
                            ansList.get(count).setAns(questionModel.getAns().toString());
                        } else if (questionModel.getLanguage().equalsIgnoreCase("hindi")) {
                            ansList.get(count).setAns(questionModel.getHindi_ans().toString());
                        }

                    }


                }
                //txt_q_no.setText("Q.no." + String.valueOf(qIndex+1));

                /*if (question_list.get(0).getLanguage().equalsIgnoreCase("english"))
                {
                    /*txt_ques.setText(question_list.get(0).getQues());
                    txt_a.setText(question_list.get(0).getOption_a());
                    txt_b.setText(question_list.get(0).getOption_b());
                    txt_c.setText(question_list.get(0).getOption_c());
                    txt_d.setText(question_list.get(0).getOption_d());
                    c_ans = question_list.get(0).getAns();
                    Log.e("Ans",question_list.get(0).getAns());
                }
                else if (question_list.get(0).getLanguage().equalsIgnoreCase("hindi"))
                {
                    /*txt_ques.setText(question_list.get(0).getHindi_ques());
                    txt_a.setText(question_list.get(0).getHindi_option_a());
                    txt_b.setText(question_list.get(0).getHindi_option_b());
                    txt_c.setText(question_list.get(0).getHindi_option_c());
                    txt_d.setText(question_list.get(0).getHindi_option_d());
                    Log.e("Anshindi",question_list.get(0).getHindi_ans());
                }*/
                Log.e("Anshindi", ansList.get(count).getAns());
                Log.e("size", ansList.size() + " " + question_list.size());


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //loadingBar.dismiss();
                //toastMsg.toastIconError(databaseError.getMessage());
            }
        });
    }


    public class GetQuestion extends AsyncTask<String, Void, ArrayList<AddQuestionModel>> {
        private ProgressDialog dialog;
        Activity activity;

        ArrayList<String> aList;
        ArrayList<QuizQuesCategoryModel> cList;

        public GetQuestion(Activity activity, ArrayList<QuizQuesCategoryModel> cList, ArrayList<String> aList) {
            this.activity = activity;
            this.cList = cList;
            this.aList = aList;

            this.dialog = new ProgressDialog(activity);
            this.dialog.setMessage("Loading...");
            if (!this.dialog.isShowing())
                this.dialog.show();

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(ArrayList<AddQuestionModel> qList) {
            super.onPostExecute(qList);

            dialog.dismiss();
            adapter = new AnswerAdapter(AnswersActivity.this, qList);
            Log.e("qList", "onPostExecute: " + qList.size());
            recyclerView.setLayoutManager(new GridLayoutManager(AnswersActivity.this, 1));
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
//                loadingBar.dismiss();
            module.showToast("Success");
//            }else{
//                module.showToast("Failed");
//            }

        }

        @Override
        protected ArrayList<AddQuestionModel> doInBackground(String... strings) {
            Log.e(TAG, "doInBackground: " + cList.size());
            ArrayList<AddQuestionModel> tempList = new ArrayList<>();
            tempList.clear();

            for (int i = 0; i < cList.size(); i++) {
//                    getQuestion(cList.get(i).getCat_id(), cList.get(i).getQues_no(), i,aList);
                getAllQuestions(cList.get(i).getCat_id(), cList.get(i).getQues_no(), i, new GetQuestionList() {
                    @Override
                    public void getQuestionList(ArrayList<AddQuestionModel> quesList) {
                        tempList.addAll(quesList);
                    }
                });
            }

            return tempList;

        }
    }

    public interface GetQuestionList {
        void getQuestionList(ArrayList<AddQuestionModel> quesList);
    }

    public void getQuessList(ArrayList<QuizQuesCategoryModel> list) {
        ArrayList<AddQuestionModel> ls = new ArrayList<>();
        for (QuizQuesCategoryModel m : list) {
            ls.add(getQuestion(m.getCat_id(), m.getQues_no()));
        }

        adapter = new AnswerAdapter(AnswersActivity.this, ls);
        recyclerView.setLayoutManager(new GridLayoutManager(AnswersActivity.this, 1));
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public AddQuestionModel getQuestion(String cId, String quesNo) {
        AddQuestionModel m = new AddQuestionModel();
        for (CatQuesModel c : cList) {
            if (c.getCat_id().equalsIgnoreCase(cId)) {
                for (int i = 0; i < c.getList().size(); i++) {
                    if (c.getList().get(i).getQue_id().equalsIgnoreCase(quesNo)) {
                        m = c.getList().get(i).getModel();
                        break;
                    }

                }
            }

        }
        Log.e(TAG, "getQuestion: " + m.getQues());
        return m;
    }

}