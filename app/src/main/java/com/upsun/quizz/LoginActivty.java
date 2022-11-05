package com.upsun.quizz;

import static com.upsun.quizz.Config.Constants.USER_REF;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hbb20.CountryCodePicker;
import com.upsun.quizz.Config.Module;
import com.upsun.quizz.Model.UserModel;
import com.upsun.quizz.networkconnectivity.NetworkConnection;
import com.upsun.quizz.utils.SessionManagment;
import com.upsun.quizz.utils.ToastMsg;

import java.util.HashMap;

public class LoginActivty extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {
    private final String TAG = LoginActivty.class.getSimpleName();
    CountryCodePicker countryCode_picker;

    Activity ctx = LoginActivty.this;
    Button btn_login;
    ProgressDialog loading;
    EditText et_pass, et_number;
    Button tv_acc;
    TextView tv_admin, tv_forgot;
    DatabaseReference user_ref;
    SessionManagment sessionManagment;
    ToastMsg toastMsg;
    Module module;
    SignInButton signInButton;
    private GoogleApiClient googleApiClient;
    TextView textView;
    private static final int RC_SIGN_IN = 1;
    FirebaseAuth firebaseAuth;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_activty);
        initViews();
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken("1017127065583-7isth6hl0mk1hu7bpd71tsa0fcavkfmf.apps.googleusercontent.com")
                .build();
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                startActivityForResult(intent, RC_SIGN_IN);
            }
        });

    }

    private void initViews() {
        btn_login = (Button) findViewById(R.id.btn_login);
        tv_acc = (Button) findViewById(R.id.tv_acc);
        tv_admin = (TextView) findViewById(R.id.tv_admin);
        tv_forgot = (TextView) findViewById(R.id.tv_forgot);
        et_number = (EditText) findViewById(R.id.et_number);
        et_pass = (EditText) findViewById(R.id.et_pass);
        countryCode_picker = (CountryCodePicker) findViewById(R.id.countryCode_picker);
        sessionManagment = new SessionManagment(ctx);
        loading = new ProgressDialog(ctx);
        loading.setMessage("Loading...");
        loading.setCanceledOnTouchOutside(false);
        toastMsg = new ToastMsg(ctx);
        module = new Module(ctx);
        user_ref = FirebaseDatabase.getInstance().getReference().child(USER_REF);
        btn_login.setOnClickListener(this);
        tv_acc.setOnClickListener(this);
        tv_admin.setOnClickListener(this);
        tv_forgot.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.btn_login) {
            String username = et_number.getText().toString();
            String pass = et_pass.getText().toString();
//            String username="8081740664";
//            String pass="Yashch";
//            String username="6376726001";
//            String pass="vikas";
            if (username.isEmpty()) {
                et_number.setError(getResources().getString(R.string.req_mobile));
                et_number.requestFocus();
            } else if (pass.isEmpty()) {
                et_pass.setError(getResources().getString(R.string.req_pass));
                et_pass.requestFocus();
            } else {
                if (NetworkConnection.connectionChecking(LoginActivty.this)) {

                    userLogin("+" + countryCode_picker.getSelectedCountryCode() + username, pass);
                } else {
                    module.noConnectionActivity();
                }

            }
//            Intent intent=new Intent(ctx,MainActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(intent);
//            finish();
        } else if (id == R.id.tv_acc) {

            Intent intent = new Intent(ctx, MobileVerification.class);
            intent.putExtra("type", "r");
            startActivity(intent);
        } else if (id == R.id.tv_forgot) {
            Intent intent = new Intent(ctx, MobileVerification.class);
            intent.putExtra("type", "f");
            startActivity(intent);
        } else if (id == R.id.tv_admin) {
            Intent intent = new Intent(ctx, AdminLoginActivity.class);
            startActivity(intent);
        }

    }

    private void userLogin(String username, final String pass) {

        loading.show();
        String android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);
        final HashMap<String, Object> mapp = new HashMap<>();
        mapp.put("device_id", android_id);
        final String number = username;
        user_ref.child(number).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                loading.dismiss();
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                    UserModel model = dataSnapshot.getValue(UserModel.class);
                    Log.e(TAG, "onDataChange: " + dataSnapshot);
                    if (pass.equals(model.getPassword())) {
                        Log.e(TAG, "onDataChange: " + model.getDevice_id());
                        if (model.getStatus().equalsIgnoreCase("0")) {
                            updateSession(model, android_id);
                            if (module.checkNull(model.getDevice_id())) {
                                user_ref.child(number).updateChildren(mapp).addOnCompleteListener(task -> {

                                    if (task.isSuccessful()) {
                                        updateSession(model, android_id);
                                    } else {
                                        Toast.makeText(ctx, "" + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                });
                            } else {
                                if (number.equals("+919424224855") || number.equals("+918320948419")) {
                                    updateSession(model, android_id);
                                } else if (android_id.equalsIgnoreCase(model.getDevice_id())) {
                                    updateSession(model, android_id);
                                } else {
                                    toastMsg.toastIconError("Your device is not matched with your credentials.");
                                }

                            }


                     /* user_ref.child(number).updateChildren(mapp).addOnCompleteListener(new OnCompleteListener<Void>() {
                          @Override
                          public void onComplete(@NonNull Task<Void> task) {

                              if(task.isSuccessful())
                              {
                                  updateSession(model,android_id);
                              }
                              else
                              {
                                  Toast.makeText(ctx,""+task.getException().getMessage(),Toast.LENGTH_LONG).show();
                              }
                          }
                      });*/
                        } else {
                            toastMsg.toastIconError("Your account is deactive. Contact to Admin");
                        }


                    } else {
                        toastMsg.toastIconError("Invalid Password");
                    }

//                    Toast.makeText(ctx,""+model.getEmail()+"\n"+model.getId(),Toast.LENGTH_LONG).show();
                } else {
                    toastMsg.toastIconError("Mobile number not registered");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                loading.dismiss();
                Toast.makeText(ctx, "" + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }

    private void updateSession(UserModel model, String android_id) {
        sessionManagment.createLoginSession(model.getId(), model.getName(), model.getEmail(), model.getMobile(), model.getImg_url(), model.getWallet(), model.getRewards(), model.getQuiz_status(), android_id);
        sessionManagment.setReferLink(model.getReferLink());
        sessionManagment.setReferCode(model.getReferCode());
        Intent intent = new Intent(ctx, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> signInAccountTask = GoogleSignIn
                    .getSignedInAccountFromIntent(data);

            handleSignInResult(signInAccountTask);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> result) {
        try {
            GoogleSignInAccount googleSignInAccount = result.getResult(ApiException.class);


            if (googleSignInAccount!=null) {
                // Initialize sign in account
                try {
                    // Initialize sign in account
                    // Check condition
                    if (googleSignInAccount != null) {
                        // When sign in account is not equal to null
                        // Initialize auth credential
                        AuthCredential authCredential = GoogleAuthProvider
                                .getCredential(googleSignInAccount.getIdToken()
                                        , null);
                        // Check credential
                        firebaseAuth.signInWithCredential(authCredential)
                                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        // Check condition
                                        if (task.isSuccessful()) {
                                            if (preferences.getBoolean("is_login", false)) {
                                                Toast.makeText(ctx, "Already registered, Please login", Toast.LENGTH_SHORT).show();
                                            } else {
                                                editor.putBoolean("is_login", true);
                                                editor.apply();
                                                Intent intent = new Intent(LoginActivty.this, RegisterActivity.class);
                                                intent.putExtra("name", googleSignInAccount.getDisplayName());
                                                intent.putExtra("email", googleSignInAccount.getEmail());
                                                startActivity(intent);
                                            }
                                        } else {
                                            Toast.makeText(ctx, "Authentication Failed", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                    }
                } catch (Exception e) {
                    Toast.makeText(ctx, e.getMessage(), Toast.LENGTH_SHORT).show();
                }


            } else {
                Toast.makeText(getApplicationContext(), "Sign in cancel", Toast.LENGTH_LONG).show();
            }

        } catch (ApiException e) {
            e.printStackTrace();
        }
    }
}