package com.upsun.quizz;

import static com.upsun.quizz.Config.Constants.ADMIN_REF;
import static com.upsun.quizz.Config.Constants.KEY_EMAIL;
import static com.upsun.quizz.Config.Constants.KEY_ID;
import static com.upsun.quizz.Config.Constants.KEY_MOBILE;
import static com.upsun.quizz.Config.Constants.KEY_NAME;
import static com.upsun.quizz.Config.Constants.KEY_PROFILE_IMG;
import static com.upsun.quizz.Config.Constants.PMETHOD_REF;
import static com.upsun.quizz.Config.Constants.PROFILE_REF;
import static com.upsun.quizz.Config.Constants.USER_REF;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.upsun.quizz.Adapter.BankDetilsAdapter;
import com.upsun.quizz.Config.Module;
import com.upsun.quizz.Model.BankDetailsModel;
import com.upsun.quizz.utils.SessionManagment;
import com.upsun.quizz.utils.ToastMsg;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener {

    HashMap<String,Object> map=null;
    ImageView iv_back;
    RelativeLayout rel_no_items;
    Button btn_add_acc;
    RecyclerView rv_acc;
    ArrayList<BankDetailsModel> list;
    BankDetilsAdapter adapter;
    String downloadImageUrl="",new_url="" ,type;
    TextView tv_title;
    CircleImageView iv_profile;
    EditText et_name,et_mobile,et_email;
    Button btn_update;
    ProgressDialog loadingBar;
    ToastMsg toastMsg;
    Module module;
    Activity ctx= EditProfileActivity.this;
    boolean flag=false;
    SessionManagment sessionManagment;
    private static final int GALLERY_REQUEST_CODE = 1;
    private Uri imageUri;
    StorageReference profile_ref;
    DatabaseReference reference,payRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        initViews();
    }

    private void initViews() {
        loadingBar=new ProgressDialog(ctx);
        loadingBar.setMessage("Loading...");
        loadingBar.setCanceledOnTouchOutside(false);
        toastMsg=new ToastMsg(ctx);
        module=new Module(ctx);
        sessionManagment=new SessionManagment(ctx);
        profile_ref= FirebaseStorage.getInstance().getReference().child(PROFILE_REF);
        payRef= FirebaseDatabase.getInstance().getReference().child(PMETHOD_REF);

        iv_back=(ImageView)findViewById(R.id.iv_back);
        tv_title=(TextView)findViewById(R.id.tv_title);
        et_name=(EditText)findViewById(R.id.et_name);
        et_mobile=(EditText)findViewById(R.id.et_mobile);
        et_email=(EditText)findViewById(R.id.et_email);
        iv_profile=(CircleImageView)findViewById(R.id.iv_profile);
        btn_update=(Button) findViewById(R.id.btn_update);
        btn_add_acc=(Button) findViewById(R.id.btn_add_acc);
        rv_acc=(RecyclerView)findViewById(R.id.rv_acc);
        rel_no_items=(RelativeLayout)findViewById(R.id.rel_no_items);
        list=new ArrayList<>();
        btn_add_acc.setOnClickListener(this);
        iv_back.setOnClickListener(this);
        iv_profile.setOnClickListener(this);
        btn_update.setOnClickListener(this);
        Bundle extras = this.getIntent().getExtras();
      type = extras.getString("user_type");
//      toastMsg.toastIconError("type"+ type);

        if (type.equals("admin"))
        {
          reference= FirebaseDatabase.getInstance().getReference().child(ADMIN_REF);
            et_email.setText(AdminLoginActivity.ad_email);
            et_mobile.setText(AdminLoginActivity.ad_mobile);
            et_name.setText(AdminLoginActivity.ad_name);
            String profileUrl=AdminLoginActivity.ad_image;
            tv_title.setText("My Profile");
            if(!profileUrl.isEmpty())
            {
                Glide.with(ctx)
                        .load(profileUrl)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
//                        .centerCrop()
                        .placeholder(R.drawable.logo)
                        .into(iv_profile);
            }
        }
        else if (type.equals("user"))
        {
            reference= FirebaseDatabase.getInstance().getReference().child(USER_REF);
            et_email.setText(sessionManagment.getUserDetails().get(KEY_EMAIL));
            et_mobile.setText(sessionManagment.getUserDetails().get(KEY_MOBILE));
            et_name.setText(sessionManagment.getUserDetails().get(KEY_NAME));
            String profileUrl=sessionManagment.getUserDetails().get(KEY_PROFILE_IMG);
            tv_title.setText("My Profile");
            if(!profileUrl.isEmpty())
            {

                Glide.with(ctx)
                        .load(profileUrl)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .centerCrop()
                        .placeholder(R.drawable.logo)
                        .into(iv_profile);
            }
        }

    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.iv_back)
        {
            finish();
        }

        else if(v.getId() == R.id.iv_profile)
        {
            openGallery();
        }
        else if(v.getId() == R.id.btn_add_acc)
        {
            Intent intent=new Intent(EditProfileActivity.this,AddBankDetailsActivity.class);
            intent.putExtra("is_edit","false");
            startActivity(intent);
        }
        else if(v.getId() == R.id.btn_update)
        {

            final String name=et_name.getText().toString();
            final String email=et_email.getText().toString();
            String mobile=et_mobile.getText().toString();
            String id=sessionManagment.getUserDetails().get(KEY_ID);

            if(name.isEmpty())
            {
                et_name.setError(getResources().getString(R.string.req_name));
                et_name.requestFocus();
            } else if(email.isEmpty())
            {
                et_email.setError(getResources().getString(R.string.req_email));
                et_email.requestFocus();
            }else if(mobile.isEmpty())
            {
                et_mobile.setError(getResources().getString(R.string.req_mobile));
                et_mobile.requestFocus();
            }
            else
            {
                loadingBar.show();
              map=new HashMap<>();
              map.put("name",name);
              map.put("email",email);
              map.put("mobile",mobile);
             if(flag)
             {
                 map.put("img_url",new_url);
                 if(new_url.isEmpty())
                 {
                     loadingBar.dismiss();
                     toastMsg.toastIconError("Please Select a image");
                 }
                 else
                 {
                     reference.child(id).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                         @Override
                         public void onComplete(@NonNull Task<Void> task) {
                             if(task.isSuccessful())
                             {
                                 loadingBar.dismiss();
                                 toastMsg.toastIconSuccess("Profile Updated Successfully..");
                                 sessionManagment.updateProfile(name,email,new_url);
                             }
                             else
                             {
                                 loadingBar.dismiss();
                                 Toast.makeText(ctx,""+task.getException().getMessage(),Toast.LENGTH_LONG).show();
                             }

                         }
                     });
                 }
             }
             else
             {
                 map.put("img_url","");
                 reference.child(id).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            loadingBar.dismiss();
                            toastMsg.toastIconSuccess("Profile Updated Successfully..");
                            String url=sessionManagment.getUserDetails().get(KEY_PROFILE_IMG);
                            sessionManagment.updateProfile(name,email,url);
                        }
                        else
                        {
                            loadingBar.dismiss();
                            Toast.makeText(ctx,""+task.getException().getMessage(),Toast.LENGTH_LONG).show();
                        }
                    }
                });
             }
         }
        }
    }

    private void openGallery() {

        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto , GALLERY_REQUEST_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_REQUEST_CODE && resultCode ==RESULT_OK && data!=null)
        {
            Uri selectedImage = data.getData();
            iv_profile.setImageURI(selectedImage);
            imageUri = selectedImage;
            flag=true;

           getDownloadImageUri();

        }
        else
        {
            toastMsg.toastIconError("Please Select any image");
        }
    }

    public String getDownloadImageUri()
    {
        loadingBar.show();
        final StorageReference filepath=profile_ref.child(imageUri.getLastPathSegment()+module.getUniqueId("pc")+".jpg");
        final UploadTask uploadTask=filepath.putFile(imageUri);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                loadingBar.dismiss();
                Toast.makeText(ctx,""+e.getMessage(),Toast.LENGTH_LONG).show();
            }
        })
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Task<Uri> uriTask=uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                if(!task.isSuccessful())
                                {
                                    loadingBar.dismiss();
                                    throw task.getException();
                                }
                                return filepath.getDownloadUrl();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {

                                if(task.isSuccessful())
                                {
                                    loadingBar.dismiss();
                                    downloadImageUrl=task.getResult().toString();
                                   new_url=downloadImageUrl;
                                    toastMsg.toastIconSuccess("Profile Pic uploaded");
                                }
                                else
                                {
                                    loadingBar.dismiss();
                                    Toast.makeText(ctx,""+task.getException().getMessage(),Toast.LENGTH_LONG).show();
                                }

                            }
                        });
                    }
                });

       // Toast.makeText(ctx,""+downloadImageUrl.toString(),Toast.LENGTH_LONG).show();
        return downloadImageUrl;
    }

    @Override
    protected void onStart() {
        super.onStart();
        getAllPaymentMethods();
    }

    public void getAllPaymentMethods()
    {
        list.clear();
        loadingBar.show();
        String user_id=sessionManagment.getUserDetails().get(KEY_ID);
        payRef.child(user_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                loadingBar.dismiss();
                if(dataSnapshot.getChildrenCount()>0 && dataSnapshot.exists())
                {
                    for(DataSnapshot snap:dataSnapshot.getChildren())
                    {
                        BankDetailsModel model=snap.getValue(BankDetailsModel.class);
                        list.add(model);
                    }
                    if(list.size()>0)
                    {
                        rel_no_items.setVisibility(View.GONE);
                        rv_acc.setVisibility(View.VISIBLE);
                        rv_acc.setLayoutManager(new LinearLayoutManager(EditProfileActivity.this));
                        adapter=new BankDetilsAdapter(EditProfileActivity.this,list);
                        rv_acc.setAdapter(adapter);
                        adapter.notifyDataSetChanged();

                    }
                    else
                    {
                        rel_no_items.setVisibility(View.VISIBLE);
                        rv_acc.setVisibility(View.GONE);

                    }
                }
                else
                {
                    rel_no_items.setVisibility(View.VISIBLE);
                    rv_acc.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
         loadingBar.dismiss();
         module.showToast(""+databaseError.getMessage().toString());
            }
        });

    }

}
