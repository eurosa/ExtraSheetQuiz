package com.upsun.quizz;

import static com.upsun.quizz.Config.Constants.JOINED_QUIZ_REF;
import static com.upsun.quizz.Config.Constants.KEY_ID;
import static com.upsun.quizz.Config.Constants.KEY_WALLET;
import static com.upsun.quizz.Config.Constants.QUIZ_REF;
import static com.upsun.quizz.Config.Constants.TRANS_REF;
import static com.upsun.quizz.Model.CreditTransactionModel.camp_trans;
import static com.upsun.quizz.Model.JoinedQuizModel.camp_jquiz;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.upsun.quizz.Adapter.CreditTransactionAdapter;
import com.upsun.quizz.Adapter.TransactionAdapter;
import com.upsun.quizz.Config.Module;
import com.upsun.quizz.Model.CreditTransactionModel;
import com.upsun.quizz.Model.JoinedQuizModel;
import com.upsun.quizz.Model.QuizModel;
import com.upsun.quizz.networkconnectivity.NetworkConnection;
import com.upsun.quizz.utils.SessionManagment;
import com.upsun.quizz.utils.ToastMsg;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class WalletActivity extends AppCompatActivity implements View.OnClickListener {
    private final String TAG=WalletActivity.class.getSimpleName();
    ImageView iv_back ;
    TextView txt_wallet_amount ,txt_today_trns ,txt_last_trans ,txt_date;
    CardView card_today ,card_last ,card_all ;
    Button btn_add_money,btn_debits,btn_credits;
    SessionManagment sessionManagment ;
    CreditTransactionAdapter cAdapter;
    DatabaseReference dref,quizRef,joinedRef;
    ArrayList<JoinedQuizModel> jList;
    ArrayList<CreditTransactionModel> cList;
    ArrayList<QuizModel> q_List;
    RecyclerView rv_trans;
    TransactionAdapter transactionAdapter ;
    String user_id ;
    ProgressDialog loadingBar ;
    RelativeLayout rel_no_item;
    Module module;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);
        iv_back = findViewById(R.id.iv_back);
        btn_debits=(Button)findViewById(R.id.btn_debits);
        btn_credits=(Button)findViewById(R.id.btn_credits);
        txt_wallet_amount = findViewById(R.id.txt_wallet_amt);
        txt_today_trns = findViewById(R.id.txt_todays_transr);
        txt_last_trans = findViewById(R.id.txt_last_trans);
        txt_date = findViewById(R.id.txt_date);
        module=new Module(this);
        card_all = findViewById(R.id.card_all_trans);
        card_last = findViewById(R.id.card_last_trans);
        card_today = findViewById(R.id.card_today_trans);
        btn_add_money = findViewById(R.id.btn_add);
        rel_no_item = findViewById(R.id.rel_no_items);
        card_today.setOnClickListener(this);
        card_last.setOnClickListener(this);
        card_all.setOnClickListener(this);
        iv_back.setOnClickListener(this);
        btn_add_money.setOnClickListener(this);
        btn_credits.setOnClickListener(this);
        btn_debits.setOnClickListener(this);
        sessionManagment = new SessionManagment(WalletActivity.this);
        jList = new ArrayList<>();
        q_List = new ArrayList<>();
        cList=new ArrayList<>();
        loadingBar=new ProgressDialog(WalletActivity.this);
        loadingBar.setMessage("Loading...");
        loadingBar.setCanceledOnTouchOutside(false);
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        txt_date.setText(format.format(date));
        rv_trans = findViewById(R.id.rv_trans);
        user_id = sessionManagment.getUserDetails().get(KEY_ID);
        txt_wallet_amount.setText(sessionManagment.getUserDetails().get(KEY_WALLET));
        quizRef=FirebaseDatabase.getInstance().getReference().child(QUIZ_REF);
        joinedRef=FirebaseDatabase.getInstance().getReference().child(JOINED_QUIZ_REF);

        if(NetworkConnection.connectionChecking(this))
        {
            getQuizData();
            getJoinedQuiz(user_id);
        }
        else
        {
            module.noConnectionActivity();
        }

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_back)
        {
            finish();
        }
        else if (id == R.id.btn_add)
        {
            Intent intent = new Intent(WalletActivity.this,PointsActivity.class);
            startActivity(intent);
        }
        else if(id == R.id.btn_credits)
        {
            ColorDrawable buttonColor=(ColorDrawable)btn_debits.getBackground();
            int colorId=buttonColor.getColor();
            if(colorId == getResources().getColor(R.color.color_cancel))
            {
                if(jList.size()>0){
                    jList.clear();
                    transactionAdapter.notifyDataSetChanged();
                }
                btn_credits.setBackgroundColor(getResources().getColor(R.color.color_cancel));
                btn_credits.setTextColor(getResources().getColor(R.color.white));
                btn_debits.setBackgroundColor(getResources().getColor(R.color.white));
                btn_debits.setTextColor(getResources().getColor(R.color.color_cancel));
                getCreditList(user_id);
            }

        }
        else if(id == R.id.btn_debits)
        {
            if(cList.size()>0){
                cList.clear();
                cAdapter.notifyDataSetChanged();
            }

            ColorDrawable buttonColor=(ColorDrawable)btn_credits.getBackground();
            int colorId=buttonColor.getColor();
            if(colorId == getResources().getColor(R.color.color_cancel))
            {
                btn_debits.setBackgroundColor(getResources().getColor(R.color.color_cancel));
                btn_debits.setTextColor(getResources().getColor(R.color.white));
                btn_credits.setBackgroundColor(getResources().getColor(R.color.white));
                btn_credits.setTextColor(getResources().getColor(R.color.color_cancel));
                getQuizData();
                getJoinedQuiz(user_id);
            }
        }
    }

    private void getCreditList(final String user_id) {
        loadingBar.show();
        cList.clear();
        dref= FirebaseDatabase.getInstance().getReference().child(TRANS_REF);
        Query query=dref.orderByChild("user_id").equalTo(user_id);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                loadingBar.dismiss();
                if (dataSnapshot.exists()|| dataSnapshot.hasChildren()) {
                    for (DataSnapshot snap_joined : dataSnapshot.getChildren()) {
                        CreditTransactionModel joinedQuizModel = snap_joined.getValue(CreditTransactionModel.class);
                        if (snap_joined.hasChild("referredTo")){
                            joinedQuizModel.setReferredTo(snap_joined.child("referredTo").getValue(String.class));
                        }

                                cList.add(joinedQuizModel);
                    }
                    for(int i=0; i<cList.size();i++)
                    {
                        String q_id = cList.get(i).getTxn_id();
                        String j_d = q_id.substring(4,12);
                        String dtstr=j_d.substring(0,2)+"-"+j_d.subSequence(2,4)+"-"+j_d.subSequence(4,j_d.length());
                        int days= module.getDateDiff(dtstr.toString());
                        cList.get(i).setDays(String.valueOf(days));
                    }
                    Collections.sort(cList,camp_trans);
                    Log.e(TAG, "onDataChangecList: "+cList.size() );
                    if (cList.size()>0) {
                        rel_no_item.setVisibility(View.GONE);
                        cAdapter = new CreditTransactionAdapter(WalletActivity.this,cList );
                        rv_trans.setLayoutManager(new LinearLayoutManager(WalletActivity.this));
                        rv_trans.setAdapter(cAdapter);
                        cAdapter.notifyDataSetChanged();
                        loadingBar.dismiss();
                    }
                    else
                    {
                        rel_no_item.setVisibility(View.VISIBLE);
                        rv_trans.setVisibility(View.GONE);
                    }
                }
                else
                {

                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                loadingBar.dismiss();
                new ToastMsg(WalletActivity.this).toastIconError(databaseError.getMessage());

            }
        });
    }

    public void getQuizData()
    {
        q_List.clear();
        loadingBar.show();
        quizRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                loadingBar.dismiss();
                if( dataSnapshot.hasChildren())
                {
                    //for QUIZ list
                    for(DataSnapshot data : dataSnapshot.getChildren())
                    {

                        QuizModel model = data.getValue(QuizModel.class);
                        try {
                                 q_List.add(model);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                                    }
                else
                {

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                loadingBar.dismiss();
                Toast.makeText(WalletActivity.this,""+databaseError.getMessage(),Toast.LENGTH_LONG).show();

            }
        });

    }
    public void getJoinedQuiz(final String user_id)
    {
        loadingBar.show();
        jList.clear();
        Query query=joinedRef.orderByChild("user_id").equalTo(user_id);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                loadingBar.dismiss();

                if (dataSnapshot.exists()|| dataSnapshot.hasChildren()) {
                    for (DataSnapshot snap_joined : dataSnapshot.getChildren()) {
                        loadingBar.dismiss();
                        JoinedQuizModel joinedQuizModel = snap_joined.getValue(JoinedQuizModel.class);

                            if (jList.contains(joinedQuizModel.getQuiz_id())) {

                            } else {
                                jList.add(joinedQuizModel);
                            }

                    }
                    Log.e(TAG, "onDataChange111: "+jList.size() );
                    for(int i=0; i<jList.size();i++)
                    {
                        String q_id = jList.get(i).getQuiz_id();
                        String j_d = q_id.substring(4,12);
                        String dtstr=j_d.substring(0,2)+"-"+j_d.subSequence(2,4)+"-"+j_d.subSequence(4,j_d.length());
                        int days= module.getDateDiff(dtstr.toString());
                        jList.get(i).setDays(String.valueOf(days));
                    }
                    Collections.sort(jList,camp_jquiz);

                    Log.e(TAG, "onDataChange: "+jList.size()+" - "+q_List.size() );
                    if (jList.size()>0) {
                        rel_no_item.setVisibility(View.GONE);
                        transactionAdapter = new TransactionAdapter(jList, q_List, WalletActivity.this);
                        rv_trans.setLayoutManager(new LinearLayoutManager(WalletActivity.this));
                        rv_trans.setAdapter(transactionAdapter);
                        transactionAdapter.notifyDataSetChanged();
                        loadingBar.dismiss();
                    }
                    else
                    {
                        rel_no_item.setVisibility(View.VISIBLE);
                        rv_trans.setVisibility(View.GONE);
                    }
                }
                else
                {

                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                loadingBar.dismiss();
                new ToastMsg(WalletActivity.this).toastIconError(databaseError.getMessage());

            }
        });
    }
}
