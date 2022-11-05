package com.upsun.quizz.admin.fragments;

import static com.upsun.quizz.Config.Constants.CATEGORY_REF;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.upsun.quizz.Adapter.CategoriesAdapter;
import com.upsun.quizz.Config.Module;
import com.upsun.quizz.Model.CategoriesModel;
import com.upsun.quizz.R;
import com.upsun.quizz.networkconnectivity.NetworkConnection;
import com.upsun.quizz.utils.ToastMsg;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class AllCategoriesFragment extends Fragment implements View.OnClickListener{

    RelativeLayout rel_no_items;
    ImageView iv_back;
    Button btn_add_cat;
    Dialog dialog;
    public static TextView no_of_cat;
    RecyclerView rv_cat;
    ProgressDialog loadingBar;
    CategoriesAdapter categoriesAdapter;
    ToastMsg toastMsg;
    Module module;
    ArrayList<CategoriesModel> cat_list;
    DatabaseReference cat_ref;
    SwipeRefreshLayout rf_cat;

    public AllCategoriesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_all_categories, container, false);
        initViews(view);
        if(NetworkConnection.connectionChecking(getActivity()))
        {
         getAllCategories();
        }
        else
        {
            module.noConnectionActivity();
        }
        rf_cat.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getAllCategories();
                rf_cat.setRefreshing(false);
            }
        });
        return view;
    }

    private void initViews(View view) {
        iv_back=(ImageView)view.findViewById(R.id.iv_back);
        no_of_cat=(TextView) view.findViewById(R.id.no_of_cat);
        rv_cat=(RecyclerView)view.findViewById(R.id.rv_cat);
        btn_add_cat=(Button)view.findViewById(R.id.btn_add_cat);
        rf_cat=(SwipeRefreshLayout)view.findViewById(R.id.rf_cat);
        loadingBar=new ProgressDialog(getActivity());
        loadingBar.setMessage("Loading...");
        loadingBar.setCanceledOnTouchOutside(false);
        toastMsg=new ToastMsg(getActivity());
        module=new Module(getActivity());
        cat_list=new ArrayList<>();
        rel_no_items=(RelativeLayout)view.findViewById(R.id.rel_no_items);
        cat_ref= FirebaseDatabase.getInstance().getReference().child(CATEGORY_REF);
        iv_back.setOnClickListener(this);
        btn_add_cat.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id=v.getId();
        if(id == R.id.iv_back)
        {
        getActivity().getSupportFragmentManager().popBackStackImmediate();
        }
        else if(id == R.id.btn_add_cat)
        {
          createAddCategory();
        }
    }

    private void createAddCategory() {
        dialog=new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_add_cat_layout);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        final EditText et_cat=(EditText)dialog.findViewById(R.id.et_cat);
        Button btn_add=(Button) dialog.findViewById(R.id.btn_add);
        Button btn_cancel=(Button) dialog.findViewById(R.id.btn_cancel);

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              if(et_cat.getText().toString().isEmpty())
                  {
                      et_cat.setError(getActivity().getResources().getString(R.string.req_cat_name));
                      et_cat.requestFocus();
                  }
                  else
                  {
                      String id=module.getUniqueId("cat");
                      addNewCategory(id,et_cat.getText().toString());
                  }
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });

    }

    private void addNewCategory(final String id, final String name) {
      loadingBar.show();
        HashMap<String,Object> map=new HashMap<>();
        map.put("cat_id",id);
        map.put("cat_name",name);
        cat_ref.child(id).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
             if(task.isSuccessful())
             {
                 loadingBar.dismiss();
                 dialog.dismiss();
                 if(cat_list.size()<=0)
                 {
                     cat_list.add(new CategoriesModel(id,name));
                     getAllCategories();
                 }
                 else
                 {
                     cat_list.add(new CategoriesModel(id,name));
                     no_of_cat.setText("No. Of Categories : "+cat_list.size());
                     categoriesAdapter.notifyDataSetChanged();
                 }
                 toastMsg.toastIconSuccess("New Category Added..");
             }
             else
             {
                 loadingBar.dismiss();
                 dialog.dismiss();
                 toastMsg.toastIconError(""+task.getException().getMessage());
             }
            }
        });
    }

    private void getAllCategories() {
        loadingBar.show();
        cat_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                loadingBar.dismiss();
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0)
                {
                    rv_cat.setVisibility(View.VISIBLE);
                    rel_no_items.setVisibility(View.GONE);
                    cat_list.clear();
                    for(DataSnapshot snapshot:dataSnapshot.getChildren())
                    {
                        CategoriesModel model=snapshot.getValue(CategoriesModel.class);
                        cat_list.add(model);
                    }
                    no_of_cat.setText("No. Of Categories : "+cat_list.size());
                    categoriesAdapter=new CategoriesAdapter(getActivity(),cat_list);
                    rv_cat.setLayoutManager(new LinearLayoutManager(getActivity()));
                    rv_cat.setAdapter(categoriesAdapter);
                    categoriesAdapter.notifyDataSetChanged();
                }
                else
                {
                    rv_cat.setVisibility(View.GONE);
                    rel_no_items.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
//
//        cat_ref.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                loadingBar.dismiss();
//                 if(dataSnapshot.exists() || dataSnapshot.getChildrenCount()<=0)
//                 {
//                     rv_cat.setVisibility(View.VISIBLE);
//                     rel_no_items.setVisibility(View.GONE);
//                     cat_list.clear();
//                     for(DataSnapshot snapshot:dataSnapshot.getChildren())
//                     {
//                         CategoriesModel model=snapshot.getValue(CategoriesModel.class);
//                         cat_list.add(model);
//                     }
//                     categoriesAdapter=new CategoriesAdapter(getActivity(),cat_list);
//                     rv_cat.setLayoutManager(new LinearLayoutManager(getActivity()));
//                     rv_cat.setAdapter(categoriesAdapter);
//                     categoriesAdapter.notifyDataSetChanged();
//                 }
//                 else
//                 {
//                     rv_cat.setVisibility(View.GONE);
//                     rel_no_items.setVisibility(View.VISIBLE);
//                 }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
    }
}
