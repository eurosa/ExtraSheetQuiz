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
import com.upsun.quizz.Model.AddQuizCategoryModel;
import com.upsun.quizz.R;

import java.util.List;

public class QuizCategoryAdapter extends RecyclerView.Adapter<QuizCategoryAdapter.ViewHolder> {
    private Context mContext;
    private List<AddQuizCategoryModel> mList;

    public QuizCategoryAdapter(Context mContext, List<AddQuizCategoryModel> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.single_quiz_categroy_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AddQuizCategoryModel model = mList.get(position);

        if(model.getP_img().isEmpty() || model.getP_img()==null){

        }else{

            Glide.with(mContext)
                    .load(model.getP_img())
                    .fitCenter()
                    .placeholder( R.drawable.logo)
                    .diskCacheStrategy( DiskCacheStrategy.ALL)
                    .dontAnimate()
                    .into(holder.categoryImageIv);

        }
//
        holder.categoryNameTv.setText(model.getP_name());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView categoryImageIv;
        TextView categoryNameTv;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryImageIv = itemView.findViewById(R.id.categoryImageIv);
            categoryNameTv = itemView.findViewById(R.id.categoryNameTv);

        }
    }
}
