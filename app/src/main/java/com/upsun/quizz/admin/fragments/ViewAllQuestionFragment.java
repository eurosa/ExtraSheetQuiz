package com.upsun.quizz.admin.fragments;

import static com.upsun.quizz.Config.Constants.QUESTION_REF;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.upsun.quizz.Adapter.QuestionAdapter;
import com.upsun.quizz.Config.Module;
import com.upsun.quizz.Model.AddQuestionModel;
import com.upsun.quizz.R;
import com.upsun.quizz.networkconnectivity.NetworkConnection;
import com.upsun.quizz.utils.SessionManagment;
import com.upsun.quizz.utils.ToastMsg;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class ViewAllQuestionFragment extends Fragment {

  public static TextView no_of_ques;
    Button btn_add_cat;
    RecyclerView rv_ques;
    RelativeLayout rel_no_items;
    ProgressDialog loadingBar;
    SessionManagment sessionManagment;
    ToastMsg toastMsg;
    Module module;
    String cat_id="" ,child_count="";
    DatabaseReference ques_ref;
    QuestionAdapter questionAdapter ;
    ArrayList<AddQuestionModel> list;
    public ViewAllQuestionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_view_all_question, container, false);
        initViews(view);
        if(NetworkConnection.connectionChecking(getActivity()))
        {
            getAllQuestions(cat_id);
        }
        else
        {
         module.noConnectionActivity();
        }
        return view;
    }



    private void initViews(View view) {
        no_of_ques=(TextView)view.findViewById(R.id.no_of_ques);
        btn_add_cat=(Button) view.findViewById(R.id.btn_add_cat);
        rv_ques=(RecyclerView) view.findViewById(R.id.rv_ques);
        rel_no_items=(RelativeLayout) view.findViewById(R.id.rel_no_items);
        final Bundle bundle=new Bundle();
        list=new ArrayList<>();
        module=new Module(getActivity());
        cat_id=getArguments().getString("cat_id");
        child_count = getArguments().getString("child_count");
        toastMsg=new ToastMsg(getActivity());
        loadingBar=new ProgressDialog(getActivity());
        loadingBar.setMessage("Loading...");
        loadingBar.setCanceledOnTouchOutside(false);
        questionAdapter = new QuestionAdapter(getActivity(),list);
        rv_ques.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false));
        rv_ques.setAdapter(questionAdapter);
        ques_ref= FirebaseDatabase.getInstance().getReference().child(QUESTION_REF);
        btn_add_cat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle1=new Bundle();
                bundle1.putString("cat_id",cat_id);
//                bundle1.putString("child_count",child_count);
                bundle1.putString("child_count", String.valueOf(list.size()));
                bundle1.putString("is_edit","false");
                Fragment fm=new AddQuestionFragment();
                fm.setArguments(bundle1);
                FragmentManager manger=getFragmentManager();
                manger.beginTransaction().replace(R.id.container_ques,fm).commit();
            }
        });
    }

    private void getAllQuestions(String cat_id) {
        loadingBar.show();
        ques_ref.child(cat_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0)
                {
                    loadingBar.dismiss();
                    rv_ques.setVisibility(View.VISIBLE);
                    rel_no_items.setVisibility(View.GONE);
                    for(DataSnapshot snapshot : dataSnapshot.getChildren())
                    {
                        AddQuestionModel model=snapshot.getValue(AddQuestionModel.class);
                        list.add(model);
                    }
                    rv_ques.setLayoutManager(new LinearLayoutManager(getActivity()));
                    no_of_ques.setText("No of Questions: "+list.size());
//                  toastMsg.toastIconSuccess(""+list.size());
                }
                else
                {
                    loadingBar.dismiss();
                    rel_no_items.setVisibility(View.VISIBLE);
                    rv_ques.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
