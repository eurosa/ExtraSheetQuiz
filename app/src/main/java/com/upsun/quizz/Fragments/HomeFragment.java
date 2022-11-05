package com.upsun.quizz.Fragments;

import static com.upsun.quizz.Config.Constants.BROAD_REWARDS;
import static com.upsun.quizz.Config.Constants.BROAD_WALLET;
import static com.upsun.quizz.Config.Constants.CONFIG_REF;
import static com.upsun.quizz.Config.Constants.JOINED_QUIZ_REF;
import static com.upsun.quizz.Config.Constants.KEY_ID;
import static com.upsun.quizz.Config.Constants.KEY_QUIZ_STATUS;
import static com.upsun.quizz.Config.Constants.KEY_WALLET;
import static com.upsun.quizz.Config.Constants.QUIZ_REF;
import static com.upsun.quizz.Config.Constants.REDUCTION_REWARDS_AMOUNT;
import static com.upsun.quizz.Config.Constants.REDUCTION_WALLET_AMOUNT;
import static com.upsun.quizz.Config.Constants.USER_REF;
import static com.upsun.quizz.Config.Constants.WITHDRAW_LIMIT_AMOUNT;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.upsun.quizz.Adapter.QuizAdapter;
import com.upsun.quizz.Config.Module;
import com.upsun.quizz.MainActivity;
import com.upsun.quizz.Model.JoinedQuizModel;
import com.upsun.quizz.Model.QuizModel;
import com.upsun.quizz.Model.UpdateModel;
import com.upsun.quizz.Model.UserModel;
import com.upsun.quizz.QuizStatusActivity;
import com.upsun.quizz.R;
import com.upsun.quizz.networkconnectivity.NetworkConnection;
import com.upsun.quizz.utils.SessionManagment;
import com.upsun.quizz.utils.ToastMsg;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
    public static ArrayList<QuizModel> allQuizList;
    RecyclerView rec_quiz;
    QuizAdapter quizAdapter;
    ArrayList<QuizModel> quiz_list;
    ArrayList<JoinedQuizModel> jList;
    ArrayList<JoinedQuizModel> pList;
    ArrayList<JoinedQuizModel> prctList;
    ArrayList<UserModel> uList;
    DatabaseReference quizRef, userRef, joinedRef;
    ;
    ProgressDialog loadingBar;
    String user_id, wallet_amt = "";
    ToastMsg toastMsg;
    Module module;
    SessionManagment sessionManagment;
    RelativeLayout rel_no_items, rel_pay;
    int version_code = 0;
    String app_link = "";
    DatabaseReference app_ref;
    String cat_id;
    Context context;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        rec_quiz = view.findViewById(R.id.rec_quiz);
        rel_no_items = view.findViewById(R.id.rel_no_items);
        quiz_list = new ArrayList<>();
        jList = new ArrayList<>();
        pList = new ArrayList<>();
        prctList = new ArrayList<>();
        uList = new ArrayList<>();
        allQuizList = new ArrayList<>();
        cat_id = getArguments().getString("cat_id");
        rel_pay = view.findViewById(R.id.rel_pay);
        app_ref = FirebaseDatabase.getInstance().getReference().child(CONFIG_REF);
        userRef = FirebaseDatabase.getInstance().getReference().child(USER_REF);
        quizRef = FirebaseDatabase.getInstance().getReference().child(QUIZ_REF);
        joinedRef = FirebaseDatabase.getInstance().getReference().child(JOINED_QUIZ_REF);
        module = new Module(getActivity());
        toastMsg = new ToastMsg(getActivity());
        loadingBar = new ProgressDialog(getActivity());
        loadingBar.setMessage("Loading...");
        loadingBar.setCanceledOnTouchOutside(false);
        sessionManagment = new SessionManagment(getActivity());
        user_id = sessionManagment.getUserDetails().get(KEY_ID);
        wallet_amt = sessionManagment.getUserDetails().get(KEY_WALLET);
        rec_quiz.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        rec_quiz.setItemAnimator(null);
//        view.setFocusableInTouchMode(true);
//        view.requestFocus();
//        view.setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//
//                if(event.getAction() == KeyEvent.ACTION_UP && keyCode==KeyEvent.KEYCODE_BACK)
//                {
//                    AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
//                    builder.setTitle("Confirmation");
//                    builder.setMessage("Are you sure want to exit?");
//                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            getActivity().finishAffinity();
//                        }
//                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    dialog.dismiss();
//                                }
//                            });
//                    AlertDialog dialog=builder.create();
//                    dialog.show();
//                    return true;
//                }
//                return false;
//            }
//        });


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (NetworkConnection.connectionChecking(getActivity())) {

            checkAppUpdates();
        } else {
            module.noConnectionActivity();
        }

    }

    private void checkAppUpdates() {

        uList.clear();
        app_ref.child("app_updater").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    UpdateModel model = dataSnapshot.getValue(UpdateModel.class);
                    version_code = Integer.parseInt(model.getApp_version());
                    app_link = model.getApp_link();
                    REDUCTION_WALLET_AMOUNT = Integer.parseInt(model.getWallet_amt().toString());
                    REDUCTION_REWARDS_AMOUNT = Integer.parseInt(model.getRewards_amt().toString());
                    WITHDRAW_LIMIT_AMOUNT = Integer.parseInt(model.getWithdraw_limit().toString());
                    if (getUpdaterInfo()) {
                        getUserData(sessionManagment.getUserDetails().get(KEY_ID).toString());
                        getParticipentsQuizList();
                        getQuizData(cat_id);
                    }
                    else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setCancelable(false);
                        builder.setMessage("The new version of app is available please update to get access.");
                        builder.setPositiveButton("Update now", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                String url = app_link;
                                Intent in = new Intent(Intent.ACTION_VIEW);
                                in.setData(Uri.parse(url));
                                context.startActivity(in);
                                ((Activity)context).finish();
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                dialogInterface.dismiss();
                                ((Activity)context).finishAffinity();
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                module.showToast("Something Went Wrong");
            }
        });
        rel_pay.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), QuizStatusActivity.class);
            startActivity(intent);
        });
    }


    public void getQuizData(String cat_id) {
        quiz_list.clear();
        loadingBar.show();
        Query query = quizRef.orderByChild("cat_id").equalTo(cat_id);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                loadingBar.dismiss();
                if (dataSnapshot.hasChildren()) {
                    //for QUIZ list
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        QuizModel model = data.getValue(QuizModel.class);
                        try {
//                            if(getAvailableQuiz(model.getQuiz_date().toString())){
                            long endDiff = module.getDiffInLong(model.getQuiz_date(), "00:00:00");
                            if (endDiff > 0) {
                                quiz_list.add(model);
                                Log.e("quizs", "onDataChange: " + model.getQuiz_id());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    if (quiz_list.size() > 0) {
                        rel_no_items.setVisibility(View.GONE);
                        rec_quiz.setVisibility(View.VISIBLE);
                        quizAdapter = new QuizAdapter((Activity) context, quiz_list, pList, prctList, user_id);
                        rec_quiz.setLayoutManager(new LinearLayoutManager(getActivity()));
                        rec_quiz.setAdapter(quizAdapter);
                        quizAdapter.notifyDataSetChanged();
                    } else {
                        loadingBar.dismiss();
                        rel_no_items.setVisibility(View.VISIBLE);
                        rec_quiz.setVisibility(View.GONE);
                    }
                } else {

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                loadingBar.dismiss();
                Toast.makeText(context, "" + databaseError.getMessage(), Toast.LENGTH_LONG).show();

            }
        });

    }

    public boolean getAvailableQuiz(String date) throws ParseException {
        boolean flag = false;
        Date cDate = new Date();
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        String cTime = timeFormat.format(cDate);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Date selDate = null;
        selDate = simpleDateFormat.parse(date + " " + cTime);

        String cDateStr = String.valueOf(cDate);
        String sDateStr = String.valueOf(selDate);
        if (cDateStr.equalsIgnoreCase(sDateStr)) {
            flag = true;
        } else if (cDate.compareTo(selDate) > 0) {
            flag = false;
        } else {
            flag = true;
//            flag=false;
        }
        return flag;
    }


    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(bWallet);
        getActivity().unregisterReceiver(bRewards);

    }


    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(bWallet, new IntentFilter(BROAD_WALLET));
        getActivity().registerReceiver(bRewards, new IntentFilter(BROAD_REWARDS));

    }


    public BroadcastReceiver bWallet = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String type = intent.getStringExtra("type");
            String amt = intent.getStringExtra("amount");
            if (type.contentEquals("update")) {
                updateWallet(amt);
            }

        }
    };


    public BroadcastReceiver bRewards = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String type = intent.getStringExtra("type");
            String amt = intent.getStringExtra("amount");
            if (type.contentEquals("update")) {
                updateReward(amt);
            }

        }
    };

    private void updateWallet(String amt) {
        sessionManagment.updateWallet(amt);
        ((MainActivity) getActivity()).setWalletCounter(amt);
    }

    private void updateReward(String amt) {
        sessionManagment.updateRewards(amt);
        ((MainActivity) getActivity()).setRewardsCounter(amt);
    }

    public boolean getUpdaterInfo() {
        boolean st = false;
        try {
            PackageInfo packageInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
            int ver_code = packageInfo.versionCode;
            if (ver_code == version_code) {
                st = true;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return st;
    }


    public void getParticipentsQuizList() {
        String c_date = module.getCurrentDate();
        DatabaseReference quizRef = FirebaseDatabase.getInstance().getReference().child(JOINED_QUIZ_REF);
//        Query query = quizRef;
        Query query = quizRef.orderByChild("join_date").equalTo(c_date);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                loadingBar.dismiss();
                prctList.clear();
                pList.clear();
                try {
                    if (dataSnapshot.hasChildren()) {
                        for (DataSnapshot snap : dataSnapshot.getChildren()) {
                            JoinedQuizModel model = snap.getValue(JoinedQuizModel.class);
                            prctList.add(model);
                            if (model.getUser_id().toString().equalsIgnoreCase(user_id)) {
                                pList.add(model);
                            }

                        }

                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    module.showToast("Something wwnt wrong");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                loadingBar.dismiss();
                module.showToast("" + databaseError.getMessage());
            }
        });
    }

    public void getUserData(String user_id) {
        Query query = userRef.orderByChild("id").equalTo(user_id);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                uList.clear();
                try {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot snap : dataSnapshot.getChildren()) {
                            UserModel model = snap.getValue(UserModel.class);
                            uList.add(model);
                        }
                        if (uList.size() > 0) {
                            if (uList.get(0).getStatus().toString().equalsIgnoreCase("1")) {
                                module.showToast("Your account deactivated. Please Contact to admin");
                            } else {
                                ((MainActivity) context).setWalletCounter(uList.get(0).getWallet().toString());
                                sessionManagment.updateWallet(uList.get(0).getWallet().toString());
                                ((MainActivity) getActivity()).setRewardsCounter(uList.get(0).getRewards().toString());
                                sessionManagment.updateRewards(uList.get(0).getRewards().toString());
                                sessionManagment.updateQuizStatus(uList.get(0).getQuiz_status());
                                if (sessionManagment.getUserDetails().get(KEY_QUIZ_STATUS).equalsIgnoreCase("0")) {
                                    rel_pay.setVisibility(View.VISIBLE);
                                } else {
                                    rel_pay.setVisibility(View.GONE);
                                }

                            }
                        } else {
                            module.showToast("Something Went Wrong. Try again later");
                        }
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
