package com.upsun.quizz;

import static com.upsun.quizz.Config.Constants.KEY_ID;
import static com.upsun.quizz.Config.Constants.KEY_WALLET;
import static com.upsun.quizz.Config.Constants.USER_REF;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.anupkumarpanwar.scratchview.ScratchView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.upsun.quizz.Config.Module;
import com.upsun.quizz.Fragments.CategoryFragment;
import com.upsun.quizz.Fragments.MoreFragment;
import com.upsun.quizz.Fragments.MyHistoryFragment;
import com.upsun.quizz.Fragments.MyProfileFragment;
import com.upsun.quizz.Fragments.RewardFragment;
import com.upsun.quizz.Model.UserModel;
import com.upsun.quizz.Worker.HistoryWorker;
import com.upsun.quizz.utils.SessionManagment;
import com.upsun.quizz.utils.ToastMsg;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    BottomNavigationView navView;
    RelativeLayout rel_wallet, rel_rewards;
    TextView wallet_point, reward_point;
    DatabaseReference user_ref;
    Activity ctx = MainActivity.this;
    ProgressDialog loading;
    ToastMsg toastMsg;
    SessionManagment sessionManagment;
    Module module;
    ArrayList<Integer> bonusList = new ArrayList<>();
    int bonusPoint = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        navView = findViewById(R.id.nav_view);
        module = new Module(ctx);
        rel_rewards = findViewById(R.id.rel_rewards);
        rel_wallet = findViewById(R.id.rel_wallet);
        loading = new ProgressDialog(ctx);
        loading.setMessage("Loading...");
        loading.setCanceledOnTouchOutside(false);
        toastMsg = new ToastMsg(ctx);
        user_ref = FirebaseDatabase.getInstance().getReference().child(USER_REF);
        sessionManagment = new SessionManagment(MainActivity.this);
        wallet_point = (TextView) findViewById(R.id.wallet_point);
        reward_point = (TextView) findViewById(R.id.reward_point);
        rel_wallet.setOnClickListener(this);
        rel_rewards.setOnClickListener(this);
//        HomeFragment home = new HomeFragment();
        CategoryFragment home = new CategoryFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.frame, home)
                .addToBackStack(null)
                .commit();
        navView.setOnNavigationItemSelectedListener(this);

        for (int i = 1; i <= 9; i++) {
            bonusList.add(i);
        }
        String limit = SessionManagment.getLimit();
        if (limit.equals("")) {
            String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
            SessionManagment.setLimit(currentDate);
        }
        limit = SessionManagment.getLimit();
        Log.d("limit", limit);
        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        int dateDifference = (int) SessionManagment.getDateDiff(new SimpleDateFormat("dd-MM-yyyy"), limit, currentDate);
        System.out.println("dateDifference: " + dateDifference);
        if (dateDifference != 0) {

//        if(sessionManagment.getAppOpenFirstTime() && sessionManagment.getExpiryTime() > 0){

            Random random = new Random();
            bonusPoint = bonusList.get(random.nextInt(bonusList.size()));
            showScratchDialog();
            currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
            SessionManagment.setLimit(currentDate);

        }
//           Toast.makeText(this, "First time app opens in a day", Toast.LENGTH_LONG).show();
//        }
//        else {
//            Toast.makeText(this, "Second time app opens in a day", Toast.LENGTH_LONG).show();
//        }

        Log.d("TAG", "onCreate: home activity");

        FirebaseMessaging.getInstance().getToken()
                .addOnFailureListener(fail -> Log.d("TAG", "onCreate: failed to get token"))
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w("TAG", "Fetching FCM registration token failed", task.getException());
                        return;
                    } else {
                        Log.d("TAG", "onComplete: fail");
                    }
                    // Get new FCM registration token
                    String token = task.getResult();
                    Log.d("TAG", "token ==>" + token);
                });
    }

    public void showScratchDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("Name");

        // set the custom layout
        final View customLayout = getLayoutInflater().inflate(R.layout.dialog_scratch_card, null);
        builder.setView(customLayout);
        ScratchView scratchView = customLayout.findViewById(R.id.scratch_view);
        TextView txtBonus = customLayout.findViewById(R.id.txtBonus);
        txtBonus.setText(String.format("%d", bonusPoint));

        scratchView.setRevealListener(new ScratchView.IRevealListener() {
            @Override
            public void onRevealed(ScratchView scratchView) {
//                Toast.makeText(getApplicationContext(), "Reveled", Toast.LENGTH_LONG).show();;
                Toast.makeText(MainActivity.this, "Bonus added in your wallet", Toast.LENGTH_LONG).show();
                String updatedWallet = String.valueOf(Integer.parseInt(sessionManagment.getUserDetails().get(KEY_WALLET)) + bonusPoint);
                sessionManagment.updateWallet(updatedWallet);
                module.updateDbWallet(sessionManagment.getUserDetails().get(KEY_ID), updatedWallet);
                System.out.println(" ******************** " + updatedWallet + "===" + sessionManagment.getUserDetails().get(KEY_ID));
            }

            @Override
            public void onRevealPercentChangedListener(ScratchView scratchView, float percent) {
                if (percent > 0.3) {
                    Log.d("Reveal Percentage", "onRevealPercentChangedListener: " + String.valueOf(percent));
                    scratchView.setVisibility(View.INVISIBLE);
                }
            }
        });

        // create and show
        // the alert dialog
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        sessionManagment.setAppOpenFirstTime(true);
        sessionManagment.setExpiryTime(System.currentTimeMillis() + TimeUnit.HOURS.toMillis(24));

//        dialog.setCancelable(false);
        dialog.show();
    }

    public void setWalletCounter(String value) {
        if (wallet_point.getVisibility() == View.GONE) {
            wallet_point.setVisibility(View.VISIBLE);
        }
        wallet_point.setText("" + value);
    }

    public void setRewardsCounter(String value) {
        if (reward_point.getVisibility() == View.GONE) {
            reward_point.setVisibility(View.VISIBLE);
        }
        reward_point.setText("" + value);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home) {

            CategoryFragment home = new CategoryFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame, home)
                    .addToBackStack(null)
                    .commit();
        } else if (id == R.id.nav_my_quiz) {

//            if ( item.getItemId()== R.id.nav_my_quiz) {
//                item.setIcon(R.drawable.icons8_quiz_100px_1);
//            }
//            MyQuizsFragmemts quiz = new MyQuizsFragmemts();
            MyHistoryFragment quiz = new MyHistoryFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame, quiz)
                    .addToBackStack(null)
                    .commit();
        } else if (id == R.id.nav_profile) {

//            if( item.getItemId()==R.id.nav_profile)
//            {
//                item.setIcon(R.drawable.icons8_profile_100px_1);
//            }

//            Intent intent=new Intent(MainActivity.this, EditProfileActivity.class);
//            startActivity(intent);
            MyProfileFragment profile = new MyProfileFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame, profile)
                    .addToBackStack(null)
                    .commit();
        } else if (id == R.id.nav_reward) {
//            if ( item.getItemId()== R.id.nav_reward) {
//                item.setIcon(R.drawable.icons8_settings_100px_1);
//            }

            RewardFragment reward = new RewardFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame, reward)
                    .addToBackStack(null)
                    .commit();
        } else if (id == R.id.nav_more) {

//            if ( item.getItemId()== R.id.nav_more) {
//                item.setIcon(R.drawable.icons8_settings_100px_1);
//
//            }

            MoreFragment more = new MoreFragment(this);
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame, more)
                    .addToBackStack(null)
                    .commit();
        }
        return true;

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.rel_wallet) {
            Intent intent = new Intent(MainActivity.this, WalletActivity.class);
            startActivity(intent);
        } else if (id == R.id.rel_rewards) {
            Intent intent = new Intent(MainActivity.this, RewardsActivity.class);
            startActivity(intent);
        }

    }

    private void checkIcon(int id) {

    }

    @Override
    protected void onStart() {
        //loading.show();
        module.enqueuePeriodicRequest(HistoryWorker.class);
        String android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);
        final HashMap<String, Object> mapp = new HashMap<>();
        mapp.put("device_id", android_id);
//        String number = "+91" + sessionManagment.getUserDetails().get(KEY_MOBILE);
        String number = sessionManagment.getUserDetails().get(KEY_ID);
        user_ref.child(number).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Log.e("DATA", dataSnapshot.toString());
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0) {

                    UserModel model = dataSnapshot.getValue(UserModel.class);
                    if (model.getDevice_id() == null) {
                        user_ref.child(number).updateChildren(mapp).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful()) {
                                    // loading.dismiss();
                                } else {
                                    // loading.dismiss();
                                    Toast.makeText(ctx, "" + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });

                    }


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // loading.dismiss();
                Toast.makeText(ctx, "" + databaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        // loading.dismiss();
        super.onStart();
    }

}
