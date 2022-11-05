package com.upsun.quizz;

import static com.upsun.quizz.Config.Constants.INSTRUCTION_REF;
import static com.upsun.quizz.Config.Constants.KEY_ID;
import static com.upsun.quizz.Config.Constants.KEY_NAME;
import static com.upsun.quizz.Config.Constants.SECTION_REF;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.upsun.quizz.Config.Module;
import com.upsun.quizz.Model.InstructionModel;
import com.upsun.quizz.Model.QuizResultModel;
import com.upsun.quizz.Model.SectionModel;
import com.upsun.quizz.networkconnectivity.NetworkConnection;
import com.upsun.quizz.utils.SessionManagment;
import com.upsun.quizz.utils.ToastMsg;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class InstructionActivity extends AppCompatActivity implements View.OnClickListener {
    private final String TAG = InstructionActivity.class.getSimpleName();
    Activity ctx = InstructionActivity.this;
    ImageView iv_back;
    TextView tv_timer, tv_ins;
    Button btn_play;
    ProgressDialog loadingBar;
    ToastMsg toastMsg;
    String sec_id = "";
    Module module;
    ArrayList<QuizResultModel> list;
    String q_id = "", ques = "", ttl = "", q_date = ""
//             ,s_tm="",e_tm=""
            , e_fee = "", rew = "",
            start_time = "", end_time = "", quiz_start = "", quiz_end = "", ins_id = "", lang = "", duration = "", question_time = "", quiz_type = "";
    Date s_time = null, e_time = null;
    CountDownTimer startTimer, endTimer;
    DatabaseReference section_ref;
    Date c_time = null;
    SessionManagment sessionManagment;
    DatabaseReference ins_ref, dRef;
    int secNum, numsec = 0;
    ArrayList<SectionModel> sectionList;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instruction);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        initViews();

//        getSections();
        if (NetworkConnection.connectionChecking(this)) {
            getInstructions(ins_id);
        } else {
            module.noConnectionActivity();
        }


        DateFormat outputformat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Date curr = new Date();
        String current_date = outputformat.format(curr);
        SimpleDateFormat dtm_Format = new SimpleDateFormat("hh:mm a");

        String tm = dtm_Format.format(curr);
        SimpleDateFormat format24 = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat parseFormat = new SimpleDateFormat("hh:mm a");
//        try {
//            s_time = parseFormat.parse(module.getAccTimeFormat(tm, s_tm));
//            e_time = parseFormat.parse(module.getAccTimeFormat(tm, e_tm));
//            start_time = format24.format(s_time);
//            end_time = format24.format(e_time);
//            quiz_start = q_date + " " + start_time;
//            quiz_end = q_date + " " + end_time;
//
//            DateFormat parseformat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
//            c_time = parseformat.parse(current_date);
//            s_time = parseformat.parse(quiz_start);
//
//            long difference = module.getTimeDiffInLong(q_date,s_tm);
//            if (difference > 0) {
//                btn_play.setText("Please Wait...");
//                btn_play.setClickable(false);
//                startTimer = new CountDownTimer(difference, 1000) {
//                    @Override
//                    public void onTick(long millisUntilFinished) {
//                        updateCounterTime(millisUntilFinished, tv_timer);
//                    }
//
//                    @Override
//                    public void onFinish() {
//                        tv_timer.setText("00:00:00");
//                        btn_play.setText("Play Now");
//                        btn_play.setClickable(true);
//                    }
//                }.start();
//            }
//            else
//            {
        btn_play.setText("Play Now");
        btn_play.setClickable(true);
//            }
//        }
//        catch(Exception e){
//            e.printStackTrace();
//        }
//
    }

    private void getInstructions(String ins_id) {
        loadingBar.show();
        ins_ref = FirebaseDatabase.getInstance().getReference().child(INSTRUCTION_REF);

        ins_ref.child(ins_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                loadingBar.dismiss();
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    InstructionModel model = dataSnapshot.getValue(InstructionModel.class);
                    tv_ins.setText(model.getDesc().toString());
                } else {
                    toastMsg.toastIconError("No Instructions Available");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                loadingBar.dismiss();
                Toast.makeText(ctx, "" + databaseError.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        getAllSections(q_id);
    }

    private void updateCounterTime(long time_in_milis, TextView tv_tm) {
        String timeLeftInFormat = "";

        timeLeftInFormat = String.format(Locale.getDefault(), "%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(time_in_milis),
                TimeUnit.MILLISECONDS.toMinutes(time_in_milis) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(time_in_milis)),
                TimeUnit.MILLISECONDS.toSeconds(time_in_milis) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time_in_milis)));
        tv_tm.setText(timeLeftInFormat);
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void initViews() {
        sectionList = new ArrayList<>();
        iv_back = (ImageView) findViewById(R.id.iv_back);
        tv_timer = (TextView) findViewById(R.id.tv_timer);
        tv_ins = (TextView) findViewById(R.id.tv_ins);
        btn_play = (Button) findViewById(R.id.btn_play);
        loadingBar = new ProgressDialog(ctx);
        loadingBar.setMessage("Loading...");
        loadingBar.setCanceledOnTouchOutside(false);
        toastMsg = new ToastMsg(ctx);
        module = new Module(ctx);
        dRef = FirebaseDatabase.getInstance().getReference();
        q_id = getIntent().getStringExtra("quiz_id");
        ques = getIntent().getStringExtra("questions");
        ttl = getIntent().getStringExtra("title");
        q_date = getIntent().getStringExtra("quiz_date");
//        s_tm=getIntent().getStringExtra("start_time");
//        e_tm=getIntent().getStringExtra("end_time");
        e_fee = getIntent().getStringExtra("entry_fee");
        duration = getIntent().getStringExtra("duration");
        question_time = getIntent().getStringExtra("question_time");
        quiz_type = getIntent().getStringExtra("quiz_type");
        rew = getIntent().getStringExtra("rewards");
        ins_id = getIntent().getStringExtra("ins");
        lang = getIntent().getStringExtra("lang");
        sessionManagment = new SessionManagment(ctx);
        list = new ArrayList<>();
        section_ref = FirebaseDatabase.getInstance().getReference().child(SECTION_REF);
        btn_play.setOnClickListener(this);
        iv_back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_back) {
            finish();
        } else if (v.getId() == R.id.btn_play) {
//          long endDiff=module.getTimeDiffInLong(q_date,e_tm);
//            long endDiff = module.getTimeDiffInLong(q_date, "00:00:00");
//            if (endDiff > 0) {
            String user_id = sessionManagment.getUserDetails().get(KEY_ID).toString();
            FirebaseDatabase.getInstance().getReference().child("quiz_ques").child(q_id).child(sec_id).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        if (snapshot.hasChildren()) {
                            checkPlayed(q_id, user_id);
//                            ìnitilQuizResult(q_id, user_id);

                        } else {
                            toastMsg.toastIconError("Quiz cannot be played.");
                        }
                    } else {
                        toastMsg.toastIconError("Quiz cannot be played.");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    toastMsg.toastIconError("Something went wrong.");
                }
            });


//            } else {
//                toastMsg.toastIconError("Quiz Ended");
//                finish();
//            }

        }
    }

    private void checkPlayed(final String q_id, final String user_id) {
        loadingBar.show();
        list.clear();
        String c_date = module.getCurrentDate();
        Query query = dRef.child("quiz_results").orderByChild("quiz_id").equalTo(q_id);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        QuizResultModel model = snapshot.getValue(QuizResultModel.class);
                        if (model.getUser_id().equalsIgnoreCase(user_id)) {
                            if (model.getDate().equals(c_date))
                                list.add(model);
                        }
                    }
                    if (list.size() > 0) {
                        loadingBar.dismiss();
                        toastMsg.toastIconError("You already played this quiz");
                    } else {
                        ìnitilQuizResult(q_id, user_id);
                    }

                } else {
                    ìnitilQuizResult(q_id, user_id);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                loadingBar.dismiss();
                module.showToast("" + databaseError.getMessage().toString());
            }
        });
    }

    public void ìnitilQuizResult(final String quiz_id, final String user_id) {
        Map<String, Object> map = new HashMap<>();
        map.put("quiz_id", quiz_id);
        map.put("user_id", user_id);
        map.put("username", sessionManagment.getUserDetails().get(KEY_NAME));
        map.put("correct_ans", "0");
        map.put("wrong_ans", "0");
        map.put("result_id", module.getUniqueId("result"));
        map.put("percentage", "0");
        map.put("time_taken", "0");
        map.put("status", "pending");
        map.put("section", sec_id);
        map.put("date", module.getCurrentDate());
        map.put("time", module.getCurrentTime());
        String unique_id = quiz_id + "_" + user_id;


        dRef.child("quiz_results").child(unique_id).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {

            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    loadingBar.dismiss();
                    Intent intent = new Intent(ctx, PlayQuizsActivity.class);
                    intent.putExtra("quiz_id", q_id);
                    intent.putExtra("questions", ques);
                    intent.putExtra("title", ttl);
                    intent.putExtra("quiz_date", q_date);
//                    intent.putExtra("start_time",s_tm);
//                    intent.putExtra("end_time",e_tm);
                    intent.putExtra("duration", duration);
                    intent.putExtra("question_time", question_time);
                    intent.putExtra("quiz_type", quiz_type);
                    intent.putExtra("entry_fee", e_fee);
                    intent.putExtra("rewards", "");
                    intent.putExtra("lang", lang.toString());
                    intent.putExtra("section", sec_id);
                    Log.e(TAG, "onComplete: " + sec_id);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);

                } else {
                    loadingBar.dismiss();
                    toastMsg.toastIconError("Something went wrong");
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                module.showToast(e.getMessage());

            }
        });

    }

    private void getSections() {
        dRef.child("quiz_ques").child(q_id).addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                numsec = 0;
                for (DataSnapshot s : dataSnapshot.getChildren()
                ) {
                    numsec++;
                }
                secNum = ThreadLocalRandom.current().nextInt(0, numsec + 1);
                Log.e("Section", String.valueOf(secNum));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void getAllSections(String quiz_id) {
        section_ref.child(quiz_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                sectionList.clear();
                if (dataSnapshot.exists()) {
                    Log.e(TAG, "onDataChange: " + dataSnapshot.toString());
                    for (DataSnapshot s : dataSnapshot.getChildren()) {
                        SectionModel m = s.getValue(SectionModel.class);
                        sectionList.add(m);
                    }
                    if (sectionList.size() > 0) {
                        Random random = new Random();
                        sec_id = sectionList.get(random.nextInt(sectionList.size())).section_id;
                        Log.e(TAG, "onDataChange: " + sec_id.toString());
//                        new ToastMsg(ctx).toastIconSuccess(""+sec_id);

                    } else {
                        new ToastMsg(ctx).toastIconError("No Questions Available");
                    }

                } else {

                }
                loadingBar.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "" + databaseError, Toast.LENGTH_LONG).show();
                loadingBar.dismiss();
            }
        });
    }

}
