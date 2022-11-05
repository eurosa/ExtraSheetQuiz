package com.upsun.quizz.admin.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import com.upsun.quizz.Config.Module;
import com.upsun.quizz.R;
import com.upsun.quizz.networkconnectivity.NetworkConnection;
import com.upsun.quizz.utils.ToastMsg;

public class AddInfoPage extends AppCompatActivity implements View.OnClickListener {
    Spinner spin_type ;
    EditText et_title ,et_content ;
    Button add_page;
    ArrayList<String> type_list ;
    String[] arr = {"Terms & Conditions","Privacy Policy","About Us"};
    String type ="" ,title = "",content = "";
    ToastMsg toastMsg ;
    ProgressDialog loadingBar ;
    ImageView iv_back ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_info_page);
        initViews();
    }

    private void initViews() {
       spin_type = findViewById(R.id.spin_page_type);
       et_title = findViewById(R.id.et_title);
       et_content= findViewById(R.id.et_content);
       add_page = findViewById(R.id.btn_add_page);
        loadingBar=new ProgressDialog(AddInfoPage.this);
        loadingBar.setMessage("Loading...");
        loadingBar.setCanceledOnTouchOutside(false);
       toastMsg = new ToastMsg(this);
       type_list = new ArrayList<>();
       type_list.add(0,"Select");
       Collections.addAll(type_list, arr);
        Log.e("typelist",String.valueOf(type_list.size()));
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(AddInfoPage.this,android.R.layout.simple_spinner_dropdown_item,type_list);
        spin_type.setAdapter(arrayAdapter);
        add_page.setOnClickListener(this);
        iv_back = findViewById(R.id.iv_back);
        iv_back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id  = v.getId();
        if (id== R.id.btn_add_page)
        {
            if (spin_type.getSelectedItemPosition()!=0) {
                type = spin_type.getSelectedItem().toString();
            }
            title = et_title.getText().toString();
            content = et_content.getText().toString();
            if (type.isEmpty())
            {
                spin_type.requestFocus();
                toastMsg.toastIconError("Select a type");
            }
            else if (title.isEmpty())
            {
                et_title.requestFocus();
                toastMsg.toastIconError("Enter a title");
            }
            else if (content.isEmpty())
            {
                et_content.requestFocus();
                toastMsg.toastIconError("Enter content");
            }
            else
            {
                if(NetworkConnection.connectionChecking(AddInfoPage.this))
                {
                    uploadInfo(type,title,content);
                }
                else
                {
                    new Module(AddInfoPage.this).noConnectionActivity();
                }

            }
        }
        else if (id == R.id.iv_back)
        {
            finish();
        }
    }

    private void uploadInfo(final String type , String title , String content)
    {
        loadingBar.show();
        HashMap<String,Object> map = new HashMap<>();
        map.put("title",title);
        map.put("content",content);

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("info_pages").child(type.toLowerCase()).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                {
                    loadingBar.dismiss();
                    toastMsg.toastIconSuccess("Successfully added");
                    finish();
                }
                else
                {
                    loadingBar.dismiss();
                    toastMsg.toastIconError("Error adding info. Please try again");

                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                loadingBar.dismiss();
                toastMsg.toastIconError(""+e.getMessage());


            }
        });

    }
}
