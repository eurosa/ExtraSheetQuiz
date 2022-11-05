package com.upsun.quizz.admin.fragments;

import static com.upsun.quizz.Config.Constants.TRANS_REF;
import static com.upsun.quizz.Config.Constants.USER_REF;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.upsun.quizz.Config.Module;
import com.upsun.quizz.Model.UserModel;
import com.upsun.quizz.R;
import com.upsun.quizz.utils.ToastMsg;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddPointsManuallyFragment extends Fragment implements View.OnClickListener{
    Button btn_search,btn_add;
    EditText et_points,et_mobile,et_amt;
    TextView tv_points;
    Module module;
    ToastMsg toastMsg;
    ProgressDialog loadingBar;
    ArrayList<UserModel> list;
    DatabaseReference trans_ref;
    int flag=0;
    public AddPointsManuallyFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_points_manually, container, false);
          initViews(view);
        return view;
    }

    private void initViews(View v) {
        btn_search=v.findViewById(R.id.btn_search);
        btn_add=v.findViewById(R.id.btn_add);
        et_points=v.findViewById(R.id.et_points);
        et_mobile=v.findViewById(R.id.et_mobile);
        et_amt=v.findViewById(R.id.et_amt);
        tv_points=v.findViewById(R.id.tv_points);
        module=new Module(getActivity());
        toastMsg=new ToastMsg(getActivity());
        loadingBar=new ProgressDialog(getActivity());
        loadingBar.setMessage("Loading...");
        loadingBar.setCanceledOnTouchOutside(false);
        btn_search.setOnClickListener(this);
        btn_add.setOnClickListener(this);
        btn_add.setVisibility(View.GONE);

        list=new ArrayList<>();
    }

    @Override
    public void onClick(View v) {

        if(v.getId() == R.id.btn_search)
        {
            String mobile=et_mobile.getText().toString().trim();
            if(mobile.isEmpty())
            {
             toastMsg.toastIconError("Enter Mobile number");
                et_mobile.requestFocus();
            }
            else if(mobile.length()!=10)
            {
                toastMsg.toastIconError("Invalid Mobile number");
                et_mobile.requestFocus();
            }
            else
            {
                searchUser(mobile);
            }
        }
        else if(v.getId() == R.id.btn_add)
        {
            String points=et_points.getText().toString();
            String amount=et_amt.getText().toString();
           if(flag==0)
           {
               toastMsg.toastIconError("Please Verify mobile Number");

           }
           else if(amount.isEmpty())
           {
               toastMsg.toastIconError("Enter Amount");
               et_amt.requestFocus();
           }
           else if(amount.equalsIgnoreCase("0"))
           {
               toastMsg.toastIconError("Enter Valid Amount");
               et_amt.requestFocus();
           }
           else if(points.isEmpty())
           {
               toastMsg.toastIconError("Enter Points");
           }
           else if(points.equalsIgnoreCase("0"))
           {
               toastMsg.toastIconError("Enter valid points");
           }

           else
           {
               String user_id=list.get(0).getId().toString();

               int pnts=Integer.parseInt(points);
               int updateWallet=Integer.parseInt(points)+Integer.parseInt(list.get(0).getWallet().toString());
               addUserPoints(user_id,pnts,amount,updateWallet);
           }

        }

    }

    private void addUserPoints(final String user_id, final int pnts, String amt,final int updtWallet) {
       String txn_id=module.getUniqueId("txn");
        loadingBar.show();
        HashMap<String,Object>  map=new HashMap<>();
        map.put("user_id",user_id);
        map.put("points",String.valueOf(pnts));
        map.put("amount",amt);
        map.put("txn_id",txn_id);
        map.put("status","success");
        trans_ref= FirebaseDatabase.getInstance().getReference().child(TRANS_REF);
        trans_ref.child(txn_id).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                loadingBar.dismiss();
                if(task.isSuccessful())
                {
                    toastMsg.toastIconSuccess("Points Added to your wallet");
                    module.updateDbWallet(user_id,String.valueOf(updtWallet));
                    clearCtrls();

                }
                else
                {
                    module.showToast(""+task.getException().getMessage().toString());
                }

            }
        });
    }

    private void searchUser(final String mobile) {
        loadingBar.show();
        list.clear();
        DatabaseReference userREf= FirebaseDatabase.getInstance().getReference().child(USER_REF);
        Query query=userREf.orderByChild("mobile").equalTo(mobile);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.e("reess",""+dataSnapshot.toString());
                loadingBar.dismiss();
                try {
                    if(dataSnapshot.exists() || dataSnapshot.hasChildren())
                    {
                        for(DataSnapshot snap:dataSnapshot.getChildren())
                        {
                            UserModel model=snap.getValue(UserModel.class);

                            list.add(model);
                        }

                        Log.e("modelll",""+list.size()+" - "+list.get(0).getWallet());
                        tv_points.setVisibility(View.VISIBLE);
                        tv_points.setText("User have only :- "+list.get(0).getWallet().toString()+" points in his wallet");
                        btn_add.setVisibility(View.VISIBLE);
                        flag=1;

                    }
                    else
                    {
                        toastMsg.toastIconError("No Records Found");
                    }
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
             loadingBar.dismiss();
             module.showToast(""+databaseError.getMessage());
            }
        });
    }
    public void clearCtrls()
    {
        et_mobile.setText("");
        et_amt.setText("");
        et_points.setText("");
        flag=0;
    }
}
