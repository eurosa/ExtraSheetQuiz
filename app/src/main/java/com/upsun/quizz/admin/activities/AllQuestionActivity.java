package com.upsun.quizz.admin.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.upsun.quizz.R;
import com.upsun.quizz.admin.fragments.ViewAllQuestionFragment;

public class AllQuestionActivity extends AppCompatActivity {

    ImageView iv_back;
    TextView tv_title;
    String cat_id="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_question);

        iv_back=(ImageView)findViewById(R.id.iv_back);
        tv_title=(TextView)findViewById(R.id.tv_title);
        cat_id=getIntent().getStringExtra("cat_id");
        Fragment fm=new ViewAllQuestionFragment();
        Bundle bundle=new Bundle();
        bundle.putString("cat_id",cat_id);
        FragmentManager manager=getSupportFragmentManager();
        fm.setArguments(bundle);
        manager.beginTransaction().add(R.id.container_ques,fm).commit();
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    public void setTitle(String title)
    {
        tv_title.setText(title);
    }

}
