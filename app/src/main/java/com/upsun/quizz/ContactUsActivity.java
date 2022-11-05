package com.upsun.quizz;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.upsun.quizz.Config.Module;
import com.upsun.quizz.Model.ContactUsModel;
import com.upsun.quizz.networkconnectivity.NetworkConnection;
import com.upsun.quizz.utils.ToastMsg;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ContactUsActivity extends AppCompatActivity implements View.OnClickListener {
    EditText et_name, et_email ,et_subject,et_message ;
    Button btn_Submit ;
    String name = "" ,email="",subject ="",message="";
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    ToastMsg toastMsg;
  ProgressDialog loadingBar;
  ImageView iv_back ;
  Module module;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        et_email = findViewById(R.id.et_email);
        et_name = findViewById(R.id.et_name);
        et_message = findViewById(R.id.et_message);
        et_subject = findViewById(R.id.et_subject);
        btn_Submit = findViewById(R.id.btn_submit);
        iv_back = findViewById(R.id.iv_back);
        iv_back.setOnClickListener(this);
        btn_Submit.setOnClickListener(this);
        toastMsg=new ToastMsg(this);
    module=new Module(this);
        loadingBar=new ProgressDialog(this);
        loadingBar.setMessage("Loading...");
        loadingBar.setCanceledOnTouchOutside(false);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_submit)
        {
            name = et_name.getText().toString();
            email = et_email.getText().toString();
            subject= et_subject.getText().toString();
            message = et_message.getText().toString();

            if (name.isEmpty())
            { et_name.setError(getResources().getString(R.string.req_name));
                et_name.requestFocus();}
            else if (email.isEmpty())
            {et_email.setText(getResources().getString(R.string.req_email));
            et_email.requestFocus();}
            else if(!(email.matches(emailPattern)))
            {
                et_email.requestFocus();
                et_email.setError("Enter Valid Email Id");
            }
            else if (subject.isEmpty())
            {
                et_subject.setError("Enter Subject");
                et_subject.requestFocus();
            }
            else if (message.isEmpty())
            {
                et_message.setError("Enter your Query");
                et_message.requestFocus();
            }
            else if (message.length()<10)
            {
                et_message.setError("Query should be minimum of 10 characters");
                et_message.requestFocus();
            }
            else
            {
                if(NetworkConnection.connectionChecking(this))
                {
                    submitEnquiry(name,email,subject,message);
                }
                else
                {
                    module.noConnectionActivity();
                }

            }

        }
        else if (id == R.id.iv_back)
        { finish();}

    }

    private void submitEnquiry(String name , String email,String subject ,String message)
    {
        loadingBar.show();
        String id=getUniqueId();
        DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference();
        ContactUsModel model = new ContactUsModel(name ,email,subject,message,id);
        dataRef.child("contact_us").child(id).setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                loadingBar.dismiss();
                toastMsg.toastIconSuccess("Enquiry Sent Successfully");
                finish();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {


                loadingBar.dismiss();
                toastMsg.toastIconError("Error Sending Enquiry.Please try Again \n "+e.getMessage());
                e.printStackTrace();
            }
        });
    }
    public String getUniqueId()
    {
        String ss = "";
        String unique_id = "";
        Date date=new Date();
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("ddMMyyyyhhmmss");
        unique_id = ss +simpleDateFormat.format(date).toString();
        return unique_id ;
    }
//    public String getUniqueId()
//    {
//        Date date=new Date();
//        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("ddMMyyyyhhmmss");
//        return simpleDateFormat.format(date).toString();
//    }
}
