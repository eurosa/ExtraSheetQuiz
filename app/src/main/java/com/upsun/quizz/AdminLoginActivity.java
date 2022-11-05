package com.upsun.quizz;

import static com.upsun.quizz.Config.Constants.ADMIN_REF;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hbb20.CountryCodePicker;
import com.upsun.quizz.Config.Module;
import com.upsun.quizz.Model.UserModel;
import com.upsun.quizz.admin.activities.HomeActivity;
import com.upsun.quizz.networkconnectivity.NetworkConnection;
import com.upsun.quizz.networkconnectivity.NoInternetConnection;

public class AdminLoginActivity extends AppCompatActivity implements View.OnClickListener {
    CountryCodePicker countryCode_picker;
    EditText et_number, et_pass;
    Button btn_login;
    ProgressDialog loadingBar;
    DatabaseReference admin_ref;
    Module module;
    public static String ad_name = "", ad_mobile = "", ad_email = "", ad_image = "";
    Activity ctx = AdminLoginActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);
        initViews();
    }

    private void initViews() {
        admin_ref = FirebaseDatabase.getInstance().getReference().child(ADMIN_REF);
        et_number = (EditText) findViewById(R.id.et_number);
        et_pass = (EditText) findViewById(R.id.et_pass);
        btn_login = (Button) findViewById(R.id.btn_login);
        countryCode_picker = (CountryCodePicker) findViewById(R.id.countryCode_picker);
        btn_login.setOnClickListener(this);
        loadingBar = new ProgressDialog(ctx);
        loadingBar.setMessage("Loading...");
        loadingBar.setCanceledOnTouchOutside(false);
        module = new Module(ctx);

//        et_number.setText("6376726001");
//        et_pass.setText("sa#&shi10#");
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_login) {
            validate();
        }
    }

    private void validate() {
        String number = et_number.getText().toString();
        String pass = et_pass.getText().toString();
//       String number="8081031624";
//        String pass="123";
        if (number.isEmpty()) {
            et_number.setError(getResources().getString(R.string.req_mobile));
            et_number.requestFocus();
        } else if (pass.isEmpty()) {
            et_pass.setError(getResources().getString(R.string.req_pass));
            et_pass.requestFocus();
        } else {
            if (NetworkConnection.connectionChecking(ctx)) {

                adminLogin("+"+countryCode_picker.getSelectedCountryCode()+number, pass);
            } else {
                Intent intent = new Intent(AdminLoginActivity.this, NoInternetConnection.class);
                startActivity(intent);
            }
        }

    }

    private void adminLogin(String number, final String pass) {
        loadingBar.show();
        String num =  number;
        Log.d("number", number);
        admin_ref.child(num).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                loadingBar.dismiss();
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    UserModel model = new UserModel();
                    model = dataSnapshot.getValue(UserModel.class);
                    Log.e("TAG", "onDataChange: " + dataSnapshot.toString());

                    if (model.getPassword().equals(pass)) {
                        ad_email = model.getEmail();
                        ad_image = model.getImg_url();
                        ad_mobile = model.getMobile();
                        ad_name = model.getName();
                        Intent intent = new Intent(ctx, HomeActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(ctx, "Wrong Password", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(ctx, "Mobile number not exists", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                loadingBar.dismiss();
                module.showToast("" + databaseError.getMessage().toString());
            }
        });

    }
}
