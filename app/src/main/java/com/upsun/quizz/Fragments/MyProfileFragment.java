package com.upsun.quizz.Fragments;

import static com.upsun.quizz.Config.Constants.KEY_MOBILE;
import static com.upsun.quizz.Config.Constants.KEY_NAME;
import static com.upsun.quizz.Config.Constants.KEY_PROFILE_IMG;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.upsun.quizz.Config.Module;
import com.upsun.quizz.EditProfileActivity;
import com.upsun.quizz.R;
import com.upsun.quizz.RewardsActivity;
import com.upsun.quizz.WalletActivity;
import com.upsun.quizz.networkconnectivity.NetworkConnection;
import com.upsun.quizz.utils.SessionManagment;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyProfileFragment extends Fragment implements View.OnClickListener {
    TextView txt_name ,txt_mobile ,txt_edit,txt_initials ;
    CircleImageView user_img ;
    CardView card_wallet, card_rewrds ;
    SessionManagment sessionManagment ;
    String user_type ="user";
    Module module;

    public MyProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();

        txt_name.setText(sessionManagment.getUserDetails().get(KEY_NAME));
        txt_mobile.setText(sessionManagment.getUserDetails().get(KEY_MOBILE));
        String profileUrl=sessionManagment.getUserDetails().get(KEY_PROFILE_IMG);

        if(NetworkConnection.connectionChecking(getActivity()))
        {
            if(!profileUrl.isEmpty())
            {

                Glide.with(getActivity())
                        .load(profileUrl)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .centerCrop()
                        .placeholder(R.drawable.logo)
                        .into(user_img);
            }
            else
            {
                txt_initials.setVisibility(View.VISIBLE);
                txt_initials.setText(sessionManagment.getUserDetails().get(KEY_NAME).toUpperCase().subSequence(0,2));
            }

        }
        else
        {
            module.noConnectionActivity();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
      View view = inflater.inflate(R.layout.fragment_my_profile, container, false);
        card_rewrds = view.findViewById(R.id.card_rewards);
        card_wallet=view.findViewById(R.id.card_wallet);
        user_img = view.findViewById(R.id.user_img);
        txt_name = view.findViewById(R.id.txt_name);
        txt_mobile = view.findViewById(R.id.txt_mobile);
        txt_edit = view.findViewById(R.id.txt_edit_profile);
        txt_initials = view.findViewById(R.id.initials);
        module=new Module(getActivity());
        sessionManagment = new SessionManagment(getActivity());
        txt_name.setText(sessionManagment.getUserDetails().get(KEY_NAME));
        txt_mobile.setText(sessionManagment.getUserDetails().get(KEY_MOBILE));
        String profileUrl=sessionManagment.getUserDetails().get(KEY_PROFILE_IMG);

        if(NetworkConnection.connectionChecking(getActivity()))
        {
            if(!profileUrl.isEmpty())
            {

                Glide.with(getActivity())
                        .load(profileUrl)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .centerCrop()
                        .placeholder(R.drawable.logo)
                        .into(user_img);
            }
            else
            {
                txt_initials.setVisibility(View.VISIBLE);
                txt_initials.setText(sessionManagment.getUserDetails().get(KEY_NAME).toUpperCase().subSequence(0,2));
            }

        }
        else
        {
            module.noConnectionActivity();
        }

        card_wallet.setOnClickListener(this);
        card_rewrds.setOnClickListener(this);
        txt_edit.setOnClickListener(this);
      return  view ;
    }

    @Override
    public void onClick(View v) {
        int id  = v.getId();
        if (id == R.id.card_rewards)
        {
            Intent intent = new Intent(getActivity(), RewardsActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.card_wallet)
        {
            Intent intent = new Intent(getActivity(), WalletActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.txt_edit_profile)
        {

            Intent intent = new Intent(getContext(), EditProfileActivity.class);
            Bundle bundle = new Bundle();
           bundle.putString("user_type","user");
            intent.putExtras(bundle);
            startActivity(intent);
        }

    }
}
