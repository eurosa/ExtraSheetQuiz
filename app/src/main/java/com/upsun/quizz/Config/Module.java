package com.upsun.quizz.Config;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.util.Log;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.upsun.quizz.Intefaces.OnJoinedQuizData;
import com.upsun.quizz.Intefaces.OnQuizListData;
import com.upsun.quizz.Intefaces.OnQuizRankRewardListener;
import com.upsun.quizz.Intefaces.OnRankListener;
import com.upsun.quizz.Intefaces.OnUsersData;
import com.upsun.quizz.Model.CalculateRankModel;
import com.upsun.quizz.Model.JoinedQuizModel;
import com.upsun.quizz.Model.NewRankModel;
import com.upsun.quizz.Model.QuizModel;
import com.upsun.quizz.Model.QuizRankRewardModel;
import com.upsun.quizz.Model.QuizResultModel;
import com.upsun.quizz.Model.RewardModel;
import com.upsun.quizz.Model.UserModel;
import com.upsun.quizz.R;
import com.upsun.quizz.networkconnectivity.NoInternetConnection;
import com.upsun.quizz.utils.SessionManagment;

import static android.content.ContentValues.TAG;
import static com.upsun.quizz.Config.Constants.HISTORY_REF;
import static com.upsun.quizz.Config.Constants.JOINED_QUIZ_REF;
import static com.upsun.quizz.Config.Constants.KEY_REWARDS;
import static com.upsun.quizz.Config.Constants.KEY_WALLET;
import static com.upsun.quizz.Config.Constants.QUIZ_REF;
import static com.upsun.quizz.Config.Constants.REDUCTION_WALLET_AMOUNT;
import static com.upsun.quizz.Config.Constants.USER_REF;

/**
 * Developed by Binplus Technologies pvt. ltd.  on 08,April,2020
 */
public class Module {
    Context context;

    public Module(Context context) {
        this.context = context;
    }

    public String getUniqueId(String type) {
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ddMMyyyyHHmmss");
        return (type + simpleDateFormat.format(date).toString());
    }

    public String getAccTimeFormat(String ctime, String sp_time) {
        //12:04 AM
        //12:09 a.m.
        String finalTm = "";
        String[] ctm_arr = ctime.split(" ");
        String ctmFormat = ctm_arr[1].toString();
        String fm = "";
        String[] sp_time_arr = sp_time.split(" ");
        if (ctmFormat.equalsIgnoreCase("a.m.") || ctmFormat.equalsIgnoreCase("p.m.")) {
            String spTime_fm = sp_time_arr[1].toString();
            if (spTime_fm.equalsIgnoreCase("a.m.")) {
                fm = sp_time_arr[0].toString() + " " + "a.m.";
            } else if (spTime_fm.equalsIgnoreCase("p.m.")) {
                fm = sp_time_arr[0].toString() + " " + "p.m.";
            } else if (spTime_fm.equalsIgnoreCase("AM")) {
                fm = sp_time_arr[0].toString() + " " + "a.m.";
            } else if (spTime_fm.equalsIgnoreCase("PM")) {
                fm = sp_time_arr[0].toString() + " " + "p.m.";
            }
            finalTm = fm;
        } else if (ctmFormat.equalsIgnoreCase("AM") || ctmFormat.equalsIgnoreCase("PM")) {
            String spTime_fm = sp_time_arr[1].toString();
            if (spTime_fm.equalsIgnoreCase("AM")) {
                fm = sp_time_arr[0].toString() + " " + "AM";
            } else if (spTime_fm.equalsIgnoreCase("PM")) {
                fm = sp_time_arr[0].toString() + " " + "PM";
            } else if (spTime_fm.equalsIgnoreCase("a.m.")) {
                fm = sp_time_arr[0].toString() + " " + "AM";
            } else if (spTime_fm.equalsIgnoreCase("p.m.")) {
                fm = sp_time_arr[0].toString() + " " + "PM";
            }
            finalTm = fm;
        }
        return finalTm;
    }

    public long getTimeDiffInLong(String qDate, String stime) {
        long difference = 0;
        DateFormat outputformat = new SimpleDateFormat("dd-MM-yyyy");
        Date curr = new Date();
        try {
            Date s_time = outputformat.parse(qDate);
            difference = s_time.getTime() - curr.getTime();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return difference;
    }
    public boolean getTimeDiffInbol(String qDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Date strDate = null;
        try {
            strDate = sdf.parse(qDate);
            String c_date=sdf.format(new Date());
            if (sdf.parse(c_date).compareTo(strDate)>0) {
                return true;
            }
            else {
                return false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    public long getDiffInLong(String qDate, String stime) {
        long difference = 0;
        String current_date = "03-10-2022 00:00:00";
        try {
            String quiz_start = qDate + " " + stime;
            DateFormat parseformat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            Date c_time = parseformat.parse(current_date);
            Date s_time = parseformat.parse(quiz_start);
            difference = s_time.getTime() - c_time.getTime();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return difference;
    }

    public void updateDbWallet(String user_id, String amt) {
        DatabaseReference user_ref = FirebaseDatabase.getInstance().getReference().child(USER_REF);
        HashMap<String, Object> params = new HashMap<>();
        params.put("wallet", amt);
        user_ref.child(user_id).updateChildren(params).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.e("taskk", "Succesffull..");
                } else {
                    Toast.makeText(context, "" + task.getException().getMessage().toString(), Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    public void updateDbRewards(String user_id, String amt) {
        DatabaseReference user_ref = FirebaseDatabase.getInstance().getReference().child(USER_REF);
        HashMap<String, Object> params = new HashMap<>();
        params.put("rewards", amt);
        user_ref.child(user_id).updateChildren(params).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.e("taskk", "Succesffull..");
                } else {
                    Toast.makeText(context, "" + task.getException().getMessage().toString(), Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    public String getLanguage(RadioButton chkEnglish, RadioButton chkHindi) {
        String str = "";
        if (chkEnglish.isChecked() == true && chkHindi.isChecked() == true) {
            str = "both";
        } else {
            if (chkEnglish.isChecked() == true) {
                str = "English";
            } else if (chkHindi.isChecked() == true) {
                str = "Hindi";
            }
        }
        return str;
    }

    public String getDateFromID(String id, String type) {
        String sr = id.substring(type.length(), id.length());
        String date = sr.subSequence(0, 2) + "/" + sr.subSequence(2, 4) + "/" + sr.subSequence(4, 8);
        return date;
    }

    public String getTimeFromID(String id, String type) {//jq17042020023424
        //14
        String tt = "";
        String sr = id.substring(type.length(), id.length());
        String time = sr.subSequence(8, 10) + ":" + sr.subSequence(10, 12) + ":" + sr.subSequence(12, 14);
        SimpleDateFormat format_t = new SimpleDateFormat("hh:mm:ss");
        SimpleDateFormat f = new SimpleDateFormat("hh:mm a");
        try {
            Date t = format_t.parse(time);
            tt = f.format(t);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return tt;
    }


    public void showToast(String s) {
        Toast.makeText(context, "" + s, Toast.LENGTH_SHORT).show();
    }

    public boolean existCounter(ArrayList<String> list, int count) {
        boolean flag = false;
        for (int i = 0; i < list.size(); i++) {
            if (i == count) {
                flag = true;
                break;
            }
        }
        return flag;
    }

    public String getLinerSelectedAns(LinearLayout linA, LinearLayout linB, LinearLayout linC, LinearLayout linD) {
        String result = "none";
        int colorLinA = ((ColorDrawable) linA.getBackground()).getColor();
        int colorLinB = ((ColorDrawable) linB.getBackground()).getColor();
        int colorLinC = ((ColorDrawable) linC.getBackground()).getColor();
        int colorLinD = ((ColorDrawable) linD.getBackground()).getColor();
        if (colorLinA == context.getResources().getColor(R.color.rc_8)) {
            result = "a";
        } else if (colorLinB == context.getResources().getColor(R.color.rc_8)) {
            result = "b";
        } else if (colorLinC == context.getResources().getColor(R.color.rc_8)) {
            result = "c";
        } else if (colorLinD == context.getResources().getColor(R.color.rc_8)) {
            result = "d";
        } else {
            result = "none";
        }
        return result;
    }

    public int getPercentage(int correct, int tot) {
        int p = 0;
        if (tot != 0) {
            p = (correct * 100) / tot;
        } else {
            p = 0;
        }
        return p;
    }

    public long getDiffernceEC(String end_time) {
        long diff = 0;
        Date c_date = new Date();
        DateFormat outputformat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

        SimpleDateFormat format24 = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat parseFormat = new SimpleDateFormat("hh:mm a");
        String current_date = parseFormat.format(c_date);

        String tm = parseFormat.format(c_date);
        try {
            Date e_time = parseFormat.parse(getAccTimeFormat(tm, end_time));
            Date c_time = parseFormat.parse(current_date);
            diff = e_time.getTime() - c_time.getTime();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return diff;
    }

    public long getDuration(String start_time, String end_time) {
        long diff = 0;
        Date c_date = new Date();
        DateFormat outputformat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

        SimpleDateFormat format24 = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat parseFormat = new SimpleDateFormat("hh:mm a");
        String current_date = parseFormat.format(c_date);

        String tm = parseFormat.format(c_date);
        try {
            Date e_time = parseFormat.parse(getAccTimeFormat(tm, end_time));
            Date s_time = parseFormat.parse(getAccTimeFormat(tm, start_time));
            diff = e_time.getTime() - s_time.getTime();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return diff;
    }

    public ArrayList<CalculateRankModel> getRankAllUsers(ArrayList<QuizResultModel> list) {
        ArrayList<CalculateRankModel> calList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            int result = 0;
            int correctAns = Integer.parseInt(list.get(i).getCorrect_ans());
            result = (correctAns);
            calList.add(new CalculateRankModel(String.valueOf(result), list.get(i).getUser_id(), list.get(i).getUsername()));
        }
        return calList;
    }

    public ArrayList<NewRankModel> getNewAllRankUsers(ArrayList<QuizResultModel> list) {
        ArrayList<NewRankModel> rankList = new ArrayList<>();

        int correct = Integer.parseInt(list.get(0).getCorrect_ans());
        int rank = 1;
        rankList.add(new NewRankModel(String.valueOf(correct), list.get(0).getUser_id(), list.get(0).getUsername(), String.valueOf(rank)));
        for (int i = 1; i < list.size(); i++) {
            Log.e(TAG, "getNewAllRankUsers: " + list.get(i).getUsername());
            if (Integer.parseInt(list.get(i).getCorrect_ans()) == correct) {

                rankList.add(new NewRankModel(String.valueOf(correct), list.get(i).getUser_id(), list.get(i).getUsername(), String.valueOf(rank)));
            } else {
                correct = Integer.parseInt(list.get(i).getCorrect_ans());
                rank = rankList.size() + 1;
                rankList.add(new NewRankModel(String.valueOf(correct), list.get(i).getUser_id(), list.get(i).getUsername(), String.valueOf(rank)));

            }
        }
        for (int j = 0; j < rankList.size(); j++) {
            Log.e("Ranks", rankList.get(j).getRank() + " " + rankList.get(j).getUsername());
        }
        return rankList;
    }

    public int getRank(final ArrayList<NewRankModel> list, String user_id) {
        int rank = 0;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getUser_id().equalsIgnoreCase(user_id)) {
                rank = Integer.parseInt(list.get(i).getRank());
            }
        }
        return rank;
    }

    public void noConnectionActivity() {
        Intent intent = new Intent(context, NoInternetConnection.class);
        context.startActivity(intent);
    }

    public void getQuizList(final ProgressDialog loadingBar, final OnQuizListData onQuizListData) {
        final ArrayList<QuizModel> list = new ArrayList<>();
        DatabaseReference quizRef = FirebaseDatabase.getInstance().getReference().child(QUIZ_REF);
        quizRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                loadingBar.dismiss();
                try {
                    if (dataSnapshot.hasChildren()) {
                        for (DataSnapshot snap : dataSnapshot.getChildren()) {
                            QuizModel model = snap.getValue(QuizModel.class);
                            list.add(model);

                        }
                        onQuizListData.getQuizList(list);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showToast("Something wwnt wrong");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                loadingBar.dismiss();
                showToast("" + databaseError.getMessage());
            }
        });


    }

    public void getQuizRankRewards(final String user_id, final ProgressDialog loadingBar, final OnQuizRankRewardListener onQuizRankRewardListener) {
        final ArrayList<QuizRankRewardModel> rewardList = new ArrayList<>();
        final ArrayList<String> rankList = new ArrayList<>();
        DatabaseReference quizRef = FirebaseDatabase.getInstance().getReference().child("quiz_rank_reward");
        quizRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                loadingBar.dismiss();
                try {
                    if (dataSnapshot.hasChildren()) {
                        rewardList.clear();
                        rankList.clear();
                        for (DataSnapshot snap : dataSnapshot.getChildren()) {
                            QuizRankRewardModel model = snap.getValue(QuizRankRewardModel.class);
                            rewardList.add(model);
                            if (model.getUser_id().equalsIgnoreCase(user_id)) {
                                rankList.add(model.getRank());
                            }

                        }
                        onQuizRankRewardListener.getQuizRankRewards(rewardList);
                        onQuizRankRewardListener.getUserQuizRank(rankList);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showToast("Something wwnt wrong");
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                loadingBar.dismiss();
                showToast("" + databaseError.getMessage());
            }
        });

    }

    public void getJoinedQuizList(final String user_id, final ProgressDialog loadingBar, final OnJoinedQuizData onJoinedQuizData) {
        final ArrayList<JoinedQuizModel> list = new ArrayList<>();
        final ArrayList<JoinedQuizModel> jList = new ArrayList<>();
        DatabaseReference quizRef = FirebaseDatabase.getInstance().getReference().child(JOINED_QUIZ_REF);
        quizRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                loadingBar.dismiss();
                try {
                    if (dataSnapshot.hasChildren()) {
                        for (DataSnapshot snap : dataSnapshot.getChildren()) {
                            JoinedQuizModel model = snap.getValue(JoinedQuizModel.class);
                            list.add(model);
                            if (model.getUser_id().equalsIgnoreCase(user_id)) {
                                jList.add(model);
                            }
                        }
                        onJoinedQuizData.getJoinedList(list);
                        onJoinedQuizData.getUserJoinedList(jList);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showToast("Something wwnt wrong");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                loadingBar.dismiss();
                showToast("" + databaseError.getMessage());
            }
        });
    }


    public void getRankRewards(final String quiz_id, final ProgressDialog loadingBar, final ArrayList<NewRankModel> calList, final OnRankListener onRankListener) {
        loadingBar.show();
        final ArrayList<RewardModel> list = new ArrayList<>();
        DatabaseReference rankRef = FirebaseDatabase.getInstance().getReference().child("ranks").child(quiz_id);
        Log.d(TAG, "getRankRewards: "+quiz_id);

        rankRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                loadingBar.dismiss();
                try {
                    if (dataSnapshot.hasChildren()) {
                        for (DataSnapshot snap : dataSnapshot.getChildren()) {
                            RewardModel model = snap.getValue(RewardModel.class);
                            list.add(model);
                        }
                        onRankListener.getRankRewards(list);
                        onRankListener.getCalListRewards(calList);

                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showToast("Something Went Wrong");
                    Log.d(TAG, "onDataChange: "+ex.getMessage());
                    Log.d(TAG, "onDataChange: "+ex.getLocalizedMessage());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                loadingBar.dismiss();
                showToast("" + databaseError.getMessage());
            }
        });

    }

    public void getUserList(final String user_id, final ProgressDialog loadingBar, final OnUsersData onUsersData) {
        final ArrayList<UserModel> list = new ArrayList<>();
        DatabaseReference quizRef = FirebaseDatabase.getInstance().getReference().child(USER_REF);
        Query query = quizRef.orderByChild("id").equalTo(user_id);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                loadingBar.dismiss();
                try {
                    if (dataSnapshot.hasChildren()) {
                        for (DataSnapshot snap : dataSnapshot.getChildren()) {
                            UserModel model = snap.getValue(UserModel.class);
                            if (model.getId().toString().equalsIgnoreCase(user_id)) {
                                list.add(model);
                            }

                        }
                        onUsersData.getUserList(list);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    showToast("Something went wrong");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                loadingBar.dismiss();
                showToast("" + databaseError.getMessage());
            }
        });
    }

    public int getSpotLeft(String str) {
        String[] sttr = str.split("/");
        return (Integer.parseInt(sttr[0]) - 1);
    }

    public void reqEditFocus(EditText et, String s) {
        et.setError(s);
        et.requestFocus();
    }

    public int getDateForCompare(String date) {
        String[] dateArr = date.split("-");
        int dt = Integer.parseInt(dateArr[0].toString());
        int month = Integer.parseInt(dateArr[1].toString());
        int year = Integer.parseInt(dateArr[2].toString());

        int tot_date = dt + (month * 100) + year;
        return tot_date;

    }

    public int[] getReductionCondition(
            SessionManagment sessionManagment, int entry_fee, int reward_fee) {
        int[] rt = new int[3];
        int cond = 0;
        int wc = 0, rc = 0;
        int rm = REDUCTION_WALLET_AMOUNT;
        int remainingRewardCut;
        int remainingWalletCut;
        int sesssionWallet = Integer.parseInt(sessionManagment.getUserDetails().get(KEY_WALLET).toString());
        int sesssionRewards = Integer.parseInt(sessionManagment.getUserDetails().get(KEY_REWARDS).toString());

        if ((sesssionWallet + sesssionRewards) < (entry_fee + reward_fee)) {
            cond = 1;
        } else {
//            if(rm<=sesssionWallet)
//            {
//                if(rm<entry_fee)
//                {
//
//                    wc=rm;
//                }
//                else
//                {
//                    wc=entry_fee;
//                }
//                sesssionWallet=sesssionWallet-wc;
//
//            }
//            else
//            {
//             wc=sesssionWallet;
//             sesssionWallet=0;
//            }
//            rc=entry_fee-wc;
//            sesssionRewards=sesssionRewards-rc;

            if (sesssionRewards < reward_fee) {

                remainingRewardCut = reward_fee - sesssionRewards;
                sesssionRewards = 0;

                sesssionWallet = sesssionWallet - (entry_fee + remainingRewardCut);

            } else {
                sesssionRewards = sesssionRewards - reward_fee;

                if (sesssionWallet < entry_fee) {
                    remainingWalletCut = entry_fee - sesssionWallet;
                    sesssionWallet = 0;
                    sesssionRewards = sesssionRewards - remainingWalletCut;

                } else {
                    sesssionWallet = sesssionWallet - entry_fee;
                }
            }


        }
        rt[0] = cond;
        rt[1] = sesssionWallet;
        rt[2] = sesssionRewards;

        return rt;
    }

    public boolean getJoinStatus(ArrayList<JoinedQuizModel> list, String user_id) {
        boolean flag = false;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getUser_id().toString().equalsIgnoreCase(user_id)) {
                flag = true;
                break;
            }
        }
        return flag;
    }

    public int getDateDiff(String dt_str) {//02-07-2020
        int days = 0;
        SimpleDateFormat myFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date date = new Date();
        SimpleDateFormat smpl = new SimpleDateFormat("dd-MM-yyyy");
        String inputString2 = dt_str;
        String c_date = smpl.format(date);

        try {
            Date date1 = myFormat.parse(c_date);
            Date date2 = myFormat.parse(inputString2);
            long diff = date1.getTime() - date2.getTime();
//            Log.e("days_count","Days: " + TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS));
            days = (int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return days;
    }


    public String getCurrentSimpleDate(String str) {
        String c_date = "";
        try {
            Date date = new Date();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ddMMyyyy");
            c_date = str + simpleDateFormat.format(date);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return c_date;
    }

    public String getPreviousSimpleDate(String str) {

        String c_date = "";

        try {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -1);
            SimpleDateFormat s = new SimpleDateFormat("ddMMyyyy");
            c_date = str + s.format(new Date(cal.getTimeInMillis()));
            ;
        } catch (Exception ex) {
            ex.printStackTrace();
            //Toast.makeText(OddEvenActivity.this,""+ex.getMessage(),Toast.LENGTH_LONG).show();
        }

        return c_date.toString();

    }

    public long getMilliSeconds(String str) {
//        Log.e("getMilliSeconds: ", str);
        String[] strArr = str.split(":");
        Log.d(TAG, "getMilliSeconds: "+strArr[0]);
        long secLong = 0;
        if(strArr.length > 1) {
            int sec = Integer.parseInt(strArr[2].toString());
            int min = Integer.parseInt(strArr[1].toString());
            int hours = Integer.parseInt(strArr[0].toString());
            int minSecs = min * 60;
            int hoursSecs = hours * 60 * 60;
            secLong = hoursSecs + minSecs + sec;
        }else {
            secLong = Integer.parseInt(strArr[0].toString());
        }
        /*LocalTime zero = LocalTime.parse("00:00:00");//00:01:00
        LocalTime time = LocalTime.parse(str);
        long mils = ChronoUnit.MILLIS.between(zero, time);*/
        return TimeUnit.SECONDS.toMillis(secLong);

    }

    public boolean checkNull(String str) {
        if (str == null || str.isEmpty() || str.equalsIgnoreCase("null"))
            return true;
        else
            return false;
    }

    public String checkNullString(String str) {
        if (str == null || str.isEmpty() || str.equalsIgnoreCase("null"))
            return "";
        else
            return str;
    }

    public String getUserRewardResult(ArrayList<RewardModel> list, String rank, Map.Entry<Object, List<NewRankModel>> entry) {
        String reward = "";
        Log.d(TAG, "getUserReward: ==>" + list);

        if (entry.getValue().size() > 1) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getRank().equalsIgnoreCase(rank)) {
                    int re = i + entry.getValue().size();
                    int v = re;
                    if (re >= list.size()) v = list.size();
                    List<RewardModel> extractList = list.subList(i, v);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        long sum = extractList.stream().collect(Collectors.summarizingInt(r -> Integer.parseInt(r.getRewards()))).getSum();
                        reward = String.valueOf(sum / entry.getValue().size());
                    }

                }
            }
        } else {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getRank().equalsIgnoreCase(rank)) {
                    reward = list.get(i).getRewards();
                }
            }
        }
        return reward;
    }

    public String getUserReward(ArrayList<RewardModel> list, String rank) {
        String reward = "";
        for (int i = 0; i < list.size(); i++) {
            Log.d(TAG, "getUserReward: ==>" + list);
            if (list.get(i).getRank().equalsIgnoreCase(rank)) {
                reward = list.get(i).getRewards();
            }
        }
        return reward;
    }

    public void deleteNode(String nodeName) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child(nodeName).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    showToast(nodeName + " Deleted...");
                } else {
                    showToast("" + task.getException());
                }

            }
        });

    }

    public String getCurrentDate() {
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
        return simpleDateFormat.format(currentTime);
    }

    public String getCurrentTime() {
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);
        return simpleDateFormat.format(currentTime);
    }

    public void enqueuePeriodicRequest(Class<? extends Worker> workerClass) {
        int intervalHours = 2;
        PeriodicWorkRequest.Builder preBuilder = new PeriodicWorkRequest.Builder(workerClass, intervalHours, TimeUnit.HOURS);
        preBuilder.setConstraints(getConstraints());
        PeriodicWorkRequest workRequest = preBuilder.build();

        Long recordsCount = 0L;
        ExistingPeriodicWorkPolicy workPolicy = recordsCount == 0 ? ExistingPeriodicWorkPolicy.REPLACE : ExistingPeriodicWorkPolicy.KEEP;
        WorkManager.getInstance().enqueueUniquePeriodicWork(workerClass.getSimpleName(), workPolicy, workRequest);
    }

    public Constraints getConstraints() {
        return new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build();
    }

    public String getPrevThirdDate(String curDate, int days) {
        try {
            final SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
            final Date date = format.parse(curDate);
            final Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.DAY_OF_YEAR, days);
            return format.format(calendar.getTime());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;

    }

    public void insertHistory(QuizModel model, String user_id) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("quiz_id", model.getQuiz_id());
        map.put("quiz_name", model.getTitle());
        map.put("start_time", model.getQuiz_start_time());
        map.put("end_time", model.getQuiz_end_time());
        map.put("quiz_date", model.getQuiz_date());
        map.put("date", getCurrentDate());
        map.put("time", getCurrentTime());
        map.put("entry_fees", model.getEntry_fee());
        map.put("id", getUniqueId("hs"));
        map.put("user_id", user_id);
        map.put("answer", "0");
        map.put("rank", "0");
        map.put("reward", "0");
        map.put("section_id", "");
        DatabaseReference historyRef = FirebaseDatabase.getInstance().getReference().child(HISTORY_REF);
        historyRef.child(map.get("id").toString()).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.e(TAG, "history Save");
                } else {
                    Log.e(TAG, "Error" + task.getException().getMessage());
                }
            }
        });
    }

    public void updateRankRewards(String id, String rank, String reward) {
        DatabaseReference hisRef = FirebaseDatabase.getInstance().getReference().child(HISTORY_REF);
        HashMap<String, Object> map = new HashMap<>();
        map.put("rank", rank);
        map.put("reward", reward);
        Log.e(TAG, "updateRankRewards: " + map.toString());
        hisRef.child(id).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.e(TAG, "Rank and Reward updatdd on " + id + " :: " + map.toString());
                }
            }
        });
    }

    public void updateAnswer(String id, String ans, String section_id) {
        DatabaseReference hisRef = FirebaseDatabase.getInstance().getReference().child(HISTORY_REF);
        HashMap<String, Object> map = new HashMap<>();
        map.put("answer", ans);
        map.put("section_id", section_id);
        hisRef.child(id).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.e(TAG, "Rank and Reward updatdd on " + id + " :: " + map.toString());
                }
            }
        });
    }


}
