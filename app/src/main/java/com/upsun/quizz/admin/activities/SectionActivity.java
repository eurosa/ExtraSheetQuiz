package com.upsun.quizz.admin.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.upsun.quizz.Adapter.SectionAdapter;
import com.upsun.quizz.Model.SectionModel;
import com.upsun.quizz.R;
import com.upsun.quizz.utils.RecyclerTouchListener;
import com.upsun.quizz.utils.ToastMsg;

import static com.upsun.quizz.Config.Constants.QUIZ_QUES_REF;
import static com.upsun.quizz.Config.Constants.SECTION_REF;
import static com.upsun.quizz.Model.SectionModel.orderCamp;

public class SectionActivity extends AppCompatActivity {

    private final String TAG=SectionActivity.class.getSimpleName();
    List<SectionModel> sectionList;
    RecyclerView rv_section;
    TextView addSection;
    int index;
    Activity ctx=SectionActivity.this;
    DatabaseReference quizQue_ref,section_ref;
    ProgressDialog loadingBar;
    String quiz_id;
    SectionAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_section);
        loadingBar = new ProgressDialog(this);
        loadingBar.setMessage("Loading...");
        loadingBar.setCanceledOnTouchOutside(false);
        sectionList = new ArrayList();
        quiz_id = getIntent().getStringExtra("quiz_id");
        addSection = findViewById(R.id.addSection);
        rv_section = findViewById(R.id.rv_section);
        rv_section.setLayoutManager(new LinearLayoutManager(this));
        quizQue_ref= FirebaseDatabase.getInstance().getReference().child(QUIZ_QUES_REF);
        section_ref= FirebaseDatabase.getInstance().getReference().child(SECTION_REF);

        getSections();

        addSection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id=getUniqueId();
                String sName="Section "+(sectionList.size()+1);
              addSection(quiz_id,id,sName);
            }
        });


//        listSection.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent intent = new Intent(SectionActivity.this,QuizQuestionActivity.class);
//                intent.putExtra("quiz_id",quiz_id);
//                intent.putExtra("section",String.valueOf(position));
//                startActivity(intent);
//            }
//        });


    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void getSections() {
        loadingBar.show();

        section_ref.child(quiz_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                sectionList.clear();
                if(dataSnapshot.exists()){
                Log.e(TAG, "onDataChange: "+dataSnapshot.toString());
                for (DataSnapshot s:dataSnapshot.getChildren()) {
                   SectionModel m=s.getValue(SectionModel.class);
                   sectionList.add(m);
                }
                for(int i=0; i<sectionList.size();i++){
                    String order=sectionList.get(i).getSection_name();
                    sectionList.get(i).setDays(getDayOrder(order));
                }
                 Collections.sort(sectionList,orderCamp);
                 adapter=new SectionAdapter(SectionActivity.this,sectionList);
                 rv_section.setAdapter(adapter);
                 adapter.notifyDataSetChanged();
                }else{

                }
                loadingBar.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),""+databaseError,Toast.LENGTH_LONG).show();
                loadingBar.dismiss();
            }
        });
    }

    public void addSection(String quiz_id,String section_id,String section_name){
      loadingBar.show();
      HashMap<String,Object> params=new HashMap<>();
        params.put("section_id",section_id);
        params.put("quiz_id",quiz_id);
        params.put("section_name",section_name);

      section_ref.child(quiz_id).child(section_id).updateChildren(params).addOnCompleteListener(new OnCompleteListener<Void>() {
          @Override
          public void onComplete(@NonNull Task<Void> task) {
              loadingBar.dismiss();
             if(task.isSuccessful()){
//                 sectionList.add(new SectionModel(section_id,quiz_id,section_name));
                 adapter.notifyDataSetChanged();
                new ToastMsg(ctx).toastIconSuccess("Section Added..");
             }else{
                 Log.e(TAG, "onError: "+task.getException() );
             }
          }
      });

    }

    public String getUniqueId()
    {
        String ss = "sec";
        String unique_id = "";
        Date date=new Date();
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("ddMMyyyyhhmmss");
        unique_id = ss +simpleDateFormat.format(date).toString();
        return unique_id ;
    }
    public String getDayOrder(String str){
        //Section 1
        String s=str.split(" ")[1].toString();
        return s;
    }


}