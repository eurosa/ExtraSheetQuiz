package com.upsun.quizz.admin.activities;

import static com.upsun.quizz.Config.Constants.QUIZ_REF;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import com.google.firebase.database.ValueEventListener;
import com.upsun.quizz.Adapter.AdminQuizAdapter;
import com.upsun.quizz.Config.Module;
import com.upsun.quizz.Model.QuizModel;
import com.upsun.quizz.R;
import com.upsun.quizz.networkconnectivity.NetworkConnection;
import com.upsun.quizz.utils.ToastMsg;

import java.util.ArrayList;
import java.util.Collections;

public class AdminAllQuizActivity extends AppCompatActivity {
    ImageView iv_back;
    public static TextView tv_title, no_of_quiz;
    RecyclerView rv_quiz;
    RelativeLayout rel_no_items;
    ToastMsg toastMsg;
    ProgressDialog loadingBar;
    Activity ctx = AdminAllQuizActivity.this;
    DatabaseReference quiz_ref;
    ArrayList<QuizModel> list;
    AdminQuizAdapter quizAdapter;
    String type = "";
    Module module;
    Button btn_add_quiz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_all_quiz);
        initViews();
        btn_add_quiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ctx, EditQuizActivity.class);
                intent.putExtra("is_edit", "false");
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (NetworkConnection.connectionChecking(ctx)) {
            getAllQuiz();
        } else {
            module.noConnectionActivity();
        }


    }

    private void initViews() {
        toastMsg = new ToastMsg(ctx);
        loadingBar = new ProgressDialog(ctx);
        loadingBar.setMessage("Loading...");
        loadingBar.setCanceledOnTouchOutside(false);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        tv_title = (TextView) findViewById(R.id.tv_title);
        no_of_quiz = (TextView) findViewById(R.id.no_of_quiz);
        rel_no_items = (RelativeLayout) findViewById(R.id.rel_no_items);
        rv_quiz = (RecyclerView) findViewById(R.id.rv_quiz);
        list = new ArrayList<>();
        module = new Module(this);
        btn_add_quiz = (Button) findViewById(R.id.btn_add_quiz);
//        type = getIntent().getStringExtra("type");
//        if (type.equals("today"))
//        {
//            tv_title.setText("Today's Quiz");
//        }
//        else if (type.equals("upcoming"))
//        {
//            tv_title.setText("Upcoming Quiz");
//        }
//       else if (type.equals("all") || type.equalsIgnoreCase(""))
//        {
        tv_title.setText("All Quiz");
//        }
        quiz_ref = FirebaseDatabase.getInstance().getReference().child(QUIZ_REF);

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void getAllQuiz() {
        loadingBar.show();
        list.clear();
        quiz_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                loadingBar.dismiss();
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        QuizModel model = snapshot.getValue(QuizModel.class);
                        list.add(model);
                    }

                    for (int i = 0; i < list.size(); i++) {
                        int days = module.getDateDiff(list.get(i).getQuiz_date().toString());
                        list.get(i).setDays(String.valueOf(days));
                    }
                    Collections.sort(list, QuizModel.camp_quiz);
                    rv_quiz.setVisibility(View.VISIBLE);
                    rel_no_items.setVisibility(View.GONE);
                    quizAdapter = new AdminQuizAdapter(ctx, list);
                    no_of_quiz.setText("No. Of Quiz: " + list.size());
                    rv_quiz.setLayoutManager(new LinearLayoutManager(ctx));
                    rv_quiz.setAdapter(quizAdapter);
                    quizAdapter.notifyDataSetChanged();
                } else {
                    rv_quiz.setVisibility(View.GONE);
                    rel_no_items.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                loadingBar.dismiss();
                Toast.makeText(ctx, "" + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
