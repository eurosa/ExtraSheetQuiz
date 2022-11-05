package com.upsun.quizz;

import static com.upsun.quizz.Config.Constants.KEY_ID;
import static com.upsun.quizz.Config.Constants.KEY_REWARDS;
import static com.upsun.quizz.Config.Constants.PMETHOD_REF;
import static com.upsun.quizz.Config.Constants.WITHDRAW_LIMIT_AMOUNT;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.upsun.quizz.Config.Module;
import com.upsun.quizz.Model.BankDetailsModel;
import com.upsun.quizz.networkconnectivity.NetworkConnection;
import com.upsun.quizz.utils.SessionManagment;
import com.upsun.quizz.utils.ToastMsg;

import java.util.ArrayList;
import java.util.HashMap;

public class WithdrawRequest extends AppCompatActivity {
    Button btn_request ;
    EditText et_amount ;
    String user_id ,type;
    Spinner spin_pay;
    Activity ctx=WithdrawRequest.this;
    int w_amout;
    ArrayList<BankDetailsModel> bankList;
    ArrayList<String> strList;
    SessionManagment session_management ;
    ProgressDialog loadingBar ;
    ArrayAdapter<String> arrayAdapter;
    Module module ;
    ToastMsg toastMsg;
    DatabaseReference payRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw_request);
        btn_request = findViewById( R.id.btnRequest );
        et_amount = findViewById( R.id.et_amount );
        spin_pay = findViewById( R.id.spin_pay );
        bankList=new ArrayList<>();
        strList=new ArrayList<>();
        payRef=FirebaseDatabase.getInstance().getReference().child(PMETHOD_REF);
        session_management = new SessionManagment(this );
        user_id = session_management.getUserDetails().get( KEY_ID );
        toastMsg=new ToastMsg(this);
        loadingBar=new ProgressDialog(ctx);
        loadingBar.setMessage("Loading...");
        loadingBar.setCanceledOnTouchOutside(false);
        module = new Module( this );
        btn_request.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String getamt = et_amount.getText().toString().trim();
                if (getamt.isEmpty())
                {
                    et_amount.setError( "Enter Amount" );
                    et_amount.requestFocus();
                }
                else
                {
                    int wAmt=Integer.parseInt(getamt.toString());
                    if(wAmt<WITHDRAW_LIMIT_AMOUNT)
                    {
                        toastMsg.toastIconError("Minimum withdraw amount is "+String.valueOf(WITHDRAW_LIMIT_AMOUNT));
                    }
                    else
                    {


                    w_amout =Integer.parseInt( session_management.getUserDetails().get(KEY_REWARDS));

                    if (Integer.parseInt(getamt)>w_amout)
                    {
                        new ToastMsg(WithdrawRequest.this).toastIconError("Insufficient points to request withdrawal");
                    }
                    else
                    {
                        if(NetworkConnection.connectionChecking(WithdrawRequest.this))
                        {

                              requestWithdrw( getamt,user_id,strList,spin_pay.getSelectedItem().toString(),bankList);
                        }
                        else
                        {
                            new Module(WithdrawRequest.this).noConnectionActivity();
                        }

                    }

                }
                }
            }
        } );

    }

    public void requestWithdrw(final String amount , final String user_id, ArrayList<String> stList, String p_mthod, ArrayList<BankDetailsModel> bnkList)
    {
        loadingBar.show();
        String uniqueid = module.getUniqueId("wr");
        HashMap<String,Object> params = new HashMap<>();
        int pos=getWithdrawPosition(stList,p_mthod);
        params.put( "request_amount",amount );
        params.put( "user_id",user_id );
        params.put("status","pending");
        params.put("id",uniqueid);
        params.put("pay_id",bnkList.get(pos).getPay_id());
        params.put("user_id",bnkList.get(pos).getUser_id());
        params.put("type",bnkList.get(pos).getType());
        params.put("upi",bnkList.get(pos).getUpi());
        params.put("bank_name",bnkList.get(pos).getBank_name());
        params.put("ifsc",bnkList.get(pos).getIfsc());
        params.put("acc_no",bnkList.get(pos).getAcc_no());
        params.put("h_name",bnkList.get(pos).getH_name());
        params.put("date",module.getCurrentDate());
        params.put("time",module.getCurrentTime());



        DatabaseReference dref = FirebaseDatabase.getInstance().getReference();
        dref.child("withdraw_request").child(uniqueid).updateChildren(params).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                {
                    loadingBar.dismiss();

                    String updatedRewards=String.valueOf(Integer.parseInt(session_management.getUserDetails().get(KEY_REWARDS))-(Integer.parseInt(amount)));
                    session_management.updateRewards(updatedRewards);

                    new ToastMsg(WithdrawRequest.this).toastIconSuccess("Withdraw Request Submitted");
                    et_amount.setText("");
                    module.updateDbRewards(user_id,updatedRewards);

                }
                else
                {
                    loadingBar.dismiss();
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                loadingBar.dismiss();
                new ToastMsg(WithdrawRequest.this).toastIconError(e.getMessage());
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        getAllPaymentMethods();
    }

    public void getAllPaymentMethods()
    {
        loadingBar.show();
        bankList.clear();
        strList.clear();
        user_id=session_management.getUserDetails().get(KEY_ID);
        payRef.child(user_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                loadingBar.dismiss();
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0)
                {
                    for(DataSnapshot snap:dataSnapshot.getChildren())
                    {
                        BankDetailsModel model=snap.getValue(BankDetailsModel.class);
                        bankList.add(model);
                        if(model.getType().equalsIgnoreCase("upi"))
                        {
                            strList.add("UPI-"+model.getUpi());
                        }
                        else
                        {
                            strList.add("BANK-"+model.getAcc_no());
                        }

                    }

                    if(bankList.size()>0)
                    {
                        arrayAdapter=new ArrayAdapter<String>(ctx,android.R.layout.simple_spinner_dropdown_item,strList);
                        spin_pay.setAdapter(arrayAdapter);
                    }
                    else
                    {
                        toastMsg.toastIconSuccess("First Add Some Payment Method");
                        Intent intent = new Intent(ctx, EditProfileActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("user_type","user");
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }

                }
                else
                {
                    toastMsg.toastIconSuccess("First Add Some Payment Method");
                    Intent intent = new Intent(ctx, EditProfileActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("user_type","user");
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
     loadingBar.dismiss();
     module.showToast(""+databaseError.getMessage().toString());
            }
        });
    }
    public int getWithdrawPosition(ArrayList<String> list,String str)
    {
        int ps=0;
        for(int i=0;i<list.size();i++)
        {
            if(list.get(i).equalsIgnoreCase(str.toString()))
            {
                ps=i;
                break;
            }
        }
        return ps;
    }

}

