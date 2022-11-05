package com.upsun.quizz.Adapter;

import static com.upsun.quizz.Config.Constants.KEY_ID;
import static com.upsun.quizz.Config.Constants.PMETHOD_REF;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.upsun.quizz.AddBankDetailsActivity;
import com.upsun.quizz.Model.BankDetailsModel;
import com.upsun.quizz.R;
import com.upsun.quizz.utils.SessionManagment;
import com.upsun.quizz.utils.ToastMsg;

import java.util.ArrayList;

/**
 * Developed by Binplus Technologies pvt. ltd.  on 25,April,2020
 */
public class BankDetilsAdapter extends RecyclerView.Adapter<BankDetilsAdapter.ViewHolder> {
    Activity activity;
    ArrayList<BankDetailsModel> list;
    SessionManagment sessionManagment;
    ProgressDialog loadingBar;
    ToastMsg toastMsg;

    public BankDetilsAdapter(Activity activity, ArrayList<BankDetailsModel> list) {
        this.activity = activity;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
     View view= LayoutInflater.from(activity).inflate(R.layout.row_bank,null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final BankDetailsModel model=list.get(position);
        if(model.getType().equalsIgnoreCase("upi"))
        {
            holder.tv_sm.setText("U");
            holder.tv_title.setText("UPI : "+model.getUpi());
        }
        else {
            holder.tv_sm.setText("B");
            holder.tv_title.setText("Bank Name : "+model.getBank_name()+"\nAccount NO : "+model.getAcc_no()+"\n IFSC : "+model.getIfsc()+"\nBank Holder Name : "+model.getH_name());
        }

        holder.iv_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(activity, AddBankDetailsActivity.class);
                intent.putExtra("is_edit","true");
                intent.putExtra("upi",model.getUpi());
                intent.putExtra("id",model.getPay_id());
                intent.putExtra("bank_name",model.getBank_name());
                intent.putExtra("acc_no",model.getAcc_no());
                intent.putExtra("ifsc",model.getIfsc());
                intent.putExtra("holder",model.getH_name());
                intent.putExtra("type",model.getType());
                activity.startActivity(intent);
            }
        });

        holder.iv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createRemoveDialog(sessionManagment.getUserDetails().get(KEY_ID),model.getPay_id(),position);
            }
        });

    }

    private void createRemoveDialog(final String s, final String pay_id, final int pos) {
        final DatabaseReference cat_ref= FirebaseDatabase.getInstance().getReference().child(PMETHOD_REF);
        AlertDialog.Builder builder=new AlertDialog.Builder(activity);
        builder.setMessage(activity.getResources().getString(R.string.txt_delete_item));
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                loadingBar.show();
                cat_ref.child(s).child(pay_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        loadingBar.dismiss();
                        if(task.isSuccessful())
                        {
                            list.remove(pos);
                            toastMsg.toastIconSuccess("Item Remove Successfully.");
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

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_sm,tv_title;
        ImageView iv_edit,iv_delete;
        public ViewHolder(@NonNull View v) {
            super(v);
            tv_sm=(TextView)v.findViewById(R.id.tv_sm);
            tv_title=(TextView)v.findViewById(R.id.tv_title);
            iv_edit=(ImageView) v.findViewById(R.id.iv_edit);
            iv_delete=(ImageView)v.findViewById(R.id.iv_delete);
            toastMsg=new ToastMsg(activity);
            loadingBar=new ProgressDialog(activity);
            loadingBar.setMessage("Loading...");
            loadingBar.setCanceledOnTouchOutside(false);
            sessionManagment=new SessionManagment(activity);

        }
    }
}
