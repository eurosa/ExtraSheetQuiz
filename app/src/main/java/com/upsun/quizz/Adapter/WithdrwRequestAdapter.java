package com.upsun.quizz.Adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.upsun.quizz.Model.UserModel;
import com.upsun.quizz.Model.WithdrawModel;
import com.upsun.quizz.R;
import com.upsun.quizz.admin.activities.ApproveRequestActivity;
import com.upsun.quizz.utils.ToastMsg;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class WithdrwRequestAdapter extends RecyclerView.Adapter<WithdrwRequestAdapter.ViewHolder> {
    ArrayList<WithdrawModel> w_list;
    Activity activity;
    ArrayList<UserModel> u_list;
    String status = "";
    ProgressDialog loadingBar;
    ToastMsg toastMsg;

    public WithdrwRequestAdapter(ArrayList<WithdrawModel> w_list, Activity activity, ArrayList<UserModel> u_list) {
        this.w_list = w_list;
        this.activity = activity;
        this.u_list = u_list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.row_wthdrw_requests, null);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final WithdrawModel model = w_list.get(position);
        holder.vw.setVisibility(View.GONE);
        holder.lin_approve.setVisibility(View.GONE);
        String j_id = model.getId();
        String j_d = j_id.substring(2, 10);
        String j_t = j_id.substring(10, j_id.length());
        String date = j_d.substring(0, 2) + ":" + j_d.subSequence(2, 4) + ":" + j_d.subSequence(4, j_d.length());
        String time = j_t.substring(0, 2) + ":" + j_t.subSequence(2, 4) + ":" + j_t.subSequence(4, 6);
        SimpleDateFormat format_t = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat f = new SimpleDateFormat("HH:mm a");

        try {
            Date t = format_t.parse(time);
            String tt = f.format(t);
            holder.txt_date.setText(date + "\n" + tt);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        holder.txt_status.setText(model.getStatus().toUpperCase());
        holder.txt_amount.setText(model.getRequest_amount());
        if (model.getType().equalsIgnoreCase("upi")) {
            holder.txt_type.setText("UPI :");
            holder.tv_type.setText(model.getUpi());
        } else {
            holder.txt_type.setText("Bank Acc No :");
            holder.tv_type.setText(model.getAcc_no());
        }
        for (int i = 0; i < u_list.size(); i++) {
            if (model.getUser_id()!=null&&u_list.get(i).getId()!=null) {
                if (u_list.get(i).getId().equals(model.getUser_id())) {
                    holder.txt_name.setText(u_list.get(i).getName());
                }
            }
        }
        if (model.getStatus().equalsIgnoreCase("Pending")) {
//        holder.lin_approve.setVisibility(View.VISIBLE);
            holder.txt_status.setTextColor(activity.getResources().getColor(R.color.rec_dahlia));

        } else if (model.getStatus().equalsIgnoreCase("approved")) {
//            holder.lin_approve.setVisibility(View.GONE);
            holder.txt_status.setTextColor(activity.getResources().getColor(R.color.green));
        } else if (model.getStatus().equalsIgnoreCase("rejected")) {
//            holder.lin_approve.setVisibility(View.GONE);
            holder.txt_status.setTextColor(activity.getResources().getColor(R.color.red_trans));
        }
        holder.btn_approve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                status = "approved";
                updateWithdraw(w_list.get(position).getUser_id(), w_list.get(position).getId(), status, w_list.get(position).getRequest_amount());
                w_list.remove(position);
                notifyDataSetChanged();

            }
        });
        holder.btn_decline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                status = "rejected";
                updateWithdraw(w_list.get(position).getUser_id(), w_list.get(position).getId(), status, w_list.get(position).getRequest_amount());

                w_list.remove(position);
                notifyDataSetChanged();
            }
        });
        holder.lin_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, ApproveRequestActivity.class);
                intent.putExtra("user_id", model.getUser_id());
                intent.putExtra("id", model.getId());
                intent.putExtra("pay_id", model.getPay_id());
                intent.putExtra("request_amount", model.getRequest_amount());
                intent.putExtra("status", model.getStatus());
                intent.putExtra("acc_no", model.getAcc_no());
                intent.putExtra("bank_name", model.getBank_name());
                intent.putExtra("h_name", model.getH_name());
                intent.putExtra("type", model.getType());
                intent.putExtra("upi", model.getUpi());
                intent.putExtra("ifsc", model.getIfsc());
                activity.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return w_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txt_name, txt_amount, txt_date, txt_status, txt_type, tv_type;
        Button btn_approve, btn_decline;
        LinearLayout lin_approve, lin_main;
        View vw;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_name = itemView.findViewById(R.id.tv_title);
            txt_amount = itemView.findViewById(R.id.tv_amt);
            txt_date = itemView.findViewById(R.id.tv_time);
            txt_type = itemView.findViewById(R.id.txt_type);
            tv_type = itemView.findViewById(R.id.tv_type);
            txt_status = itemView.findViewById(R.id.tv_status);
            btn_approve = itemView.findViewById(R.id.approve);
            btn_decline = itemView.findViewById(R.id.decline);
            lin_approve = itemView.findViewById(R.id.lin_approve);
            lin_main = itemView.findViewById(R.id.lin_main);
            vw = itemView.findViewById(R.id.vw);
            loadingBar = new ProgressDialog(activity);
            loadingBar.setMessage("Loading...");
            loadingBar.setCanceledOnTouchOutside(false);
            toastMsg = new ToastMsg(activity);
        }
    }

    public void updateWithdraw(String user_id, String id, final String status, String amount) {
        loadingBar.show();

        HashMap<String, Object> params = new HashMap<>();
        params.put("request_amount", amount);
        params.put("user_id", user_id);
        params.put("status", status);
        params.put("id", id);


        DatabaseReference dref = FirebaseDatabase.getInstance().getReference();
        dref.child("withdraw_request").child(id).updateChildren(params).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    loadingBar.dismiss();
                    if (status.equals("approved")) {
                        new ToastMsg(activity).toastIconSuccess("Withdraw Request Approved");
                    } else if (status.equals("rejected")) {
                        new ToastMsg(activity).toastIconError("Withdraw Request Rejected");
                    }
                } else {
                    loadingBar.dismiss();
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                loadingBar.dismiss();
                new ToastMsg(activity).toastIconError(e.getMessage());
            }
        });

    }

}
