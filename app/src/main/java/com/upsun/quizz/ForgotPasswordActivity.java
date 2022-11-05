package com.upsun.quizz;

import static com.upsun.quizz.Config.Constants.USER_REF;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.upsun.quizz.Config.Module;
import com.upsun.quizz.networkconnectivity.NetworkConnection;
import com.upsun.quizz.utils.ToastMsg;

import java.util.HashMap;

public class ForgotPasswordActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView iv_back;
    Button btn_pass;
    TextView tv_title;
    EditText et_pass,et_con_pass;
    ProgressDialog loadingBar;
    ToastMsg toastMsg;
    String number="";
    Activity ctx=ForgotPasswordActivity.this;
    DatabaseReference user_reef;
    Module module;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        initViews();
    }

    private void initViews() {
        iv_back=(ImageView)findViewById(R.id.iv_back);
        tv_title=(TextView) findViewById(R.id.tv_title);
        btn_pass=(Button) findViewById(R.id.btn_pass);
        et_pass=(EditText) findViewById(R.id.et_pass);
        et_con_pass=(EditText) findViewById(R.id.et_con_pass);
        loadingBar=new ProgressDialog(ctx);
        loadingBar.setMessage("Loading...");
        loadingBar.setCanceledOnTouchOutside(false);
        toastMsg=new ToastMsg(ctx);
        number=getIntent().getStringExtra("number");
        tv_title.setText("Update Password");
        user_reef= FirebaseDatabase.getInstance().getReference().child(USER_REF);
        btn_pass.setOnClickListener(this);
        iv_back.setOnClickListener(this);
        module=new Module(ctx);

    }

    @Override
    public void onClick(View v) {

        if(v.getId() == R.id.btn_pass)
        {
            String pass=et_pass.getText().toString();
            String con_pass=et_con_pass.getText().toString();
            if(pass.isEmpty())
            {
                et_pass.setError("Enter Password");
                et_pass.requestFocus();
            }
            else if(con_pass.isEmpty())
            {
                et_con_pass.setError("Enter Confirm Password");
                et_con_pass.requestFocus();
            }
            else
            {
                if(NetworkConnection.connectionChecking(this))
                {
                    if(con_pass.equals(pass))
                    {
                        updatePassword(number,pass);
                    }
                    else
                    {
                        toastMsg.toastIconError("Both Password must be matched");
                    }
                }
                else
                {
                    module.noConnectionActivity();
                }

            }
        }
        else if(v.getId()== R.id.iv_back)
        {
            Intent intent=new Intent(ctx,LoginActivty.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
    }

    private void updatePassword(String number, String pass) {
        loadingBar.show();
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("password",pass);
//        user_reef.child("+91"+number).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
        user_reef.child(number).updateChildren(hashMap).addOnCompleteListener(task -> {
            loadingBar.dismiss();
            if(task.isSuccessful())
            {
                toastMsg.toastIconSuccess("Password Updated..");
                Intent intent=new Intent(ctx,LoginActivty.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
            else
            {
                module.showToast(""+task.getException().getMessage());
            }

        });

    }
}
