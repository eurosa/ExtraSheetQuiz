package com.upsun.quizz;

import static com.upsun.quizz.Config.Constants.KEY_EMAIL;
import static com.upsun.quizz.Config.Constants.KEY_ID;
import static com.upsun.quizz.Config.Constants.KEY_MOBILE;
import static com.upsun.quizz.Config.Constants.KEY_NAME;
import static com.upsun.quizz.Config.Constants.KEY_WALLET;
import static com.upsun.quizz.Config.Constants.TRANS_REF;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.payumoney.core.PayUmoneySdkInitializer;
import com.payumoney.core.entity.TransactionResponse;
import com.payumoney.sdkui.ui.utils.PayUmoneyFlowManager;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;
import com.upsun.quizz.Config.Module;
import com.upsun.quizz.networkconnectivity.NetworkConnection;
import com.upsun.quizz.payment.ServiceWrapper;
import com.upsun.quizz.utils.SessionManagment;
import com.upsun.quizz.utils.ToastMsg;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentActivity extends AppCompatActivity implements View.OnClickListener, PaymentResultListener {
    ImageView iv_back;
    TextView tv_title,tv_amt,tv_points;
    ProgressDialog loadingBar;
    ToastMsg toastMsg;
    SessionManagment sessionManagment;
    Module module;
    Button btn_pay;
    String amt="",points="";
    DatabaseReference trans_ref;
    PayUmoneySdkInitializer.PaymentParam.Builder builder = new PayUmoneySdkInitializer.PaymentParam.Builder();
    //declare paymentParam object
    PayUmoneySdkInitializer.PaymentParam paymentParam = null;

    String TAG ="mainActivity", txnid =getUniqueId("txn"), amount ="20", phone ="8081031624",
            prodname ="Brain King Quiz", firstname ="anas", email ="asnfg@gmail.com",
            merchantId ="7034003", merchantkey="I2T6TO8Y";

    private Checkout checkout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        initViews();
        checkout = new Checkout();
        Checkout.preload(getApplicationContext());
    }

    private void initViews() {
        module=new Module(this);
        sessionManagment=new SessionManagment(this);
        loadingBar=new ProgressDialog(this);
        loadingBar.setMessage("Loading...");
        loadingBar.setCanceledOnTouchOutside(false);
        toastMsg=new ToastMsg(this);
        iv_back=(ImageView)findViewById(R.id.iv_back);
        tv_title=(TextView) findViewById(R.id.tv_title);
        tv_amt=(TextView) findViewById(R.id.tv_amt);
        tv_points=(TextView) findViewById(R.id.tv_points);
        btn_pay=(Button)findViewById(R.id.btn_pay);
        btn_pay.setOnClickListener(this);
        iv_back.setOnClickListener(this);
        tv_title.setText("Pay Now");
        amt=getIntent().getStringExtra("amt");
        points=getIntent().getStringExtra("points");
        tv_amt.setText(amt);
        tv_points.setText(points);
        trans_ref= FirebaseDatabase.getInstance().getReference().child(TRANS_REF);
      }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.iv_back)
        {
            finish();
        }
        else if(v.getId() == R.id.btn_pay)
        {
            if(NetworkConnection.connectionChecking(this))
            {



              txnid=module.getUniqueId("txn");
              phone=sessionManagment.getUserDetails().get(KEY_MOBILE);
              firstname=sessionManagment.getUserDetails().get(KEY_NAME);
              email=sessionManagment.getUserDetails().get(KEY_EMAIL);
              amount=tv_amt.getText().toString();

              Log.e("sdadasd",""+txnid+" - "+phone+" - "+firstname+" - "+email+" - "+amt);



              startpay();
//                addPaymentGateway(amt,points,sessionManagment.getUserDetails().get(KEY_ID));
            }
            else
            {
                module.noConnectionActivity();
            }
        }
    }

    private void startpay() {

        OkHttpClient httpClient = new OkHttpClient();

        Request request;

        request = new Request.Builder()
                .url("https://projects.smdevelopment.in/others/upsun/payments.php")
                .post(
                    new FormBody.Builder()
                            .add("amount", String.valueOf(Integer.parseInt(amount)*100))
                            .build()
                )
                .build();

        httpClient.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(@NotNull okhttp3.Call call, @NotNull IOException e) {
                runOnUiThread(() -> {
                    startPayment(null, e.getMessage());
                    e.printStackTrace();
                });
            }

            @Override
            public void onResponse(@NotNull okhttp3.Call call, @NotNull okhttp3.Response response) throws IOException {
                if (response.isSuccessful()){
                    String body = response.body().string();
                    try {
                        JSONObject jsonObject = new JSONObject(body);
                        runOnUiThread(() -> {
                            try {
                                if (jsonObject.getBoolean("status")){
                                    checkout.setKeyID(jsonObject.getString("api_key"));
                                    startPayment(jsonObject.getString("paymentId"), null);
                                }else{
                                    startPayment(null, jsonObject.getString("message"));
                                }

                            } catch (JSONException e) {
                                startPayment(null, e.getMessage());
                                e.printStackTrace();
                            }
                        });
                    } catch (JSONException e) {
                        runOnUiThread(() -> {
                            startPayment(null, e.getMessage());
                            e.printStackTrace();
                        });
                    }
                }else {
                    runOnUiThread(() -> {
                        startPayment(null, response.message() + "22");
                        Log.e(TAG, "onResponse: "+response);
                    });
                }
            }
        });



        /*



        builder.setAmount(amount)                          // Payment amount
                .setTxnId(txnid)                     // Transaction ID
                .setPhone(phone)                   // User Phone number
                .setProductName(prodname)                   // Product Name or description
                .setFirstName(firstname)                              // User First name
                .setEmail(email)              // User Email ID
                .setsUrl("https://www.payumoney.com/mobileapp/payumoney/success.php")     // Success URL (surl)
                .setfUrl("https://www.payumoney.com/mobileapp/payumoney/failure.php")     //Failure URL (furl)
                .setUdf1("")
                .setUdf2("")
                .setUdf3("")
                .setUdf4("")
                .setUdf5("")
                .setUdf6("")
                .setUdf7("")
                .setUdf8("")
                .setUdf9("")
                .setUdf10("")
                .setIsDebug(false)                              // Integration environment - true (Debug)/ false(Production)
                .setKey(merchantkey)                        // Merchant key
                .setMerchantId(merchantId);
        try {
            paymentParam = builder.build();
            // generateHashFromServer(paymentParam );
            getHashkey();

        } catch (Exception e) {
            Log.e(TAG, " error s "+e.toString());
        }*/

    }

    private void startPayment(String paymentId, String error) {
        if (paymentId != null){
            try {
                JSONObject options = new JSONObject();
                options.put("name", getString(R.string.app_name));
                options.put("description",getString(R.string.app_name));

                options.put("currency","INR");
                options.put("order_id", paymentId); //txnid
                options.put("amount", amount);


                JSONObject prefill = new JSONObject();
                prefill.put("email", email);
                prefill.put("contact", phone);

                options.put("prefill", prefill);
                checkout.open(this, options);
            }catch (Exception e){
                e.printStackTrace();
            }
        }else {
            new AlertDialog.Builder(this)
                    .setTitle("Opsss!")
                    .setMessage(error)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
        }

    }

    public void getHashkey(){
        ServiceWrapper service = new ServiceWrapper(null);
        Call<String> call = service.newHashCall(merchantkey, txnid, amount, prodname,
                firstname, email);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                Log.e(TAG, "hash res "+response.body());
                String merchantHash= response.body();
                if (merchantHash.isEmpty() || merchantHash.equals("")) {
                    Toast.makeText(PaymentActivity.this, "Could not generate hash", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "hash empty");
                } else {
                    // mPaymentParams.setMerchantHash(merchantHash);
                    paymentParam.setMerchantHash(merchantHash);
                    // Invoke the following function to open the checkout page.
                    // PayUmoneyFlowManager.startPayUMoneyFlow(paymentParam, StartPaymentActivity.this,-1, true);
                    PayUmoneyFlowManager.startPayUMoneyFlow(paymentParam, PaymentActivity.this, R.style.AppTheme_Green, false);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e(TAG, "hash error "+ t.toString());
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
// PayUMoneySdk: Success -- payuResponse{"id":225642,"mode":"CC","status":"success","unmappedstatus":"captured","key":"9yrcMzso","txnid":"223013","transaction_fee":"20.00","amount":"20.00","cardCategory":"domestic","discount":"0.00","addedon":"2018-12-31 09:09:43","productinfo":"a2z shop","firstname":"kamal","email":"kamal.bunkar07@gmail.com","phone":"9144040888","hash":"b22172fcc0ab6dbc0a52925ebbd0297cca6793328a8dd1e61ef510b9545d9c851600fdbdc985960f803412c49e4faa56968b3e70c67fe62eaed7cecacdfdb5b3","field1":"562178","field2":"823386","field3":"2061","field4":"MC","field5":"167227964249","field6":"00","field7":"0","field8":"3DS","field9":" Verification of Secure Hash Failed: E700 -- Approved -- Transaction Successful -- Unable to be determined--E000","payment_source":"payu","PG_TYPE":"AXISPG","bank_ref_no":"562178","ibibo_code":"VISA","error_code":"E000","Error_Message":"No Error","name_on_card":"payu","card_no":"401200XXXXXX1112","is_seamless":1,"surl":"https://www.payumoney.com/sandbox/payment/postBackParam.do","furl":"https://www.payumoney.com/sandbox/payment/postBackParam.do"}
//PayUMoneySdk: Success -- merchantResponse438104
// on successfull txn
        //  request code 10000 resultcode -1
        //tran {"status":0,"message":"payment status for :438104","result":{"postBackParamId":292490,"mihpayid":"225642","paymentId":438104,"mode":"CC","status":"success","unmappedstatus":"captured","key":"9yrcMzso","txnid":"txt12345","amount":"20.00","additionalCharges":"","addedon":"2018-12-31 09:09:43","createdOn":1546227592000,"productinfo":"a2z shop","firstname":"kamal","lastname":"","address1":"","address2":"","city":"","state":"","country":"","zipcode":"","email":"kamal.bunkar07@gmail.com","phone":"9144040888","udf1":"","udf2":"","udf3":"","udf4":"","udf5":"","udf6":"","udf7":"","udf8":"","udf9":"","udf10":"","hash":"0e285d3a1166a1c51b72670ecfc8569645b133611988ad0b9c03df4bf73e6adcca799a3844cd279e934fed7325abc6c7b45b9c57bb15047eb9607fff41b5960e","field1":"562178","field2":"823386","field3":"2061","field4":"MC","field5":"167227964249","field6":"00","field7":"0","field8":"3DS","field9":" Verification of Secure Hash Failed: E700 -- Approved -- Transaction Successful -- Unable to be determined--E000","bank_ref_num":"562178","bankcode":"VISA","error":"E000","error_Message":"No Error","cardToken":"","offer_key":"","offer_type":"","offer_availed":"","pg_ref_no":"","offer_failure_reason":"","name_on_card":"payu","cardnum":"401200XXXXXX1112","cardhash":"This field is no longer supported in postback params.","card_type":"","card_merchant_param":null,"version":"","postUrl":"https:\/\/www.payumoney.com\/mobileapp\/payumoney\/success.php","calledStatus":false,"additional_param":"","amount_split":"{\"PAYU\":\"20.0\"}","discount":"0.00","net_amount_debit":"20","fetchAPI":null,"paisa_mecode":"","meCode":"{\"vpc_Merchant\":\"TESTIBIBOWEB\"}","payuMoneyId":"438104","encryptedPaymentId":null,"id":null,"surl":null,"furl":null,"baseUrl":null,"retryCount":0,"merchantid":null,"payment_source":null,"pg_TYPE":"AXISPG"},"errorCode":null,"responseCode":null}---438104

        // Result Code is -1 send from Payumoney activity
        Log.e("StartPaymentActivity", "request code " + requestCode + " resultcode " + resultCode);
        if (requestCode == PayUmoneyFlowManager.REQUEST_CODE_PAYMENT && resultCode == RESULT_OK && data != null) {
            TransactionResponse transactionResponse = data.getParcelableExtra( PayUmoneyFlowManager.INTENT_EXTRA_TRANSACTION_RESPONSE );

            if (transactionResponse != null && transactionResponse.getPayuResponse() != null) {

                if(transactionResponse.getTransactionStatus().equals( TransactionResponse.TransactionStatus.SUCCESSFUL )){

                   addTranscation(tv_points.getText().toString(),tv_amt.getText().toString(),sessionManagment.getUserDetails().get(KEY_ID),txnid,"success");
                    //Success Transaction
                } else{
                    //Failure Transaction
                    toastMsg.toastIconError("Transaction Failed. Try again later");
                }

                // Response from Payumoney
                String payuResponse = transactionResponse.getPayuResponse();

                // Response from SURl and FURL
                String merchantResponse = transactionResponse.getTransactionDetails();
                Log.e(TAG, "tran "+payuResponse+"---"+ merchantResponse);
            } /* else if (resultModel != null && resultModel.getError() != null) {
                Log.d(TAG, "Error response : " + resultModel.getError().getTransactionResponse());
            } else {
                Log.d(TAG, "Both objects are null!");
            }*/
        }
    }

    private void addTranscation(final String points, String amt, final String user_id, String txnid, final String status) {
        loadingBar.show();
        HashMap<String,Object> map=new HashMap<>();
        map.put("txn_id",txnid);
        map.put("user_id",user_id);
        map.put("amount",amt);
        map.put("points",points);
        map.put("status",status);
        map.put("date",module.getCurrentDate());
        map.put("time",module.getCurrentTime());
        trans_ref.child(txnid).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                loadingBar.dismiss();
                if(task.isSuccessful())
                {
                        toastMsg.toastIconSuccess("Points Added to your wallet");
                        String updatedWallet=String.valueOf(Integer.parseInt(sessionManagment.getUserDetails().get(KEY_WALLET))+Integer.parseInt(points));
                        sessionManagment.updateWallet(updatedWallet);
                        module.updateDbWallet(user_id,updatedWallet);

                        Intent intent=new Intent(PaymentActivity.this,MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();

                }
                else
                {
                    module.showToast(""+task.getException().getMessage().toString());
                }

            }
        });
    }

    public String getUniqueId(String type)
    {
        Date date=new Date();
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("ddMMyyyyhhmmss");
        return (type+simpleDateFormat.format(date).toString());
    }











    @Override
    public void onPaymentSuccess(String s) {
        addTranscation(tv_points.getText().toString(),tv_amt.getText().toString(),sessionManagment.getUserDetails().get(KEY_ID),txnid,"success");
    }

    @Override
    public void onPaymentError(int i, String s) {
        toastMsg.toastIconError("Transaction Failed. Try again later");
    }

    /*override fun onPaymentSuccess(razorpayPaymentID: String?) {

        superManager.paymentStatusChange(paymentId!!){
            if (it.getBoolean("status")){
                val superr = Gson().fromJson(it.getJSONObject("super").toString(), Super::class.java)
                if (superr.status == "Active"){

                    val intent = Intent(activity, SuperPurchasedDoneActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)

                }else {
                    binding.failedLayout.visibility = View.VISIBLE
                    binding.retryBt.visibility = View.VISIBLE
                    binding.progressBar.visibility = View.GONE
                }
            }else{
                binding.failedLayout.visibility = View.VISIBLE
                binding.retryBt.visibility = View.VISIBLE
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    override fun onPaymentError(code: Int, response: String?) {
        superManager.paymentStatusChange(paymentId!!){
            if (it.getBoolean("status")){

                val superr = Gson().fromJson(it.getJSONObject("super").toString(), Super::class.java)
                if (superr.status == "Active"){

                    val intent = Intent(activity, SuperPurchasedDoneActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)

                }
            }
        }

        binding.failedLayout.visibility = View.VISIBLE
        binding.retryBt.visibility = View.VISIBLE
        binding.progressBar.visibility = View.GONE
    }*/
}
