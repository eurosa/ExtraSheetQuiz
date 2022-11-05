package com.upsun.quizz.admin.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.upsun.quizz.Adapter.AllQuizCategoryAdapter;
import com.upsun.quizz.Config.Module;
import com.upsun.quizz.Model.AddQuizCategoryModel;
import com.upsun.quizz.R;

import static com.upsun.quizz.Model.AddQuizCategoryModel.comp_cat;


public class AllQuizCategoryActivity extends AppCompatActivity {
    private final String TAG = AllQuizCategoryActivity.class.getSimpleName();
    private List<AddQuizCategoryModel> mList;
    private DatabaseReference demoRef;
    private ProgressDialog loadingBar;
    Button btn_add_category;
    private Activity ctx = AllQuizCategoryActivity.this;
    AllQuizCategoryAdapter adapter;
    RecyclerView rv_category;
    TextView no_of_quiz_category;
    Module module;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_quiz_category);

        init();
        setAllQuizCategoryAdapter();

    }

    private void init(){
        demoRef = FirebaseDatabase.getInstance().getReference().child("parents");
        loadingBar=new ProgressDialog(ctx);
        loadingBar.setMessage("Loading...");
        loadingBar.setCanceledOnTouchOutside(false);
        btn_add_category=findViewById(R.id.btn_add_category);
        no_of_quiz_category=findViewById(R.id.no_of_quiz_category);
        rv_category=findViewById(R.id.rv_category);
        rv_category.setLayoutManager(new LinearLayoutManager(this));
        rv_category.setNestedScrollingEnabled(false);
        module=new Module(ctx);
        btn_add_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AllQuizCategoryActivity.this, AddQuizCategoryActivity.class);
                intent.putExtra("is_edit","false");
                startActivity(intent);
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        setAllQuizCategoryAdapter();
    }

    //fetching categories
    private void setAllQuizCategoryAdapter() {

        mList = new ArrayList<>();
         loadingBar.show();
        demoRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mList.clear();
                for(DataSnapshot dataSnap : dataSnapshot.getChildren() ){
                    AddQuizCategoryModel categoryModel = new AddQuizCategoryModel();
                    categoryModel  = dataSnap.getValue(AddQuizCategoryModel.class);
                    mList.add(categoryModel);
                }
                no_of_quiz_category.setText("No. of Categories : "+mList.size());
                //parent04092020011149
                for(int i=0; i<mList.size();i++)
                {
                    String q_id = mList.get(i).getP_id();
                    String j_d = q_id.substring(7,12);
                    String dtstr=j_d.substring(0,2)+"-"+j_d.subSequence(2,4)+"-"+j_d.subSequence(4,j_d.length());
                    int days= module.getDateDiff(dtstr.toString());
                    mList.get(i).setDays(String.valueOf(days));
                    Log.e("asdas",""+mList.size()+" - "+mList.get(i).getP_status());
                }

                Collections.sort(mList,comp_cat);
                adapter = new AllQuizCategoryAdapter(AllQuizCategoryActivity.this,mList);
                rv_category.setAdapter(adapter);
                loadingBar.dismiss();
                adapter.notifyDataSetChanged();


            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                loadingBar.dismiss();
                Log.e(TAG,""+databaseError);
            }
        });
    }


    //General function to show Progres dailog
//    private void showProgressDailog(){
//
//        if(mDailog != null && mDailog.isShowing()){
//            mDailog.dismiss();
//        }
//
//        mDailog.setMessage("Updating ...Please wait.");
//        mDailog.setCancelable(false);
//        mDailog.show();
//
//    }


    //General function to Dismiss Progres dailog
//    private void dismissDailog(){
//
//        if(mDailog != null && !mDailog.isShowing()){
//            return;
//        }
//
//        mDailog.dismiss();
//
//    }

    //function to update Adapter
    public void updateCount(int size){

            no_of_quiz_category.setText("No. of Categories : "+size);


    }

//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.iv_back:
//                startActivity(new Intent(AllQuizCategoryActivity.this, HomeActivity.class));
//                finish();
//                break;
//            case R.id.btn_add_category:
//                startActivity(new Intent(AllQuizCategoryActivity.this, AddQuizCategoryActivity.class));
//                break;
//        }
//    }
}
