package com.upsun.quizz.admin.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.upsun.quizz.R;
import com.upsun.quizz.admin.fragments.AddPointsManuallyFragment;

public class PointsAdminActivity extends AppCompatActivity {
    ImageView iv_back;
    TextView tv_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_points_admin);
        initViews();
        AddPointsManuallyFragment fm=new AddPointsManuallyFragment();
        Bundle bundle=new Bundle();
        addFragment(fm,bundle);
    }

    private void initViews() {
        iv_back=findViewById(R.id.iv_back);
        tv_title=findViewById(R.id.tv_title);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        tv_title.setText("Add Points");

    }
    public void addFragment(Fragment fm,Bundle bundle)
    {  fm.setArguments(bundle);
        FragmentManager manager=getSupportFragmentManager();
        manager.beginTransaction().add(R.id.points_frame,fm).addToBackStack(null)
                .commit();
    }
}
