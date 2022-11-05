package com.upsun.quizz.admin.fragments;

import static com.upsun.quizz.AdminLoginActivity.ad_image;
import static com.upsun.quizz.AdminLoginActivity.ad_mobile;
import static com.upsun.quizz.AdminLoginActivity.ad_name;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.upsun.quizz.EditProfileActivity;
import com.upsun.quizz.LoginActivty;
import com.upsun.quizz.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AdminProfileFragment extends Fragment implements View.OnClickListener {
    TextView txt_name ,txt_mobile ,txt_edit,txt_initials;
    CardView card_edit ,card_switch ;
    ImageView user_img  ,iv_back;

    public AdminProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
     View view = inflater.inflate(R.layout.fragment_admin_profile, container, false);
        card_edit = view.findViewById(R.id.card_edi);
        card_switch = view.findViewById(R.id.card_account);
        user_img = view.findViewById(R.id.user_img);
        txt_name = view.findViewById(R.id.txt_name);
        txt_mobile = view.findViewById(R.id.txt_mobile);
        txt_edit = view.findViewById(R.id.txt_edit_profile);
        txt_initials = view.findViewById(R.id.initials);
        iv_back = view.findViewById(R.id.iv_back);
   ad_name="Admin";
        txt_name.setText(ad_name);
        txt_mobile.setText(ad_mobile);
        String profile = ad_image;
//        new ToastMsg(getActivity()).toastIconSuccess("profile"+profile);
//        if ( ad_image.isEmpty()|| ad_image.equals("null"))
//        {

            txt_initials.setVisibility(View.VISIBLE);
            txt_initials.setText(ad_name.toUpperCase().subSequence(0,2));
//        }
//        else {
//            Glide.with(getActivity())
//                    .load(ad_image)
//                    .diskCacheStrategy(DiskCacheStrategy.ALL)
//                    .placeholder(R.drawable.logo)
//                    .into(user_img);
//        }
        card_switch.setOnClickListener(this);
        card_edit.setOnClickListener(this);
        iv_back.setOnClickListener(this);
     return  view ;

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.card_account)
        {
            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
            dialog.setTitle("Switch Account");
            dialog.setMessage("You will be logged out as Admin. Would you like to continue ? ");
            dialog.setCancelable(false);
            dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(getActivity() , LoginActivty.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    dialog.cancel();

                }
            });
            dialog.show();

        }
        else if (id == R.id.card_edi)
        {
            Intent intent = new Intent(getContext(), EditProfileActivity.class);
            Bundle bundle = new Bundle();
           bundle.putString("user_type","admin");
            intent.putExtras(bundle);
            startActivity(intent);
        }
        else if (id == R.id.iv_back)
        {
            getActivity().getSupportFragmentManager().popBackStackImmediate();
        }
    }

}
