package com.upsun.quizz.Adapter;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.upsun.quizz.Model.AddProductModel;
import com.upsun.quizz.R;
import com.upsun.quizz.admin.activities.AddProductActivity;
import com.upsun.quizz.admin.activities.AllProductActivity;
import com.upsun.quizz.utils.ToastMsg;

import java.util.List;


public class AllProductAdapter extends RecyclerView.Adapter<AllProductAdapter.ViewHolder> {

    private final Context mContext;
    private final List<AddProductModel> mList;
    private ProgressDialog loadingBar;
    private ToastMsg toastMsg;

    public AllProductAdapter(Context mContext, List<AddProductModel> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.single_all_product_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final AddProductModel model = mList.get(position);
        if(model.getP_img().isEmpty() || model.getP_img()==null){

        }else{
            Glide.with(mContext)
                    .load(model.getP_img())
                    .into(holder.iv_product_image);
        }
//

        holder.tv_product_name.setText("Product Name : "+model.getP_name());
        holder.tv_product_reward.setText("Product Reward : "+model.getP_reward());
//        holder.tv_cate_id.setText("Quiz Category Id : "+model.getQuizCategoryId());
        String st="";
        if(model.getP_status().toString().equals("0")){
            st="Active";
            holder.tv_product_status.setTextColor(mContext.getResources().getColor(R.color.green_500));
        }else{
            st="Deactive";
            holder.tv_product_status.setTextColor(mContext.getResources().getColor(R.color.red_600));
        }
        holder.tv_product_status.setText("Status : "+st);

        //click actions
        holder.lin_disable.setOnClickListener(new View.OnClickListener() {
                                                  @Override
                                                  public void onClick(View v) {
                                                      disableProductDialog(model.getP_id(),position);
                                                  }
                                              }
        );

        holder.lin_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createRemoveDialog(position, model.getP_id(),model.getP_img());

            }
        });
        //edit of quiz category
        holder.lin_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, AddProductActivity.class);
                intent.putExtra("is_edit","true");
                intent.putExtra("p_id",model.getP_id());
                intent.putExtra("p_name",model.getP_name());
                intent.putExtra("p_img",model.getP_img());
                intent.putExtra("p_status",model.getP_status());
                intent.putExtra("p_reward",model.getP_reward());
                intent.putExtra("p_detail",model.getP_detail());
                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView tv_product_name,tv_product_reward, tv_product_id, tv_product_status;
        LinearLayout lin_edit,lin_delete,lin_disable;
        ImageView iv_product_image;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            //textViews
            tv_product_name = itemView.findViewById(R.id.tv_product_name);
            tv_product_reward = itemView.findViewById(R.id.tv_product_reward);
            tv_product_id = itemView.findViewById(R.id.tv_product_id);
            tv_product_status =  itemView.findViewById(R.id.tv_product_status);
            //imageView
            iv_product_image = itemView.findViewById(R.id.iv_product_image);
            //layouts
            lin_edit= itemView.findViewById(R.id.lin_edit);
            lin_disable= itemView.findViewById(R.id.lin_disable);
            lin_delete= itemView.findViewById(R.id.lin_delete);
            toastMsg=new ToastMsg(mContext);
            loadingBar=new ProgressDialog(mContext);
            loadingBar.setMessage("Loading...");


        }
    }

    //function to disable quiz Category
    private void disableProductDialog(final String quizCateId, final int pos){
        final DatabaseReference demoRef = FirebaseDatabase.getInstance().getReference().child("parents");
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage("Are you sure you want to Enable or Disable ?");
        builder.setPositiveButton("Enable", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        loadingBar.show();
                        demoRef.child(quizCateId).child("p_status").setValue("0").addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                loadingBar.dismiss();
                                if(task.isSuccessful()){

                                    mList.get(pos).setP_status("0");
                                    notifyDataSetChanged();
//                        ((AllQuizCategoryActivity)mContext).updateAdapter();
                                    toastMsg.toastIconSuccess("Product Enabled Successfully.");

                                }else{
                                    toastMsg.toastIconError("Something went wrong please try again");
                                }
                            }
                        });


                    }
                })
                .setNegativeButton("Disable", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {
                        loadingBar.show();
                        demoRef.child(quizCateId).child("p_status").setValue("1").addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                loadingBar.dismiss();
                                if(task.isSuccessful()){
                                    mList.get(pos).setP_status("1");
                                    notifyDataSetChanged();
                                    //  notifyDataSetChanged();
//                                ((AllQuizCategoryActivity)mContext).updateAdapter();
                                    toastMsg.toastIconSuccess("Product Disabled Successfully.");
                                    dialog.dismiss();
                                }else{
                                    toastMsg.toastIconError("Something went wrong please try again");
                                }
                            }
                        });

                    }
                });

        builder.create().show();
    }



    //function to remove specific quiz Category
    private void createRemoveDialog(final int pos, final String pId,final String image) {

        final DatabaseReference pro_ref= FirebaseDatabase.getInstance().getReference().child("products");


        AlertDialog.Builder builder=new AlertDialog.Builder(mContext);
        builder.setMessage(mContext.getResources().getString(R.string.txt_delete_item));
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {
                        loadingBar.show();
                        pro_ref.child(pId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                loadingBar.dismiss();
                                if(task.isSuccessful())
                                {
                                    mList.remove(pos);
                                    ((AllProductActivity)mContext).updateCount(mList.size());
                                    deleteFromFirebase(image);
                                    toastMsg.toastIconSuccess("Product Remove Successfully.");
                                    notifyDataSetChanged();
                                    dialog.dismiss();

                                }
                                else {
                                    toastMsg.toastIconError(""+task.getException().getMessage());
                                }
                            }
                        });

                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }

    private void deleteFromFirebase(String image){
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageReference = firebaseStorage.getReferenceFromUrl(image);
        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.e("Picture","#deleted");
            }
        });
    }
}
