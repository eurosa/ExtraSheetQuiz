package com.upsun.quizz.Fragments;

import static com.upsun.quizz.Config.Constants.BROAD_REWARDS;
import static com.upsun.quizz.Config.Constants.BROAD_WALLET;
import static com.upsun.quizz.Config.Constants.CONFIG_REF;
import static com.upsun.quizz.Config.Constants.USER_REF;
import static com.upsun.quizz.Model.AddProductModel.comp_pro;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.upsun.quizz.Adapter.ProductRewardAdapter;
import com.upsun.quizz.Config.Module;
import com.upsun.quizz.MainActivity;
import com.upsun.quizz.Model.AddProductModel;
import com.upsun.quizz.Model.UserModel;
import com.upsun.quizz.ProductDetailActivity;
import com.upsun.quizz.R;
import com.upsun.quizz.Worker.HistoryWorker;
import com.upsun.quizz.utils.RecyclerTouchListener;
import com.upsun.quizz.utils.SessionManagment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RewardTabFragment extends Fragment {
    public RewardTabFragment() {
        // Required empty public constructor
    }

    private List<AddProductModel> mList, allRewList;
    private ProductRewardAdapter mAdapter;
    DatabaseReference userRef, app_ref;
    SessionManagment sessionManagment;
    private DatabaseReference demoRef;
    Module module;
    ArrayList<UserModel> uList;
    RecyclerView rv_rew;
    ProgressDialog loadingBar;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reward_tab, container, false);
        ProgressDialog loadingBar = new ProgressDialog(RewardTabFragment.this.getActivity());
        loadingBar.setMessage("Loading...");
        init(view);

        return view;
    }

    private void init(View v) {

        demoRef = FirebaseDatabase.getInstance().getReference();

        rv_rew = v.findViewById(R.id.rv_rew);
        module = new Module(RewardTabFragment.this.getActivity());
        sessionManagment = new SessionManagment(RewardTabFragment.this.getActivity());
        uList = new ArrayList<>();
        app_ref = FirebaseDatabase.getInstance().getReference().child(CONFIG_REF);
        userRef = FirebaseDatabase.getInstance().getReference().child(USER_REF);
        rv_rew.setLayoutManager(new GridLayoutManager(RewardTabFragment.this.getActivity(), 2));
        rv_rew.setNestedScrollingEnabled(false);
        allRewList = new ArrayList<>();

        String TAG = "REWARD_FRAGMENT";
        v.setFocusableInTouchMode(true);
        v.requestFocus();

        fetchingCategory();

        rv_rew.addOnItemTouchListener(new RecyclerTouchListener(RewardTabFragment.this.getActivity(), rv_rew, new RecyclerTouchListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                ImageView v = view.findViewById(R.id.singleItemImage);
                ActivityOptionsCompat options1 = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(getActivity(), v, "profile");


                Intent intent = new Intent(RewardTabFragment.this.getActivity(), ProductDetailActivity.class);
                intent.putExtra("tv_pro_title", mList.get(position).getP_name());
                intent.putExtra("tv_pro_rew", mList.get(position).getP_reward());
                intent.putExtra("tv_pro_detail", mList.get(position).getP_detail());
                intent.putExtra("iv_pro_img", mList.get(position).getP_img());
                startActivity(intent, options1.toBundle());
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));
    }

    private void setAdapters() {

    }

    @Override
    public void onStart() {
        super.onStart();
        module.enqueuePeriodicRequest(HistoryWorker.class);

    }

    //fetching category from firebase
    private void fetchingCategory() {
//      loadingBar.show();
        mList = new ArrayList<>();
        allRewList.clear();
        Query query = demoRef.child("products").orderByChild("p_status").equalTo("0");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mList.clear();
                if (dataSnapshot.exists()) {
                    try {
                        getView().findViewById(R.id.noReward).setVisibility(View.GONE);
                    } catch (Exception ignored) {

                    }
                }
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    AddProductModel model = new AddProductModel();
                    model = snap.getValue(AddProductModel.class);
                    allRewList.add(model);
                    mList.add(model);
                }
                for (int i = 0; i < mList.size(); i++) {
                    String q_id = mList.get(i).getP_id();
                    String j_d = q_id.substring(7, 12);
                    String dtstr = j_d.substring(0, 2) + "-" + j_d.subSequence(2, 4) + "-" + j_d.subSequence(4, j_d.length());
                    int days = module.getDateDiff(dtstr.toString());
                    mList.get(i).setDays(String.valueOf(days));
                    Log.e("REWARD_FRAG", "" + mList.size() + " - " + mList.get(i).getP_status());
                }

                Collections.sort(mList, comp_pro);
                // loadingBar.dismiss();

                mAdapter = new ProductRewardAdapter(RewardTabFragment.this.getActivity(), mList);
                rv_rew.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
//            loadingBar.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(RewardTabFragment.this.getActivity(), "Please check your internet connection...", Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(bWallet);
        getActivity().unregisterReceiver(bRewards);

    }


    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(bWallet, new IntentFilter(BROAD_WALLET));
        getActivity().registerReceiver(bRewards, new IntentFilter(BROAD_REWARDS));
    }


    public BroadcastReceiver bWallet = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String type = intent.getStringExtra("type");
            String amt = intent.getStringExtra("amount");
            if (type.contentEquals("update")) {
                updateWallet(amt);
            }

        }
    };


    public BroadcastReceiver bRewards = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String type = intent.getStringExtra("type");
            String amt = intent.getStringExtra("amount");
            if (type.contentEquals("update")) {
                updateReward(amt);
            }

        }
    };

    private void updateWallet(String amt) {
        sessionManagment.updateWallet(amt);
        ((MainActivity) getActivity()).setWalletCounter(amt);
    }

    private void updateReward(String amt) {
        sessionManagment.updateRewards(amt);
        ((MainActivity) getActivity()).setRewardsCounter(amt);
    }
}