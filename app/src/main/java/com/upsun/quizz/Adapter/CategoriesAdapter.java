package com.upsun.quizz.Adapter;

import static com.upsun.quizz.Config.Constants.CATEGORY_REF;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.upsun.quizz.Config.Module;
import com.upsun.quizz.Model.CategoriesModel;
import com.upsun.quizz.R;
import com.upsun.quizz.admin.activities.AllQuestionActivity;
import com.upsun.quizz.admin.fragments.AllCategoriesFragment;
import com.upsun.quizz.utils.ToastMsg;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Developed by Binplus Technologies pvt. ltd.  on 08,April,2020
 */
public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.ViewHolder> {
    Activity activity;
    ArrayList<CategoriesModel> list;
    String child_count ;
    ProgressDialog loadingBar;
    ToastMsg toastMsg;
    Module module;

    public CategoriesAdapter(Activity activity, ArrayList<CategoriesModel> list) {
        this.activity = activity;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view= LayoutInflater.from(activity).inflate(R.layout.row_categories,null);
        ViewHolder viewHolder=new ViewHolder(view);
        return viewHolder;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final CategoriesModel model=list.get(position);
         holder.tv_title.setText(model.getCat_name());
         holder.tv_sm.setText(model.getCat_name().substring(0,1).toUpperCase());

         switch (position%5)
         {
             case 0:
                 holder.tv_title.setTextColor(activity.getResources().getColor(R.color.rc_1));
                 holder.rel_bcl.getBackground().setTint(activity.getResources().getColor(R.color.rc_1));
                 holder.rel_fcl.getBackground().setTint(activity.getResources().getColor(R.color.rc_1));
                 break;
             case 1:
                 holder.tv_title.setTextColor(activity.getResources().getColor(R.color.rc_2));
                 holder.rel_bcl.getBackground().setTint(activity.getResources().getColor(R.color.rc_2));
                 holder.rel_fcl.getBackground().setTint(activity.getResources().getColor(R.color.rc_2));
                 break;
             case 2:
                 holder.tv_title.setTextColor(activity.getResources().getColor(R.color.rc_3));
                 holder.rel_bcl.getBackground().setTint(activity.getResources().getColor(R.color.rc_3));
                 holder.rel_fcl.getBackground().setTint(activity.getResources().getColor(R.color.rc_3));
                 break;
             case 3:
                 holder.tv_title.setTextColor(activity.getResources().getColor(R.color.rc_4));
                 holder.rel_bcl.getBackground().setTint(activity.getResources().getColor(R.color.rc_4));
                 holder.rel_fcl.getBackground().setTint(activity.getResources().getColor(R.color.rc_4));
                 break;
             case 4:
                 holder.tv_title.setTextColor(activity.getResources().getColor(R.color.rc_5));
                 holder.rel_bcl.getBackground().setTint(activity.getResources().getColor(R.color.rc_5));
                 holder.rel_fcl.getBackground().setTint(activity.getResources().getColor(R.color.rc_5));
                 break;
             default :
                 holder.tv_title.setTextColor(activity.getResources().getColor(R.color.rc_1));
                 holder.rel_bcl.getBackground().setTint(activity.getResources().getColor(R.color.rc_1));
                 holder.rel_fcl.getBackground().setTint(activity.getResources().getColor(R.color.rc_1));
                 break;


         }
        DatabaseReference  dRef = FirebaseDatabase.getInstance().getReference();
        dRef.child(model.getCat_id()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren())
                {
                    child_count = String.valueOf(dataSnapshot.getChildrenCount());
                }
                else
                {
                    child_count = "0";
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


         holder.lin_main.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Intent intent=new Intent(activity, AllQuestionActivity.class);
                 intent.putExtra("cat_id",model.getCat_id());
                 intent.putExtra("child_count",child_count);
                 activity.startActivity(intent);
             }
         });

         holder.iv_delete.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
             createRemoveDialog(position,model.getCat_id().toString());
             }
         });

         holder.iv_edit.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
             createUpdateCategory(model.getCat_id().toString(),model.getCat_name().toString());
             }
         });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout rel_bcl,rel_fcl;
        TextView tv_sm,tv_title;
        LinearLayout lin_main;
        ImageView iv_edit,iv_delete;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            rel_bcl=(RelativeLayout)itemView.findViewById(R.id.rel_bcl);
            rel_fcl=(RelativeLayout)itemView.findViewById(R.id.rel_fcl);
            lin_main=(LinearLayout) itemView.findViewById(R.id.lin_main);
            tv_sm=(TextView)itemView.findViewById(R.id.tv_sm);
            tv_title=(TextView)itemView.findViewById(R.id.tv_title);
            iv_delete=(ImageView)itemView.findViewById(R.id.iv_delete);
            iv_edit=(ImageView)itemView.findViewById(R.id.iv_edit);
            toastMsg=new ToastMsg(activity);
            loadingBar=new ProgressDialog(activity);
            loadingBar.setMessage("Loading...");
            loadingBar.setCanceledOnTouchOutside(false);
            module=new Module(activity);
        }
    }
    private void createRemoveDialog(final int pos, final String cId) {

     final DatabaseReference cat_ref= FirebaseDatabase.getInstance().getReference().child(CATEGORY_REF);
        AlertDialog.Builder builder=new AlertDialog.Builder(activity);
        builder.setMessage(activity.getResources().getString(R.string.txt_delete_item));
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                loadingBar.show();
                cat_ref.child(cId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        loadingBar.dismiss();
                        if(task.isSuccessful())
                        {
                            list.remove(pos);
                            toastMsg.toastIconSuccess("Category Remove Successfully.");
                            AllCategoriesFragment.no_of_cat.setText("No. Of Categories : "+list.size());
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


    private void createUpdateCategory(final String id, String cat_name) {
        final Dialog dialog=new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_add_cat_layout);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        final EditText et_cat=(EditText)dialog.findViewById(R.id.et_cat);
        Button btn_add=(Button) dialog.findViewById(R.id.btn_add);
        Button btn_cancel=(Button) dialog.findViewById(R.id.btn_cancel);
        et_cat.setText(cat_name);
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(et_cat.getText().toString().isEmpty()){
                    et_cat.setError(activity.getResources().getString(R.string.req_cat_name));
                    et_cat.requestFocus();
                }
                else{
                    updateCategory(id,et_cat.getText().toString(),dialog);
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

    private void updateCategory(final String id, final String name, final Dialog dialog) {
        loadingBar.show();
        HashMap<String,Object> map=new HashMap<>();
        map.put("cat_id",id);
        map.put("cat_name",name);
       DatabaseReference cat_ref= FirebaseDatabase.getInstance().getReference().child(CATEGORY_REF);

        cat_ref.child(id).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    loadingBar.dismiss();
                    dialog.dismiss();
                    toastMsg.toastIconSuccess("Category Name Updated..");
                    toastMsg.toastIconSuccess("Swipe to refresh list");
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
}
