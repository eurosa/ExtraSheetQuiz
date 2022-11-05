package com.upsun.quizz.Fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.upsun.quizz.ProductDetailActivity;
import com.upsun.quizz.R;
import com.upsun.quizz.utils.SessionManagment;

import java.util.ArrayList;

public class HistoryFragment extends Fragment {
    public HistoryFragment() {
        // Required empty public constructor
    }

    String myId = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        ArrayList<orderModel> models;
        models = new ArrayList<>();
//        myId = "+91"+new SessionManagment(getContext()).getNumber();
        myId = new SessionManagment(getActivity()).getId();
        historyAdapter adapter = new historyAdapter(getContext(), models);
        RecyclerView rec = view.findViewById(R.id.detailsRec);
        rec.setLayoutManager(new LinearLayoutManager(getContext()));
        rec.setAdapter(adapter);

        //Toast.makeText(getContext(), ""+myId, Toast.LENGTH_SHORT).show();

        FirebaseDatabase.getInstance().getReference()
                .child("product_history").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            view.findViewById(R.id.historyTv).setVisibility(View.GONE);
                            models.clear();
                            for (DataSnapshot data : snapshot.getChildren()) {

                                if (data.child("userId").getValue(String.class).equals(myId)) {

                                    orderModel model = data.getValue(orderModel.class);
                                    models.add(model);

                                }
                                adapter.notifyDataSetChanged();

                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        return view;
    }
}

class orderModel {
    String claimDate;
    String claimerAddress;
    String claimerName;
    String claimerNumber;
    String itemDesc;
    String itemImageUrl;
    String itemName;
    String itemValue;

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

class historyAdapter extends RecyclerView.Adapter {
    Context context;
    ArrayList<orderModel> models;

    public historyAdapter(Context context, ArrayList<orderModel> models) {
        this.context = context;
        this.models = models;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new viewHolder(LayoutInflater.from(context).inflate(R.layout.claimed_history_item, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {


        ((viewHolder) holder).productName.setText(models.get(position).getItemName());
        ((viewHolder) holder).productPrice.setText(models.get(position).getItemValue());
        ((viewHolder) holder).productStatus.setText(models.get(position).getStatus());

        Glide.with(context).load(models.get(position).getItemImageUrl()).into(((viewHolder) holder).productImage);


        ((viewHolder) holder).itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ProductDetailActivity.class);
                intent.putExtra("tv_pro_title", models.get(position).getItemName());
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

    }

    @Override
    public int getItemCount() {
        return models.size();
    }
}

class viewHolder extends RecyclerView.ViewHolder {
    TextView productName, productPrice, productStatus;
    ImageView productImage;

    public viewHolder(@NonNull View itemView) {
        super(itemView);

        productName = itemView.findViewById(R.id.orderName);
        productPrice = itemView.findViewById(R.id.orderPrice);
        productStatus = itemView.findViewById(R.id.orderStatus);
        productImage = itemView.findViewById(R.id.orderImage);

    }
}
