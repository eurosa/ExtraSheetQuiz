package com.upsun.quizz.admin.activities;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.upsun.quizz.Config.Module;
import com.upsun.quizz.Model.AddProductModel;
import com.upsun.quizz.R;
import com.upsun.quizz.utils.ToastMsg;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;


public class AddProductActivity extends AppCompatActivity implements View.OnClickListener{
    private final String TAG = AddProductActivity.class.getSimpleName();
    ToastMsg toastMsg;
    boolean is_edit=false;
    ProgressDialog loadingBar ;
    Activity ctx=AddProductActivity.this;
    ImageView iv_back,iv_selectedImage;
    RelativeLayout rl_select,rl_image;
    EditText et_title,et_reward,et_detail;
    Button btn_add_product;
    DatabaseReference dbRef;
    Module module;
    ArrayList<AddProductModel> list;
    Uri postImageUri=null;
    String IMAGE_URL="";
    private byte[] postImageData;
    StorageReference storageRef;
    String ePID,ePImg,ePName,ePStatus,eReward,eDetail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        initViews();

    }

    private void initViews() {
        loadingBar=new ProgressDialog(ctx);
        loadingBar.setMessage("Loading...");
        loadingBar.setCanceledOnTouchOutside(false);
        toastMsg = new ToastMsg(ctx);
        iv_back=findViewById(R.id.iv_back);
        iv_selectedImage=findViewById(R.id.iv_selectedImage);
        rl_select=findViewById(R.id.rl_select);
        rl_image=findViewById(R.id.rl_image);
        et_title=findViewById(R.id.et_title);
        et_reward=findViewById(R.id.et_reward);
        et_detail=findViewById(R.id.et_detail);
        btn_add_product=findViewById(R.id.btn_add_product);
        dbRef=FirebaseDatabase.getInstance().getReference();
        storageRef=FirebaseStorage.getInstance().getReference();
        btn_add_product.setOnClickListener(this);
        list=new ArrayList<>();
        module=new Module(ctx);
        is_edit= getIntent().getStringExtra("is_edit").equalsIgnoreCase("true");
        if(is_edit){
            ePID=getIntent().getStringExtra("p_id");
            ePName=getIntent().getStringExtra("p_name");
            ePStatus=getIntent().getStringExtra("p_status");
            ePImg=getIntent().getStringExtra("p_img");
            eReward=getIntent().getStringExtra("p_reward");
            eDetail=getIntent().getStringExtra("p_detail");
            et_title.setText(ePName);
            et_reward.setText(eReward);
            et_detail.setText(eDetail);
            btn_add_product.setText(R.string.update_reward);
            IMAGE_URL=ePImg;
            if(ePImg.isEmpty()){

            }else{
                rl_image.setVisibility(View.GONE);
                iv_selectedImage.setVisibility(View.VISIBLE);
                Glide.with(ctx)
                        .load(ePImg)
                        .into(iv_selectedImage);
            }
        }
        rl_select.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:
                finish();
                break;
            case R.id.btn_add_product:
                if(et_title.getText().toString().isEmpty()){
                    toastMsg.toastIconError("Enter Product Title");
                    et_title.requestFocus();
                }else if(et_reward.getText().toString().isEmpty()){
                    toastMsg.toastIconError("Enter Reward");
                    et_reward.requestFocus();
                }else if(et_detail.getText().toString().isEmpty()){
                    toastMsg.toastIconError("Enter Detail");
                    et_detail.requestFocus();
                }
                else if((!is_edit)&&postImageData==null){
                    toastMsg.toastIconError("Select A Image");
                }
                else{
                    String p="";
//                    String parent= list.get(spin_cate.getSelectedItemPosition()).getP_name();
//                    if(parent.equalsIgnoreCase("No Parent")){
//                        p="0";
//                    }else{
//                        p=list.get(spin_cate.getSelectedItemPosition()).getP_id();
//                    }
                    String id="";
                    if(is_edit){
                        id=ePID;

                    }else {
                        id=getUniqueId();
                    }
                    addProduct(id,et_title.getText().toString(),et_reward.getText().toString(),et_detail.getText().toString(),IMAGE_URL);
//
                }
                break;
            case R.id.rl_select:
                selectImageFromGallery();
                break;
        }

    }

    private void addProduct(String id,String title,String reward,String detail,String img_url) {
        loadingBar.show();


        HashMap<String,Object> params=new HashMap<>();
        params.put("p_id",id);
        params.put("p_name",title);
        params.put("p_status","0");
        params.put("p_reward",reward);
        params.put("p_detail",detail);
        params.put("p_img",img_url);
        dbRef.child("products").child(id).updateChildren(params).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    loadingBar.dismiss();
                    if(is_edit){
                        module.showToast("Product Updated..");
                    }else{
                        module.showToast("Product Added..");
                    }
                    finish();

                }else{
                    loadingBar.dismiss();
                    module.showToast(""+task.getException().getMessage().toString());
                }
            }
        });
    }

    public String getUniqueId()
    {
        String ss = "product";
        String unique_id = "";
        Date date=new Date();
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("ddMMyyyyhhmmss");
        unique_id = ss +simpleDateFormat.format(date).toString();
        return unique_id ;
    }
    private void selectImageFromGallery() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            getImageIntent();
        }else{
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
    }

    //image from internal storage
    private void getImageIntent(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 86);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            getImageIntent();
        }else {
            toastMsg.toastIconError("Please provide permission...");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 86 && resultCode == RESULT_OK && data != null){
            postImageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(AddProductActivity.this.getContentResolver(), postImageUri);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 25, baos);
                rl_image.setVisibility(View.GONE);
                iv_selectedImage.setVisibility(View.VISIBLE);
                iv_selectedImage.setImageBitmap(bitmap);
                postImageData = baos.toByteArray();
                uploadImage();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }else{
            toastMsg.toastIconError("Please select image...");

        }
    }

    private void uploadImage(){
        loadingBar.show();
        final String imageName=System.currentTimeMillis()+"";
        storageRef.child("Products").child(imageName).putBytes(postImageData)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        storageRef.child("Products").child(imageName).getDownloadUrl()
                                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        Uri downloadUrl = uri;
                                        IMAGE_URL = downloadUrl.toString();
                                        loadingBar.dismiss();
                                    }
                                });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        loadingBar.dismiss();
                        Toast.makeText(AddProductActivity.this, "Server Busy .... Not able to update post at this moment, please try again later...", Toast.LENGTH_SHORT).show();

                    }
                });
    }



    public int getIndex(ArrayList<AddProductModel> list,String str){
        int idnx=0;
        for(int i=0; i<list.size();i++){
            if(list.get(i).getP_id().toString().equalsIgnoreCase(str)){
                idnx=i;
                break;
            }
        }
        Log.e(TAG, "getIndex: "+idnx );
        return idnx;
    }

}
