package com.upsun.quizz.admin.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.upsun.quizz.ProductDetailActivity;
import com.upsun.quizz.R;

import java.util.ArrayList;

public class Product_requests extends AppCompatActivity {

    public Product_requests() {
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_requests);


        ArrayList<orderModel1> models;
        models = new ArrayList<>();

        historyAdapter1 adapter = new historyAdapter1(this, models);
        RecyclerView rec = findViewById(R.id.detailsRec);
        rec.setLayoutManager(new LinearLayoutManager(this));
        rec.setAdapter(adapter);


        FirebaseDatabase.getInstance().getReference()
                .child("product_history").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            findViewById(R.id.historyTv).setVisibility(View.GONE);
                            models.clear();
                            for (DataSnapshot data : snapshot.getChildren()){


                                    orderModel1 model = data.getValue(orderModel1.class);
                                    model.setRef(data.getRef());
                                    models.add(model);


                                adapter.notifyDataSetChanged();

                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });




    }}

class orderModel1{
    String claimDate;
    String claimerAddress;
    String claimerName;
    String claimerNumber;
    String itemDesc;
    String itemImageUrl;
    String itemName;
    String itemValue;
    DatabaseReference ref;

    public DatabaseReference getRef() {
        return ref;
    }

    public void setRef(DatabaseReference ref) {
        this.ref = ref;
    }

    public orderModel1() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    String status;

    public String getClaimDate() {
        return claimDate;
    }

    public void setClaimDate(String claimDate) {
        this.claimDate = claimDate;
    }

    public String getClaimerAddress() {
        return claimerAddress;
    }

    public void setClaimerAddress(String claimerAddress) {
        this.claimerAddress = claimerAddress;
    }

    public String getClaimerName() {
        return claimerName;
    }

    public void setClaimerName(String claimerName) {
        this.claimerName = claimerName;
    }

    public String getClaimerNumber() {
        return claimerNumber;
    }

    public void setClaimerNumber(String claimerNumber) {
        this.claimerNumber = claimerNumber;
    }

    public String getItemDesc() {
        return itemDesc;
    }

    public void setItemDesc(String itemDesc) {
        this.itemDesc = itemDesc;
    }

    public String getItemImageUrl() {
        return itemImageUrl;
    }

    public void setItemImageUrl(String itemImageUrl) {
        this.itemImageUrl = itemImageUrl;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemValue() {
        return itemValue;
    }

    public void setItemValue(String itemValue) {
        this.itemValue = itemValue;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    String userId;
}
    class historyAdapter1 extends RecyclerView.Adapter{
        Context context;
        ArrayList<orderModel1> models;

        public historyAdapter1(Context context, ArrayList<orderModel1> models) {
            this.context = context;
            this.models = models;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new viewHolder1(LayoutInflater.from(context).inflate(R.layout.admin_products_history_item, parent, false));

        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {


            ((viewHolder1)holder).productName.setText(models.get(position).getItemName());
            ((viewHolder1)holder).productPrice.setText(models.get(position).getItemValue());
            ((viewHolder1)holder).productStatus.setText(models.get(position).getStatus());

            Glide.with(context).load(models.get(position).getItemImageUrl()).into(((viewHolder1)holder).productImage);


            ((viewHolder1)holder).productNumber.setText(models.get(position).getClaimerNumber());
            ((viewHolder1)holder).productAddress.setText(models.get(position).getClaimerAddress());


            ((viewHolder1)holder).itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ProductDetailActivity.class);
                    intent.putExtra("tv_pro_title",models.get(position).getItemName());
                    intent.putExtra("tv_pro_rew", models.get(position).getItemValue());
                    intent.putExtra("tv_pro_detail", models.get(position).getItemDesc());
                    intent.putExtra("iv_pro_img", models.get(position).getItemImageUrl());
                    intent.putExtra("iv_pro_username", models.get(position).getClaimerName());
                    intent.putExtra("iv_pro_usernumber", models.get(position).getClaimerNumber());
                    intent.putExtra("iv_pro_useraddress", models.get(position).getClaimerAddress());
                    intent.putExtra("status", models.get(position).getStatus());
                    intent.putExtra("comingFromHistory", 1);




                    context.startActivity(intent);
                }
            });

            ((viewHolder1)holder).saveStatus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (! ((viewHolder1)holder).productStatus.getText().toString().equals("")  ){
                        models.get(position).getRef().child("status").setValue(((viewHolder1)holder).productStatus.getText().toString());
                        Toast.makeText(context, "Status Updated", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }

        @Override
        public int getItemCount() {
            return models.size();
        }
    }
    class viewHolder1 extends RecyclerView.ViewHolder{
        TextView productName, productPrice,productNumber,productAddress;
        EditText productStatus;
        ImageView productImage,saveStatus;
        public viewHolder1(@NonNull View itemView) {
            super(itemView);

            productName = itemView.findViewById(R.id.orderName);
            productPrice = itemView.findViewById(R.id.orderPrice);
            productStatus = itemView.findViewById(R.id.orderStatus);
            productImage = itemView.findViewById(R.id.orderImage);
            saveStatus = itemView.findViewById(R.id.statusSaveBtn);
            productNumber = itemView.findViewById(R.id.orderNumber);
            productAddress = itemView.findViewById(R.id.orderAddress);

        }
}