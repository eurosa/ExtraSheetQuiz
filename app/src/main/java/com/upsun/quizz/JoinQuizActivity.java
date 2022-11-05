package com.upsun.quizz;

import static com.upsun.quizz.Config.Constants.KEY_ID;
import static com.upsun.quizz.Config.Constants.KEY_QUIZ_STATUS;
import static com.upsun.quizz.Config.Constants.KEY_WALLET;
import static com.upsun.quizz.Config.Constants.RANK_REF;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.upsun.quizz.Adapter.UserRanksAdapter;
import com.upsun.quizz.Config.Module;
import com.upsun.quizz.Model.JoinedQuizModel;
import com.upsun.quizz.Model.QuizModel;
import com.upsun.quizz.Model.ViewRankModel;
import com.upsun.quizz.networkconnectivity.NetworkConnection;
import com.upsun.quizz.utils.SessionManagment;
import com.upsun.quizz.utils.ToastMsg;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JoinQuizActivity extends AppCompatActivity implements View.OnClickListener {
     SessionManagment sessionManagment;
    Activity ctx=JoinQuizActivity.this;
    Module module;
    ToastMsg toastMsg;
    ProgressDialog loadingBar ;
   public TextView tv_title ,txt_ini,quiz_title ,txt_desc,txt_date , txt_s_time , txt_e_time ,txt_participants;
   public TextView txt_left ,txt_p_list ,txt_questions ,txt_points ,txt_rewards,quiz_lang,tv_qJoin;
    Button btn_join ;
    String title ,desc,ques , start_time ,end_time ,tot_p ,q_date ,points,rewardPoint,
            rewards,user_id ,quiz_id ,joined ,left ,q_status,lang="",duration,instruction,rank_visible;
    ImageView img_back;
    RecyclerView rv_ranks ;
    ArrayList<JoinedQuizModel> quiz_p_list;
    ArrayList<? extends JoinedQuizModel> p_list;
    UserRanksAdapter userRanksAdapter ;
    DatabaseReference rankRef;
    ArrayList<ViewRankModel> rankList;
    QuizModel quizModel;
    int j_p=0 ,l_p=0 ,t_p=0 ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_quiz);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        sessionManagment = new SessionManagment(this);
        loadingBar=new ProgressDialog(this);
        loadingBar.setMessage("Loading...");
        loadingBar.setCanceledOnTouchOutside(false);
        module=new Module(ctx);
        toastMsg=new ToastMsg(ctx);
        tv_title = findViewById(R.id.tv_title);
        quiz_title = findViewById(R.id.quizname);
        txt_desc = findViewById(R.id.quiz_desc);
        txt_date = findViewById(R.id.quiz_date);
        tv_qJoin =(TextView) findViewById(R.id.tv_qJoin);
        txt_e_time = findViewById(R.id.quiz_end);
        txt_s_time = findViewById(R.id.quiz_start);
        txt_participants = findViewById(R.id.tot_participants);
        txt_left = findViewById(R.id.p_remaining);
        txt_p_list = findViewById(R.id.see_p_list);
        txt_questions = findViewById(R.id.quiz_questions);
        txt_points = findViewById(R.id.quiz_points);
        txt_rewards = findViewById(R.id.quiz_rewards);
        btn_join = findViewById(R.id.join);
        img_back = findViewById(R.id.iv_back);
        txt_ini = findViewById(R.id.quiz_initial);
        rv_ranks = findViewById(R.id.rv_ranks);
        quiz_lang =(TextView) findViewById(R.id.quiz_lang);
        rankRef=FirebaseDatabase.getInstance().getReference().child(RANK_REF);
        img_back.setOnClickListener(this);
        btn_join.setOnClickListener(this);
        quiz_p_list = new ArrayList<>();
        rankList = new ArrayList<>();

      title= getIntent().getStringExtra("title");
      desc = getIntent().getStringExtra("description");
      q_date=  getIntent().getStringExtra("quiz_date");
      quiz_id=   getIntent().getStringExtra("quiz_id");
      start_time=   getIntent().getStringExtra("start_time");
      end_time=   getIntent().getStringExtra("end_time");
      points=   getIntent().getStringExtra("entry_fee");
      rewardPoint=   getIntent().getStringExtra("reward_fee");
      rewards=  getIntent().getStringExtra("rewards");
      ques=   getIntent().getStringExtra("questions");
        duration=   getIntent().getStringExtra("duration");
      tot_p=   getIntent().getStringExtra("participants");
        instruction=   getIntent().getStringExtra("ins");
      q_status = getIntent().getStringExtra("q_status");
      joined = getIntent().getStringExtra("joined");
      rank_visible = getIntent().getStringExtra("rank_visibility");
      lang = getIntent().getStringExtra("lang");
      j_p = Integer.parseInt(getIntent().getStringExtra("cnt_prtcpnts"));
      t_p = Integer.parseInt(tot_p);
      l_p = t_p-j_p;
      quizModel=new Gson().fromJson(getIntent().getStringExtra("model"),QuizModel.class);
//
//      Log.e("p_lsit" , String.valueOf(QuizAdapter.quiz_p_list.size()));

        if(NetworkConnection.connectionChecking(ctx))
        {

            getAllRanks(quiz_id);
        }
        else
        {
            module.noConnectionActivity();
        }
      if (joined.equalsIgnoreCase("true"))
      {
          btn_join.setText("Joined");
          btn_join.setBackgroundColor(getResources().getColor(R.color.gray));
          btn_join.setEnabled(false);
          if (rank_visible==null || rank_visible.equals("0"))
          {
              txt_p_list.setVisibility(View.GONE);
          }
          else
          {
              txt_p_list.setVisibility(View.VISIBLE);
          }

      }

      user_id = sessionManagment.getUserDetails().get(KEY_ID);
      txt_ini.setText(String.valueOf(title.charAt(0)));
      quiz_title.setText(title);
      txt_desc.setText(desc);
      txt_rewards.setText(rewards);
      txt_date.setText(q_date);
      txt_questions.setText(ques);
      txt_points.setText(points);
      txt_s_time.setText(start_time);
      txt_e_time.setText(end_time);
      txt_participants.setText(tot_p);
        tv_qJoin.setText(String.valueOf(j_p));
      txt_left.setText(String.valueOf(l_p));
      if(lang.equalsIgnoreCase("Both"))
      {
          quiz_lang.setText("English,Hindi");
      }
      else
      {
          quiz_lang.setText(lang);
      }

      txt_p_list.setOnClickListener(this);
//      participantsAdapter = new ParticipantsAdapter(QuizAdapter.quiz_p_list,JoinQuizActivity.this,loadingBar);
//      rv_participants.setLayoutManager(new LinearLayoutManager(JoinQuizActivity.this,RecyclerView.VERTICAL,false));
//      rv_participants.setAdapter(participantsAdapter);

    }

    private void getAllRanks(String quiz_id) {
        loadingBar.show();
        rankList.clear();
        rankRef.child(quiz_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                loadingBar.dismiss();
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0)
                {
                    for(DataSnapshot snap:dataSnapshot.getChildren())
                    {
                      ViewRankModel model=snap.getValue(ViewRankModel.class);
                      rankList.add(model);
                    }
                    if(rankList.size()>0)
                    {
                        rv_ranks.setLayoutManager(new GridLayoutManager(ctx,2));
                        userRanksAdapter=new UserRanksAdapter(ctx,rankList);
                        rv_ranks.setAdapter(userRanksAdapter);
                        userRanksAdapter.notifyDataSetChanged();
                    }
                }
                else
                {

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
           loadingBar.dismiss();
           module.showToast(""+databaseError.getMessage().toString());
            }
        });

    }


    @Override
    public void onClick(View v) {
        final int id =v.getId();
        if (id== R.id.iv_back)
        {
            finish();
        }
        else if (id == R.id.see_p_list)
        {
            int visible = rv_ranks.getVisibility();
            if (visible == View.GONE)
            {
                if(rankList.size()>0)
                {
                    rv_ranks.setVisibility(View.VISIBLE);
                    txt_p_list.setText("Hide Ranks");

                }
                else {
                    toastMsg.toastIconError("No Ranks Available for this quiz");
                }
                  }
            else if (visible == View.VISIBLE)
            {
                rv_ranks.setVisibility(View.GONE);
                txt_p_list.setText("See Ranks");
            }

        }
        else if (id== R.id.join)
        {
            if(NetworkConnection.connectionChecking(ctx))
            {
            int wallet=Integer.parseInt(sessionManagment.getUserDetails().get(KEY_WALLET).toString());
            final int entryFee=Integer.parseInt(points.toString());
            final int rewardFee=Integer.parseInt(rewardPoint.toString());
            if(wallet>=entryFee)
            {
                final String updatedWallet=String.valueOf(wallet-entryFee);

            String btn_text = btn_join.getText().toString();
            if (btn_text.equalsIgnoreCase("Join")) {

                if(sessionManagment.getUserDetails().get(KEY_QUIZ_STATUS).equalsIgnoreCase("1")) {

                    final AlertDialog.Builder builder = new AlertDialog.Builder(JoinQuizActivity.this);
                    builder.setTitle(title);
                    builder.setIcon(R.drawable.logo);
                    builder.setMessage("Would you like to take the quiz ?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            int[] cond = module.getReductionCondition(sessionManagment, entryFee, rewardFee);
                            if (cond[0] == 1) {
                                toastMsg.toastIconError("Insufficient Amount");
                            } else {
                                joinQuiz(quiz_id, user_id, tot_p,String.valueOf(cond[1]),String.valueOf(cond[2]), tv_qJoin, txt_left, btn_join);
                            }
                            dialog.dismiss();

                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.show();
                }
                else
                {
                    Intent intent=new Intent(ctx,JoinQuizActivity.class);
                    startActivity(intent);
                }
            }
            else if (btn_text.equalsIgnoreCase("Joined"))
            {
                new ToastMsg(JoinQuizActivity.this).toastIconSuccess("You have already Joined ,we'll notify you when the quiz starts");
            }
            }
            else
            {
                toastMsg.toastIconError("Insufficient Wallet Amount");
            }
            }
            else
            {
                module.noConnectionActivity();
            }
        }
        else if (id == R.id.see_p_list)
        {
            if (txt_p_list.getText().equals("See Participants")) {
                txt_p_list.setText("Hide Participants");
                rv_ranks.setVisibility(View.VISIBLE);
            }
            else if (txt_p_list.getText().equals("Hide Participants"))
            {
                txt_p_list.setText("See Participants");
                rv_ranks.setVisibility(View.GONE);
            }
        }
    }
    public String getUniqueId()
    {
        String ss = "jq";
        String unique_id = "";
        Date date=new Date();
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("ddMMyyyyhhmmss");
        unique_id = ss +simpleDateFormat.format(date).toString();
        return unique_id ;
    }
//    public String getUniqueId()
//    {
//        Date date=new Date();
//        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("ddMMyyyyhhmmss");
//        return simpleDateFormat.format(date).toString();
//    }
     public void joinQuiz(final String quiz_id , final String user_id, final String pncts, final String updatedWallet,final String updatedRewards, final TextView tv_qJoin, final TextView txt_left, final Button btn_join)
    {
        loadingBar.show();
        final String jId=getUniqueId();
        final Map<String ,Object> quiz_map = new HashMap<>();
        quiz_map.put("quiz_id",quiz_id);
        quiz_map.put("description",desc);
        quiz_map.put("entry_fee",points);
        quiz_map.put("participents",tot_p);
        quiz_map.put("questions",ques);
        quiz_map.put("quiz_date",q_date);
        quiz_map.put("quiz_end_time",end_time);
        quiz_map.put("quiz_start_time",start_time);
        quiz_map.put("duration",duration);
        quiz_map.put("title",title);
        quiz_map.put("instruction",instruction);
        quiz_map.put("language",lang);
        quiz_map.put("join_id",jId.toString());
        quiz_map.put("join_date",module.getCurrentDate().toString());
        quiz_map.put("user_id",user_id);
        quiz_map.put("date",module.getCurrentDate());
        quiz_map.put("time",module.getCurrentTime());
        final DatabaseReference dRef  = FirebaseDatabase.getInstance().getReference().child("joined_quiz");
        Query query=dRef.orderByChild("quiz_id").equalTo(quiz_id);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int count=0;

                ArrayList<JoinedQuizModel> tempList=new ArrayList<>();
                tempList.clear();
                for(DataSnapshot snap:dataSnapshot.getChildren())
                {
                    JoinedQuizModel model=snap.getValue(JoinedQuizModel.class);
                    tempList.add(model);
                    count++;
                }
                if(count>=Integer.parseInt(pncts))
                {
                    loadingBar.dismiss();
                    toastMsg.toastIconError("No Space is available");

                }
                else
                {

                    module.insertHistory(quizModel,user_id);
                    dRef.child(jId).updateChildren(quiz_map).addOnCompleteListener(ctx, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            sessionManagment.updateWallet(updatedWallet);
                            sessionManagment.updateRewards(updatedRewards);

                            module.updateDbWallet(user_id, updatedWallet);
                            module.updateDbRewards(user_id, updatedRewards);
                            //tvSpots.setText(String.valueOf(module.getSpotLeft(tvSpots.getText().toString())) + "/" + pncts);
                            loadingBar.dismiss();
                            btn_join.setText("Joined");
                            if (rank_visible=="1")
                            {
                                txt_p_list.setVisibility(View.VISIBLE);
                            }
                            q_status = "joined";
                            btn_join.setEnabled(false);



                        }
                    }).addOnFailureListener(ctx, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            loadingBar.dismiss();
                            Toast.makeText(ctx,"Please try again later"+e.getMessage(),Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                loadingBar.dismiss();
                module.showToast(databaseError.getMessage().toString());
            }
        });

    }


}
