package com.upsun.quizz.Fragments;

import static com.upsun.quizz.Config.Constants.USER_REF;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hbb20.CountryCodePicker;
import com.upsun.quizz.Config.Module;
import com.upsun.quizz.Model.UserModel;
import com.upsun.quizz.R;
import com.upsun.quizz.networkconnectivity.NetworkConnection;
import com.upsun.quizz.utils.ToastMsg;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * A simple {@link Fragment} subclass.
 */
public class SendOtpFragment extends Fragment implements View.OnClickListener{
    private final String TAG=SendOtpFragment.class.getSimpleName();
    DatabaseReference user_ref;
    ProgressDialog loadingBar;
    String type="";
    EditText et_number;
    String code="";
    Module module;
    public static String mVerificationId="";
    Button btn_send;
    boolean is_exist=false;
    ToastMsg toastMsg;
    String android_id="";
    UserModel userModel;

    CountryCodePicker countryCode_picker;



    FirebaseAuth auth = FirebaseAuth.getInstance();

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBacks;
    public SendOtpFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_send_otp, container, false);
        initView(view);
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(event.getAction() == KeyEvent.ACTION_UP && keyCode==KeyEvent.KEYCODE_BACK)
                {
                    getActivity().finish();
                    return true;
                }
                return false;
            }
        });
        mCallBacks=new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                 code=phoneAuthCredential.getSmsCode();
//                Toast.makeText(getActivity(),"onVerificationCompleted-  "+code,Toast.LENGTH_LONG).show();
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
//                Toast.makeText(getActivity(),"onVerificationFailed-  "+e.getMessage(),Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(verificationId, forceResendingToken);
                mVerificationId=verificationId;
                OtpVerificationFragment fm=new OtpVerificationFragment();
                Bundle bundle=new Bundle();
                bundle.putString("code",code);
                bundle.putString("number",et_number.getText().toString());
                bundle.putString("type",type);
                bundle.putString("verification_id",verificationId);
                loadFragment(fm,bundle);

//                Toast.makeText(getActivity(),"onCodeSent- "+mVerificationId,Toast.LENGTH_LONG).show();
            }
        };
        return view;
    }

    private void initView(View view) {
        countryCode_picker=view.findViewById(R.id.countryCode_picker);
        et_number=(EditText)view.findViewById(R.id.et_number);
        btn_send=(Button) view.findViewById(R.id.btn_send);
        type=getArguments().getString("type");
        loadingBar=new ProgressDialog(getActivity());
        loadingBar.setMessage("Loading...");
        loadingBar.setCanceledOnTouchOutside(false);
        user_ref= FirebaseDatabase.getInstance().getReference().child(USER_REF);
        btn_send.setOnClickListener(this);
        module=new Module(getActivity());
        toastMsg=new ToastMsg(getActivity());
         android_id= Settings.Secure.getString(getActivity().getContentResolver(),Settings.Secure.ANDROID_ID);
        Log.e(TAG, "device_id "+android_id );
        checkDeviceId(android_id);

    }

    private void checkDeviceId(String android_id) {
        loadingBar.show();
        Query query=user_ref.orderByChild("device_id").equalTo(android_id);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                loadingBar.dismiss();
                try {
                    if(dataSnapshot.getValue()==null){
                        is_exist=false;
                    }else{
                        for(DataSnapshot snap:dataSnapshot.getChildren()){
                             userModel=snap.getValue(UserModel.class);
                            }
                        Log.e(TAG, "onDataChange: "+dataSnapshot.toString() );
                      is_exist=true;
                    }

                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                loadingBar.dismiss();
                Toast.makeText(getActivity(),""+databaseError.getMessage(),Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public void onClick(View v) {
     int id=v.getId();
     if(id == R.id.btn_send){
         if(et_number.getText().toString().length()!=10){
             toastMsg.toastIconError("Invalid Mobile Number");
         }else{
             sendRequest();
         }

      }

    }

    private void sendRequest() {
        if(NetworkConnection.connectionChecking(getActivity())){
            loadingBar.show();
//            String num="+91"+et_number.getText().toString().trim();
            String num="+"+countryCode_picker.getSelectedCountryCode()+et_number.getText().toString().trim();

            Log.d("thisisalok",num);

            user_ref.child(num).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(type.equalsIgnoreCase("r")){

                        if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {
                            Toast.makeText(getActivity(), "This mobile number already registered.", Toast.LENGTH_LONG).show();
                        }
                        else {
//                            if(is_exist){
//                                toastMsg.toastIconError(getString(R.string.already_attached));
//                            }else {
                                startPhoneNumberVerification();
//                            }
//                            startPhoneNumberVerification();
                        }


                    }
                    else if(type.equalsIgnoreCase("f"))
                    {
                        UserModel mUserModel=null;
//                        if(is_exist){
//
//
//                        }

                        if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                            Log.e(TAG, "onDataChange: "+dataSnapshot.toString() );
                            UserModel model=dataSnapshot.getValue(UserModel.class);
                            mUserModel=model;
                            if(!module.checkNull(mUserModel.getDevice_id())){
                                if(mUserModel.getDevice_id().equalsIgnoreCase(android_id)){
                                    startPhoneNumberVerification();
                            }
                                else {
                                    toastMsg.toastIconError(getString(R.string.already_attached));
                                }

                            }else{
                                startPhoneNumberVerification();
                            }

                        }
                        else
                        {

                            Toast.makeText(getActivity(),"This mobile number not registered.",Toast.LENGTH_LONG).show();
                        }


                    }
                    loadingBar.dismiss();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
        else
        {
            module.noConnectionActivity();
        }
    }

    private void startPhoneNumberVerification()
    {

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(auth)
//                        .setPhoneNumber("+91"+et_number.getText().toString().trim())
                        .setPhoneNumber("+"+countryCode_picker.getSelectedCountryCode()+et_number.getText().toString().trim())
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(Objects.requireNonNull(getActivity()))
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onCodeSent(String verificationId,
                               PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            // Save the verification id somewhere
            // ...


            // The corresponding whitelisted code above should be used to complete sign-in.

            mVerificationId=verificationId;
            Toast.makeText(getActivity(), "Code sent on your mobile", Toast.LENGTH_SHORT).show();

            OtpVerificationFragment fm=new OtpVerificationFragment();
            Bundle bundle=new Bundle();
            bundle.putString("code",code);
            bundle.putString("number","+"+countryCode_picker.getSelectedCountryCode()+et_number.getText().toString());
            bundle.putString("type",type);
            bundle.putString("verification_id",verificationId);
            loadFragment(fm,bundle);
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            // Sign in with the credential
            // ...


            Log.d("7870287340",phoneAuthCredential.toString());
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            // ...
            Log.d("7870287340","not verified "+e);
        }
    })
            .build();

        PhoneAuthProvider.verifyPhoneNumber(options);


        loadingBar.dismiss();
    }
    public void loadFragment(Fragment fm,Bundle args)
    {
        FragmentManager fragmentManager = getFragmentManager();
        fm.setArguments(args);
        fragmentManager.beginTransaction()
                .replace( R.id.container_otp,fm)
                .addToBackStack(null)
                .commit();
    }
}
