package com.upsun.quizz;

import static com.upsun.quizz.Config.Constants.KEY_ID;
import static com.upsun.quizz.Config.Constants.PMETHOD_REF;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.upsun.quizz.Config.Module;
import com.upsun.quizz.utils.SessionManagment;
import com.upsun.quizz.utils.ToastMsg;

import java.util.HashMap;

public class AddBankDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView iv_back;
    TextView tv_title;
    RadioButton rb_upi,rb_bank;
    Button btn_add;
    LinearLayout lin_upi,lin_bank;
    EditText et_upi,et_name,et_acc_no,et_ifsc,et_holder;
    ToastMsg toastMsg;
    Module module;
    ProgressDialog loadingBar;
    String is_edit,type,id;
    SessionManagment sessionManagment;
    DatabaseReference payRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_add_bank_details);
        initViews();
    }

    private void initViews() {
        iv_back=(ImageView)findViewById(R.id.iv_back);
        tv_title=(TextView) findViewById(R.id.tv_title);
        rb_upi=(RadioButton) findViewById(R.id.rb_upi);
        rb_bank=(RadioButton) findViewById(R.id.rb_bank);
        btn_add=(Button) findViewById(R.id.btn_add);
        lin_upi=(LinearLayout) findViewById(R.id.lin_upi);
        lin_bank=(LinearLayout) findViewById(R.id.lin_bank);
        et_upi=(EditText) findViewById(R.id.et_upi);
        et_name=(EditText) findViewById(R.id.et_name);
        et_acc_no=(EditText) findViewById(R.id.et_acc_no);
        et_ifsc=(EditText) findViewById(R.id.et_ifsc);
        et_holder=(EditText) findViewById(R.id.et_holder);
        toastMsg=new ToastMsg(this);
        loadingBar=new ProgressDialog(this);
        loadingBar.setMessage("Loading...");
        loadingBar.setCanceledOnTouchOutside(false);
        module=new Module(this);
        btn_add.setOnClickListener(this);
        payRef= FirebaseDatabase.getInstance().getReference().child(PMETHOD_REF);
        sessionManagment=new SessionManagment(this);
        is_edit=getIntent().getStringExtra("is_edit");

        if(is_edit.equalsIgnoreCase("true"))
        { type=getIntent().getStringExtra("type");

            if(type.equalsIgnoreCase("upi"))
           {

               et_upi.setText(getIntent().getStringExtra("upi"));
               id=getIntent().getStringExtra("id");
               rb_upi.setChecked(true);
               rb_bank.setVisibility(View.GONE);
               rb_bank.setChecked(false);

               lin_upi.setVisibility(View.VISIBLE);
               if(lin_bank.getVisibility()==View.VISIBLE)
               {
                   lin_bank.setVisibility(View.GONE);
               }
           }
           else
           {
               rb_bank.setSelected(true);
               lin_upi.setVisibility(View.VISIBLE);
               id=getIntent().getStringExtra("id");
               et_name.setText(getIntent().getStringExtra("bank_name"));
               et_acc_no.setText(getIntent().getStringExtra("acc_no"));
               et_ifsc.setText(getIntent().getStringExtra("ifsc"));
               et_holder.setText(getIntent().getStringExtra("holder"));
               rb_upi.setChecked(true);
               rb_upi.setVisibility(View.GONE);
               rb_bank.setChecked(false);

               lin_bank.setVisibility(View.VISIBLE);
               if(lin_upi.getVisibility()==View.VISIBLE)
               {
                   lin_upi.setVisibility(View.GONE);
               }
           }
            btn_add.setText("Update Details");
        }

        tv_title.setText("Add Payment Method");

        rb_bank.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (lin_bank.getVisibility()==View.VISIBLE)
                {
                    lin_bank.setVisibility(View.GONE);
                }
                lin_upi.setVisibility(View.VISIBLE);

            }
        });

        rb_upi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (lin_upi.getVisibility()==View.VISIBLE)
                {
                    lin_upi.setVisibility(View.GONE);
                }
                lin_bank.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onClick(View v) {

        if(v.getId() == R.id.btn_add)
        {
             String typ="",bank_name="",ifsc="",h_name="",upi="",acc_no="",unique_id="";
             String user_id=sessionManagment.getUserDetails().get(KEY_ID);
             if(is_edit.equalsIgnoreCase("true"))
             {
                 unique_id=id;
             }
             else
             {
                 unique_id=module.getUniqueId("pay");
             }
              if(rb_upi.isChecked())
              {
               typ="upi";
               if(et_upi.getText().toString().isEmpty())
               {
                   et_upi.setError("Enter UPI");
                   et_upi.requestFocus();
               }
               else
               {
                   upi=et_upi.getText().toString();
                   addPaymentMethod(user_id,unique_id,typ,upi,bank_name,ifsc,acc_no,h_name);
               }
              }
              else if(rb_bank.isChecked())
              {
                  typ="bank";
                  bank_name=et_name.getText().toString();
                  ifsc=et_ifsc.getText().toString();
                  h_name=et_holder.getText().toString();
                  acc_no=et_acc_no.getText().toString();

                  if(bank_name.isEmpty())
                  {
                      module.reqEditFocus(et_name,"Enter Bank Name");
                  }
                  else if(ifsc.isEmpty())
                  {
                      module.reqEditFocus(et_ifsc,"Enter IFSC Code");
                  }
                  else if(h_name.isEmpty())
                  {
                      module.reqEditFocus(et_holder,"Enter Acoount Holder Name");
                  }
                  else if(acc_no.isEmpty())
                  {
                      module.reqEditFocus(et_acc_no,"Enter Account Number");
                  }
                  else
                  {
                      upi="";
                      addPaymentMethod(user_id,unique_id,typ,upi,bank_name,ifsc,acc_no,h_name);
                  }

              }
              else {
                  toastMsg.toastIconSuccess("Somethign wrong");
              }
        }
    }

    private void addPaymentMethod(String user_id, String unique_id, final String typ, String upi, String bank_name, String ifsc, String acc_no, String h_name) {
        loadingBar.show();
        HashMap<String,Object> map=new HashMap<>();
        map.put("pay_id",unique_id);
        map.put("user_id",user_id);
        map.put("type",typ);
        map.put("upi",upi);
        map.put("bank_name",bank_name);
        map.put("ifsc",ifsc);
        map.put("acc_no",acc_no);
        map.put("h_name",h_name);
        map.put("date",module.getCurrentDate());
        map.put("time",module.getCurrentTime());

        payRef.child(user_id).child(unique_id).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                loadingBar.dismiss();
                if(task.isSuccessful())
                {
                    if(is_edit.equalsIgnoreCase("true"))
                    {
                        toastMsg.toastIconSuccess("Details Updated..");
                    }
                    else
                    {
                        toastMsg.toastIconSuccess("Details Added..");
                    }
                    finish();
                }

                else
                {
                    module.showToast(""+task.getException().getMessage());
                }

            }
        });
    }
}
