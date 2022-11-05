package com.upsun.quizz.admin.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.upsun.quizz.Adapter.AllProductAdapter;
import com.upsun.quizz.Adapter.AllQuizCategoryAdapter;
import com.upsun.quizz.Config.Module;
import com.upsun.quizz.Model.AddProductModel;
import com.upsun.quizz.Model.AddQuizCategoryModel;
import com.upsun.quizz.R;

import static com.upsun.quizz.Model.AddProductModel.comp_pro;
import static com.upsun.quizz.Model.AddQuizCategoryModel.comp_cat;


public class AllProductActivity extends AppCompatActivity {
    private final String TAG = AllProductActivity.class.getSimpleName();
    private List<AddProductModel> mList;
    private DatabaseReference demoRef;
    private ProgressDialog loadingBar;
    Button btn_add_product;
    private final Activity ctx = AllProductActivity.this;
    AllProductAdapter adapter;
    RecyclerView rv_product;
    TextView no_of_product;
    Module module;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_product);

        init();
        setAllProductAdapter();

    }

    private void init(){
        demoRef = FirebaseDatabase.getInstance().getReference().child("products");
        loadingBar=new ProgressDialog(ctx);
        loadingBar.setMessage("Loading...");
        loadingBar.setCanceledOnTouchOutside(false);
        btn_add_product=findViewById(R.id.btn_add_product);
        no_of_product=findViewById(R.id.no_of_product);
        rv_product=findViewById(R.id.rv_product);
        rv_product.setLayoutManager(new LinearLayoutManager(this));
        rv_product.setNestedScrollingEnabled(false);
        module=new Module(ctx);
        btn_add_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(AllProductActivity.this, AddProductActivity.class);
                intent.putExtra("is_edit","false");
                startActivity(intent);
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        setAllProductAdapter();
    }

    //fetching categories
    private void setAllProductAdapter() {

        mList = new ArrayList<>();
        loadingBar.show();
        demoRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mList.clear();
                for(DataSnapshot dataSnap : dataSnapshot.getChildren() ){
                    AddProductModel productModel = new AddProductModel();
                    productModel  = dataSnap.getValue(AddProductModel.class);
                    mList.add(productModel);
                }
                no_of_product.setText("No. of Products : "+mList.size());
                //parent04092020011149
                for(int i=0; i<mList.size();i++)
                {
                    String q_id = mList.get(i).getP_id();
                    String j_d = q_id.substring(7,12);
                    String dtstr=j_d.substring(0,2)+"-"+j_d.subSequence(2,4)+"-"+j_d.subSequence(4,j_d.length());
                    int days= module.getDateDiff(dtstr.toString());
                    mList.get(i).setDays(String.valueOf(days));
                    Log.e("asdas",""+mList.size()+" - "+mList.get(i).getP_status());
                }

                Collections.sort(mList,comp_pro);
                adapter = new AllProductAdapter(AllProductActivity.this,mList);
                rv_product.setAdapter(adapter);
                loadingBar.dismiss();
                adapter.notifyDataSetChanged();


            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                loadingBar.dismiss();
                Log.e(TAG,""+databaseError);
            }
        });
    }

    //function to update Adapter
    public void updateCount(int size){

        no_of_product.setText("No. of Categories : "+size);


    }


}
