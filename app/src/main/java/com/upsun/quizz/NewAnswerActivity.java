package com.upsun.quizz;

import static com.upsun.quizz.Config.Constants.SEC_QUES_REF;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.upsun.quizz.Adapter.NewAnswerAdapter;
import com.upsun.quizz.Config.Module;
import com.upsun.quizz.Model.HistoryModel;
import com.upsun.quizz.Model.SectionQuestionModel;
import com.upsun.quizz.networkconnectivity.NetworkConnection;
import com.upsun.quizz.utils.SessionManagment;

import java.util.ArrayList;

/**
 * Developed by Binplus Technologies pvt. ltd.  on 15,June,2021
 */
public class NewAnswerActivity extends AppCompatActivity {

    ProgressDialog loadingBar;
    ImageView iv_back;
    TextView tv_title;
    private final String TAG = NewAnswerActivity.class.getSimpleName();
    String quiz_id;
    ArrayList<SectionQuestionModel> qList;
    DatabaseReference dRef;
    SessionManagment sessionManagment;
    Module module;
    NewAnswerAdapter adapter;
    RecyclerView recyclerView;

    HistoryModel historyModel = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answers);
        loadingBar = new ProgressDialog(NewAnswerActivity.this);
        loadingBar.setMessage("Loading...");
        loadingBar.setCanceledOnTouchOutside(false);
//        quiz_id = getIntent().getStringExtra("quiz_id");
//        section_id = getIntent().getStringExtra("section_id");
        Gson gson = new Gson();
        historyModel = gson.fromJson(getIntent().getStringExtra("model"), HistoryModel.class);
        recyclerView = findViewById(R.id.rec_ans);
        tv_title = findViewById(R.id.tv_title);
        iv_back = findViewById(R.id.iv_back);
        qList = new ArrayList<>();
        tv_title.setText("Answer Keys");
        qList = new ArrayList<>();
        module = new Module(this);
        dRef = FirebaseDatabase.getInstance().getReference();
        sessionManagment = new SessionManagment(this);

        iv_back.setOnClickListener(v -> finish());


    }

    @Override
    protected void onStart() {
        super.onStart();
        if (NetworkConnection.connectionChecking(this)) {
//            section_id="sec15062021022757";
            Log.e(TAG, "onCreate: " + historyModel.getSection_id());
            getAllQuestions(historyModel.getSection_id());

        }
    }

    private void getAllQuestions(String section_id) {
        Log.e(TAG, "getAllQuestions: callll");
        loadingBar.show();
        qList.clear();
        DatabaseReference secQuesRef = FirebaseDatabase.getInstance().getReference().child(SEC_QUES_REF);
        secQuesRef.child(section_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.e(TAG, "onDataChange: " + snapshot.toString());
                try {
                    if (snapshot.hasChildren()) {
                        for (DataSnapshot snp : snapshot.getChildren()) {
                            SectionQuestionModel model = snp.getValue(SectionQuestionModel.class);
                            qList.add(model);
                            Log.e(TAG, "onDataChange: " + model.toString());
                        }
                        adapter = new NewAnswerAdapter(NewAnswerActivity.this, qList);
                        recyclerView.setLayoutManager(new GridLayoutManager(NewAnswerActivity.this, 1));
                        recyclerView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    } else {
                        module.showToast("No Question Available");
                    }
                    loadingBar.dismiss();
                } catch (Exception ex) {
                    loadingBar.dismiss();
                    ex.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                module.showToast("" + error.getMessage().toString());
            }
        });
    }


}
