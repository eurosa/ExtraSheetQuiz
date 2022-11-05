package com.upsun.quizz;

import static com.upsun.quizz.Config.Constants.KEY_ID;
import static com.upsun.quizz.Config.Constants.KEY_REWARDS;
import static com.upsun.quizz.Model.QuizRankRewardModel.camp_rank_reward;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.upsun.quizz.Adapter.RewardsAdapter;
import com.upsun.quizz.Config.Module;
import com.upsun.quizz.Intefaces.OnQuizListData;
import com.upsun.quizz.Model.QuizModel;
import com.upsun.quizz.Model.QuizRankRewardModel;
import com.upsun.quizz.networkconnectivity.NetworkConnection;
import com.upsun.quizz.utils.SessionManagment;
import com.upsun.quizz.utils.ToastMsg;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.Random;

public class RewardsActivity extends AppCompatActivity implements View.OnClickListener {
    ImageView iv_back;
    SessionManagment sessionManagment;
    DatabaseReference dref;
    ArrayList<QuizRankRewardModel> r_List;
    ArrayList<QuizModel> q_List;
    RecyclerView rv_trans;
    RewardsAdapter rewardsAdapter;
    String user_id;
    ProgressDialog loadingBar;
    TextView txt_rewards;
    RelativeLayout rel_no_items;
    LinearLayout lin_rewards;
    Button btn_withdraw;
    Module module;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rewards);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        iv_back = findViewById(R.id.iv_back);
        txt_rewards = findViewById(R.id.txt_rewards);
        rel_no_items = findViewById(R.id.rel_no_items);
        lin_rewards = findViewById(R.id.lin_trans);
        btn_withdraw = findViewById(R.id.btn_add);
        iv_back.setOnClickListener(this);
        module = new Module(this);
        btn_withdraw.setOnClickListener(this);
        sessionManagment = new SessionManagment(this);
        r_List = new ArrayList<>();
        q_List = new ArrayList<>();
        loadingBar = new ProgressDialog(this);
        loadingBar.setMessage("Loading...");
        loadingBar.setCanceledOnTouchOutside(false);
//        Date date = new Date();
//        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
//        txt_date.setText(format.format(date));
        rv_trans = findViewById(R.id.rv_trans);
        user_id = sessionManagment.getUserDetails().get(KEY_ID);


    }

     void CreateAdvanceLink(String referCode) {
        DynamicLink dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse("https://www.example.com/?referCode=" + referCode))
                .setDomainUriPrefix("https://upsunquiz.page.link")
                .setAndroidParameters(
                        new DynamicLink.AndroidParameters.Builder(getPackageName())
                                .setMinimumVersion(1)
                                .build())
                .setSocialMetaTagParameters(
                        new DynamicLink.SocialMetaTagParameters.Builder()
                                .setTitle("Example of a Dynamic Link")
                                .setDescription("This link works whether the app is installed or not!")
                                .setImageUrl(Uri.parse("https://cdn.pixabay.com/photo/2015/04/23/22/00/tree-736885__480.jpg"))
                                .build())
                .buildDynamicLink();

        FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLongLink(dynamicLink.getUri())
                .buildShortDynamicLink()
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {

                        Uri shortLink = task.getResult().getShortLink();
                        try {
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_SEND);
                            intent.putExtra(Intent.EXTRA_TEXT, shortLink.toString());
                            intent.setType("text/plain");
                            startActivity(intent);
                        } catch (Exception e) {
                            Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }


                    }
                }).addOnFailureListener(e -> Toast.makeText(RewardsActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show());


    }

    public String createRandomCode(int codeLength) {
        char[] chars = "abcdefghijklmnopqrstuvwxyz1234567890".toCharArray();
        StringBuilder sb = new StringBuilder();
        Random random = new SecureRandom();
        for (int i = 0; i < codeLength; i++) {
            char c = chars[random.nextInt(chars.length)];
            sb.append(c);
        }
        String output = sb.toString();
        return output.toUpperCase(Locale.ROOT);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_back) {
            finish();
        } else if (id == R.id.btn_add) {
            Intent intent = new Intent(RewardsActivity.this, WithdrawRequest.class);
            startActivity(intent);
        }
    }

    public void getQuizRewards(final String user_id) {
        loadingBar.show();
        r_List.clear();
        dref = FirebaseDatabase.getInstance().getReference().child("quiz_rank_reward");
        Query query = dref.orderByChild("user_id").equalTo(user_id);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                loadingBar.dismiss();
                if (dataSnapshot.hasChildren()) {
                    for (DataSnapshot snap : dataSnapshot.getChildren()) {
                        loadingBar.dismiss();
                        QuizRankRewardModel model = snap.getValue(QuizRankRewardModel.class);

//                        if (model.getUser_id().equalsIgnoreCase(user_id)) {
                        if (r_List.contains(model.getQuiz_id())) {

                        } else {
                            r_List.add(model);
                        }
//                        }
                    }
                    for (int i = 0; i < r_List.size(); i++) {
                        String q_id = r_List.get(i).getQuiz_id();
                        String j_d = q_id.substring(4, 12);
                        String dtstr = j_d.substring(0, 2) + "-" + j_d.subSequence(2, 4) + "-" + j_d.subSequence(4, j_d.length());
                        int days = module.getDateDiff(dtstr.toString());
                        r_List.get(i).setDays(String.valueOf(days));
                    }
                    Collections.sort(r_List, camp_rank_reward);
                    if (r_List.size() > 0) {
                        rel_no_items.setVisibility(View.GONE);

                        rewardsAdapter = new RewardsAdapter(r_List, q_List, RewardsActivity.this);
                        rv_trans.setLayoutManager(new LinearLayoutManager(RewardsActivity.this));
                        rv_trans.setAdapter(rewardsAdapter);
                        rewardsAdapter.notifyDataSetChanged();
                        loadingBar.dismiss();
                    } else {
                        rel_no_items.setVisibility(View.VISIBLE);
                        rv_trans.setVisibility(View.GONE);
                    }
                }

            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                loadingBar.dismiss();
                new ToastMsg(RewardsActivity.this).toastIconError(databaseError.getMessage());

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (NetworkConnection.connectionChecking(this)) {

            txt_rewards.setText(sessionManagment.getUserDetails().get(KEY_REWARDS));
            module.getQuizList(loadingBar, new OnQuizListData() {
                @Override
                public void getQuizList(ArrayList<QuizModel> list) {
                    q_List.clear();
                    q_List.addAll(list);

                }
            });
            getQuizRewards(user_id);
        } else {
            module.noConnectionActivity();
        }
    }
}
