package com.upsun.quizz.Adapter;

import static com.upsun.quizz.Config.Constants.BROAD_REWARDS;
import static com.upsun.quizz.Config.Constants.BROAD_WALLET;
import static com.upsun.quizz.Config.Constants.KEY_QUIZ_STATUS;
import static com.upsun.quizz.Config.Constants.KEY_WALLET;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.LoadAdError;
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
import com.upsun.quizz.Config.Constants;
import com.upsun.quizz.Config.Module;
import com.upsun.quizz.InstructionActivity;
import com.upsun.quizz.JoinQuizActivity;
import com.upsun.quizz.Model.JoinedQuizModel;
import com.upsun.quizz.Model.QuizModel;
import com.upsun.quizz.PrizeActivity;
import com.upsun.quizz.QuizStatusActivity;
import com.upsun.quizz.R;
import com.upsun.quizz.RankActivity;
import com.upsun.quizz.utils.SessionManagment;
import com.upsun.quizz.utils.ToastMsg;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class QuizAdapter extends RecyclerView.Adapter<QuizAdapter.ViewHolder> {
    Activity activity;
    ArrayList<QuizModel> quiz_list;
    ArrayList<JoinedQuizModel> quiz_p_list;
    ArrayList<JoinedQuizModel> pList;
    ArrayList<JoinedQuizModel> prctList;
    String user_id;
    SessionManagment sessionManagment;
    ProgressDialog loadingBar;
    String q_status = "join";
    Date c_date, q_date;
    String current_date = null, quiz_date = "", quiz_end = "", quiz_start = "", start_time = "", end_time = "", curr_time = "", duration = "";
    ToastMsg toastMsg;
    int joined_p = 0;
    CountDownTimer countDownTimer, endCDTimer;
    Date s_time, e_time, c_time;
    Module module;
    InterstitialAd mInterstitialAd, mInterstitialAd1;
    boolean adShow = false;

    public QuizAdapter(Activity activity, ArrayList<QuizModel> quiz_list, ArrayList<JoinedQuizModel> pList, ArrayList<JoinedQuizModel> prctList, String user_id) {
        this.activity = activity;
        this.quiz_list = quiz_list;
        this.pList = pList;
        this.prctList = prctList;
        this.user_id = user_id;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.row_quiz2, null);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @SuppressLint("NewApi")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final QuizModel model = quiz_list.get(position);

        if (Boolean.parseBoolean(activity.getString(R.string.show_adds))) {
            loadISNTAd(model.getQuiz_id().toString());
        }


        Date quizDbStartDate = null, quizDbEndDate = null;
        String jStatus = "";

//        getUserQuizList(user_id,model.getQuiz_id(),holder.btn_join);
        final ArrayList<JoinedQuizModel> quizModelArrayList = pList;
        if (model.getRank_visible().equals("0")) {
            //Toast.makeText(activity, "called", Toast.LENGTH_SHORT).show();
            if (holder.rank.getVisibility() == View.VISIBLE)
                holder.rank.setVisibility(View.GONE);
        } else {
            holder.rank.setVisibility(View.VISIBLE);
        }
        if (checkJoinnedQuiz(quizModelArrayList, model.getQuiz_id())) {
            holder.btn_join.setText("Joined");
            holder.tv_prize.setVisibility(View.VISIBLE);
            holder.btn_join.setEnabled(false);
        }
        if (model.getLanguage().equalsIgnoreCase("both")) {
            holder.txt_rewards.setText("Language : English,Hindi");
        } else {
            holder.txt_rewards.setText("Language : " + model.getLanguage().toString());
        }
        final ArrayList<JoinedQuizModel> prcntList = prctList;
        final String cnt_part = getPrticipants(prcntList, model.getQuiz_id(), holder.p_progress, model.getParticipents(), holder.txt_spots);
        String jv = holder.btn_join.getText().toString();
        if (Boolean.parseBoolean(activity.getString(R.string.show_adds))) {
            loadISNTAd1(model, jv, cnt_part);
        }
        final String ini = model.getTitle();
        holder.txt_quiz_ini.setText(String.valueOf(ini.charAt(0)).toUpperCase());
        DateFormat outputformat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Date curr = new Date();
        current_date = outputformat.format(curr);

//        SimpleDateFormat dtm_Format = new SimpleDateFormat("hh:mm a");
//        String tm = dtm_Format.format(curr);
//        SimpleDateFormat format24 = new SimpleDateFormat("HH:mm:ss");
//        SimpleDateFormat parseFormat = new SimpleDateFormat("hh:mm a");
//        try {
//            s_time = parseFormat.parse(module.getAccTimeFormat(tm, model.getQuiz_start_time()));
//            e_time = parseFormat.parse(module.getAccTimeFormat(tm, model.getQuiz_end_time()));
//            start_time = format24.format(s_time);
//            end_time = format24.format(e_time);
//            quiz_start = current_date + " " + start_time;
//            quiz_end = current_date + " " + end_time;
//            quizDbStartDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").parse(quiz_start);
//            quizDbEndDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").parse(quiz_end);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

//        if(current_date.compareToIgnoreCase(quiz_start) < 0)
//        if(curr.compareTo(quizDbStartDate)<0)
//        {
//            Log.e("Compre"+position, "quizDbStartDate<0"+model.getTitle() );
//            try {
//                DateFormat parseformat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
//                c_time = parseformat.parse(current_date);
//                s_time = parseformat.parse(quiz_start);
//
//                long difference = s_time.getTime() - c_time.getTime();
//                countDownTimer = new CountDownTimer(difference, 1000) {
//                    @Override
//                    public void onTick(long millisUntilFinished) {
//                        int remaining = (int) TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished);
//                        if(remaining<=30)
//                        {
//                            if(holder.btn_play.getVisibility()== View.INVISIBLE || holder.btn_play.getVisibility() == View.GONE)
//                            {
//                                holder.btn_play.setVisibility(View.VISIBLE);
//                            }
//
//                        }
//                        updateCounterText(millisUntilFinished, holder.txt_timer,current_date.toString(),model.getQuiz_start_time());
//                    }
//
//                    @Override
//                    public void onFinish() {
//                        DateFormat psformat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
//                        Date eetm=null;
//                        Date cctm=null;
//                        DateFormat outputformat1 = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
//                        Date curr1 = new Date();
//                        String curdate = outputformat1.format(curr1);
//                        try {
//                            eetm=psformat.parse(current_date + " " +getTimeValue(model.getQuiz_end_time()));
//                            cctm=psformat.parse(curdate);
//                        } catch (ParseException e) {
//                            e.printStackTrace();
//                        }
//                        long de=eetm.getTime()-cctm.getTime();
//
//                        endCDTimer=new CountDownTimer(de,1000) {
//                            @Override
//                            public void onTick(long mil) {
//                                int remain = (int) TimeUnit.MILLISECONDS.toSeconds(mil);

        holder.txt_timer.setText("Running..");
//                            }
//
//                            @Override
//                            public void onFinish() {
//                                holder.txt_timer.setText("Expired");
//                            }
//                        }.start();
//                    }
//                }.start();
//
//            } catch (ParseException e)
//            {
//                e.printStackTrace();
//            }
//
//        }

////           else if((current_date.compareToIgnoreCase(quiz_start) > 0) && (current_date.compareToIgnoreCase(quiz_end)<0))
//         if((curr.compareTo(quizDbStartDate) > 0) && (curr.compareTo(quizDbEndDate)<0))
//        {

        Log.e("Compre" + position, "(curr.compareTo(quizDbStartDate) > 0) && (curr.compareTo(quizDbEndDate)<0)" + model.getTitle());
        holder.txt_timer.setText("Running...");
        holder.btn_play.setVisibility(View.VISIBLE);
//        }
//        else if(curr.compareTo(quizDbEndDate)>0)
//        {
//            Log.e("Compre"+position, "curr.compareTo(quizDbEndDate)>0"+model.getTitle() );
//            holder.txt_timer.setText("Quiz Expired");
//            holder.btn_join.setVisibility(View.INVISIBLE);
//            holder.btn_join.setEnabled(false);
//
//        }
        try {
            switch (position % 5) {
                case 0:
                    holder.rel_quiz.setBackgroundTintList(ColorStateList.valueOf(activity.getColor(R.color.rec_ice)));
                    holder.txt_name.setTextColor(activity.getResources().getColor(R.color.rec_purple));
                    holder.rel_bcl.getBackground().setTint(activity.getResources().getColor(R.color.rec_purple));
                    holder.rel_fcl.getBackground().setTint(activity.getResources().getColor(R.color.rec_purple));
//                holder.txt_quiz_ini.setTextColor(activity.getColor(R.color.rec_orange);
                    break;
                case 1:
                    holder.rel_quiz.setBackgroundTintList(ColorStateList.valueOf(activity.getColor(R.color.rec_peach)));
                    holder.txt_name.setTextColor(activity.getResources().getColor(R.color.rec_r_blue));
                    holder.rel_bcl.getBackground().setTint(activity.getResources().getColor(R.color.rec_r_blue));
                    holder.rel_fcl.getBackground().setTint(activity.getResources().getColor(R.color.rec_r_blue));
                    break;
                case 2:

                    holder.rel_quiz.setBackgroundTintList(ColorStateList.valueOf(activity.getColor(R.color.rec_ice)));
                    holder.txt_name.setTextColor(activity.getResources().getColor(R.color.rec_purple));
                    holder.rel_bcl.getBackground().setTint(activity.getResources().getColor(R.color.rec_purple));
                    holder.rel_fcl.getBackground().setTint(activity.getResources().getColor(R.color.rec_purple));
                    break;
                case 3:
                    holder.rel_quiz.setBackgroundTintList(ColorStateList.valueOf(activity.getColor(R.color.rc_3)));

                    holder.txt_name.setTextColor(activity.getResources().getColor(R.color.rec_r_blue));
                    holder.rel_bcl.getBackground().setTint(activity.getResources().getColor(R.color.rec_r_blue));
                    holder.rel_fcl.getBackground().setTint(activity.getResources().getColor(R.color.rec_r_blue));
                    break;
                case 4:
                    holder.rel_quiz.setBackgroundTintList(ColorStateList.valueOf(activity.getColor(R.color.rec_dahlia)));

                    holder.txt_name.setTextColor(activity.getResources().getColor(R.color.rec_voilet));
                    holder.rel_bcl.getBackground().setTint(activity.getResources().getColor(R.color.rec_voilet));
                    holder.rel_fcl.getBackground().setTint(activity.getResources().getColor(R.color.rec_voilet));
                    break;
                default:
                    holder.rel_quiz.setBackgroundTintList(ColorStateList.valueOf(activity.getColor(R.color.rc_8)));
                    holder.txt_name.setTextColor(activity.getResources().getColor(R.color.rec_d_blue));
                    holder.rel_bcl.getBackground().setTint(activity.getResources().getColor(R.color.rec_d_blue));
                    holder.rel_fcl.getBackground().setTint(activity.getResources().getColor(R.color.rec_d_blue));
                    break;


            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        holder.txt_name.setText(model.getTitle());
//                +"\n"+model.getQuiz_id());

        if (model.getReward_fee() == null) {
            holder.txt_points.setText("Entry Fee : " + activity.getResources().getString(R.string.rupee) + model.getEntry_fee());
        } else {
            holder.txt_points.setText("Entry Fee : " + activity.getResources().getString(R.string.rupee) + (Integer.parseInt(model.getEntry_fee()) + Integer.parseInt(model.getReward_fee())));

        }
        holder.txt_desc.setText(model.getDescription());


        try {
            // Toast.makeText(activity, ""+model.getFee_type(), Toast.LENGTH_SHORT).show();
        } catch (Exception ignored) {

        }


        holder.btn_join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sessionManagment.getUserDetails().get(KEY_QUIZ_STATUS).equalsIgnoreCase("1")) {

                    final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    builder.setTitle(model.getTitle());
                    builder.setIcon(R.drawable.logo);
                    builder.setMessage("Would you like to take the quiz ?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            int wallet = Integer.parseInt(sessionManagment.getUserDetails().get(KEY_WALLET).toString());
                            int entryFee = Integer.parseInt(model.getEntry_fee().toString());
                            int rewardFee = Integer.parseInt(model.getReward_fee().toString());
                            dialog.dismiss();


                            int[] cond = module.getReductionCondition(sessionManagment, entryFee, rewardFee);
                            if (cond[0] == 1) {
                                toastMsg.toastIconError("Insufficient Amount");
                            } else {
                                Log.d("TAG", "onDataChange: "+model.getParticipents());
                                int flg = joinQuiz(model, user_id, String.valueOf(cond[1]), String.valueOf(cond[2]), model.getParticipents(), holder.btn_join, holder.p_progress, holder.txt_spots, "join", holder.tv_prize);
                                if (flg == 2) {
                                    Toast.makeText(activity, "You have successfully joined , we'll notify you when the quiz starts", Toast.LENGTH_LONG).show();
                                } else {

                                }
                            }


                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            q_status = "join";
                            dialog.dismiss();
                        }
                    });
                    builder.show();
                } else {
                    Intent intent = new Intent(activity, QuizStatusActivity.class);
                    activity.startActivity(intent);
                }
//                Toast.makeText(activity,""+current_date +quiz_date ,Toast.LENGTH_LONG).show();
            }
        });

        holder.rank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("quiz_id", "onClick: " + model.getQuiz_id());
                String jValue = holder.btn_join.getText().toString();
                if (mInterstitialAd1 != null && mInterstitialAd1.isLoaded()) {
                    mInterstitialAd1.show();
                } else {
                    goToRankActivity(model, jValue, cnt_part);
                }

            }
        });
        holder.tv_prize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(activity, PrizeActivity.class);
                intent.putExtra("quiz_id", model.getQuiz_id());
                activity.startActivity(intent);


            }
        });

        holder.rel_quiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String jValue = holder.btn_join.getText().toString();
                Intent intent = new Intent(activity, JoinQuizActivity.class);
                intent.putExtra("title", model.getTitle());
                intent.putExtra("description", model.getDescription());
                intent.putExtra("quiz_date", model.getQuiz_date());
                intent.putExtra("quiz_id", model.getQuiz_id());
                intent.putExtra("start_time", model.getQuiz_start_time());
                intent.putExtra("end_time", model.getQuiz_end_time());
                intent.putExtra("duration", model.getDuration());
                intent.putExtra("entry_fee", model.getEntry_fee());
                intent.putExtra("reward_fee", model.getReward_fee());
                intent.putExtra("rewards", "");
                intent.putExtra("questions", model.getQuestions());
                intent.putExtra("participants", model.getParticipents());
                intent.putExtra("ins", model.getInstruction());
                intent.putExtra("lang", model.getLanguage());
                intent.putExtra("rank_visibility", model.getRank_visible());
                intent.putExtra("joined", (jValue.equalsIgnoreCase("join") ? "false" : "true"));
                intent.putExtra("cnt_prtcpnts", cnt_part);
                intent.putExtra("q_status", q_status);
                intent.putExtra("quiz_type", model.getQuizType());
                intent.putExtra("model", new Gson().toJson(model).toString());
                activity.startActivity(intent);
            }
        });
        int entryFee = Integer.parseInt(model.getEntry_fee().toString());


        holder.btn_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                long tr= getPlayStatus(current_date.toString(),getTimeValue(model.getQuiz_start_time()),getTimeValue(model.getQuiz_end_time()),"end");
//                if(tr>0)
//                {
//                    toastMsg.toastIconError("This quiz ended");
//                }
//                else
                {
                    if (holder.btn_join.getText().toString().equalsIgnoreCase("Joined")) {
                        Intent intent = new Intent(activity, InstructionActivity .class);
                        intent.putExtra("quiz_id", model.getQuiz_id());
                        intent.putExtra("questions", model.getQuestions());
                        intent.putExtra("title", model.getTitle());
                        intent.putExtra("quiz_date", model.getQuiz_date());
                        intent.putExtra("start_time", model.getQuiz_start_time());
                        intent.putExtra("end_time", model.getQuiz_end_time());
                        intent.putExtra("duration", model.getDuration());
                        intent.putExtra("entry_fee", model.getEntry_fee());
                        intent.putExtra("ins", model.getInstruction());
                        intent.putExtra("lang", model.getLanguage());
                        intent.putExtra("quiz_type", model.getQuizType());
                        intent.putExtra("rewards", "");
                        activity.startActivity(intent);
                    } else {
                        toastMsg.toastIconError("Please join this quiz first.");

//                        if (sessionManagment.getUserDetails().get(KEY_QUIZ_STATUS).equalsIgnoreCase("1")) {
//
//                            final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
//                            builder.setTitle(model.getTitle());
//                            builder.setIcon(R.drawable.logo);
//                            builder.setMessage("Would you like to take the quiz ?");
//                            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    int wallet = Integer.parseInt(sessionManagment.getUserDetails().get(KEY_WALLET).toString());
//                                    int entryFee = Integer.parseInt(model.getEntry_fee().toString());
//                                    int rewardFee = Integer.parseInt(model.getReward_fee().toString());
//                                    dialog.dismiss();
//
//                                    int[] cond = module.getReductionCondition(sessionManagment, entryFee, rewardFee);
//                                    if (cond[0] == 1) {
//                                        toastMsg.toastIconError("Insufficient Amount");
//                                    } else {
//                                        int flag = joinQuiz(model, user_id, String.valueOf(cond[1]), String.valueOf(cond[2]), model.getParticipents(), holder.btn_join, holder.p_progress, holder.txt_spots, "play", holder.tv_prize);
//                                        if (flag == 1) {
//                                            Intent intent = new Intent(activity, InstructionActivity.class);
//                                            intent.putExtra("quiz_id", model.getQuiz_id());
//                                            intent.putExtra("questions", model.getQuestions());
//                                            intent.putExtra("title", model.getTitle());
//                                            intent.putExtra("quiz_date", model.getQuiz_date());
//                                            intent.putExtra("start_time", model.getQuiz_start_time());
//                                            intent.putExtra("end_time", model.getQuiz_end_time());
//                                            intent.putExtra("duration", model.getDuration());
//                                            intent.putExtra("entry_fee", model.getEntry_fee());
//                                            intent.putExtra("ins", model.getInstruction());
//                                            intent.putExtra("lang", model.getLanguage());
//                                            intent.putExtra("quiz_type", model.getQuizType());
//                                            intent.putExtra("rewards", "");
//                                            activity.startActivity(intent);
//                                        } else if (flag == 3) {
//                                            loadingBar.dismiss();
//                                        }
//                                    }
//
//                                }
//
//
//                            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    q_status = "join";
//                                    dialog.dismiss();
//                                }
//                            });
//                            builder.show();
////
//                        }
//                        else {
//                            Intent intent = new Intent(activity, QuizStatusActivity.class);
//                            activity.startActivity(intent);
//                        }
                    }

                }
            }
        });
        Log.e("check_join " + position, "onBind : " + model.getTitle() + " :: " + checkVisibility(holder.btn_join));

    }

    private String checkVisibility(Button button) {
        String ts = "";
        if (button.getVisibility() == View.INVISIBLE) {
            ts = "invisible";
        } else if (button.getVisibility() == View.VISIBLE) {
            ts = "visible";
        } else {
            ts = "gone";
        }
        return ts;
    }

    private void goToRankActivity(QuizModel model, String jValue, String cnt_part) {
        Intent intent = new Intent(activity, RankActivity.class);
        intent.putExtra("title", model.getTitle());
        intent.putExtra("description", model.getDescription());
        intent.putExtra("quiz_date", model.getQuiz_date());
        intent.putExtra("quiz_id", model.getQuiz_id());
        intent.putExtra("start_time", model.getQuiz_start_time());
        intent.putExtra("end_time", model.getQuiz_end_time());
        intent.putExtra("duration", model.getDuration());
        intent.putExtra("entry_fee", model.getEntry_fee());
        intent.putExtra("rewards", "");
        intent.putExtra("questions", model.getQuestions());
        intent.putExtra("participants", model.getParticipents());
        intent.putExtra("ins", model.getInstruction());
        intent.putExtra("lang", model.getLanguage());
        intent.putExtra("joined", (jValue.equalsIgnoreCase("join") ? "false" : "true"));
        intent.putExtra("cnt_prtcpnts", cnt_part);
        intent.putExtra("quiz_type", model.getQuizType());
        intent.putExtra("q_status", q_status);
        activity.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return quiz_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txt_name, txt_desc, txt_rewards, txt_points, txt_timer, txt_quiz_ini, txt_spots;
        Button tv_prize, rank;
        Button btn_join, btn_play;

        //        CardView card_quiz ;
        LinearLayout lin_quiz, ln_play;
        RelativeLayout rel_bcl, rel_fcl, rel_quiz;
        ProgressBar p_progress;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_desc = itemView.findViewById(R.id.quiz_desc);
            txt_name = itemView.findViewById(R.id.quizname);
            tv_prize = itemView.findViewById(R.id.btn_prize);
            txt_points = itemView.findViewById(R.id.quiz_points);
            txt_timer = itemView.findViewById(R.id.quiz_timer);
            txt_rewards = itemView.findViewById(R.id.quiz_rewards);
            btn_join = itemView.findViewById(R.id.join);
            ln_play = itemView.findViewById(R.id.ln_play);
            btn_play = itemView.findViewById(R.id.play);
            txt_quiz_ini = itemView.findViewById(R.id.tv_sm);
            rel_quiz = itemView.findViewById(R.id.rel_quiz);
            lin_quiz = itemView.findViewById(R.id.lin_quiz);
            rank = itemView.findViewById(R.id.btn_ranks);
            rel_bcl = (RelativeLayout) itemView.findViewById(R.id.rel_bcl);
            rel_fcl = (RelativeLayout) itemView.findViewById(R.id.rel_fcl);
            p_progress = itemView.findViewById(R.id.pg_bar);
            txt_spots = itemView.findViewById(R.id.spots);

            toastMsg = new ToastMsg(activity);
            loadingBar = new ProgressDialog(activity);
            loadingBar.setMessage("Loading...");
            loadingBar.setCanceledOnTouchOutside(false);

            module = new Module(activity);
            sessionManagment = new SessionManagment(activity);

        }
    }

    public int joinQuiz(final QuizModel model, final String user_id, final String updatedWallet, final String updatedRewards, final String pncts, final Button btJoin, final ProgressBar progressBar, final TextView tvSpots, final String s, final TextView tvprize) {
        final int[] flag = {0};
        loadingBar.show();
        final String jId = getUniqueId();
        String quiz_id = model.getQuiz_id().toString();
        final Map<String, Object> quiz_map = new HashMap<>();
        quiz_map.put("quiz_id", quiz_id);
        quiz_map.put("description", model.getDescription().toString());
        quiz_map.put("entry_fee", model.getEntry_fee().toString());
        quiz_map.put("participents", model.getParticipents().toString());
        quiz_map.put("questions", model.getParticipents().toString());
        quiz_map.put("quiz_date", current_date.toString());
        quiz_map.put("quiz_end_time", model.getQuiz_end_time().toString());
        quiz_map.put("quiz_start_time", model.getQuiz_start_time().toString());
        quiz_map.put("duration", model.getDuration().toString());
        quiz_map.put("title", model.getTitle().toString());
        quiz_map.put("instruction", model.getInstruction().toString());
        quiz_map.put("language", model.getLanguage().toString());
        quiz_map.put("join_id", jId.toString());
        quiz_map.put("join_date", module.getCurrentDate().toString());
        quiz_map.put("user_id", user_id);
        quiz_map.put("date", module.getCurrentDate());
        quiz_map.put("time", module.getCurrentTime());
        final DatabaseReference dRef = FirebaseDatabase.getInstance().getReference().child("joined_quiz");
        Query query = dRef.orderByChild("quiz_id").equalTo(quiz_id);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int count = 0;

                ArrayList<JoinedQuizModel> tempList = new ArrayList<>();
                tempList.clear();
                String c_date = module.getCurrentDate();
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    JoinedQuizModel model = snap.getValue(JoinedQuizModel.class);
                    if (model.getJoin_date().equals(c_date)) {
                        tempList.add(model);
                        count++;
                    }
                }
                Log.d("TAG", "onDataChange: "+pncts);
                Log.d("TAG", "onDataChange: "+count);

                if (count >= Integer.parseInt(pncts)) {
                    loadingBar.dismiss();
                    toastMsg.toastIconError("No Space is available");
                    flag[0] = 3;
                } else {
                    if (module.getJoinStatus(tempList, user_id)) {
                        loadingBar.dismiss();
                        toastMsg.toastIconError("You already join this quiz");
                    } else {
                        module.insertHistory(model, user_id);

                        dRef.child(jId).updateChildren(quiz_map).addOnCompleteListener(activity, new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                updateReceiver(updatedWallet);
                                updateRewardReceiver(updatedRewards);

                                module.updateDbWallet(user_id, updatedWallet);
                                module.updateDbRewards(user_id, updatedRewards);
                                int p = progressBar.getProgress();
                                progressBar.setProgress(p + 1);
                                tvSpots.setText(String.valueOf(module.getSpotLeft(tvSpots.getText().toString())) + "/" + pncts);
                                loadingBar.dismiss();
                                btJoin.setText("Joined");
                                tvprize.setVisibility(View.VISIBLE);

                                q_status = "joined";
                                btJoin.setEnabled(false);
                                if (s.equalsIgnoreCase("play")) {
                                    flag[0] = 1;
                                } else {
                                    flag[0] = 2;
//                                Toast.makeText(activity,"You have successfully joined , we'll notify you when the quiz starts",Toast.LENGTH_LONG).show();
                                }


                            }
                        }).addOnFailureListener(activity, new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                loadingBar.dismiss();
                                Toast.makeText(activity, "Please try again later" + e.getMessage(), Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                loadingBar.dismiss();
                module.showToast(databaseError.getMessage().toString());
            }
        });

        return flag[0];
    }


    public String getUniqueId() {
        String ss = "jq";
        String unique_id = "";
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ddMMyyyyHHmmss");
        unique_id = ss + simpleDateFormat.format(date).toString();
        return unique_id;
    }
//public void getUserQuizList(final String user_id , final String quiz_id , final Button join)
//{
//    DatabaseReference dRef = FirebaseDatabase.getInstance().getReference().child("joined_quiz");
//    Query query = dRef.orderByChild("user_id").equalTo(user_id);
//    query.addValueEventListener(new ValueEventListener() {
//        @Override
//        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//            if (dataSnapshot.hasChildren())
//            {
//                for(DataSnapshot data : dataSnapshot.getChildren())
//                {
//                    JoinedQuizModel model = data.getValue(JoinedQuizModel.class);
//
////                    if (model.getUser_id().equalsIgnoreCase(user_id))
////                    {
//                        if (model.getQuiz_id().equals(quiz_id)) {
//                            join.setText("Joined");
//                            q_status = "joinned";
//                        }
//                        else
//                        {
//                            join.setText("Join");
//                            q_status = "join";
//                        }
//                }
//            }
//
//        }
//
//        @Override
//        public void onCancelled(@NonNull DatabaseError databaseError) {
//        }
//    });
//
//}


    private String getPrticipants(ArrayList<JoinedQuizModel> list, final String quiz_id, final ProgressBar pg_bar, final String tot_participants, TextView tv_spots) {
        int cnt = 0;
        for (int i = 0; i < list.size(); i++) {
            String qID = list.get(i).getQuiz_id().toString();
            if (qID.equalsIgnoreCase(quiz_id)) {
                cnt++;
            }

        }
        pg_bar.setMax(Integer.parseInt(tot_participants));
//            pg_bar.setProgress(getPercentage(cnt, Integer.parseInt(tot_participants)));
        pg_bar.setProgress(cnt);
        int diff = Integer.parseInt(tot_participants) - cnt;
        tv_spots.setText("" + diff + "/" + tot_participants);
        return String.valueOf(cnt);
    }

    int getPercentage(int start, int end) {
        int p = 0;
        if (end != 0) {
            p = (start * 100) / end;
        } else {
            p = 0;
        }
        return p;
    }

    private void updateCounterText(long time_in_milis, TextView txt_time, String quiz_date, String time) {
        int minutes = (int) (time_in_milis / 1000) / 60;
        int seconds = (int) (time_in_milis / 1000) % 60;
        String timeLeftInFormat = "";
        int days = (int) (time_in_milis / (1000 * 60 * 60 * 24));
        if (!getTodayDateStatus(quiz_date)) {
            timeLeftInFormat = quiz_date + " " + time;
        } else {
            timeLeftInFormat = String.format(Locale.getDefault(), "%02d:%02d:%02d",
                    TimeUnit.MILLISECONDS.toHours(time_in_milis),
                    TimeUnit.MILLISECONDS.toMinutes(time_in_milis) -
                            TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(time_in_milis)),
                    TimeUnit.MILLISECONDS.toSeconds(time_in_milis) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time_in_milis)));
        }

        txt_time.setText(timeLeftInFormat);
    }

    public String getTimeIn24(String tm) {
        //12:05 PM
        String[] tm_arr = tm.split(" ");
        String t_fs = "";
        String fm = tm_arr[1].toString();
        if (fm.equalsIgnoreCase("a.m.") || fm.equalsIgnoreCase("AM")) {
            t_fs = tm_arr[0] + ":00";
        } else if (fm.equalsIgnoreCase("p.m.") || fm.equalsIgnoreCase("PM")) {
            String[] t_arr = tm_arr[0].split(":");
            int t = Integer.parseInt(t_arr[0].toString());
            if (t != 12) {
                t = t + 12;
            } else {
                t = 12;
            }

            t_fs = String.valueOf(t) + ":" + t_arr[1].toString() + ":00";
        }
        return t_fs;
    }


    public long getPlayStatus(String sDate, String sTime, String eTime, String type) {
        long tmValue = 0;
        Date date = new Date();
        if (getTodayDateStatus(sDate)) {
            SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");

            Date d1 = null;
            Date d2 = null;
            Date cd3 = null;

            try {
                d1 = format.parse(sTime);
                d2 = format.parse(eTime);
                cd3 = format.parse(format.format(date));
                //in milliseconds
                long diff = d2.getTime() - d1.getTime();
                long diffcs = cd3.getTime() - d1.getTime();
                long diffce = cd3.getTime() - d2.getTime();

                if (type.equalsIgnoreCase("start")) {
                    tmValue = diffcs;
                } else if (type.equalsIgnoreCase("end")) {
                    tmValue = diffce;
                }
                //    long diffHours = diff / (60 * 60 * 1000) % 24;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            toastMsg.toastIconError("Quiz Not Started Yet. Please Wait..");
        }
        return tmValue;
    }

    public boolean getTodayDateStatus(String date) {
        boolean flag = false;
        Date cDate = new Date();
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        String cTime = timeFormat.format(cDate);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Date selDate = null;
        try {
            selDate = simpleDateFormat.parse(date + " " + cTime);
        } catch (Exception ex) {
            ex.getMessage();
        }


        String cDateStr = String.valueOf(cDate);
        String sDateStr = String.valueOf(selDate);
        if (cDateStr.equalsIgnoreCase(sDateStr)) {
            flag = true;
        } else {
            flag = false;
        }
        return flag;
    }

    public String getTimeValue(String sTime) {
        Date st = null;
        String stm = "";
        Date curr = new Date();
        SimpleDateFormat dtm_Format = new SimpleDateFormat("hh:mm a");

        String tm = dtm_Format.format(curr);
        SimpleDateFormat format24 = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat parseFormat = new SimpleDateFormat("hh:mm a");
        try {
            st = parseFormat.parse(module.getAccTimeFormat(tm, sTime));
            stm = format24.format(st);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return stm;
    }

    //Joinned quiz
    public boolean checkJoinnedQuiz(ArrayList<JoinedQuizModel> list, String q_id) {
        boolean flag = false;
        for (int l = 0; l < list.size(); l++) {
            String sds = list.get(l).getQuiz_id().toString();
            if (sds.equalsIgnoreCase(q_id)) {
                flag = true;
                break;
            }
        }
        return flag;
    }

    public void updateReceiver(String amt) {
        Intent intent = new Intent(BROAD_WALLET);
        intent.putExtra("type", "update");
        intent.putExtra("amount", amt);
        activity.sendBroadcast(intent);
    }

    public void updateRewardReceiver(String amt) {
        Intent intent = new Intent(BROAD_REWARDS);
        intent.putExtra("type", "update");
        intent.putExtra("amount", amt);
        activity.sendBroadcast(intent);
    }

    public void loadISNTAd(String quiz_id) {
        mInterstitialAd = new InterstitialAd(activity);

        // Insert the Ad Unit ID
        mInterstitialAd.setAdUnitId(Constants.ad_intertitial);

        // Create ad request.
        AdRequest adRequest = new AdRequest.Builder().build();

        // Begin loading your interstitial.
        mInterstitialAd.loadAd(adRequest);

        // Prepare an Interstitial Ad Listener
        mInterstitialAd.setAdListener(new AdListener() {
            public void onAdLoaded() {
                // Call displayInterstitial() function
            }

            @Override
            public void onAdClosed() {
                super.onAdClosed();
                Intent intent = new Intent(activity, PrizeActivity.class);
                intent.putExtra("quiz_id", quiz_id);
                activity.startActivity(intent);
            }

            @Override
            public void onAdFailedToLoad(LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
            }

            @Override
            public void onAdClicked() {
                super.onAdClicked();
            }
        });
    }

    public void loadISNTAd1(QuizModel model, String jv, String cnt_part) {
        mInterstitialAd1 = new InterstitialAd(activity);

        // Insert the Ad Unit ID
        mInterstitialAd1.setAdUnitId(Constants.ad_intertitial);

        // Create ad request.
        AdRequest adRequest = new AdRequest.Builder().build();

        // Begin loading your interstitial.
        mInterstitialAd1.loadAd(adRequest);

        // Prepare an Interstitial Ad Listener
        mInterstitialAd1.setAdListener(new AdListener() {
            public void onAdLoaded() {
                // Call displayInterstitial() function
            }

            @Override
            public void onAdClosed() {
                super.onAdClosed();
                goToRankActivity(model, jv, cnt_part);

            }

            @Override
            public void onAdFailedToLoad(LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
            }

            @Override
            public void onAdClicked() {
                super.onAdClicked();
            }
        });
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

}
