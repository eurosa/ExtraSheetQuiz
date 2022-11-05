package com.upsun.quizz;

import static com.upsun.quizz.Config.Constants.KEY_ID;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
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
import com.upsun.quizz.Model.QuizQuesCategoryModel;
import com.upsun.quizz.utils.SessionManagment;
import com.upsun.quizz.utils.ToastMsg;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class PlayQuizActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    LinearLayout lin_a, lin_b, lin_c, lin_d ,lin_lang;
    TextView txt_ques, txt_q_no, txt_timer, txt_q_name;
    Button btn_next, btn_prev,btn_clear;
    Button btn_finish;
    CheckedTextView txt_a, txt_b, txt_c, txt_d;
    RadioButton r_english ,r_hindi;
    ProgressDialog loadingBar;
    CountDownTimer countDownTimer ;
    ImageView iv_back ;
    String quiz_id, cat_id, ques_no, ans = "" ,c_ans ="" ,s_lang ="english" ,s_ans="" ,q_start ,q_end ,c_time;
    int tot_q, count = 0, c_count = 0, w_count = 0 ,na_count=0;
    ArrayList<QuizQuesCategoryModel> category_list;
    ArrayList<AddQuestionModel> question_list;
    ArrayList<String>s_ans_list;
    DatabaseReference dRef;
    SessionManagment sessionManagment;
    ToastMsg toastMsg;
    Timer t ;
    long diff , r_time ,t_count=1 ;
    Module module;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_quiz);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        startTimer();
        initView();
        getQuizData(quiz_id, count);
        Date c_date=new Date();
        SimpleDateFormat format24 = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat parseFormat = new SimpleDateFormat("hh:mm a");
        String tm=parseFormat.format(c_date);
        try {
            Date s_time = parseFormat.parse(module.getAccTimeFormat(tm,getIntent().getStringExtra("start_time")));
            Date e_time = parseFormat.parse(module.getAccTimeFormat(tm,getIntent().getStringExtra("end_time")));
            q_start = format24.format(s_time);
            q_end = format24.format(e_time);
            long diff_e_s= findMillis(getIntent().getStringExtra("duration"));
            Log.d("diff", String.valueOf(diff_e_s));
//
            countDownTimer= new CountDownTimer(diff_e_s, 1000)
            {
                @Override
                public void onTick(long millisUntilFinished) {
                    String text = String.format(Locale.getDefault(), " %02d : %02d : %02d ",

                            TimeUnit.MILLISECONDS.toHours(millisUntilFinished)%60,TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) % 60,
                            TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60);
                    txt_timer.setText(text);
                    r_time = millisUntilFinished;

                }

                @Override
                public void onFinish() {
//                    finishQuiz();
                    int p = getPercentage(c_count,tot_q);
//                    Log.e("timer count",String.valueOf(t_count) + "\n w_count"+String.valueOf(w_count)+"\n c_count"+String.valueOf(c_count) +"\npercent "+String.valueOf(p));
                    submitQuizResult(quiz_id,sessionManagment.getUserDetails().get(KEY_ID),String.valueOf(c_count),String.valueOf(w_count),p,t_count*1000);
                }
            }.start();
//            Log.e("diff",String.valueOf(diff));

//            toastMsg.toastIconSuccess("stime"+s_time.toString()+"\n e_time"+e_time.toString());

        } catch (ParseException e) {
            e.printStackTrace();
        }



    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private long findMillis(String duration) {
        LocalTime time = LocalTime.parse(duration);
        Long mils = ChronoUnit.MILLIS.between(LocalTime.MIDNIGHT, time);
        return mils;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public void initView() {
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
        btn_prev = findViewById(R.id.prev);
        btn_finish = findViewById(R.id.btn_finish);
        r_english = findViewById(R.id.english);
        r_hindi = findViewById(R.id.hindi);
        iv_back = findViewById(R.id.iv_back);
        iv_back.setOnClickListener(this);
        r_hindi.setOnCheckedChangeListener(this);
        r_english.setOnCheckedChangeListener(this);
        loadingBar=new ProgressDialog(this);
        loadingBar.setMessage("Loading...");
        loadingBar.setCanceledOnTouchOutside(false);
        toastMsg = new ToastMsg(PlayQuizActivity.this);
        lin_lang.setVisibility(View.GONE);
        lin_a.setOnClickListener(this);
        lin_b.setOnClickListener(this);
        lin_c.setOnClickListener(this);
        lin_d.setOnClickListener(this);
        btn_next.setOnClickListener(this);
        btn_prev.setOnClickListener(this);
        btn_clear.setOnClickListener(this);
        btn_finish.setOnClickListener(this);
        txt_q_name.setText(getIntent().getStringExtra("title"));
        quiz_id = getIntent().getStringExtra("quiz_id");
        String lng = getIntent().getStringExtra("lang");
        if(lng.equalsIgnoreCase("Both"))
        {
            s_lang="english,hindi";
        }
        else {
            s_lang=lng;
        }
        s_ans_list = new ArrayList<>();
        module=new Module(PlayQuizActivity.this);
        category_list = new ArrayList<>();
        question_list = new ArrayList<>();
//        Toast.makeText(PlayQuizActivity.this, "id: " + quiz_id, Toast.LENGTH_LONG).show();
        dRef = FirebaseDatabase.getInstance().getReference();
        sessionManagment = new SessionManagment(PlayQuizActivity.this);
        tot_q= Integer.parseInt(getIntent().getStringExtra("questions"));


    }

    public void getQuizData(String quiz_id, final int count) {
        loadingBar.show();

        dRef.child("quiz_ques").child(quiz_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            loadingBar.dismiss();
//            Log.e("dataSnapshot",dataSnapshot.toString());

                if (dataSnapshot.hasChildren()) {
                    loadingBar.dismiss();
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        QuizQuesCategoryModel model = data.getValue(QuizQuesCategoryModel.class);
                        category_list.add(model);
//                        Log.e("cat_list", data.toString() + "\n" + category_list.size());

                    }


                }
                if (category_list.size()>1)
                {

                    getQuestion(category_list.get(count).getCat_id(), category_list.get(count).getQues_no(), count);

                }
                else
                {
                    getQuestion(category_list.get(0).getCat_id(), category_list.get(0).getQues_no(), count);
                    btn_finish.setVisibility(View.VISIBLE);
                    btn_next.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                loadingBar.dismiss();
                toastMsg.toastIconError(databaseError.getMessage());
            }
        });

    }

    public void getQuestion(String cat_id , String ques_no , final int count)
    {
        loadingBar.show();

        dRef.child("questions").child(cat_id).child(ques_no).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                loadingBar.dismiss();
                if (dataSnapshot.hasChildren())
                {
                        clearViews();
                        AddQuestionModel questionModel = dataSnapshot.getValue(AddQuestionModel.class);
                        question_list.add(questionModel);
//                        Log.e("question",dataSnapshot.toString()+ "\n"+question_list.size());

                }
                txt_q_no.setText("Q.no." + String.valueOf(count+1));

                if (question_list.get(count).getLanguage().equalsIgnoreCase("english"))
                {
                    txt_ques.setText(question_list.get(count).getQues());
                    txt_a.setText(question_list.get(count).getOption_a());
                    txt_b.setText(question_list.get(count).getOption_b());
                    txt_c.setText(question_list.get(count).getOption_c());
                    txt_d.setText(question_list.get(count).getOption_d());
                    c_ans = question_list.get(count).getAns();

                }
                else if (question_list.get(count).getLanguage().equalsIgnoreCase("hindi"))
                {
                    txt_ques.setText(question_list.get(count).getHindi_ques());
                    txt_a.setText(question_list.get(count).getHindi_option_a());
                    txt_b.setText(question_list.get(count).getHindi_option_b());
                    txt_c.setText(question_list.get(count).getHindi_option_c());
                    txt_d.setText(question_list.get(count).getHindi_option_d());
                    c_ans = question_list.get(count).getHindi_ans();

                }
                else if (question_list.get(count).getLanguage().equalsIgnoreCase("english,hindi"))
                {
//                   lin_lang.setVisibility(View.VISIBLE);
                   if (s_lang.equals("hindi"))
                   {
                       txt_ques.setText(question_list.get(count).getHindi_ques());
                       txt_a.setText(question_list.get(count).getHindi_option_a());
                       txt_b.setText(question_list.get(count).getHindi_option_b());
                       txt_c.setText(question_list.get(count).getHindi_option_c());
                       txt_d.setText(question_list.get(count).getHindi_option_d());
                       c_ans = question_list.get(count).getHindi_ans();
                   }
                   else if (s_lang.equals("english"))
                   {
                       txt_ques.setText(question_list.get(count).getQues());
                       txt_a.setText(question_list.get(count).getOption_a());
                       txt_b.setText(question_list.get(count).getOption_b());
                       txt_c.setText(question_list.get(count).getOption_c());
                       txt_d.setText(question_list.get(count).getOption_d());
                       c_ans = question_list.get(count).getAns();
                   }
                }
                else
                {
                    lin_lang.setVisibility(View.GONE);
                }
                switch (s_ans)
                {
                    case "a":lin_a.setBackgroundColor(PlayQuizActivity.this.getResources().getColor(R.color.rc_8));
                        break;
                    case "b":lin_b.setBackgroundColor(PlayQuizActivity.this.getResources().getColor(R.color.rc_8));
                        break;
                    case "c":lin_c.setBackgroundColor(PlayQuizActivity.this.getResources().getColor(R.color.rc_8));
                        break;
                    case  "d":lin_d.setBackgroundColor(PlayQuizActivity.this.getResources().getColor(R.color.rc_8));
                        break;
                    default: clearViews();
                    break;

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                loadingBar.dismiss();
                toastMsg.toastIconError(databaseError.getMessage());
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.lin_a) {
            lin_a.setBackgroundColor(this.getResources().getColor(R.color.rc_8));
            lin_b.setBackgroundColor(this.getResources().getColor(R.color.white));
            lin_c.setBackgroundColor(this.getResources().getColor(R.color.white));
            lin_d.setBackgroundColor(this.getResources().getColor(R.color.white));
            ans = "a";
            if(existCounter(s_ans_list,count))
            {
                s_ans_list.set(count,ans);
            }
            else
            {
                s_ans_list.add(count,ans);
            }

        } else if (id == R.id.lin_b) {
            lin_b.setBackgroundColor(this.getResources().getColor(R.color.rc_8));
            lin_a.setBackgroundColor(this.getResources().getColor(R.color.white));
            lin_c.setBackgroundColor(this.getResources().getColor(R.color.white));
            lin_d.setBackgroundColor(this.getResources().getColor(R.color.white));
            ans ="b";
            if(existCounter(s_ans_list,count))
            {
                s_ans_list.set(count,ans);
            }
            else
            {
                s_ans_list.add(count,ans);
            }
        } else if (id == R.id.lin_c) {
            lin_c.setBackgroundColor(this.getResources().getColor(R.color.rc_8));
            lin_b.setBackgroundColor(this.getResources().getColor(R.color.white));
            lin_a.setBackgroundColor(this.getResources().getColor(R.color.white));
            lin_d.setBackgroundColor(this.getResources().getColor(R.color.white));
            ans = "c";
            if(existCounter(s_ans_list,count))
            {
                s_ans_list.set(count,ans);
            }
            else
            {
                s_ans_list.add(count,ans);
            }
        } else if (id == R.id.lin_d) {
            lin_d.setBackgroundColor(this.getResources().getColor(R.color.rc_8));
            lin_b.setBackgroundColor(this.getResources().getColor(R.color.white));
            lin_c.setBackgroundColor(this.getResources().getColor(R.color.white));
            lin_a.setBackgroundColor(this.getResources().getColor(R.color.white));
            ans = "d";
            if(existCounter(s_ans_list,count))
            {
                s_ans_list.set(count,ans);
            }
            else
            {
                s_ans_list.add(count,ans);
            }
//            toastMsg.toastIconSuccess(String.valueOf(s_ans_list.size()));

        } else if (id == R.id.prev) {

            if (count > 0) {
                count = count - 1;
                if(s_ans_list.size()!=0)
                {
                    s_ans = s_ans_list.get(count);
                }
                getQuestion(category_list.get(count).getCat_id(), category_list.get(count).getQues_no(), count);
            }
            if (count==0)
            {
                btn_prev.setVisibility(View.GONE);
                btn_next.setVisibility(View.VISIBLE);
                btn_finish.setVisibility(View.GONE);
            }

//            toastMsg.toastIconSuccess("ans"+count);
        } else if (id == R.id.next) {
            if (ans.equals(c_ans)) {
                c_count++;
            }
           else
            {
                if(ans.isEmpty())
                {
                    if(existCounter(s_ans_list,count))
                    {
                        s_ans_list.set(count,ans);
                    }
                    else
                    {
                        s_ans_list.add(count,ans);
                    }

                }
                else
                {
                    w_count++;
                }

            }
            count = count + 1;
            if (count > 0) {
                btn_prev.setVisibility(View.VISIBLE);
            } else {
                btn_prev.setVisibility(View.GONE);
            }
            if (count == category_list.size() - 1) {
                btn_next.setVisibility(View.GONE);
                btn_finish.setVisibility(View.VISIBLE);
            }
            getQuestion(category_list.get(count).getCat_id(), category_list.get(count).getQues_no(), count);
        }
        else if (id == R.id.btn_finish)
        {
            if (ans.equals(c_ans)) {
                c_count++;
            }
            else
            {
                w_count++;
            }
            t.cancel();
            int p = getPercentage(c_count,tot_q);
            submitQuizResult(quiz_id,sessionManagment.getUserDetails().get(KEY_ID),String.valueOf(c_count),String.valueOf(w_count),p,t_count);
            countDownTimer.cancel();
        }
        else if (id == R.id.btn_clear)
        {
//             module.showToast(""+count+"\n"+w_count+"\n"+c_count+"\n"+c_ans+"\n"+ans);
        }
    }

        public void clearViews ()
        {
            lin_d.setBackgroundColor(this.getResources().getColor(R.color.white));
            lin_b.setBackgroundColor(this.getResources().getColor(R.color.white));
            lin_c.setBackgroundColor(this.getResources().getColor(R.color.white));
            lin_a.setBackgroundColor(this.getResources().getColor(R.color.white));
            lin_lang.setVisibility(View.GONE);
        }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int id = buttonView.getId();
        if (id == R.id.hindi)
        {
            if (isChecked) {
                s_lang = "hindi";
                r_english.setChecked(false);
//                toastMsg.toastIconSuccess(s_lang);
            }
        }
        else if (id == R.id.english)
        {
            if (isChecked) {
                s_lang = "english";
                r_hindi.setChecked(false);
//                toastMsg.toastIconSuccess(s_lang);
            }
        }
    }

    public void submitQuizResult(final String quiz_id, final String user_id , final String c_ans , final String w_ans, int percent , long time)
    {
        loadingBar.show();
       Map<String ,Object> map = new HashMap<>();
       map.put("quiz_id",quiz_id);
       map.put("user_id",user_id);
       map.put("correct_ans",c_ans);
       map.put("wrong_ans",w_ans);
       map.put("result_id",getUniqueId());
       map.put("percentage",String.valueOf(percent));
       map.put("time_taken",String.valueOf(time));
       map.put("status","complete");
        map.put("date",module.getCurrentDate());
        map.put("time",module.getCurrentTime());
      String unique_id = quiz_id+"_"+user_id;

       dRef.child("quiz_results").child(unique_id).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {

           @Override
           public void onComplete(@NonNull Task<Void> task) {
               if (task.isSuccessful()) {
                   loadingBar.dismiss();
                   toastMsg.toastIconSuccess("Submitted Successfully");

                   Intent intent = new Intent(PlayQuizActivity.this, QuizEnd.class);
                   intent.putExtra("correct_ans", c_ans);
                   intent.putExtra("wrong_ans", w_ans);
                   intent.putExtra("quiz_id", quiz_id);
                   intent.putExtra("user_id", user_id);
                   intent.putExtra("end_time",(getIntent().getStringExtra("end_time")));
                   intent.putExtra("title", getIntent().getStringExtra("title"));
                   intent.putExtra("questions",String.valueOf(question_list.size()));
                   intent.putExtra("remaining",String.valueOf(r_time));
                   startActivity(intent);
               }
               else
               {
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
    public String getUniqueId()
    {
        String ss = "result";
        String unique_id = "";
        Date date=new Date();
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("ddMMyyyyhhmmss");
        unique_id = ss +simpleDateFormat.format(date).toString();
        return unique_id ;
    }
    int getPercentage(int correct ,int tot)
    {
        int p=0;
        if (tot !=0) {
            p = (correct * 100) / tot;
        }
        else
        {
            p=0;
        }
        return p ;
    }

    public  void startTimer ()
    {
    t=new Timer();
        t.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
//                            myTextView.setText("count="+count);
                        t_count++;
                    }
                });
            }
        }, 1000, 1000);
    }

    public boolean existCounter(ArrayList<String> list,int count)
    {
        boolean flag=false;
        for(int i=0; i<list.size();i++)
        {
            if(i==count)
            {
                flag=true;
                break;
            }
        }
        return flag;
    }
}

