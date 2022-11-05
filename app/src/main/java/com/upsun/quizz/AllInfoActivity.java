package com.upsun.quizz;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.upsun.quizz.Config.Module;
import com.upsun.quizz.Model.InfoPageModel;
import com.upsun.quizz.networkconnectivity.NetworkConnection;
import com.upsun.quizz.utils.ToastMsg;

public class AllInfoActivity extends AppCompatActivity {
    ImageView iv_back ;
    TextView tv_title ,tv_content ;
    String type = "" ,getType="";
    ProgressDialog loadingBar ;
    Module module;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_info);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        iv_back = findViewById(R.id.iv_back);
        tv_title = findViewById(R.id.tv_title);
        tv_content = findViewById(R.id.tv_content);
        type = getIntent().getStringExtra("type");
        loadingBar=new ProgressDialog(this);
        loadingBar.setMessage("Loading...");
        loadingBar.setCanceledOnTouchOutside(false);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        module=new Module(this);

        if (type.equalsIgnoreCase("terms & conditions"))
        {
            tv_title.setText("Terms & Conditions");
        }
        else if (type.equalsIgnoreCase("about us"))
        {
            tv_title.setText("About Us");
        }
        else if (type.equalsIgnoreCase("privacy policy"))
        {
            tv_title.setText("Privacy Policy");
        }

        if(NetworkConnection.connectionChecking(AllInfoActivity.this))
        {
            getInfo(type);
        }
        else {
            module.noConnectionActivity();
        }

    }
    private void getInfo(final String type)
    {
        loadingBar.show();
        DatabaseReference dRef = FirebaseDatabase.getInstance().getReference().child("info_pages").child(type.toLowerCase());

        dRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.e("data",dataSnapshot.toString());

                  loadingBar.dismiss();

                     InfoPageModel model = dataSnapshot.getValue(InfoPageModel.class);
                      tv_content.setText(model.getTitle()+"\n \n"+model.getContent());

                      Log.e("data",dataSnapshot.toString()+"\n"+model.getContent()+"\n"+model.getTitle());
        }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                new ToastMsg(AllInfoActivity.this).toastIconError(databaseError.getMessage());


            }
        });

    }
}
