package com.upsun.quizz.Adapter;

import static com.upsun.quizz.Config.Constants.SECTION_REF;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.upsun.quizz.Model.SectionModel;
import com.upsun.quizz.R;
import com.upsun.quizz.admin.activities.QuizQuestionActivity;
import com.upsun.quizz.utils.ToastMsg;

/**
 * Developed by Binplus Technologies pvt. ltd.  on 15,December,2020
 */
public class SectionAdapter extends RecyclerView.Adapter<SectionAdapter.ViewHolder> {
    Activity activity;
    List<SectionModel> list;

    public SectionAdapter(Activity activity, List<SectionModel> list) {
        this.activity = activity;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(activity).inflate(R.layout.row_section,null);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tv_title.setText(list.get(position).getSection_name());
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               FirebaseDatabase.getInstance().getReference().child(SECTION_REF).child(list.get(position).getQuiz_id()).child(list.get(position).getSection_id()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                   @Override
                   public void onComplete(@NonNull Task<Void> task) {
                       ToastMsg toastMsg= new ToastMsg(activity);
                       toastMsg.toastIconSuccess("Successfully deleted");
                   }
               }).addOnFailureListener(new OnFailureListener() {
                   @Override
                   public void onFailure(@NonNull Exception e) {
                       ToastMsg toastMsg= new ToastMsg(activity);
                       toastMsg.toastIconSuccess("Something went wrong");

                   }
               });

            }
        });
holder.itemView.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        SectionModel model=list.get(position);
        Intent intent = new Intent(activity, QuizQuestionActivity.class);
        intent.putExtra("quiz_id",model.getQuiz_id().toString());
        intent.putExtra("section_id",model.getSection_id().toString());
        intent.putExtra("section_name",model.getSection_name().toString());
        activity.startActivity(intent);
    }
});
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_title;
        ImageView delete;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_title=itemView.findViewById(R.id.tv_title);
            delete=itemView.findViewById(R.id.delete);
        }
    }
}
