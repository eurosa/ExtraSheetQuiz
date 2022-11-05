package com.upsun.quizz;

import static com.upsun.quizz.Config.Constants.HISTORY_REF;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.upsun.quizz.utils.SessionManagment;

import java.util.concurrent.TimeUnit;

public class SplashActivity extends AppCompatActivity {
    SessionManagment sessionManagment;
    private static final int MY_PERMISSIONS_REQUEST_CODE = 123;
    Activity mActivity=SplashActivity.this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        FirebaseApp.initializeApp(mActivity);


        try {
            FirebaseDatabase.getInstance().getReference().child("parents").keepSynced(true);
            //FirebaseDatabase.getInstance().getReference().child(QUIZ_REF).keepSynced(true);
            FirebaseDatabase.getInstance().getReference().child("products").keepSynced(true);
            FirebaseDatabase.getInstance().getReference().child("parents").keepSynced(true);
            FirebaseDatabase.getInstance().getReference().child(HISTORY_REF).keepSynced(true);

        }catch (Exception ignored){

        }

        try{
            getDynamicLike();
        }
        catch (Exception ignored){}



    sessionManagment=new SessionManagment(SplashActivity.this);

        if (sessionManagment.getExpiryTime() < System.currentTimeMillis()) {
            // read email and password
            sessionManagment.setExpiryTime(0);
            sessionManagment.setAppOpenFirstTime(false);
        } else {
//            Toast.makeText(this, "Time not expired", Toast.LENGTH_LONG).show();
        }

        Log.d("TAG", "onCreate: session==>"+sessionManagment.getAppOpenFirstTime() + " == "+(sessionManagment.getExpiryTime() < System.currentTimeMillis()));
        if(sessionManagment.getAppOpenFirstTime()){
            sessionManagment.setAppOpenFirstTime(false);
        }else {
            sessionManagment.setAppOpenFirstTime(true);
            sessionManagment.setExpiryTime(System.currentTimeMillis() + TimeUnit.HOURS.toMillis(24));
        }

        Thread background = new Thread() {
            public void run() {
                try {

                    sleep(2 * 1000);
                        if (sessionManagment.isLogin()) {
                            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        } else {
                            Intent intent = new Intent(getBaseContext(), LoginActivty.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();

                        }




                    finish();
                } catch (Exception e) {
                }
            }
        };

        background.start();
    }

    protected void checkPermission() {
        if (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.READ_EXTERNAL_STORAGE)
                + ContextCompat.checkSelfPermission(
                mActivity, Manifest.permission.READ_CONTACTS)
                + ContextCompat.checkSelfPermission(
                mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                + ContextCompat.checkSelfPermission(mActivity, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            // Do something, when permissions not granted
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    mActivity, Manifest.permission.READ_EXTERNAL_STORAGE)
                    || ActivityCompat.shouldShowRequestPermissionRationale(
                    mActivity, Manifest.permission.READ_CONTACTS)
                    || ActivityCompat.shouldShowRequestPermissionRationale(
                    mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    || ActivityCompat.shouldShowRequestPermissionRationale(
                    mActivity, Manifest.permission.CAMERA)
            ) {
                // If we should give explanation of requested permissions

                // Show an alert dialog here with request explanation
                AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                builder.setMessage("Read External, Read Contacts and Write External" +
                        " Storage permissions are required to do the task." + "Grant permission to access Camera");
                builder.setTitle("Please grant those permissions");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ActivityCompat.requestPermissions(
                                mActivity,
                                new String[]{
                                        Manifest.permission.READ_EXTERNAL_STORAGE,
                                        Manifest.permission.READ_CONTACTS,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                        Manifest.permission.CAMERA
                                },
                                MY_PERMISSIONS_REQUEST_CODE
                        );
                    }
                });
                builder.setNeutralButton("Cancel", null);
                AlertDialog dialog = builder.create();
                dialog.show();
            } else {
                // Directly request for required permissions, without explanation
                ActivityCompat.requestPermissions(
                        mActivity,
                        new String[]{
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.READ_CONTACTS,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.CAMERA
                        },
                        MY_PERMISSIONS_REQUEST_CODE
                );
            }
        } else {
            // Do something, when permissions are already granted
            Toast.makeText(mActivity, "Permissions already granted", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CODE: {
                // When request is cancelled, the results array are empty
                if (
                        (grantResults.length > 0) &&
                                (grantResults[0]
                                        + grantResults[1]
                                        + grantResults[2]
                                        + grantResults[3]
                                        == PackageManager.PERMISSION_GRANTED
                                )
                ) {
                    // Permissions are granted
                    Toast.makeText(mActivity, "Permissions granted.", Toast.LENGTH_SHORT).show();

//                    sessionManagement.setAllPermission(true);
                    Intent intent = new Intent(SplashActivity.this, LoginActivty.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();

                } else {
                    // Permissions are denied
                    Toast.makeText(mActivity, "Permissions denied.", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }
    void getDynamicLike(){

        //Toast.makeText(this, "called", Toast.LENGTH_SHORT).show();
        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(pendingDynamicLinkData -> {
                    Uri uri = null;
                    if (pendingDynamicLinkData!=null){
                        uri = pendingDynamicLinkData.getLink();
                        if(uri!= null){
                           // Toast.makeText(this, ""+uri.toString(), Toast.LENGTH_SHORT).show();
                            String Code = uri.getQueryParameter("referCode");
                            new SessionManagment(this).setReferredBy(Code);

                           // Toast.makeText(this, Code, Toast.LENGTH_SHORT).show();
                        }
                    }


                }).addOnFailureListener(e -> Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show());
    }
}