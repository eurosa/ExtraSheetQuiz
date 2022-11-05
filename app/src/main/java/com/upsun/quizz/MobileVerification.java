package com.upsun.quizz;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.upsun.quizz.Fragments.SendOtpFragment;

public class MobileVerification extends AppCompatActivity {

    ImageView iv_back;
    String type="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_verification);
        initViews();
    }

    private void initViews() {
        iv_back=(ImageView)findViewById(R.id.iv_back);
        type=getIntent().getStringExtra("type");
        SendOtpFragment fm=new SendOtpFragment();
        Bundle bundle=new Bundle();
        bundle.putString("type",type);
        loadFragment(fm,bundle);


        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void addFragment(Fragment fm,Bundle args)
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fm.setArguments(args);
        fragmentManager.beginTransaction()
                .add( R.id.container_otp,fm)
                .commit();
    }
    public void loadFragment(Fragment fm,Bundle args)
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fm.setArguments(args);
        fragmentManager.beginTransaction()
                .add( R.id.container_otp,fm)
                .commit();
    }

    }
