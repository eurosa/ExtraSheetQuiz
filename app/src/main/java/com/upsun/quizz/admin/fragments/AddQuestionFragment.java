package com.upsun.quizz.admin.fragments;

import static com.upsun.quizz.Config.Constants.QUESTION_REF;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.upsun.quizz.Config.Module;
import com.upsun.quizz.R;
import com.upsun.quizz.networkconnectivity.NetworkConnection;
import com.upsun.quizz.utils.ToastMsg;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddQuestionFragment extends Fragment implements View.OnClickListener {

    Module module;
    TextView tv_ques_no;
    EditText et_ques,et_option_a,et_option_b,et_option_c,et_option_d ,et_h_ques,et_h_option_a,et_h_option_b,et_h_option_c,et_h_option_d;
    Spinner spin_ans,spin_h_ans;
    Button btn_add_ques,btn_view_ques ,btn_edit_ques;
    String cat_id="",child_count = "" ,is_edit="" ,ques ,ans ,o_a ,o_b,o_c,o_d ,q_id,h_ques ,h_ans ,h_o_a ,h_o_b,h_o_c,h_o_d ;
    DatabaseReference ques_ref;
    ProgressDialog loadingBar;
    ArrayList<String> ans_list;
    String q_no="" ,language="english",lang= "";
    ToastMsg toastMsg;
    RadioButton ck_english ,ck_hindi ;
    LinearLayout lin_hindi ,lin_eng ;
//    ImageView iv_back ;
    int count ;

    public AddQuestionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_add_question, container, false);
        initViews(view);
//        getMaxId(cat_id);
        return view;
    }



    private void initViews(View view) {
        tv_ques_no=(TextView)view.findViewById(R.id.tv_ques_no);
        et_ques=(EditText) view.findViewById(R.id.et_ques);
        et_option_a=(EditText) view.findViewById(R.id.et_option_a);
        et_option_b=(EditText) view.findViewById(R.id.et_option_b);
        et_option_c=(EditText) view.findViewById(R.id.et_option_c);
        et_option_d=(EditText) view.findViewById(R.id.et_option_d);
        spin_ans=(Spinner)view.findViewById(R.id.spin_ans);
        module=new Module(getActivity());
        et_h_ques=(EditText) view.findViewById(R.id.et_h_ques);
        et_h_option_a=(EditText) view.findViewById(R.id.et_h_option_a);
        et_h_option_b=(EditText) view.findViewById(R.id.et_h_option_b);
        et_h_option_c=(EditText) view.findViewById(R.id.et_h_option_c);
        et_h_option_d=(EditText) view.findViewById(R.id.et_h_option_d);
        spin_h_ans=(Spinner)view.findViewById(R.id.spin_h_ans);
        btn_add_ques=(Button)view.findViewById(R.id.btn_add_ques);
        btn_view_ques=(Button)view.findViewById(R.id.btn_view_ques);
        btn_edit_ques  = view.findViewById(R.id.btn_edit_ques);
//        iv_back= view.findViewById(R.id.iv_back);
        ck_english = view.findViewById(R.id.english);
        ck_hindi = view.findViewById(R.id.hindi);
        lin_hindi = view.findViewById(R.id.linear_hindi);
        lin_eng = view.findViewById(R.id.linear_english);
        loadingBar=new ProgressDialog(getActivity());
        loadingBar.setMessage("Loading...");
        loadingBar.setCanceledOnTouchOutside(false);
        toastMsg=new ToastMsg(getActivity());
        ans_list=new ArrayList<>();
        ans_list.add("Select Answer");
        ans_list.add("a");
        ans_list.add("b");
        ans_list.add("c");
        ans_list.add("d");
        ArrayAdapter<String> ansAdapter=new ArrayAdapter<>(getActivity(),android.R.layout.simple_spinner_dropdown_item,ans_list);
        spin_ans.setAdapter(ansAdapter);
        spin_h_ans.setAdapter(ansAdapter);
        ques_ref= FirebaseDatabase.getInstance().getReference().child(QUESTION_REF);
        cat_id=getArguments().getString("cat_id");
       is_edit=getArguments().getString("is_edit");
       child_count=getArguments().getString("child_count");
       count = Integer.parseInt(child_count)+1;
       q_no = "QUES"+String.valueOf(count);
       tv_ques_no.setText(String.valueOf(count));

       if (is_edit.equalsIgnoreCase("true"))
       {
           btn_add_ques.setVisibility(View.GONE);
           lang= getArguments().getString("language");
           ques =getArguments().getString("question");
           q_id =getArguments().getString("question_id");
           ans = getArguments().getString("answer");
           o_a = getArguments().getString("option_a");
           o_b = getArguments().getString("option_b");
           o_c = getArguments().getString("option_c");
           o_d = getArguments().getString("option_d");
           h_ques =getArguments().getString("h_question");
           child_count=getArguments().getString("child_count");
          h_ans = getArguments().getString("h_answer");
           h_o_a = getArguments().getString("h_option_a");
          h_o_b = getArguments().getString("h_option_b");
          h_o_c = getArguments().getString("h_option_c");
          h_o_d = getArguments().getString("h_option_d");
          if (lang.equals("hindi") )
          {
              language="hindi";
              lin_hindi.setVisibility(View.VISIBLE);
              lin_eng.setVisibility(View.GONE);
              ck_hindi.setChecked(true);

          }
          else if (lang.equals("english"))
          {
              language="english";
              lin_hindi.setVisibility(View.GONE);
              lin_eng.setVisibility(View.VISIBLE);
              ck_english.setChecked(true);
          }
          else if (lang.equals("english,hindi"))
          {
              lin_hindi.setVisibility(View.VISIBLE);
              lin_eng.setVisibility(View.VISIBLE);
              ck_english.setChecked(true);
              ck_hindi.setChecked(true);
          }
           btn_edit_ques.setVisibility(View.VISIBLE);
           et_ques.setText(ques);
           et_option_a.setText(o_a);
           et_option_b.setText(o_b);
           et_option_c.setText(o_c);
           et_option_d.setText(o_d);
           et_ques.setText(ques);
           et_h_ques.setText(h_ques);
           et_h_option_a.setText(h_o_a);
           et_h_option_b.setText(h_o_b);
           et_h_option_c.setText(h_o_c);
           et_h_option_d.setText(h_o_d);
           count = Integer.parseInt(child_count)+1;
           tv_ques_no.setText(String.valueOf(count));
           switch (ans)
           {
               case "a":spin_ans.setSelection(1);
               break;
               case "b":spin_ans.setSelection(2);
               break;
               case "c":spin_ans.setSelection(3);
               break;
               case "d": spin_ans.setSelection(4);

           }
           switch (h_ans)
           {
               case "a":spin_h_ans.setSelection(1);
                   break;
               case "b":spin_h_ans.setSelection(2);
                   break;
               case "c":spin_h_ans.setSelection(3);
                   break;
               case "d": spin_h_ans.setSelection(4);

           }
       }
       else
       {
           ck_english.setChecked(true);
           btn_edit_ques.setVisibility(View.GONE);
       }
        btn_view_ques.setOnClickListener(this);
        btn_add_ques.setOnClickListener(this);
        btn_edit_ques.setOnClickListener(this);
     ck_hindi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
         @Override
         public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
             language="english";
             if(lin_hindi.getVisibility()==View.VISIBLE)
             {
                 lin_hindi.setVisibility(View.GONE);
             }
             lin_eng.setVisibility(View.VISIBLE);


              }
     });

     ck_english.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
         @Override
         public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

             language="hindi";
             if(lin_eng.getVisibility()==View.VISIBLE)
             {
                 lin_eng.setVisibility(View.GONE);
             }
             lin_hindi.setVisibility(View.VISIBLE);
         }
     });
//        iv_back.setOnClickListener(this);


    }
    private void getMaxId(String cat_id) {
        loadingBar.show();


        ques_ref.child(cat_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                loadingBar.dismiss();
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.getChildrenCount() > 0) {

                        count = (int) dataSnapshot.getChildrenCount();
                        q_no = "QUENO" + String.valueOf(++count);
                        Log.e("q_noe", q_no);
                        tv_ques_no.setText("" + dataSnapshot.getChildrenCount() + 1);
                    } else {
                        q_no = "QUENO" + 1;
                        tv_ques_no.setText("" + 1);
                        Log.e("q_noe", q_no);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                loadingBar.dismiss();
                toastMsg.toastIconError(databaseError.getMessage());

            }
        });

    }

    @Override
    public void onClick(View v) {
        int id =v.getId();
  if(id == R.id.btn_add_ques)
        {
//            Toast.makeText(getActivity(),"language:" +language,Toast.LENGTH_LONG).show();
            String ques = et_ques.getText().toString();
            String opt_a = et_option_a.getText().toString();
            String opt_b = et_option_b.getText().toString();
            String opt_c = et_option_c.getText().toString();
            String opt_d = et_option_d.getText().toString();
            String ans = spin_ans.getSelectedItem().toString();
            String h_ques = et_h_ques.getText().toString();
            String h_opt_a = et_h_option_a.getText().toString();
            String h_opt_b = et_h_option_b.getText().toString();
            String h_opt_c = et_h_option_c.getText().toString();
            String h_opt_d = et_h_option_d.getText().toString();
            String h_ans = spin_h_ans.getSelectedItem().toString();
            if (language.equalsIgnoreCase("English,Hindi")) {

                if (ques.isEmpty()) {
                    et_ques.setError(getActivity().getResources().getString(R.string.req_ques));
                    et_ques.requestFocus();
                } else if (opt_a.isEmpty()) {
                    et_option_a.setError(getActivity().getResources().getString(R.string.req_a));
                    et_option_a.requestFocus();

                } else if (opt_b.isEmpty()) {
                    et_option_b.setError(getActivity().getResources().getString(R.string.req_b));
                    et_option_b.requestFocus();

                } else if (opt_c.isEmpty()) {
                    et_option_c.setError(getActivity().getResources().getString(R.string.req_c));
                    et_option_c.requestFocus();

                } else if (opt_d.isEmpty()) {
                    et_option_d.setError(getActivity().getResources().getString(R.string.req_d));
                    et_option_d.requestFocus();

                } else if (ans.equalsIgnoreCase("Select Answer")) {
                    toastMsg.toastIconError(getActivity().getResources().getString(R.string.req_ans));
                }
               else if (h_ques.isEmpty()) {
                    et_h_ques.setError(getActivity().getResources().getString(R.string.req_ques));
                    et_h_ques.requestFocus();
                } else if (h_opt_a.isEmpty()) {
                    et_h_option_a.setError(getActivity().getResources().getString(R.string.req_a));
                    et_h_option_a.requestFocus();

                } else if (h_opt_b.isEmpty()) {
                    et_h_option_b.setError(getActivity().getResources().getString(R.string.req_b));
                    et_h_option_b.requestFocus();

                } else if (h_opt_c.isEmpty()) {
                    et_h_option_c.setError(getActivity().getResources().getString(R.string.req_c));
                    et_h_option_c.requestFocus();

                } else if (h_opt_d.isEmpty()) {
                    et_h_option_d.setError(getActivity().getResources().getString(R.string.req_d));
                    et_h_option_d.requestFocus();

                } else if (h_ans.equalsIgnoreCase("Select Answer")) {
                    toastMsg.toastIconError(getActivity().getResources().getString(R.string.req_ans));
                }else {
                       addQuestion(q_no, cat_id, ques, opt_a, opt_b, opt_c, opt_d, ans, h_ques,h_opt_a,h_opt_b,h_opt_c, h_opt_d, h_ans, language);


                }
            }
          if (language.equalsIgnoreCase("english"))
            {
                if(ques.isEmpty())
                {
                    et_ques.setError(getActivity().getResources().getString(R.string.req_ques));
                    et_ques.requestFocus();
                }else if(opt_a.isEmpty())
                {
                    et_option_a.setError(getActivity().getResources().getString(R.string.req_a));
                    et_option_a.requestFocus();

                }else if(opt_b.isEmpty())
                {
                    et_option_b.setError(getActivity().getResources().getString(R.string.req_b));
                    et_option_b.requestFocus();

                }else if(opt_c.isEmpty())
                {
                    et_option_c.setError(getActivity().getResources().getString(R.string.req_c));
                    et_option_c.requestFocus();

                }else if(opt_d.isEmpty())
                {
                    et_option_d.setError(getActivity().getResources().getString(R.string.req_d));
                    et_option_d.requestFocus();

                }else if(ans.equalsIgnoreCase("Select Answer"))
                {
                    toastMsg.toastIconError(getActivity().getResources().getString(R.string.req_ans));
                }else
                {
                    addQuestion(q_no,cat_id,ques,opt_a,opt_b,opt_c,opt_d,ans,h_ques,h_opt_a,h_opt_b,h_opt_c,h_opt_d,h_ans,language);
                }
            }
            if (language.equalsIgnoreCase("hindi"))
            {

            if (h_ques.isEmpty()) {
                et_h_ques.setError(getActivity().getResources().getString(R.string.req_ques));
                et_h_ques.requestFocus();
            } else if (h_opt_a.isEmpty()) {
                et_h_option_a.setError(getActivity().getResources().getString(R.string.req_a));
                et_h_option_a.requestFocus();

            } else if (h_opt_b.isEmpty()) {
                et_h_option_b.setError(getActivity().getResources().getString(R.string.req_b));
                et_h_option_b.requestFocus();

            } else if (h_opt_c.isEmpty()) {
                et_h_option_c.setError(getActivity().getResources().getString(R.string.req_c));
                et_h_option_c.requestFocus();

            } else if (h_opt_d.isEmpty()) {
                et_h_option_d.setError(getActivity().getResources().getString(R.string.req_d));
                et_h_option_d.requestFocus();

            } else if (h_ans.equalsIgnoreCase("Select Answer")) {
                toastMsg.toastIconError(getActivity().getResources().getString(R.string.req_ans));
            }else {
                addQuestion(q_no, cat_id, ques, opt_a, opt_b, opt_c, opt_d, ans, h_ques,h_opt_a,h_opt_b,h_opt_c, h_opt_d, h_ans, language);

            }
            }
            else
            {
//                toastMsg.toastIconError("Select Language for Questions");
            }

        }
        else if (id == R.id.btn_edit_ques)
        {
            String ans ="";
            String h_ans = "";
            String ques = et_ques.getText().toString();
            String opt_a = et_option_a.getText().toString();
            String opt_b = et_option_b.getText().toString();
            String opt_c = et_option_c.getText().toString();
            String opt_d = et_option_d.getText().toString();
            if (spin_ans.getSelectedItemPosition()!=0)
            {
                ans = spin_ans.getSelectedItem().toString();
            }

            String h_ques = et_h_ques.getText().toString();
            String h_opt_a = et_h_option_a.getText().toString();
            String h_opt_b = et_h_option_b.getText().toString();
            String h_opt_c = et_h_option_c.getText().toString();
            String h_opt_d = et_h_option_d.getText().toString();
            if (spin_h_ans.getSelectedItemPosition()!=0)
            {
                h_ans = spin_h_ans.getSelectedItem().toString();}
            if (language.equalsIgnoreCase("English,Hindi")) {

                if (ques.isEmpty()) {
                    et_ques.setError(getActivity().getResources().getString(R.string.req_ques));
                    et_ques.requestFocus();
                } else if (opt_a.isEmpty()) {
                    et_option_a.setError(getActivity().getResources().getString(R.string.req_a));
                    et_option_a.requestFocus();

                } else if (opt_b.isEmpty()) {
                    et_option_b.setError(getActivity().getResources().getString(R.string.req_b));
                    et_option_b.requestFocus();

                } else if (opt_c.isEmpty()) {
                    et_option_c.setError(getActivity().getResources().getString(R.string.req_c));
                    et_option_c.requestFocus();

                } else if (opt_d.isEmpty()) {
                    et_option_d.setError(getActivity().getResources().getString(R.string.req_d));
                    et_option_d.requestFocus();

                } else if (ans.equalsIgnoreCase("Select Answer")) {
                    toastMsg.toastIconError(getActivity().getResources().getString(R.string.req_ans));
                }
                else if (h_ques.isEmpty()) {
                    et_h_ques.setError(getActivity().getResources().getString(R.string.req_ques));
                    et_h_ques.requestFocus();
                } else if (h_opt_a.isEmpty()) {
                    et_h_option_a.setError(getActivity().getResources().getString(R.string.req_a));
                    et_h_option_a.requestFocus();

                } else if (h_opt_b.isEmpty()) {
                    et_h_option_b.setError(getActivity().getResources().getString(R.string.req_b));
                    et_h_option_b.requestFocus();

                } else if (h_opt_c.isEmpty()) {
                    et_h_option_c.setError(getActivity().getResources().getString(R.string.req_c));
                    et_h_option_c.requestFocus();

                } else if (h_opt_d.isEmpty()) {
                    et_h_option_d.setError(getActivity().getResources().getString(R.string.req_d));
                    et_h_option_d.requestFocus();

                } else if (h_ans.equalsIgnoreCase("Select Answer")) {
                    toastMsg.toastIconError(getActivity().getResources().getString(R.string.req_ans));
                }else {
                    addQuestion(q_id, cat_id, ques, opt_a, opt_b, opt_c, opt_d, ans, h_ques,h_opt_a,h_opt_b,h_opt_c, h_opt_d, h_ans, language);
                }
            }
            if (language.equalsIgnoreCase("english"))
            {
                if(ques.isEmpty())
                {
                    et_ques.setError(getActivity().getResources().getString(R.string.req_ques));
                    et_ques.requestFocus();
                }else if(opt_a.isEmpty())
                {
                    et_option_a.setError(getActivity().getResources().getString(R.string.req_a));
                    et_option_a.requestFocus();

                }else if(opt_b.isEmpty())
                {
                    et_option_b.setError(getActivity().getResources().getString(R.string.req_b));
                    et_option_b.requestFocus();

                }else if(opt_c.isEmpty())
                {
                    et_option_c.setError(getActivity().getResources().getString(R.string.req_c));
                    et_option_c.requestFocus();

                }else if(opt_d.isEmpty())
                {
                    et_option_d.setError(getActivity().getResources().getString(R.string.req_d));
                    et_option_d.requestFocus();

                }else if(ans.equalsIgnoreCase("Select Answer"))
                {
                    toastMsg.toastIconError(getActivity().getResources().getString(R.string.req_ans));
                }else
                {
                    addQuestion(q_id,cat_id,ques,opt_a,opt_b,opt_c,opt_d,ans,h_ques,h_opt_a,h_opt_b,h_opt_c,h_opt_d,h_ans,language);
                }
            }
            if (language.equalsIgnoreCase("hindi"))
            {

                if (h_ques.isEmpty()) {
                    et_h_ques.setError(getActivity().getResources().getString(R.string.req_ques));
                    et_h_ques.requestFocus();
                } else if (h_opt_a.isEmpty()) {
                    et_h_option_a.setError(getActivity().getResources().getString(R.string.req_a));
                    et_h_option_a.requestFocus();

                } else if (h_opt_b.isEmpty()) {
                    et_h_option_b.setError(getActivity().getResources().getString(R.string.req_b));
                    et_h_option_b.requestFocus();

                } else if (h_opt_c.isEmpty()) {
                    et_h_option_c.setError(getActivity().getResources().getString(R.string.req_c));
                    et_h_option_c.requestFocus();

                } else if (h_opt_d.isEmpty()) {
                    et_h_option_d.setError(getActivity().getResources().getString(R.string.req_d));
                    et_h_option_d.requestFocus();

                } else if (h_ans.equalsIgnoreCase("Select Answer")) {
                    toastMsg.toastIconError(getActivity().getResources().getString(R.string.req_ans));
                }else {
                    addQuestion(q_id, cat_id, ques, opt_a, opt_b, opt_c, opt_d, ans, h_ques,h_opt_a,h_opt_b,h_opt_c, h_opt_d, h_ans, language);

                }
            }
            else
            {
//                toastMsg.toastIconError("Select Language for Questions");
            }
        }
        else if(id == R.id.btn_view_ques)
        {
            Bundle bundle=new Bundle();
           Fragment fm= new ViewAllQuestionFragment();
            FragmentManager manager=getActivity().getSupportFragmentManager();
            bundle.putString("cat_id",cat_id);
            fm.setArguments(bundle);
            manager.beginTransaction().replace(R.id.container_ques,fm)
                    .commit();

        }


//        if (ck_english.isChecked())
//        {
//            String s = "english";
//            language = s ;
//            lin_hindi.setVisibility(View.GONE);
//            lin_eng.setVisibility(View.VISIBLE);
////            toastMsg.toastIconSuccess("language"+language);
//        }
////        else if (id == R.id.iv_back)
////        {
////           getActivity().getSupportFragmentManager().popBackStackImmediate();
////        }
//        if (ck_hindi.isChecked())
//        {
//            String s = "hindi";
//            language = s ;
//            lin_hindi.setVisibility(View.VISIBLE);
//            lin_eng.setVisibility(View.GONE);
//
////            toastMsg.toastIconSuccess("language"+language);
//        }
//        else {
//            lin_hindi.setVisibility(View.GONE);
//        }
//        if (ck_hindi.isChecked() && ck_english.isChecked())
//        {
//            language ="english"+","+"hindi";
////            toastMsg.toastIconSuccess("language"+language);
//            lin_hindi.setVisibility(View.VISIBLE);
//            lin_eng.setVisibility(View.VISIBLE);
//        }

    }

    private void addQuestion( String ques_no, String cat_id, String ques, String opt_a, String opt_b, String opt_c, String opt_d, String ans ,
                             String h_ques, String h_opt_a, String h_opt_b, String h_opt_c, String h_opt_d, String h_ans , String lang) {

        loadingBar.show();
        HashMap<String,Object> map=new HashMap<>();
        map.put("ques_no",ques_no);
        map.put("cat_id",cat_id);
        map.put("ques",ques);
        map.put("option_a",opt_a);
        map.put("option_b",opt_b);
        map.put("option_c",opt_c);
        map.put("option_d",opt_d);
        map.put("ans",ans);
        map.put("hindi_ques",h_ques);
        map.put("hindi_option_a",h_opt_a);
        map.put("hindi_option_b",h_opt_b);
        map.put("hindi_option_c",h_opt_c);
        map.put("hindi_option_d",h_opt_d);
        map.put("hindi_ans",h_ans);
        map.put("language",lang);

        if(NetworkConnection.connectionChecking(getActivity()))
        {


        ques_ref.child(cat_id).child(q_no).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                loadingBar.dismiss();
                if(task.isSuccessful())
                {
                    toastMsg.toastIconSuccess("Question Added..");
                   count++ ;
                   q_no = "QUES"+String.valueOf(count);
                    tv_ques_no.setText(String.valueOf(count));
                    clearCtrl();
                }
                else
                {
                    toastMsg.toastIconError(""+task.getException().getMessage());
                }

            }
        });
        }
        else
        {
       module.noConnectionActivity();
        }

    }

    public void clearCtrl()
    {   et_ques.setText("");
        et_option_a.setText("");
        et_option_d.setText("");
        et_option_c.setText("");
        et_option_b.setText("");
        spin_ans.setSelection(0);
        et_h_ques.setText("");
        et_h_option_a.setText("");
        et_h_option_d.setText("");
        et_h_option_c.setText("");
        et_h_option_b.setText("");
        spin_h_ans.setSelection(0);
    }
}
