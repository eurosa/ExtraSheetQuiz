package com.upsun.quizz;

import static com.upsun.quizz.Config.Constants.CONFIG_REF;
import static com.upsun.quizz.Config.Constants.PREF_NAME;
import static com.upsun.quizz.Config.Constants.USER_REF;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.upsun.quizz.Config.Module;
import com.upsun.quizz.Model.ConfigWalletModel;
import com.upsun.quizz.networkconnectivity.NetworkConnection;
import com.upsun.quizz.utils.SessionManagment;
import com.upsun.quizz.utils.ToastMsg;

import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;

public class RegisterActivity extends AppCompatActivity {

    ProgressDialog loadingBar;
    ToastMsg toastMsg;
    EditText et_name, et_email, et_mobile, et_pass, et_con_pass, et_refer;
    Button btn_reg;
    String num = "";
    DatabaseReference config_ref;
    String chld = "wallet";
    String amt = "0";
    DatabaseReference user_ref;
    Activity ctx = RegisterActivity.this;
    Module module;
    String name, mobile, email, pass, con_pass, referLink, referCode;
    String main_id = "";
    boolean matched = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initViews();


    }

    private void initViews() {


        et_name = (EditText) findViewById(R.id.et_name);
        et_email = (EditText) findViewById(R.id.et_email);
        et_mobile = (EditText) findViewById(R.id.et_mobile);
        et_pass = (EditText) findViewById(R.id.et_pass);
        et_con_pass = (EditText) findViewById(R.id.et_con_pass);
        btn_reg = (Button) findViewById(R.id.btn_reg);
        et_refer = (EditText) findViewById(R.id.et_refer);
        num = getIntent().getStringExtra("number");
        et_mobile.setText(num);
        et_mobile.setEnabled(false);
        loadingBar = new ProgressDialog(ctx);
        module = new Module(ctx);
        loadingBar.setMessage("Loading...");
        loadingBar.setCanceledOnTouchOutside(false);
        toastMsg = new ToastMsg(ctx);

        String referredBy = new SessionManagment(this).getReferredBy();
        if (referredBy != null) {
            et_refer.setText(referredBy);
        }


        user_ref = FirebaseDatabase.getInstance().getReference().child(USER_REF);
        if (NetworkConnection.connectionChecking(this)) {
            getAmount();
        } else {
            new Module(this).noConnectionActivity();
        }
        btn_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateViews();


            }
        });
    }

    private void validateViews() {
        name = et_name.getText().toString();
        mobile = et_mobile.getText().toString();
        email = et_email.getText().toString();
        pass = et_pass.getText().toString();
        con_pass = et_con_pass.getText().toString();

        if (name.isEmpty()) {
            et_name.setError(getResources().getString(R.string.req_name));
            et_name.requestFocus();
        } else if (mobile.isEmpty()) {
            et_mobile.setError(getResources().getString(R.string.req_mobile));
            et_mobile.requestFocus();
        } else if (email.isEmpty()) {
            et_email.setError(getResources().getString(R.string.req_email));
            et_email.requestFocus();
        } else if (!(email.contains("@") || !(email.contains(".")))) {
            et_email.setError(getResources().getString(R.string.inavalid_email));
            et_email.requestFocus();
        } else if (pass.isEmpty()) {
            et_pass.setError(getResources().getString(R.string.req_pass));
            et_pass.requestFocus();
        } else if (con_pass.isEmpty()) {
            et_con_pass.setError(getResources().getString(R.string.req_con_pass));
            et_con_pass.requestFocus();
        } else if (pass.length() < 5) {
            et_pass.setError(getResources().getString(R.string.invalid_password));
            et_pass.requestFocus();

        } else if (con_pass.length() < 5) {
            et_con_pass.setError(getResources().getString(R.string.invalid_password));
            et_con_pass.requestFocus();
        } else {
            if (pass.equals(con_pass)) {
                btn_reg.setEnabled(false);
                if (et_refer.getText().toString().equals("")) {
//                main_id="+91"+mobile;
                    main_id = mobile;
                    SaveReferCode();
                } else {

//                main_id="+91"+mobile;
                    main_id = mobile;
                    queryReferCode(et_refer.getText().toString());


                }

            } else {
                Toast.makeText(ctx, "" + getResources().getString(R.string.match_pass), Toast.LENGTH_LONG).show();

            }

        }


    }

    private void registerUser(String id, String name, String email, String mobile, String pass, String referCode1, String referLink1) {

        loadingBar.show();
//         main_id="+91"+id;
        main_id = id;
        String android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);
        final HashMap<String, Object> map = new HashMap<>();
        map.put("id", main_id);
        map.put("name", name);
        map.put("email", email);
        map.put("mobile", mobile);
        map.put("password", pass);
        map.put("wallet", amt);
        map.put("img_url", "");
        map.put("rewards", "0");
        map.put("status", "0");
        map.put("quiz_status", "1");
        map.put("device_id", android_id);
        map.put("date", module.getCurrentDate());
        map.put("time", module.getCurrentTime());
        map.put("referCode", referCode1);
        map.put("referLink", referLink1);

        Log.e("Running", "Yes");

        user_ref.child(main_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                loadingBar.dismiss();
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    Log.e("Running", "No");
                    Toast.makeText(ctx, "This mobile number already registered.", Toast.LENGTH_LONG).show();
                } else {
                    Log.e("Running", "Yes");
                    user_ref.child(main_id).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {
                                Log.e("Running", "Yes");
                                Toast.makeText(ctx, "Registration Successful", Toast.LENGTH_LONG).show();


                                Intent intent = new Intent(ctx, LoginActivty.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(ctx, "" + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                loadingBar.dismiss();
                Toast.makeText(ctx, "" + databaseError.getMessage().toString(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private String getNewUserId(String mobile) {
        Date date = new Date();
        String mob_str = mobile.substring(0, 3);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ddmmyyHHmmss");
        String dt = mob_str + simpleDateFormat.format(date);
        return dt;
    }

    private void getAmount() {
        config_ref = FirebaseDatabase.getInstance().getReference().child(CONFIG_REF);
        loadingBar.show();
        config_ref.child(chld).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                loadingBar.dismiss();
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    ConfigWalletModel model = dataSnapshot.getValue(ConfigWalletModel.class);
                    String db_amt = model.getAmount().toString();
                    amt = db_amt;
                } else {
                    amt = "0";
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                loadingBar.dismiss();
                Toast.makeText(ctx, "" + databaseError.getMessage().toString(), Toast.LENGTH_LONG).show();
            }
        });
    }


    public void queryReferCode(String referCode) {
        matched = false;
        FirebaseDatabase.getInstance().getReference().child("users")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {

                            for (DataSnapshot data : snapshot.getChildren()) {


                                if (data.hasChild("referCode")) {
                                    if (data.child("referCode").getValue(String.class).equals(referCode)) {
                                        matched = true;

                                        // Toast.makeText(ctx, "exists" + data.getKey(), Toast.LENGTH_SHORT).show();
                                        FirebaseDatabase.getInstance().getReference().child("config")
                                                .child("wallet")
                                                .child("refer_amount")
                                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        if (snapshot.exists()) {

                                                            int reward_amount = Integer.parseInt(snapshot.getValue(String.class));
                                                            SaveReferCode();
                                                            //adding reward amount to the referral user
                                                            FirebaseDatabase.getInstance().getReference()
                                                                    .child("users")
                                                                    .child(data.getKey()).child("wallet").setValue(String.valueOf(Integer.parseInt(data.child("wallet").getValue(String.class)) + reward_amount));

                                                            addTranscation(String.valueOf(reward_amount), String.valueOf(0), data.getKey(), "successfully referred to", getUniqueId("txn"));


                                                            //Toast.makeText(RegisterActivity.this, "added to utkarsh", Toast.LENGTH_SHORT).show();
                                                            //adding reward for current user

                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {
                                                    }
                                                });
                                    }

                                }
                            }

                            if (!matched) {
                                btn_reg.setEnabled(true);
                                Toast.makeText(ctx, "Enter a correct refer code or leave it blank", Toast.LENGTH_SHORT).show();
                            }


                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });


    }

    private void addTranscation(final String points, String amt, final String user_id, final String status, String txnid) {
        loadingBar.show();
        HashMap<String, Object> map = new HashMap<>();
        map.put("txn_id", txnid);
        map.put("user_id", user_id);
        map.put("amount", amt);
        map.put("points", points);
        map.put("status", status);
        map.put("referredTo", name);
        map.put("date", module.getCurrentDate());
        map.put("time", module.getCurrentTime());
        FirebaseDatabase.getInstance().getReference().child("transactions")
                .child(txnid).setValue(map);

    }

    public String getUniqueId(String type) {
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ddMMyyyyhhmmss");
        return (type + simpleDateFormat.format(date).toString());
    }


    void SaveReferCode() {
        SharedPreferences pref = getSharedPreferences(PREF_NAME, 0);
        DatabaseReference newDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(main_id);

        newDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("referCode").exists()) {
                    String referCode = snapshot.child("referCode").getValue(String.class);
                    CreateAdvanceLink(referCode, newDatabaseReference);


                } else {
                    String referCode = createRandomCode(6);
                    newDatabaseReference.setValue(referCode);
                    CreateAdvanceLink(referCode, newDatabaseReference);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    void CreateAdvanceLink(String referCode, DatabaseReference ref) {

        DynamicLink dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse("https://www.example.com/?referCode=" + referCode))
                .setDomainUriPrefix("https://upsunquiz.page.link")
                .setAndroidParameters(
                        new DynamicLink.AndroidParameters.Builder(getPackageName())
                                .setMinimumVersion(1)
                                .build())
                .setSocialMetaTagParameters(
                        new DynamicLink.SocialMetaTagParameters.Builder()
                                .setTitle("UPSUN - Apps on Google Play")
                                .setDescription("UPSUN application is a quiz app to learn. use Refer Code " + referCode)
                                .setImageUrl(Uri.parse("https://play.google.com/store/apps/details?id=com.upsun.quizz"))
                                .build())
                .buildDynamicLink();

        FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLongLink(dynamicLink.getUri())
                .buildShortDynamicLink()
                .addOnCompleteListener(new OnCompleteListener<ShortDynamicLink>() {
                    @Override
                    public void onComplete(@NonNull Task<ShortDynamicLink> task) {
                        if (task.isSuccessful()) {

                            Uri shortLink = task.getResult().getShortLink();
                            try {


                                //  ref.child("referLink").setValue(shortLink.toString());
                                //  ref.child("referCode").setValue(referCode);
                                new SessionManagment(RegisterActivity.this).setReferCode(referCode);
                                new SessionManagment(RegisterActivity.this).setReferLink(shortLink.toString());

                                //Toast.makeText(ctx, "saved", Toast.LENGTH_SHORT).show();
                                registerUser(mobile, name, email, mobile, pass, referCode, shortLink.toString());
                            } catch (Exception e) {
                                Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }


                        }
                    }
                }).addOnFailureListener(e -> Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show());


    }

    public String createRandomCode(int codeLength) {
        char[] chars = "abcdefghijklmnopqrstuvwxyz1234567890".toCharArray();
        StringBuilder sb = new StringBuilder();
        Random random = new SecureRandom();
        for (int i = 0; i < codeLength; i++) {
            char c = chars[random.nextInt(chars.length)];
            sb.append(c);
        }
        String output = sb.toString();
        return output.toUpperCase(Locale.ROOT);
    }

}
