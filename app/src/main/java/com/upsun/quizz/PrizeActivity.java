package com.upsun.quizz;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.LoadAdError;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.upsun.quizz.Adapter.PrizeAdapter;
import com.upsun.quizz.Config.Constants;
import com.upsun.quizz.Config.Module;
import com.upsun.quizz.Model.RewardModel;
import com.upsun.quizz.utils.BannerAds;
import com.upsun.quizz.utils.LoadingBar;

import java.util.ArrayList;

public class PrizeActivity extends AppCompatActivity {

    LoadingBar loadingBar;
    Module module;
    PrizeAdapter adapter;
    RecyclerView recyclerView;
    RelativeLayout adView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prize);
        loadingBar = new LoadingBar(this);
        module = new Module(this);
        loadingBar.show();
        adView=findViewById(R.id.adView);
        adView.setVisibility(View.VISIBLE);
        String quiz_id= getIntent().getStringExtra("quiz_id");
        Log.e("Quiz Id",quiz_id);
        recyclerView = findViewById(R.id.rv_prize);

        loadISNTAd();

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        final ArrayList<RewardModel> list=new ArrayList<>();
        DatabaseReference rankRef= FirebaseDatabase.getInstance().getReference().child("ranks").child(quiz_id);
        rankRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                loadingBar.dismiss();
                try {
                    if (dataSnapshot.hasChildren()) {
                        for(DataSnapshot snap:dataSnapshot.getChildren())
                        {
                            RewardModel model=snap.getValue(RewardModel.class);
                            list.add(model);
                        }
                        adapter = new PrizeAdapter(list,PrizeActivity.this);
                        recyclerView.setLayoutManager(new GridLayoutManager(PrizeActivity.this,1));
                        recyclerView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                        Log.e("DATA",list.toString());

                    }
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                    module.showToast("Something Went Wrong");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                loadingBar.dismiss();
                module.showToast(""+databaseError.getMessage());
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        new BannerAds().showBannerAds(PrizeActivity.this,adView);
    }
    InterstitialAd mInterstitialAd;
    public void  loadISNTAd(){
        mInterstitialAd = new InterstitialAd(this);

        // Insert the Ad Unit ID
        mInterstitialAd.setAdUnitId(Constants.ad_intertitial);

        // Create ad request.
        AdRequest adRequest = new AdRequest.Builder().build();

        // Begin loading your interstitial.
        mInterstitialAd.loadAd(adRequest);

        // Prepare an Interstitial Ad Listener
        mInterstitialAd.setAdListener(new AdListener() {
            public void onAdLoaded() {
                // Call displayInterstitial() function
                mInterstitialAd.show();
                // Toast.makeText(ctx, "loaded", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdClosed() {
                super.onAdClosed();

            }

            @Override
            public void onAdFailedToLoad(LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                //Toast.makeText(ctx, ""+loadAdError.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
            }

            @Override
            public void onAdClicked() {
                super.onAdClicked();
            }
        });
    }
}