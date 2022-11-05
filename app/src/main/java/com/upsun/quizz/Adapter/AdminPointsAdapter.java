package com.upsun.quizz.Adapter;

import static com.upsun.quizz.Config.Constants.POINT_REF;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.upsun.quizz.Model.PointsModel;
import com.upsun.quizz.R;
import com.upsun.quizz.admin.activities.AddPointsActivity;
import com.upsun.quizz.admin.activities.AllPointsActivity;
import com.upsun.quizz.utils.ToastMsg;

import java.util.ArrayList;

/**
 * Developed by Binplus Technologies pvt. ltd.  on 20,April,2020
 */
public class AdminPointsAdapter extends RecyclerView.Adapter<AdminPointsAdapter.ViewHolder> {
    Activity activity;
    ArrayList<PointsModel> list;
    ProgressDialog loadingBar;
    DatabaseReference points_ref;
    ToastMsg toastMsg;

    public AdminPointsAdapter(Activity activity, ArrayList<PointsModel> list) {
        this.activity = activity;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(activity).inflate(R.layout.row_all_question,null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
       final PointsModel model=list.get(position);
       holder.tv_ques.setText("Points : "+model.getPoints()+"    "+activity.getResources().getString(R.string.rupee)+model.getAmount().toString());
       holder.iv_delete.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               createDeleteDialog(position);
           }
       });

       holder.lin_main.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent=new Intent(activity, AddPointsActivity.class);
               intent.putExtra("id",model.getId().toString());
               intent.putExtra("points",model.getPoints().toString());
               intent.putExtra("amt",model.getAmount().toString());
               intent.putExtra("is_edit","true");
               activity.startActivity(intent);

           }
       });
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_ques;
        ImageView iv_delete;
        LinearLayout lin_main;
        public ViewHolder(@NonNull View v) {
            super(v);
            tv_ques=(TextView)v.findViewById(R.id.tv_ques);
            iv_delete=(ImageView)v.findViewById(R.id.iv_delete);
            loadingBar=new ProgressDialog(activity);
            loadingBar.setMessage("Loading...");
            loadingBar.setCanceledOnTouchOutside(false);
            points_ref= FirebaseDatabase.getInstance().getReference().child(POINT_REF);
            toastMsg=new ToastMsg(activity);
            lin_main=(LinearLayout)v.findViewById(R.id.lin_main);
        }
    }

    private void createDeleteDialog(final int position) {
        AlertDialog.Builder builder=new AlertDialog.Builder(activity);
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteItem(points_ref,list,position,dialog);
            }
        }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog=builder.create();
        alertDialog.show();
    }

    private void deleteItem(DatabaseReference points_ref, final ArrayList<PointsModel> list, final int position, final DialogInterface dialog) {
        loadingBar.show();
        points_ref.child(list.get(position).getId().toString()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                loadingBar.dismiss();
                if(task.isSuccessful())
                {
                    list.remove(position);
                    toastMsg.toastIconSuccess("Question Remove Successfully.");
                    AllPointsActivity.no_of_points.setText("No. Of Items : "+list.size());
                    notifyDataSetChanged();
                    dialog.dismiss();

                }
                else {
                    toastMsg.toastIconError(""+task.getException().getMessage());
                }
            }
        });
    }

}
