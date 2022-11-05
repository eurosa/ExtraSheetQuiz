package com.upsun.quizz.Fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DatabaseReference;
import com.upsun.quizz.Adapter.ProductRewardAdapter;
import com.upsun.quizz.Adapter.RewardHistoryAdaptor;
import com.upsun.quizz.Config.Module;
import com.upsun.quizz.Model.AddProductModel;
import com.upsun.quizz.Model.UserModel;
import com.upsun.quizz.R;
import com.upsun.quizz.utils.SessionManagment;

import java.util.ArrayList;
import java.util.List;


public class RewardFragment extends Fragment {
    private List<AddProductModel> mList, allRewList;
    private ProductRewardAdapter mAdapter;
    DatabaseReference userRef, app_ref;
    SessionManagment sessionManagment;
    private DatabaseReference demoRef;
    Module module;
    ArrayList<UserModel> uList;
    RecyclerView rv_rew;
    ProgressDialog loadingBar;
    ViewPager viewPager;
    FragmentPagerAdapter adapterViewPager;
    TabLayout tabLayout;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_reward, container, false);
        loadingBar = new ProgressDialog(getActivity());
        loadingBar.setMessage("Loading...");
        loadingBar.setCanceledOnTouchOutside(false);
        viewPager = (ViewPager) view.findViewById(R.id.pager);
        tabLayout = (TabLayout) view.findViewById(R.id.tab_layout);

        tabLayout.addTab(tabLayout.newTab().setText("Reward"));
        tabLayout.addTab(tabLayout.newTab().setText("History"));
        setRHAdapter();



//        init(view);
        return view;
    }

    private void setRHAdapter() {
        FragmentManager fragmentManager = getChildFragmentManager();
        final RewardHistoryAdaptor adapter = new RewardHistoryAdaptor(fragmentManager, tabLayout.getTabCount());
        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


    }


//    private void init(View v) {
//        ProgressDialog loadingBar = new ProgressDialog(getActivity());
//        demoRef = FirebaseDatabase.getInstance().getReference();
//        loadingBar.setMessage("Loading...");
//        rv_rew = v.findViewById(R.id.rv_rew);
//        module = new Module(getActivity());
//        sessionManagment = new SessionManagment(getActivity());
//        uList = new ArrayList<>();
//        app_ref = FirebaseDatabase.getInstance().getReference().child(CONFIG_REF);
//        userRef = FirebaseDatabase.getInstance().getReference().child(USER_REF);
//        rv_rew.setLayoutManager(new GridLayoutManager(getActivity(), 2));
//        rv_rew.setNestedScrollingEnabled(false);
//        allRewList = new ArrayList<>();
//
//        String TAG = "REWARD_FRAGMENT";
//        v.setFocusableInTouchMode(true);
//        v.requestFocus();
//        v.setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//
//                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
//                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//                    builder.setTitle("Confirmation");
//                    builder.setMessage("Are you sure want to exit?");
//                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            getActivity().finishAffinity();
//                        }
//                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.dismiss();
//                        }
//                    });
//                    AlertDialog dialog = builder.create();
//                    dialog.show();
//                    return true;
//                }
//                return false;
//            }
//        });
////        fetchingCategory();
//
//        rv_rew.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), rv_rew, new RecyclerTouchListener.OnItemClickListener() {
//            @Override
//            public void onItemClick(View view, int position) {
//
//                Intent intent = new Intent(RewardFragment.this.getActivity(), ProductDetailActivity.class);
//                intent.putExtra("tv_pro_title", mList.get(position).getP_name());
//                intent.putExtra("tv_pro_rew", mList.get(position).getP_reward());
//                intent.putExtra("tv_pro_detail", mList.get(position).getP_detail());
//                intent.putExtra("iv_pro_img", mList.get(position).getP_img());
//                startActivity(intent);
//            }
//
//            @Override
//            public void onLongItemClick(View view, int position) {
//
//            }
//        }));
//    }
//
//    private void setAdapters() {
//
//    }
//
//    @Override
//    public void onStart() {
//        super.onStart();
//        module.enqueuePeriodicRequest(HistoryWorker.class);
//        fetchingCategory();
//    }
//
//    //fetching category from firebase
//    private void fetchingCategory() {
//        loadingBar.show();
//        mList = new ArrayList<>();
//        allRewList.clear();
//        Query query = demoRef.child("products").orderByChild("p_status").equalTo("0");
//        query.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                mList.clear();
//                for (DataSnapshot snap : dataSnapshot.getChildren()) {
//                    AddProductModel model = new AddProductModel();
//                    model = snap.getValue(AddProductModel.class);
//                    allRewList.add(model);
//                    mList.add(model);
//                }
//                for (int i = 0; i < mList.size(); i++) {
//                    String q_id = mList.get(i).getP_id();
//                    String j_d = q_id.substring(7, 12);
//                    String dtstr = j_d.substring(0, 2) + "-" + j_d.subSequence(2, 4) + "-" + j_d.subSequence(4, j_d.length());
//                    int days = module.getDateDiff(dtstr.toString());
//                    mList.get(i).setDays(String.valueOf(days));
//                    Log.e("REWARD_FRAG", "" + mList.size() + " - " + mList.get(i).getP_status());
//                }
//
//                Collections.sort(mList, comp_pro);
//                // loadingBar.dismiss();
//
//                mAdapter = new ProductRewardAdapter(getActivity(), mList);
//                rv_rew.setAdapter(mAdapter);
//                mAdapter.notifyDataSetChanged();
//                loadingBar.dismiss();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Toast.makeText(getActivity(), "Please check your internet connection...", Toast.LENGTH_SHORT).show();
//                loadingBar.dismiss();
//            }
//        });
//    }

    public void loadFragment(Fragment fm, Bundle bundle) {

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fm.setArguments(bundle);
        fragmentManager.beginTransaction()
                .replace(R.id.frame, fm)
                .addToBackStack(null)
                .commit();
    }


}