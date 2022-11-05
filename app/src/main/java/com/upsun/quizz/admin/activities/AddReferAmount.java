package com.upsun.quizz.admin.activities;

import static com.upsun.quizz.Config.Constants.CONFIG_REF;

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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.upsun.quizz.Config.Module;
import com.upsun.quizz.R;
import com.upsun.quizz.networkconnectivity.NetworkConnection;
import com.upsun.quizz.utils.ToastMsg;

import java.util.HashMap;

public class AddReferAmount extends AppCompatActivity {
    TextView tv_title;
    ImageView iv_back;
    EditText et_amt;
    Button btn_add_amt;
    ToastMsg toastMsg;
    ProgressDialog loadingBar;
    Activity ctx=AddReferAmount.this;
    DatabaseReference config_ref;
    Module module;
    String chld="wallet";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_initial_wallet);
        module=new Module(ctx);
        initViews();
        if(NetworkConnection.connectionChecking(ctx))
        {
            getAmount();
        }
        else
        {
            module.noConnectionActivity();
        }

        btn_add_amt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateAmount();
            }
        });

    }


    private void initViews() {
        loadingBar=new ProgressDialog(ctx);
        loadingBar.setMessage("Loading...");
        loadingBar.setCanceledOnTouchOutside(false);
        toastMsg=new ToastMsg(ctx);
        tv_title=(TextView)findViewById(R.id.tv_title);
        iv_back=(ImageView)findViewById(R.id.iv_back);
        et_amt=(EditText)findViewById(R.id.et_amt);
        btn_add_amt=(Button)findViewById(R.id.btn_add_amt);
        config_ref= FirebaseDatabase.getInstance().getReference().child(CONFIG_REF);

        ((Button) findViewById(R.id.btn_add_amt)).setText("Update Refer Amount");

    }
    private void getAmount() {
        loadingBar.show();
        config_ref.child(chld).child("refer_amount").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                loadingBar.dismiss();
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0)
                {
                    String amt=dataSnapshot.getValue(String.class);
                    et_amt.setText(amt);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ctx,""+databaseError.getMessage().toString(),Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updateAmount() {
        String amt=et_amt.getText().toString();
        if(amt.isEmpty())
        {
            et_amt.setError(getResources().getString(R.string.req_amt));
            et_amt.requestFocus();
        }
        else
        {

            loadingBar.show();
            HashMap<String,Object> hashMap=new HashMap<>();
            hashMap.put("refer_amount",amt);
            config_ref.child(chld).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    loadingBar.dismiss();
                    if(task.isSuccessful())
                    {
                        toastMsg.toastIconSuccess("Refer amount update successfully.");
                        finish();
                    }
                    else
                    {
                        Toast.makeText(ctx,""+task.getException().getMessage(),Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

}
