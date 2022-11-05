package com.upsun.quizz.Fragments;

import static com.upsun.quizz.Config.Constants.CONFIG_REF;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.upsun.quizz.AllInfoActivity;
import com.upsun.quizz.Config.Module;
import com.upsun.quizz.ContactUsActivity;
import com.upsun.quizz.Fonts.LatoBLack;
import com.upsun.quizz.LoginActivty;
import com.upsun.quizz.R;
import com.upsun.quizz.WithdrwHistry;
import com.upsun.quizz.utils.SessionManagment;

/**
 * A simple {@link Fragment} subclass.
 */
public class MoreFragment extends Fragment implements View.OnClickListener{

    ProgressDialog loadingBar;
    CardView card_logout ,card_contact,card_terms ,card_privacy,card_withdrw,card_about,card_share;
    TextView txt_version ;

    SessionManagment sessionManagment;
    String share_link="";
    DatabaseReference app_ref;
    Module module;
    Context context;
    public MoreFragment( Context context) {
        // Required empty public constructor
        this.context = context;
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_more, container, false);
        initViews(view);
        getUpdaterInfo();
        return view;
    }

    private void initViews(View view) {
        sessionManagment=new SessionManagment(getActivity());
        card_logout=(CardView)view.findViewById(R.id.card_logout);
        card_contact= view.findViewById(R.id.card_contact);
        card_privacy = view.findViewById(R.id.card_privacy);
        card_terms = view.findViewById(R.id.card_terms);
        card_withdrw = view.findViewById(R.id.card_withdraw);
        card_about = view.findViewById(R.id.card_about);
        app_ref= FirebaseDatabase.getInstance().getReference().child(CONFIG_REF);

        card_share = view.findViewById(R.id.card_share);
        txt_version = view.findViewById(R.id.txt_version);
        card_about.setOnClickListener(this);
        loadingBar=new ProgressDialog(getActivity());
        loadingBar.setMessage("Loading...");
        loadingBar.setCanceledOnTouchOutside(false);
        module=new Module(getActivity());
        card_withdrw.setOnClickListener(this);
        card_terms.setOnClickListener(this);
        card_privacy.setOnClickListener(this);
        card_contact.setOnClickListener(this);
        card_logout.setOnClickListener(this);
        card_share.setOnClickListener(this);

        LatoBLack showReferCode = view.findViewById(R.id.showReferCode);

        showReferCode.setText("Refer and Earn ( refer code - "+new SessionManagment(getContext()).getReferCode() +")");

        view.findViewById(R.id.card_refer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                shareApp(new SessionManagment(getContext()).getReferLink());


            }
        });
    }

    @Override
    public void onClick(View v) {
        int id=v.getId();
        if(id == R.id.card_logout)
        {
            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
            dialog.setTitle("Logout ");
            dialog.setMessage("Sure to Logout ?");
            dialog.setCancelable(false);
            dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    sessionManagment.logoutSession();
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
        else if (id== R.id.card_privacy)
        {  Intent intent=new Intent(getActivity(), AllInfoActivity.class);
        intent.putExtra("type","privacy policy");
            startActivity(intent);
        }
        else if (id== R.id.card_terms)
        {
            Intent intent=new Intent(getActivity(), AllInfoActivity.class);
            intent.putExtra("type","terms & conditions");
            startActivity(intent);
        }
        else if (id== R.id.card_contact)
        {Intent intent=new Intent(getActivity(), ContactUsActivity.class);

            startActivity(intent);}
        else if (id== R.id.card_about)
        {
            Intent intent=new Intent(getActivity(), AllInfoActivity.class);
            intent.putExtra("type","about us");
            startActivity(intent);
        }
        else if (id== R.id.card_withdraw)
        {
            Intent intent=new Intent(getActivity(), WithdrwHistry.class);
               getActivity().startActivity(intent);
        }
        else if(id == R.id.card_share)
        {

        }

    }


//    public void getVersion()
//    {
//        loadingBar.show();
//        HashMap<String,String> params = new HashMap<>();
//        CustomVolleyJsonRequest jsonRequest = new CustomVolleyJsonRequest(Request.Method.POST, BaseURL.GET_VERSION_URL, params, new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//                Log.e("version",response.toString());
//                try {
//                    loadingBar.dismiss();
//                    boolean status = response.getBoolean("responce");
//                    if (status)
//                    {
//                        if (response.has("data"))
//                        {
//                            JSONObject data = response.getJSONObject("data");
//
//                            store_link = data.getString("data");
//                        }
//
//                    }
//                } catch (JSONException e) {
//                    loadingBar.dismiss();
//                    e.printStackTrace();
//                }
//
//            }
//        },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        loadingBar.dismiss();
//                        String msg=module.VolleyError(error);
//                        if(!msg.equals(""))
//                        {
//                            Toast.makeText(getActivity(),""+msg,Toast.LENGTH_SHORT).show();
//                        }
//
//
//                    }
//                });
//        AppController.getInstance().addToRequestQueue(jsonRequest,"get version");
//    }

    public boolean getUpdaterInfo()
    {
        boolean st=false;
        try
        {
            PackageInfo packageInfo=getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(),0);
            String ver_code=packageInfo.versionName;
//            if(ver_code == version_code)
//            {
//                st=true;
//            }
            txt_version.setText("v"+ver_code);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return st;
    }

    public void shareApp(String s) {
        Intent sendIntent = new Intent();
        sendIntent.setAction( Intent.ACTION_SEND);
        sendIntent.putExtra( Intent.EXTRA_TEXT, s);
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }
}
