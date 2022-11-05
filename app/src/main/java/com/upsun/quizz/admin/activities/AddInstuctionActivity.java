package com.upsun.quizz.admin.activities;

import static com.upsun.quizz.Config.Constants.INSTRUCTION_REF;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

public class AddInstuctionActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView iv_back;
    TextView tv_title;
    EditText et_title,et_desc;
    Button btn_add;
    ProgressDialog loadingBar;
    ToastMsg toastMsg;
    Activity activity=AddInstuctionActivity.this;
    DatabaseReference ins_ref;
    Module module;
    String id="",tl="",dsc="",is_edit="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_instuction);
        initViews();
    }

    private void initViews() {
        ins_ref= FirebaseDatabase.getInstance().getReference().child(INSTRUCTION_REF);
        iv_back=(ImageView)findViewById(R.id.iv_back);
        tv_title=(TextView) findViewById(R.id.tv_title);
        btn_add=(Button)findViewById(R.id.btn_add);
        et_title=(EditText) findViewById(R.id.et_title);
        et_desc=(EditText) findViewById(R.id.et_desc);
        is_edit=getIntent().getStringExtra("is_edit");
        if(is_edit.equalsIgnoreCase("true"))
        {
            id=getIntent().getStringExtra("id");
            tl=getIntent().getStringExtra("title");
            dsc=getIntent().getStringExtra("desc");
        }
        loadingBar=new ProgressDialog(activity);
        loadingBar.setMessage("Loading...");
        loadingBar.setCanceledOnTouchOutside(false);
        toastMsg=new ToastMsg(activity);
        module=new Module(activity);
        btn_add.setOnClickListener(this);
        if(is_edit.equalsIgnoreCase("true"))
        {
            et_title.setText(tl);
            et_desc.setText(dsc);

            btn_add.setText("Update Instruction");
        }

    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btn_add)
        {
            String ins_id="";
            String title=et_title.getText().toString();
            String desc=et_desc.getText().toString();

            if(title.isEmpty())
            {
                et_title.setError("Enter Title");
                et_title.requestFocus();
            }
            else if(desc.isEmpty())
            {
                et_desc.setError("Enter Title");
                et_desc.requestFocus();
            }
            else
            {
                if(is_edit.equalsIgnoreCase("true"))
                {
                    ins_id=id;
                }
                else
                {
                   ins_id=module.getUniqueId("ins");
                }

                if(NetworkConnection.connectionChecking(AddInstuctionActivity.this))
                {
                    addInstructions(ins_id,title,desc);
                }
                else
                {
                    module.noConnectionActivity();
                }

            }
        }
    }

    private void addInstructions(String ins_id, String title, String desc) {
        loadingBar.show();
        HashMap<String,Object> params=new HashMap<>();
        params.put("id",ins_id);
        params.put("title",title);
        params.put("desc",desc);

        ins_ref.child(ins_id).updateChildren(params).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                loadingBar.dismiss();
                if(task.isSuccessful())
                {
                    if(is_edit.equalsIgnoreCase("true"))
                    {
                        toastMsg.toastIconSuccess("Update Instruction Successfully..");
                    }
                    else
                    {
                        toastMsg.toastIconSuccess("Add Instruction Successfully..");
                    }
                    finish();

                }
                else
                {
                    Toast.makeText(activity, ""+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
