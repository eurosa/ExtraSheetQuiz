package com.upsun.quizz.admin.activities;

import static com.upsun.quizz.Config.Constants.USER_REF;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.upsun.quizz.Config.Module;
import com.upsun.quizz.R;
import com.upsun.quizz.networkconnectivity.NetworkConnection;
import com.upsun.quizz.utils.ToastMsg;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserDetailsActivity extends AppCompatActivity {

    ImageView iv_back;
    CircleImageView iv_profile;
    TextView tv_title,tv_name,tv_id,tv_email,tv_status;
    Button btn_ban;
    ProgressDialog loadingBar;
    DatabaseReference user_ref;
    ToastMsg toastMsg;
    Module module;
    Activity ctx=UserDetailsActivity.this;
    String id="",email="",name="",status="",st_str="",img_url="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);
        initViews();
        tv_id.setText(id);
        tv_email.setText(email);
        tv_name.setText(name);
        tv_title.setText(name);
        if(status.equalsIgnoreCase("0"))
        {
            tv_status.setText("Active");
            tv_status.setTextColor(getResources().getColor(R.color.green_500));
            btn_ban.setText("Deactive This User");
        }
        else
        {
            tv_status.setText("Deactive");
            tv_status.setTextColor(getResources().getColor(R.color.red_600));
            btn_ban.setText("Active This User");
        }
        if(!img_url.isEmpty())
        {

            Glide.with(ctx)
                    .load(img_url)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .centerCrop()
                    .placeholder(R.drawable.logo)
                    .into(iv_profile);
        }
        else
        {
            iv_profile.setImageResource(R.drawable.icons8_user_100px_1);
        }

    }

    private void initViews() {
        iv_back=(ImageView)findViewById(R.id.iv_back);
        iv_profile=(CircleImageView) findViewById(R.id.iv_profile);
        tv_title=(TextView) findViewById(R.id.tv_title);
        tv_name=(TextView) findViewById(R.id.tv_name);
        tv_id=(TextView) findViewById(R.id.tv_id);
        tv_status=(TextView) findViewById(R.id.tv_status);
        tv_email=(TextView) findViewById(R.id.tv_email);
        btn_ban=(Button)findViewById(R.id.btn_ban);
        loadingBar=new ProgressDialog(ctx);
        loadingBar.setMessage("Loading...");
        loadingBar.setCanceledOnTouchOutside(false);
        module=new Module(ctx);
        id=getIntent().getStringExtra("id");
        email=getIntent().getStringExtra("email");
        name=getIntent().getStringExtra("name");
        status=getIntent().getStringExtra("status");
        img_url=getIntent().getStringExtra("img_url");
        toastMsg=new ToastMsg(ctx);


        user_ref= FirebaseDatabase.getInstance().getReference().child(USER_REF);
        btn_ban.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (NetworkConnection.connectionChecking(ctx)) {


                    loadingBar.show();
                    if (status.equalsIgnoreCase("0")) {
                        st_str = "1";
                    } else if (status.equalsIgnoreCase("1")) {
                        st_str = "0";
                    }
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("status", st_str);
                    user_ref.child(id).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                loadingBar.dismiss();
                                if (st_str.equalsIgnoreCase("0")) {
                                    toastMsg.toastIconSuccess("User Active Successfully..");
                                    tv_status.setText("Active");
                                    tv_status.setTextColor(getResources().getColor(R.color.green_500));

                                    btn_ban.setText(getResources().getString(R.string.txt_deactive));
                                    finish();
                                } else if (st_str.equalsIgnoreCase("1")) {
                                    toastMsg.toastIconSuccess("User Deative Sussefullyy..");
                                    tv_status.setText("Deactive");
                                    tv_status.setTextColor(getResources().getColor(R.color.red_600));
                                    btn_ban.setText(getResources().getString(R.string.txt_active));
                                    finish();
                                }

                            } else {
                                loadingBar.dismiss();
                                toastMsg.toastIconError("" + task.getException().getMessage());
                            }
                        }
                    });
                } else {
                    module.noConnectionActivity();
                }

            }
        });


        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
           finish();
            }
        });
    }
}
