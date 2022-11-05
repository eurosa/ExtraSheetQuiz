package com.upsun.quizz;

import static com.upsun.quizz.Config.Constants.KEY_ID;
import static com.upsun.quizz.Config.Constants.KEY_NAME;
import static com.upsun.quizz.Config.Constants.QUIZ_RESULTS_REF;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.upsun.quizz.Config.Module;
import com.upsun.quizz.Model.AddQuestionModel;
import com.upsun.quizz.Model.AnswerModel;
import com.upsun.quizz.Model.QuizQuesCategoryModel;
import com.upsun.quizz.networkconnectivity.NetworkConnection;
import com.upsun.quizz.utils.SessionManagment;
import com.upsun.quizz.utils.ToastMsg;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;


/**
 * Developed by Binplus Technologies pvt. ltd.  on 21,April,2020
 */
public class PlayQuizsActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = PlayQuizsActivity.class.getSimpleName();
    String diffCatId = "";
    LinearLayout lin_a, lin_b, lin_c, lin_d, lin_lang;
    TextView txt_ques, txt_q_no, txt_timer, txt_q_name;
    Button btn_next, btn_clear; //btn_prev
    Button btn_finish;
    ArrayList<String> selectAnsList;
    ArrayList<AnswerModel> ansList;
    ArrayList<QuizQuesCategoryModel> category_list;
    ArrayList<AddQuestionModel> question_list;
    CheckedTextView txt_a, txt_b, txt_c, txt_d;
    RadioButton r_english, r_hindi;
    ProgressDialog loadingBar;
    CountDownTimer countDownTimer;
    ImageView iv_back;
    DatabaseReference dRef;
    SessionManagment sessionManagment;
    ToastMsg toastMsg;
    Timer t;
    RelativeLayout adView;
    int numsec = 0;
    int secNum = 0;
    int qIndex = 0, c_count = 0, w_count = 0, totQuestions = 0;
    String end_time = "";
    String quiz_id, section, cat_id, ques_no, ans = "none", c_ans = "", s_lang = "english", s_ans = "", q_start, q_end, sec_id = "", qType = "", noQuestion = "";
    long diff, r_time, t_count = 1;
    Module module;
    Activity ctx = PlayQuizsActivity.this;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_quiz);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        startTimer();
        initViews();
        if (NetworkConnection.connectionChecking(this)) {
            getQuizData(quiz_id, qIndex);

        } else {
            module.noConnectionActivity();
        }
        Date c_date = new Date();
        DateFormat outputformat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

        SimpleDateFormat format24 = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat parseFormat = new SimpleDateFormat("hh:mm a");
        String current_date = parseFormat.format(c_date);

        String tm = parseFormat.format(c_date);
//        try {
//            Date s_time = parseFormat.parse(module.getAccTimeFormat(tm,getIntent().getStringExtra("start_time")));
//            Date e_time = parseFormat.parse(module.getAccTimeFormat(tm,getIntent().getStringExtra("end_time")));
//            Date c_time = parseFormat.parse(current_date);

//            q_start = format24.format(s_time);
//            q_end = format24.format(e_time);

        String qDuration = getIntent().getStringExtra("question_time");
        String duration = getIntent().getStringExtra("duration");
        qType = getIntent().getStringExtra("quiz_type");
        totQuestions = Integer.parseInt(getIntent().getStringExtra("questions"));

        Log.d(TAG, "onCreate: qType==>" + qType);

        String qd = "";
        if (qType != null) {
            if (qType.equalsIgnoreCase("Quiz")) qd = duration;
            else qd = qDuration;

            Log.d(TAG, "onCreate: qd==>" + qd);
            if (qd == null) {
            } else {
                long diff_e_s = findMillis(qd);
                Log.d("diff", String.valueOf(diff_e_s));
//
                countDownTimer = new CountDownTimer(diff_e_s, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        String text = String.format(Locale.getDefault(), " %02d : %02d : %02d ",
                                TimeUnit.MILLISECONDS.toHours(millisUntilFinished) % 60, TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) % 60,
                                TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60);
                        txt_timer.setText(text);
                        r_time = millisUntilFinished;

                    }

                    @Override
                    public void onFinish() {

                        Log.e(TAG, "onFinish: " + ansList.size() + " - " + selectAnsList.size() + " -- " + question_list.size());
//                    if(category_list.size() > 0) {
                        if (qType != null) {
                            if (qType.equalsIgnoreCase("Quiz")) {
                                int p = module.getPercentage(getAnswerTimeEndCount(ansList, selectAnsList)[0], question_list.size());
                                showAnswers(ansList, selectAnsList);
                                submitQuizResult(quiz_id, sessionManagment.getUserDetails().get(KEY_ID), String.valueOf(getAnswerTimeEndCount(ansList, selectAnsList)[0]), String.valueOf(getAnswerTimeEndCount(ansList, selectAnsList)[1]), p, t_count * 1000);
                            } else {
                                btn_next.performClick();
//                            countDownTimer.start();
                            }
                        }
//                    }else {
//                        Toast.makeText(ctx, "Please add questions in quiz", Toast.LENGTH_LONG).show();
//                    }
                    }
                }.start();
            }
        }
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }


    }

    private long findMillis(String duration) {
//        LocalTime time = LocalTime.parse(duration);
//        Long mils = ChronoUnit.MILLIS.between(LocalTime.MIDNIGHT, time);
        Log.e("duration", "findMillis: " + module.getMilliSeconds(duration));
        return module.getMilliSeconds(duration);
    }

    private void initViews() {
        lin_a = findViewById(R.id.lin_a);
        lin_b = findViewById(R.id.lin_b);
        lin_c = findViewById(R.id.lin_c);
        lin_d = findViewById(R.id.lin_d);
        lin_lang = findViewById(R.id.lin_lang);
        txt_a = findViewById(R.id.opt_a);
        txt_b = findViewById(R.id.opt_b);
        txt_c = findViewById(R.id.opt_c);
        txt_d = findViewById(R.id.opt_d);
        txt_q_no = findViewById(R.id.ques_no);
        txt_ques = findViewById(R.id.question);
        txt_timer = findViewById(R.id.quiz_timer);
        txt_q_name = findViewById(R.id.quizname);
        btn_next = findViewById(R.id.next);
        btn_clear = findViewById(R.id.btn_clear);
//        btn_prev = findViewById(R.id.prev);
        btn_finish = findViewById(R.id.btn_finish);
        r_english = findViewById(R.id.english);
        r_hindi = findViewById(R.id.hindi);
        iv_back = findViewById(R.id.iv_back);
        iv_back.setOnClickListener(this);

        adView = findViewById(R.id.adView);
//        adView.setVisibility(View.VISIBLE);
//        r_hindi.setOnCheckedChangeListener(this);
//        r_english.setOnCheckedChangeListener(this);
        loadingBar = new ProgressDialog(ctx);
        loadingBar.setMessage("Loading...");
        loadingBar.setCanceledOnTouchOutside(false);
        toastMsg = new ToastMsg(ctx);
        lin_lang.setVisibility(View.GONE);
        lin_a.setOnClickListener(this);
        lin_b.setOnClickListener(this);
        lin_c.setOnClickListener(this);
        lin_d.setOnClickListener(this);
        btn_next.setOnClickListener(this);
//        btn_prev.setOnClickListener(this);
        btn_clear.setOnClickListener(this);
        btn_finish.setOnClickListener(this);
        txt_q_name.setText(getIntent().getStringExtra("title"));
        quiz_id = getIntent().getStringExtra("quiz_id");
        String lng = getIntent().getStringExtra("lang");
        section = getIntent().getStringExtra("section");
        sec_id = getIntent().getStringExtra("section");
        Log.e("Section", sec_id);
        category_list = new ArrayList<>();
        ansList = new ArrayList<>();
        question_list = new ArrayList<>();
        selectAnsList = new ArrayList<>();

        module = new Module(ctx);
        dRef = FirebaseDatabase.getInstance().getReference();
        sessionManagment = new SessionManagment(ctx);

    }

    @Override
    public void onClick(View v) {

        int id = v.getId();
        if (id == R.id.lin_a) {
            lin_a.setBackgroundColor(this.getResources().getColor(R.color.rc_8));
            lin_b.setBackgroundColor(this.getResources().getColor(R.color.white));
            lin_c.setBackgroundColor(this.getResources().getColor(R.color.white));
            lin_d.setBackgroundColor(this.getResources().getColor(R.color.white));
        } else if (id == R.id.lin_b) {
            lin_b.setBackgroundColor(this.getResources().getColor(R.color.rc_8));
            lin_a.setBackgroundColor(this.getResources().getColor(R.color.white));
            lin_c.setBackgroundColor(this.getResources().getColor(R.color.white));
            lin_d.setBackgroundColor(this.getResources().getColor(R.color.white));
        } else if (id == R.id.lin_c) {
            lin_c.setBackgroundColor(this.getResources().getColor(R.color.rc_8));
            lin_b.setBackgroundColor(this.getResources().getColor(R.color.white));
            lin_a.setBackgroundColor(this.getResources().getColor(R.color.white));
            lin_d.setBackgroundColor(this.getResources().getColor(R.color.white));
        } else if (id == R.id.lin_d) {
            lin_d.setBackgroundColor(this.getResources().getColor(R.color.rc_8));
            lin_b.setBackgroundColor(this.getResources().getColor(R.color.white));
            lin_c.setBackgroundColor(this.getResources().getColor(R.color.white));
            lin_a.setBackgroundColor(this.getResources().getColor(R.color.white));

        } else if (id == R.id.next) {
            if (qType != null) {
                if (qType.equalsIgnoreCase("Question")) {
                    if (countDownTimer != null)
                        countDownTimer.start();
                }
            }
            String ansOption = module.getLinerSelectedAns(lin_a, lin_b, lin_c, lin_d);
            if (module.existCounter(selectAnsList, qIndex)) {
                selectAnsList.set(qIndex, ansOption);
            } else {
                selectAnsList.add(qIndex, ansOption);
            }

            if (ansOption.equals(c_ans)) {
                c_count++;
            } else {
                w_count++;
            }
//                if(ans.equalsIgnoreCase("none"))
//                {
//                    if(module.existCounter(selectAnsList,qIndex))
//                    {
//                        selectAnsList.set(qIndex,ans);
//                    }
//                    else
//                    {
//                        selectAnsList.add(qIndex,ans);
//                    }
//
//                }
//                else
//                {
//                    w_count++;
//                }
//
//            }
//            if(module.existCounter(selectAnsList,qIndex))
//            {
//                selectAnsList.set(qIndex,ans);
//            }
//            else
//            {
//                selectAnsList.add(qIndex,ans);
//            }
            qIndex = qIndex + 1;
//            if(selectAnsList.size()!=0)
//            {
//                if(selectAnsList.size()>qIndex)
//                {
//                    s_ans = selectAnsList.get(qIndex);
//                }
//
//            }
            /*if (qIndex > 0) {
                btn_prev.setVisibility(View.VISIBLE);
            } else {
                btn_prev.setVisibility(View.GONE);
            }*/
            if (qIndex == totQuestions - 1) {
                btn_next.setVisibility(View.GONE);
                btn_finish.setVisibility(View.VISIBLE);
            }

            Log.e("ques_data", "cat - " + category_list.get(qIndex).getCat_id().toString() + "\n ques - " + category_list.get(qIndex).getQues_no());
            getQuestion(category_list.get(qIndex).getCat_id(), category_list.get(qIndex).getQues_no(), qIndex, selectAnsList);
        } else if (id == R.id.prev) {


            if (qIndex > 0) {
                qIndex = qIndex - 1;

                getQuestion(category_list.get(qIndex).getCat_id(), category_list.get(qIndex).getQues_no(), qIndex, selectAnsList);

            }
            if (qIndex == 0) {
//                btn_prev.setVisibility(View.GONE);
                btn_next.setVisibility(View.VISIBLE);
                btn_finish.setVisibility(View.GONE);
            } else {
                btn_next.setVisibility(View.VISIBLE);
                btn_finish.setVisibility(View.GONE);
            }

//            toastMsg.toastIconSuccess("ans"+count);
        } else if (id == R.id.btn_clear) {
            clearViews();
//          toastMsg.toastIconSuccess(""+getAnswerCount(ansList,selectAnsList)[0]+" - "+getAnswerCount(ansList,selectAnsList)[1]);

        } else if (id == R.id.btn_finish) {
            String ansOption = module.getLinerSelectedAns(lin_a, lin_b, lin_c, lin_d);
            if (module.existCounter(selectAnsList, qIndex)) {
                selectAnsList.set(qIndex, ansOption);
            } else {
                selectAnsList.add(qIndex, ansOption);
            }

            t.cancel();
            int p = module.getPercentage(c_count, question_list.size());
            showAnswers(ansList, selectAnsList);
            submitQuizResult(quiz_id, sessionManagment.getUserDetails().get(KEY_ID), String.valueOf(getAnswerCount(ansList, selectAnsList)[0]), String.valueOf(getAnswerCount(ansList, selectAnsList)[1]), p, t_count);
            if (countDownTimer != null)
                countDownTimer.cancel();
        }
    }

    private void selectAnswerOption(ArrayList<String> selAnsList, int cnt) {

        String answer = "";
        if (!(cnt == selectAnsList.size())) {
            if (selAnsList.size() > 1) {
                answer = selectAnsList.get(cnt).toString();
            }
        }


        switch (answer) {
            case "a":
                lin_a.setBackgroundColor(this.getResources().getColor(R.color.rc_8));
                break;
            case "b":
                lin_b.setBackgroundColor(this.getResources().getColor(R.color.rc_8));
                break;
            case "c":
                lin_c.setBackgroundColor(this.getResources().getColor(R.color.rc_8));
                break;
            case "d":
                lin_d.setBackgroundColor(this.getResources().getColor(R.color.rc_8));
                break;
            case "none":
//                clearViews();
                break;
        }
//         Log.e("asdasd",""+answer+" - "+qIndex);
    }

    public void startTimer() {
        t = new Timer();
        t.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                            myTextView.setText("count="+count);
                        t_count++;
                    }
                });
            }
        }, 1000, 1000);
    }

    public void getQuizData(String quiz_id, final int qIndex) {
        loadingBar.show();
        category_list.clear();
        dRef.child("quiz_ques").child(quiz_id).child(sec_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                loadingBar.dismiss();
                if (dataSnapshot.hasChildren()) {

                    loadingBar.dismiss();
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        QuizQuesCategoryModel model = data.getValue(QuizQuesCategoryModel.class);

                        category_list.add(model);
                        Collections.shuffle(category_list);
//                        totQuestions=category_list.size();
                        ansList.add(new AnswerModel(model.getCat_id(), model.getQues_no(), "ans", model.getId()));
//                        Log.e("cat_list", data.toString() + "\n" + category_list.size());

                    }
                    for (int i = 0; i < category_list.size(); i++) {
                        Log.e("catettt", "" + category_list.get(i).getQues_no() + " - " + category_list.get(i).getCat_id());
                    }


                }
                if (!category_list.isEmpty())
                    getQuestion(category_list.get(0).getCat_id(), category_list.get(0).getQues_no(), qIndex, selectAnsList);

                if (totQuestions > 0) {

                    btn_next.setVisibility(View.VISIBLE);
                    if (btn_finish.getVisibility() == View.VISIBLE) {
                        btn_finish.setVisibility(View.GONE);
                    }
                    /*if(btn_prev.getVisibility()==View.VISIBLE)
                    {
                        btn_prev.setVisibility(View.GONE);
                    }*/
                } else {
                    btn_finish.setVisibility(View.VISIBLE);
//                    btn_next.setVisibility(View.GONE);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                loadingBar.dismiss();
                toastMsg.toastIconError(databaseError.getMessage());
            }
        });


    }

    public void getQuestion(final String cat_id, final String ques_no, final int count, final ArrayList<String> selAnsList) {
        question_list.clear();
        loadingBar.show();
        dRef.child("questions").child(cat_id).child(ques_no).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                loadingBar.dismiss();
                if (dataSnapshot.hasChildren()) {
                    clearViews();
                    AddQuestionModel questionModel = dataSnapshot.getValue(AddQuestionModel.class);
                    question_list.add(questionModel);
                    if (ansList.size() > 0) {
                        if (questionModel.getLanguage().equalsIgnoreCase("english")) {
                            ansList.get(count).setAns(questionModel.getAns().toString());
                        } else if (questionModel.getLanguage().equalsIgnoreCase("hindi")) {
                            ansList.get(count).setAns(questionModel.getHindi_ans().toString());
                        }

                    }


                } else {
                    btn_next.setVisibility(View.GONE);
                }
                txt_q_no.setText("Q.no." + String.valueOf(qIndex + 1));

                if (question_list.get(0).getLanguage().equalsIgnoreCase("english")) {
                    txt_ques.setText(question_list.get(0).getQues());
                    txt_a.setText(question_list.get(0).getOption_a());
                    txt_b.setText(question_list.get(0).getOption_b());
                    txt_c.setText(question_list.get(0).getOption_c());
                    txt_d.setText(question_list.get(0).getOption_d());
                    c_ans = question_list.get(0).getAns();
                } else if (question_list.get(0).getLanguage().equalsIgnoreCase("hindi")) {
                    txt_ques.setText(question_list.get(0).getHindi_ques());
                    txt_a.setText(question_list.get(0).getHindi_option_a());
                    txt_b.setText(question_list.get(0).getHindi_option_b());
                    txt_c.setText(question_list.get(0).getHindi_option_c());
                    txt_d.setText(question_list.get(0).getHindi_option_d());
                    c_ans = question_list.get(0).getHindi_ans();
                } else {
                    lin_lang.setVisibility(View.GONE);
                }
                if (selAnsList.size() > 0) {
                    selectAnswerOption(selectAnsList, qIndex);
                }

//                if(selAnsList.size()>0)
//                {
//                    if(qIndex==(question_list.size()-1))
//                    {
//                        clearViews();
//                    }
//                    else
//                    {
//                        if(selectAnsList.size()<=(qIndex-1)) {
//                            switch (selectAnsList.get(qIndex).toString()) {
//                                case "a":
//                                    lin_a.setBackgroundColor(ctx.getResources().getColor(R.color.rc_8));
//                                    break;
//                                case "b":
//                                    lin_b.setBackgroundColor(ctx.getResources().getColor(R.color.rc_8));
//                                    break;
//                                case "c":
//                                    lin_c.setBackgroundColor(ctx.getResources().getColor(R.color.rc_8));
//                                    break;
//                                case "d":
//                                    lin_d.setBackgroundColor(ctx.getResources().getColor(R.color.rc_8));
//                                    break;
//                                case "none":
//                                    clearViews();
//                                    break;
//                                default:
//                                    clearViews();
//                                    break;
//
//                            }
//
//                        }
//                        else
//                        {
//                            clearViews();
//                        }
//                    }
//
//                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                loadingBar.dismiss();
                toastMsg.toastIconError(databaseError.getMessage());
            }
        });
    }

    public void clearViews() {
        lin_d.setBackgroundColor(this.getResources().getColor(R.color.white));
        lin_b.setBackgroundColor(this.getResources().getColor(R.color.white));
        lin_c.setBackgroundColor(this.getResources().getColor(R.color.white));
        lin_a.setBackgroundColor(this.getResources().getColor(R.color.white));
        lin_lang.setVisibility(View.GONE);
    }

    public int[] getAnswerCount(ArrayList<AnswerModel> list, ArrayList<String> aList) {
        int correctAns = 0;
        int wrongAns = 0;
        int noAttAns = 0;


        int[] ansCount = new int[3];
        Log.e(TAG, "getAnswerCount: " + aList.size() + " :: " + list.size());
        if (list.size() != aList.size()) {
            int newListSize = list.size() - aList.size();
            for (int j = aList.size(); j < list.size(); j++) {
                aList.add("none");
            }
        }
        Log.e(TAG, "getAnswerCount23 : " + aList.size() + " :: " + list.size());
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getAns().equalsIgnoreCase(aList.get(i))) {
                correctAns++;
            } else if (aList.get(i).equalsIgnoreCase("none")) {
                noAttAns++;
            } else {
                wrongAns++;
            }
        }
        ansCount[0] = correctAns;
        ansCount[1] = wrongAns;
        ansCount[2] = noAttAns;
        Log.e(TAG, "getAnswerCount: " + correctAns + " :: " + wrongAns + " :: " + noAttAns);
        return ansCount;
    }

    public int[] getAnswerTimeEndCount(ArrayList<AnswerModel> list, ArrayList<String> aList) {
        int correctAns = 0;
        int wrongAns = 0;
        int noAttAns = 0;
        int remainListSize = list.size() - aList.size();
        int[] ansCount = new int[3];
        for (int i = 0; i < aList.size(); i++) {
            if (list.get(i).getAns().equalsIgnoreCase(aList.get(i))) {
                correctAns++;
            } else if (aList.get(i).equalsIgnoreCase("none")) {
                noAttAns++;
            } else {
                wrongAns++;
            }
        }

        for (int j = 0; j < remainListSize; j++) {
            noAttAns++;
        }
        ansCount[0] = correctAns;
        ansCount[1] = wrongAns;
        ansCount[2] = noAttAns;
        return ansCount;
    }

    public void submitQuizResult(final String quiz_id, final String user_id, final String c_ans, final String w_ans, int percent, long time) {
        loadingBar.show();
        Map<String, Object> map = new HashMap<>();
        map.put("quiz_id", quiz_id);
        map.put("user_id", user_id);
        map.put("correct_ans", c_ans);
        map.put("wrong_ans", w_ans);
//        map.put("duration",String.valueOf(module.getDuration(getIntent().getStringExtra("start_time").toString(),getIntent().getStringExtra("end_time"))));
        map.put("duration", "");
        map.put("result_id", module.getUniqueId("result"));
        map.put("percentage", String.valueOf(percent));
        map.put("time_taken", String.valueOf(time * 1000));
        map.put("tot_ques", String.valueOf(totQuestions));
        map.put("status", "complete");
        map.put("date", module.getCurrentDate());
        map.put("time", module.getCurrentTime());
        map.put("username", sessionManagment.getUserDetails().get(KEY_NAME));
        String unique_id = quiz_id + "_" + user_id + "_" + module.getCurrentDate();

        dRef.child(QUIZ_RESULTS_REF).child(unique_id).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {

            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    loadingBar.dismiss();
                    toastMsg.toastIconSuccess("Submitted Successfully");
//                    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child(USER_REF);
//                    userRef.child(new SessionManagment(PlayQuizsActivity.this).getUserDetails().get(KEY_ID)).child("quiz_dates").child(quiz_id).setValue(getcurrentDate());
                    Intent intent = new Intent(ctx, QuizEnd.class);
                    intent.putExtra("correct_ans", c_ans);
                    intent.putExtra("wrong_ans", w_ans);
                    intent.putExtra("quiz_id", quiz_id);
                    intent.putExtra("user_id", user_id);
//                    intent.putExtra("end_time",(getIntent().getStringExtra("end_time")));
                    intent.putExtra("end_time", "");
                    intent.putExtra("title", getIntent().getStringExtra("title"));
                    intent.putExtra("questions", String.valueOf(totQuestions));
                    intent.putExtra("remaining", String.valueOf(r_time));
                    intent.putExtra("section_id", sec_id);

                    startActivity(intent);
                } else {
                    loadingBar.dismiss();
                    toastMsg.toastIconError("Something went wrong");
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                toastMsg.toastIconError(e.getMessage());

            }
        });

    }

    private String getcurrentDate() {
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        return simpleDateFormat.format(date);
    }

    public void showAnswers(ArrayList<AnswerModel> list, ArrayList<String> aList) {
        for (int i = 0; i < list.size(); i++) {
            Log.e("answers", "" + list.get(i).getAns().toString());
        }
        for (int i = 0; i < aList.size(); i++) {
            Log.e("selectedAnswers", "" + aList.get(i).toString());
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setMessage("If you quit now, your quiz submitted automatically..");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                btn_finish.performClick();
                dialog.dismiss();
            }
        }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }


}
