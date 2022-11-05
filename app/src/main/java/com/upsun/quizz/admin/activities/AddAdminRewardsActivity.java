package com.upsun.quizz.admin.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import com.upsun.quizz.Adapter.AdminRankAdapter;
import com.upsun.quizz.Config.Module;
import com.upsun.quizz.Model.RankModel;
import com.upsun.quizz.R;
import com.upsun.quizz.networkconnectivity.NetworkConnection;
import com.upsun.quizz.utils.LoadingBar;
import com.upsun.quizz.utils.RecyclerTouchListener;
import com.upsun.quizz.utils.ToastMsg;

import static com.upsun.quizz.Config.Constants.RANK_REF;

public class AddAdminRewardsActivity extends AppCompatActivity implements View.OnClickListener{
    ImageView iv_back;
    TextView tv_title;
    Button btn_add;
    RecyclerView rv_rewards;
    LinearLayout lin_range,lin_rank;
    RadioButton rd_range,rd_rank;
    ToastMsg toastMsg;
    ProgressDialog loadingBar;
    RelativeLayout rel_no_items;
    String quiz_id="";
    int ptcnt=0;
    DatabaseReference rank_ref;
    Activity ctx=AddAdminRewardsActivity.this;
    ArrayList<RankModel> list;
    AdminRankAdapter rankAdapter;
    ArrayList<String> list_rank;
    ArrayList<String> updatedRankList;
    Spinner spin_rank,spin_from,spin_end;
    EditText et_reward,et_reward_range;
    ArrayAdapter<String> rankSpinAdapter;
    Module module;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_admin_rewards);
        initViews();
        if(NetworkConnection.connectionChecking(ctx))
        {
            getAllRanks(quiz_id);

        }
        else
        {
            module.noConnectionActivity();
        }
        rd_rank.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    if(lin_range.getVisibility()== View.VISIBLE)
                    {
                        lin_range.setVisibility(View.GONE);
                    }
                    if(lin_rank.getVisibility() == View.GONE)
                    {
                        lin_rank.setVisibility(View.VISIBLE);
                    }
                    getAllRanks(quiz_id);
//                    if(ptcnt>0)
//                    {
//                        for(int i=1;i<=ptcnt;i++)
//                        {
////                            if(getExistingRank(list,String.valueOf(i)))
////                            {
//                                list_rank.add(String.valueOf(i));
////                            }
//
//                        }
//                        rankSpinAdapter=new ArrayAdapter<String>(ctx,android.R.layout.simple_spinner_dropdown_item,list_rank);
//                        spin_rank.setAdapter(rankSpinAdapter);
//                        spin_from.setAdapter(rankSpinAdapter);
//                        spin_end.setAdapter(rankSpinAdapter);
//
//                    }

                }
            }
        });
        rd_range.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    if(lin_range.getVisibility()== View.GONE)
                    {
                        lin_range.setVisibility(View.VISIBLE);
                    }
                    if(lin_rank.getVisibility() == View.VISIBLE)
                    {
                        lin_rank.setVisibility(View.GONE);
                    }

                    getAllRanks(quiz_id);
                }
            }
        });

        rv_rewards.addOnItemTouchListener(new RecyclerTouchListener(ctx, rv_rewards, new RecyclerTouchListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                if(lin_rank.getVisibility()==View.GONE)
                {
                    lin_rank.setVisibility(View.VISIBLE);

                }
                if(lin_range.getVisibility()==View.VISIBLE)
                {
                    lin_range.setVisibility(View.GONE);

                }
                ArrayList<String> strList=new ArrayList<>();
                strList.add(list.get(position).getRank().toString());
                setSpinnerValues(spin_rank,strList,rankSpinAdapter);
                et_reward.setText(list.get(position).getRewards().toString());
                btn_add.setText("Update");
              }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));
    }

    private void initViews() {
        spin_rank=(Spinner)findViewById(R.id.spin_rank);
        spin_from=(Spinner)findViewById(R.id.spin_from);
        spin_end=(Spinner)findViewById(R.id.spin_end);
        et_reward=(EditText)findViewById(R.id.et_reward);
        et_reward_range=(EditText)findViewById(R.id.et_reward_range);
        iv_back=(ImageView)findViewById(R.id.iv_back);
        tv_title=(TextView) findViewById(R.id.tv_title);
        rv_rewards=(RecyclerView) findViewById(R.id.rv_rewards);
        lin_range=(LinearLayout)findViewById(R.id.lin_range);
        lin_rank=(LinearLayout)findViewById(R.id.lin_rank);
        rd_range=(RadioButton) findViewById(R.id.rd_range);
        rd_rank=(RadioButton) findViewById(R.id.rd_rank);
        rel_no_items=(RelativeLayout) findViewById(R.id.rel_no_items);
        list=new ArrayList<>();
        module=new Module(ctx);
        list_rank=new ArrayList<>();
        updatedRankList=new ArrayList<>();
        quiz_id=getIntent().getStringExtra("quiz_id");
        ptcnt=Integer.parseInt(getIntent().getStringExtra("participents"));
        toastMsg=new ToastMsg(ctx);
        loadingBar=new ProgressDialog(ctx);
        loadingBar.setMessage("Loading...");
        loadingBar.setCanceledOnTouchOutside(false);
        tv_title.setText("Add Rewards");
        btn_add=(Button)findViewById(R.id.btn_add);
        rank_ref= FirebaseDatabase.getInstance().getReference().child(RANK_REF);
        btn_add.setOnClickListener(this);
        iv_back.setOnClickListener(this);
        if(ptcnt>0) {
            list_rank.add("Select");
            for (int i = 1; i <= ptcnt; i++) {
              list_rank.add(String.valueOf(i));
            }
        }
    }
    private void getAllRanks(String quiz_id) {
      loadingBar.show();
      list.clear();
      rank_ref.child(quiz_id).addListenerForSingleValueEvent(new ValueEventListener() {
          @Override
          public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
              loadingBar.dismiss();
              if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0)
              {
                  rel_no_items.setVisibility(View.GONE);
                  rv_rewards.setVisibility(View.VISIBLE);
                  for(DataSnapshot snapshot:dataSnapshot.getChildren())
                  {
                      RankModel model=snapshot.getValue(RankModel.class);
                      list.add(model);
                  }
                  btn_add.setText("Add");
                  rv_rewards.setLayoutManager(new LinearLayoutManager(ctx));
                  rankAdapter=new AdminRankAdapter(ctx,list);
                  rv_rewards.setAdapter(rankAdapter);
                  rankAdapter.notifyDataSetChanged();
                  ArrayList<String> newList=getUpdatedRankList(list,list_rank);
                  getAllSpinnerData(ptcnt,newList);
              }
              else
              {
                 rel_no_items.setVisibility(View.VISIBLE);
                 rv_rewards.setVisibility(View.GONE);
                  getAllSpinnerData(ptcnt,list_rank);
              }
          }

          @Override
          public void onCancelled(@NonNull DatabaseError databaseError) {
           loadingBar.dismiss();
              Toast.makeText(ctx, ""+databaseError.getMessage().toString(), Toast.LENGTH_SHORT).show();
          }
      });
    }

    public boolean getExistingRank(ArrayList<RankModel> list,String rank)
    {
        boolean flag=false;
        for(int i=0;i<list.size();i++)
        {
            if(list.get(i).getRank().equalsIgnoreCase(rank))
            {
                flag=true;
                break;
            }
        }
        return flag;
    }

    public void getRankRange(String quiz_id)
    {
        loadingBar.show();
        list.clear();
        rank_ref.child(quiz_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                loadingBar.dismiss();
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0)
                {
                    for(DataSnapshot snapshot:dataSnapshot.getChildren())
                    {
                        RankModel model=snapshot.getValue(RankModel.class);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                loadingBar.dismiss();
                Toast.makeText(ctx, ""+databaseError.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public ArrayList<String> getUpdatedRankList(ArrayList<RankModel> quizList,ArrayList<String> allRank)
    {
        ArrayList<String> allRankList=new ArrayList<>();
        ArrayList<String> upList=new ArrayList<>();
        allRankList=allRank;
        ArrayList<String> list=new ArrayList<>();
      for(int i=0; i<allRankList.size();i++)
      {
          if(!getExistingRank(quizList,allRank.get(i).toString()))
          {
           upList.add(allRankList.get(i).toString());
          }
      }
//        toastMsg.toastIconSuccess(""+allRankList.size());
    return upList;
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btn_add)
        {
             if(rd_rank.isChecked())
             {
                String rew=et_reward.getText().toString();
                String rank=spin_rank.getSelectedItem().toString();

                 if(rank.equalsIgnoreCase("Select"))
                {
                    toastMsg.toastIconError("Select a rank first");
                }
                 else if(rew.isEmpty())
                 {
                  et_reward.setError("Enter rewards");
                  et_reward.requestFocus();
                 }
                 else
                 {
                     addParicularRank(quiz_id,rank,rew);
                 }
             }
             else if(rd_range.isChecked())
             {
                 String rgFrom=spin_from.getSelectedItem().toString();
                 String rgEnd=spin_end.getSelectedItem().toString();
                 String rw=et_reward_range.getText().toString();

                 if(rgFrom.equalsIgnoreCase("Select"))
                 {
                     toastMsg.toastIconError("Select Range From");
                 }else if(rgEnd.equalsIgnoreCase("Select"))
                 {
                     toastMsg.toastIconError("Select Range To");
                 }
                 else if(rw.isEmpty())
                 {
                     et_reward_range.setError("Enter Rewards");
                     et_reward_range.requestFocus();
                 }
                 else
                 {
                     addRange(quiz_id,rgFrom,rgEnd,rw);
                 }
             }
        }
        else if(v.getId() == R.id.iv_back)
        {
          finish();
        }
    }

    private void addRange(String quiz_id, String rgFrom, String rgEnd, String rw) {
       new AddRangeRewards(quiz_id,rgFrom,rgEnd,rw).execute();
    }



    private void addParicularRank(final String quiz_id, String rank, String rew) {
        loadingBar.show();
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("rank",rank);
        hashMap.put("rewards",rew);
        rank_ref.child(quiz_id).child(rank).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                loadingBar.dismiss();
                if(task.isSuccessful())
                {
                    et_reward.setText("");
                    toastMsg.toastIconSuccess("Reward Added...");
                    getAllRanks(quiz_id);
                }
                else {
                    toastMsg.toastIconError(""+task.getException().getMessage().toString());
                }
            }
        });
    }
    public class  AddRangeRewards extends AsyncTask<Void,Void,Void>
    {
     String quiz_id="";
     String rgFrom="";
     String rgEnd="";
     String rew="";
     LoadingBar bar;
     ToastMsg msg;
        public AddRangeRewards(String quiz_id, String rgFrom, String rgEnd, String rew) {
            this.quiz_id = quiz_id;
            this.rgFrom = rgFrom;
            this.rgEnd = rgEnd;
            this.rew = rew;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            bar=new LoadingBar(ctx);
            bar.show();
            msg=new ToastMsg(ctx);

        }


        @Override
        protected Void doInBackground(Void... voids) {
            int rg_from=Integer.parseInt(rgFrom);
            int rg_end=Integer.parseInt(rgEnd);
            int len=rg_end-(rg_from);
            for(int i=0;i<=len;i++)
            {

                int newrg=rg_from;
                rg_from++;
                Log.e("ssss",""+rgFrom);
                HashMap<String,Object> parms=new HashMap<>();
                parms.put("rank",String.valueOf(newrg));
                parms.put("rewards",rew);
                rank_ref.child(quiz_id).child(String.valueOf(newrg)).updateChildren(parms).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                        }
                        else {
                            msg.toastIconError(""+task.getException().getMessage().toString());
                        }
                    }
                });
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            et_reward_range.setText("");
            bar.dismiss();
            getAllRanks(quiz_id);
            msg.toastIconSuccess("Reward Added...");

        }

    }

    public void getAllSpinnerData(int ptcnt,ArrayList<String> rnkList)
    {
        setSpinnerValues(spin_rank,rnkList,rankSpinAdapter);
            setSpinnerValues(spin_from,rnkList,rankSpinAdapter);
            setSpinnerValues(spin_end,rnkList,rankSpinAdapter);
    }

    public void setSpinnerValues(Spinner spinner,ArrayList<String> list,ArrayAdapter<String> arrayAdapter)
    {
        arrayAdapter=new ArrayAdapter<String>(ctx,android.R.layout.simple_spinner_dropdown_item,list);
        spinner.setAdapter(arrayAdapter);
    }

}
