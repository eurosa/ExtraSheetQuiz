package com.upsun.quizz.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.upsun.quizz.Model.AddProductModel;
import com.upsun.quizz.R;

import java.util.List;

public class ProductRewardAdapter extends RecyclerView.Adapter<ProductRewardAdapter.ViewHolder> {
    private final Context mContext;
    private final List<AddProductModel> mList;

    public ProductRewardAdapter(Context mContext, List<AddProductModel> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.single_product_reward_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AddProductModel model = mList.get(position);

        if(model.getP_img().isEmpty() || model.getP_img()==null){

        }else{

            Glide.with(mContext)
                    .load(model.getP_img())
                    .fitCenter()
                    .placeholder( R.drawable.logo)
                    .diskCacheStrategy( DiskCacheStrategy.ALL)
                    .dontAnimate()
                    .into(holder.rewardImageIv);

        }
//
        holder.rewardNameTv.setText(model.getP_name());
        holder.rewardTv.setText(model.getP_reward());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView rewardImageIv;
        TextView rewardNameTv,rewardTv;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            rewardImageIv = itemView.findViewById(R.id.singleItemImage);
            rewardNameTv = itemView.findViewById(R.id.singleItemName);
            rewardTv = itemView.findViewById(R.id.singleItemPrice);

        }
    }
}
