package com.upsun.quizz.admin.activities;

import static com.upsun.quizz.Config.Constants.POINT_REF;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
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
import com.upsun.quizz.R;
import com.upsun.quizz.networkconnectivity.NetworkConnection;
import com.upsun.quizz.utils.ToastMsg;

import java.util.HashMap;

public class AddPointsActivity extends AppCompatActivity implements View.OnClickListener {
  ImageView iv_back;
  TextView tv_title;
  ProgressDialog loadingBar;
  ToastMsg toastMsg;
  Module module;
  Activity ctx=AddPointsActivity.this;
  DatabaseReference points_ref;
  EditText et_points,et_amount;
  Button btn_add;
  String id,is_edit,points,amount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_points);
        initViews();
    }

    private void initViews() {
        iv_back=(ImageView)findViewById(R.id.iv_back);
        tv_title=(TextView)findViewById(R.id.tv_title);
        loadingBar=new ProgressDialog(ctx);
        loadingBar.setMessage("Loading...");
        loadingBar.setCanceledOnTouchOutside(false);
        toastMsg=new ToastMsg(ctx);
        module=new Module(ctx);
        points_ref= FirebaseDatabase.getInstance().getReference().child(POINT_REF);
        et_amount=(EditText)findViewById(R.id.et_amount);
        et_points=(EditText)findViewById(R.id.et_points);
        btn_add=(Button) findViewById(R.id.btn_add);
        tv_title.setText("Add Points");
        is_edit=getIntent().getStringExtra("is_edit");

        iv_back.setOnClickListener(this);
        btn_add.setOnClickListener(this);
        if(is_edit.equalsIgnoreCase("true"))
        {
            id=getIntent().getStringExtra("id");
            points=getIntent().getStringExtra("points");
            amount=getIntent().getStringExtra("amt");
            et_amount.setText(amount);
            et_points.setText(points);
            btn_add.setText("Update Points");
        }
        tv_title.setText("Add Points");
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.iv_back)
        {
            finish();
        }
        else if(v.getId() == R.id.btn_add)
        {
            if(et_points.getText().toString().isEmpty())
            {
                et_points.setError("Enter Some Points");
                et_points.requestFocus();
            }
            else if(et_amount.getText().toString().isEmpty())
            {
                et_amount.setError("Enter Some Amount");
                et_amount.requestFocus();
            }
            else
            {
                String unq_id="";
                if(is_edit.equalsIgnoreCase("true"))
                {
                    unq_id=id;
                }
                else
                {
                    unq_id=module.getUniqueId("points").toString();
                }
                if(NetworkConnection.connectionChecking(ctx))
                {
                    addPoints(unq_id,et_points.getText().toString(),et_amount.getText().toString());
                }
                else
                {
                    module.noConnectionActivity();
                }

            }
        }
    }

    private void addPoints(String id, String points, String amt) {
        loadingBar.show();
        HashMap<String,Object> params=new HashMap<>();
        params.put("id",id);
        params.put("points",points);
        params.put("amount",amt);

        points_ref.child(id).updateChildren(params).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                loadingBar.dismiss();
                if(task.isSuccessful())
                {
                    if(is_edit.equalsIgnoreCase("true"))
                    {
                        toastMsg.toastIconSuccess("Points Updated..");
                    }
                    else
                    {
                        toastMsg.toastIconSuccess("Points Added..");
                    }
                    finish();

                }
                else
                {
                    module.showToast(""+task.getException().getMessage().toString());
                }

            }
        });
    }
}
