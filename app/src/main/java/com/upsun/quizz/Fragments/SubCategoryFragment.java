package com.upsun.quizz.Fragments;

import static com.upsun.quizz.Model.AddQuizCategoryModel.comp_cat;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.upsun.quizz.Adapter.QuizCategoryAdapter;
import com.upsun.quizz.Config.Module;
import com.upsun.quizz.Model.AddQuizCategoryModel;
import com.upsun.quizz.R;
import com.upsun.quizz.utils.BannerAds;
import com.upsun.quizz.utils.RecyclerTouchListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class SubCategoryFragment extends Fragment {

    private final String TAG = SubCategoryFragment.class.getSimpleName();
    //    private FragmentCategoryBinding mFragBindings;
    private List<AddQuizCategoryModel> mList;
    private QuizCategoryAdapter mAdapter;
    private DatabaseReference demoRef;
    private ProgressDialog loadingBar;
    Module module;
    RelativeLayout adView;
    RecyclerView rv_cat;
    String cat_id="";

    public SubCategoryFragment() {
        // Required empty public constructor
    }
 @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_category, container, false);
        init(view);
        return view;
    }
    private void init(View v){
        loadingBar  = new ProgressDialog(getActivity());
        demoRef = FirebaseDatabase.getInstance().getReference();
        loadingBar.setMessage("Loading...");
        rv_cat=v.findViewById(R.id.rv_cat);
        adView=v.findViewById(R.id.adView);
        adView.setVisibility(View.VISIBLE);
        module=new Module(getActivity());
        rv_cat.setLayoutManager(new GridLayoutManager(getActivity(),2));
        rv_cat.setNestedScrollingEnabled(false);
        cat_id=getArguments().getString("cat_id");
        fetchingCategory(cat_id);

        rv_cat.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), rv_cat, new RecyclerTouchListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Fragment fm=null;
                Bundle bundle=new Bundle();
                bundle.putString("cat_id",mList.get(position).getP_id());

                    fm=new HomeFragment();
                   loadFragment(fm,bundle);
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));
    }

    @Override
    public void onStart() {
        super.onStart();

        fetchingCategory(cat_id);
       new BannerAds().showBannerAds(getActivity(),adView);

    }


    //fetching category from firebase
    private void fetchingCategory(String cat_id){

        //loadingBar.show();
        mList = new ArrayList<>();
        Query query=demoRef.child("parents").orderByChild("parent").equalTo(cat_id);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mList.clear();
                for (DataSnapshot snap: dataSnapshot.getChildren()) {
                    AddQuizCategoryModel model =new  AddQuizCategoryModel();
                    model = snap.getValue(AddQuizCategoryModel.class);
                    if(model.getP_status().equals("0")){
                        mList.add(model);
                    }

                }
                for(int i=0; i<mList.size();i++)
                {
                    String q_id = mList.get(i).getP_id();
                    String j_d = q_id.substring(7,12);
                    String dtstr=j_d.substring(0,2)+"-"+j_d.subSequence(2,4)+"-"+j_d.subSequence(4,j_d.length());
                    int days= module.getDateDiff(dtstr.toString());
                    mList.get(i).setDays(String.valueOf(days));
                    Log.e("asdas",""+mList.size()+" - "+mList.get(i).getP_status());
                }

                Collections.sort(mList,comp_cat);
               // loadingBar.dismiss();

                mAdapter = new QuizCategoryAdapter(getActivity(), mList);
                rv_cat.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Please check your internet connection...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void loadFragment(Fragment fm ,Bundle bundle){

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fm.setArguments(bundle);
        fragmentManager.beginTransaction()
                .replace( R.id.frame,fm )
                .addToBackStack(null)
                .commit();
    }


}