package com.upsun.quizz.Fragments;

import static com.upsun.quizz.Config.Constants.CONFIG_REF;
import static com.upsun.quizz.Config.Constants.KEY_ID;
import static com.upsun.quizz.Config.Constants.PREF_NAME;
import static com.upsun.quizz.Config.Constants.USER_REF;
import static com.upsun.quizz.Model.AddQuizCategoryModel.comp_cat;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.upsun.quizz.Adapter.QuizCategoryAdapter;
import com.upsun.quizz.Config.Module;
import com.upsun.quizz.LoginActivty;
import com.upsun.quizz.MainActivity;
import com.upsun.quizz.Model.AddQuizCategoryModel;
import com.upsun.quizz.Model.UserModel;
import com.upsun.quizz.R;
import com.upsun.quizz.Worker.HistoryWorker;
import com.upsun.quizz.utils.RecyclerTouchListener;
import com.upsun.quizz.utils.SessionManagment;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Random;
//import in.binplus.extrasheetquiz.databinding.FragmentCategoryBinding;

public class CategoryFragment extends Fragment {
    private final String TAG = "Category_FRAGMENT";
    //    private FragmentCategoryBinding mFragBindings;
    private List<AddQuizCategoryModel> mList, allCatList;
    private QuizCategoryAdapter mAdapter;
    DatabaseReference quizRef, userRef, app_ref;
    SessionManagment sessionManagment;
    private DatabaseReference demoRef;
    private ProgressDialog loadingBar;
    Module module;
    int version_code = 0;
    String app_link = "";
    ArrayList<UserModel> uList;
    RecyclerView rv_cat;
    Context context;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_category, container, false);
        init(view);
//        module.deleteNode("questions");


        return view;
    }


    private void init(View v) {
        loadingBar = new ProgressDialog(context);
        demoRef = FirebaseDatabase.getInstance().getReference();
        loadingBar.setMessage("Loading...");
        rv_cat = v.findViewById(R.id.rv_cat);
        module = new Module(context);
        sessionManagment = new SessionManagment(context);
        uList = new ArrayList<>();
        app_ref = FirebaseDatabase.getInstance().getReference().child(CONFIG_REF);
        userRef = FirebaseDatabase.getInstance().getReference().child(USER_REF);
        rv_cat.setLayoutManager(new GridLayoutManager(context, 2));
        rv_cat.setNestedScrollingEnabled(false);
        allCatList = new ArrayList<>();
        Log.e(TAG, "init: " + module.getCurrentDate() + " TIme : " + module.getCurrentTime())
        ;
        v.setFocusableInTouchMode(true);
        v.requestFocus();
        v.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Confirmation");
                    builder.setMessage("Are you sure want to exit?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ((Activity) context).finishAffinity();
                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    return true;
                }
                return false;
            }
        });
//        fetchingCategory();
//        checkAppUpdates();
        checkReferCode();
        getUserData(sessionManagment.getUserDetails().get(KEY_ID).toString());
        fetchingCategory();

        rv_cat.addOnItemTouchListener(new RecyclerTouchListener(context, rv_cat, new RecyclerTouchListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Fragment fm = null;
                Bundle bundle = new Bundle();
//                module.showToast(""+mList.get(position).getParent().toString());
                bundle.putString("cat_id", mList.get(position).getP_id());
                if (getSubCategory(allCatList, mList.get(position).getP_id().toString())) {
                    fm = new SubCategoryFragment();
                } else {
                    fm = new HomeFragment();
                }
                loadFragment(fm, bundle);
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));
    }

    @Override
    public void onStart() {
        super.onStart();
        module.enqueuePeriodicRequest(HistoryWorker.class);
        fetchingCategory();
    }

    //fetching category from firebase
    private void fetchingCategory() {
        // loadingBar.show();
        mList = new ArrayList<>();
        allCatList.clear();
        Query query = demoRef.child("parents").orderByChild("p_status").equalTo("0");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mList.clear();
                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    AddQuizCategoryModel model = new AddQuizCategoryModel();
                    model = snap.getValue(AddQuizCategoryModel.class);
                    allCatList.add(model);
                    if (model.getParent().equals("0")) {
                        mList.add(model);
                    }


                }
                for (int i = 0; i < mList.size(); i++) {
                    String q_id = mList.get(i).getP_id();
                    String j_d = q_id.substring(7, 12);
                    String dtstr = j_d.substring(0, 2) + "-" + j_d.subSequence(2, 4) + "-" + j_d.subSequence(4, j_d.length());
                    int days = module.getDateDiff(dtstr.toString());
                    mList.get(i).setDays(String.valueOf(days));
                    Log.e("asdas", "" + mList.size() + " - " + mList.get(i).getP_status());
                }

                Collections.sort(mList, comp_cat);
                // loadingBar.dismiss();

                mAdapter = new QuizCategoryAdapter(context, mList);
                rv_cat.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context, "Please check your internet connection...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void loadFragment(Fragment fm, Bundle bundle) {

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fm.setArguments(bundle);
        fragmentManager.beginTransaction()
                .replace(R.id.frame, fm)
                .addToBackStack(null)
                .commit();
    }

    public boolean getSubCategory(List<AddQuizCategoryModel> list, String pnt) {
        boolean flag = false;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getParent().equalsIgnoreCase(pnt)) {
                flag = true;
                break;
            }
        }
        return flag;
    }

    public void getUserData(String user_id) {
        Query query = userRef.orderByChild("id").equalTo(user_id);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                uList.clear();
                try {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot snap : dataSnapshot.getChildren()) {
                            UserModel model = snap.getValue(UserModel.class);
                            uList.add(model);
                        }
                        if (uList.size() > 0) {
                            if (uList.get(0).getStatus().toString().equalsIgnoreCase("1")) {
                                module.showToast("Your account deactivated. Please Contact to admin");
                            } else {
                                ((MainActivity) context).setWalletCounter(uList.get(0).getWallet().toString());
                                sessionManagment.updateWallet(uList.get(0).getWallet().toString());
                                ((MainActivity) context).setRewardsCounter(uList.get(0).getRewards().toString());
                                sessionManagment.updateRewards(uList.get(0).getRewards().toString());
                                sessionManagment.updateQuizStatus(uList.get(0).getQuiz_status());


                            }
                        } else {
                            module.showToast("Something Went Wrong. Try again later");
                        }
                    }

                } catch (Exception ex) {
                    ex.printStackTrace();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
//    private void checkAppUpdates() {
//        //loadingBar.show();
//        uList.clear();
//        app_ref.child("app_updater").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                //loadingBar.dismiss();
//                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0)
//                {
//
//
//
//                    UpdateModel model=dataSnapshot.getValue(UpdateModel.class);
//                    version_code=Integer.parseInt(model.getApp_version());
//                    app_link=model.getApp_link();
//                    REDUCTION_WALLET_AMOUNT=Integer.parseInt(model.getWallet_amt().toString());
//                    REDUCTION_REWARDS_AMOUNT=Integer.parseInt(model.getRewards_amt().toString());
//                    WITHDRAW_LIMIT_AMOUNT=Integer.parseInt(model.getWithdraw_limit().toString());
//                    if(getUpdaterInfo())
//                    {
//                        checkReferCode();
//                        getUserData(sessionManagment.getUserDetails().get(KEY_ID).toString());
//                        fetchingCategory();
//                          }
//                    else
//                    {
//                        AlertDialog.Builder builder=new AlertDialog.Builder(context);
//                        builder.setCancelable(false);
//                        builder.setMessage("The new version of app is available please update to get access.");
//                        builder.setPositiveButton("Update now", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                dialogInterface.dismiss();
//                                String url = app_link;
//                                Intent in = new Intent(Intent.ACTION_VIEW);
//                                in.setData(Uri.parse(url));
//                                context.startActivity(in);
//                                context.finish();
//                            }
//                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//
//                                dialogInterface.dismiss();
//                                context.finishAffinity();
//                            }
//                        });
//                        AlertDialog dialog=builder.create();
//                        dialog.show();
//
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//               // loadingBar.dismiss();
//                module.showToast("Something Went Wrong");
//            }
//        });
//
//    }

    public boolean getUpdaterInfo() {
        boolean st = false;
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            int ver_code = packageInfo.versionCode;
            if (ver_code == version_code) {
                st = true;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return st;
    }

    void checkReferCode() {
        FirebaseDatabase.getInstance().getReference().child("users")
//                .child("+91"+sessionManagment.getNumber())
                .child(sessionManagment.getId())
                .child("referCode").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            //Toast.makeText(getContext(), "exists", Toast.LENGTH_SHORT).show();
                        } else {
                            SaveReferCode();
                            sessionManagment.logoutSession();
                            Intent intent = new Intent(context, LoginActivty.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


    }

    void SaveReferCode() {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, 0);
//        DatabaseReference newDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users").child("+91"+new SessionManagment(getContext()).getNumber());
        DatabaseReference newDatabaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(new SessionManagment(getContext()).getId());

        newDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("referCode").exists()) {

                } else {
                    String referCode = createRandomCode(6);
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
                        new DynamicLink.AndroidParameters.Builder(context.getPackageName())
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


                                ref.child("referLink").setValue(shortLink.toString());
                                ref.child("referCode").setValue(referCode);
                                new SessionManagment(getContext()).setReferCode(referCode);
                                new SessionManagment(getContext()).setReferLink(shortLink.toString());

                                //Toast.makeText(ctx, "saved", Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }


                        }
                    }
                }).addOnFailureListener(e -> Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show());


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