package com.upsun.quizz.Fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.upsun.quizz.Config.Module;
import com.upsun.quizz.ForgotPasswordActivity;
import com.upsun.quizz.R;
import com.upsun.quizz.RegisterActivity;
import com.upsun.quizz.networkconnectivity.NetworkConnection;

/**
 * A simple {@link Fragment} subclass.
 */
public class OtpVerificationFragment extends Fragment implements View.OnClickListener {

    EditText et_otp;
    String type = "";
    Button btn_verify;
    ProgressDialog loadingBar;
    Module module;
    String code = "", verificationId = "", number = "";

    public OtpVerificationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_otp_verification, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        code = getArguments().getString("code");
        number = getArguments().getString("number");
        type = getArguments().getString("type");
        verificationId = getArguments().getString("verification_id");
        et_otp = (EditText) view.findViewById(R.id.et_otp);
        btn_verify = (Button) view.findViewById(R.id.btn_verify);
        btn_verify.setOnClickListener(this);
        module = new Module(getActivity());
        loadingBar = new ProgressDialog(getActivity());
        loadingBar.setMessage("Loading...");
        loadingBar.setCanceledOnTouchOutside(false);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_verify) {
            if (et_otp.getText().toString().isEmpty()) {
                et_otp.setError("Please Enter");
            } else {
                verifyOtpWithMobile();
            }
        }
    }

    public void verifyOtpWithMobile() {
        if (NetworkConnection.connectionChecking(getActivity())) {


//            Toast.makeText(getActivity(), "Verification id "+verificationId, Toast.LENGTH_SHORT).show();
//            Toast.makeText(getActivity(), "et id "+verificationId, Toast.LENGTH_SHORT).show();
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, et_otp.getText().toString().trim());
            signUpwithCredentials(credential);

        } else {
            module.noConnectionActivity();
        }

    }

    private void signUpwithCredentials(PhoneAuthCredential credential) {
        loadingBar.show();
        FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    loadingBar.dismiss();
                    Toast.makeText(getActivity(), "successfull", Toast.LENGTH_LONG).show();
                    if (type.equalsIgnoreCase("r")) {
                        Intent intent = new Intent(getActivity(), RegisterActivity.class);
                        intent.putExtra("number", number);
                        startActivity(intent);
                    } else if (type.equalsIgnoreCase("f")) {
                        Intent intent = new Intent(getActivity(), ForgotPasswordActivity.class);
                        intent.putExtra("number", number);
                        startActivity(intent);
                    }

                } else {
                    loadingBar.dismiss();
                    if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                        String message = "Invalid code entered...";
                        Toast.makeText(getActivity(), "" + message, Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getActivity(), "" + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }
}
