package com.upsun.quizz.admin.fragments;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.upsun.quizz.Config.Module;
import com.upsun.quizz.Model.QuizModel;
import com.upsun.quizz.Model.UserModel;
import com.upsun.quizz.R;
import com.upsun.quizz.admin.activities.AdminAllQuizActivity;
import com.upsun.quizz.admin.activities.AdminJoinQuizActivity;
import com.upsun.quizz.networkconnectivity.NetworkConnection;
import com.upsun.quizz.networkconnectivity.NoInternetConnection;
import com.upsun.quizz.utils.ToastMsg;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class AdminDashboradFragment extends Fragment implements View.OnClickListener {

    FloatingActionButton floatingActionButton;
    CardView card_active_users , card_all_quiz ,card_upcoming_quiz,card_todays_quiz,card_view_result ;
    TextView txt_active_users , txt_all_quiz ,txt_upcoming_quiz,txt_todays_quiz;
    ProgressDialog loadingBar;
    ToastMsg toastMsg;
    ArrayList<UserModel> activeUserList;
    ArrayList<QuizModel> allQuizList;
    ArrayList<QuizModel> todayQuizList;
    ArrayList<QuizModel> upcomingQuizList;
    Module module;


    public AdminDashboradFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View view = inflater.inflate(R.layout.fragment_admin_dashborad, container, false);

       initViews(view);
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if(event.getAction() == KeyEvent.ACTION_UP && keyCode==KeyEvent.KEYCODE_BACK)
                {
                    AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
                    builder.setTitle("Confirmation");
                    builder.setMessage("Are you sure want to exit?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            getActivity().finishAffinity();
                        }
                    })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    dialog.dismiss();
                                }
                            });
                    AlertDialog dialog=builder.create();
                    dialog.show();
                    return true;
                }
                return false;
            }
        });

        return view ;
    }
    private void initViews(View view) {
        floatingActionButton = view.findViewById(R.id.floating);
        txt_active_users = view.findViewById(R.id.txt_active_member);
        txt_all_quiz = view.findViewById(R.id.txt_all_quiz);
        txt_todays_quiz = view.findViewById(R.id.txt_todays_quiz);
        txt_upcoming_quiz = view.findViewById(R.id.txt_upcoming_quiz);
        card_todays_quiz=view.findViewById(R.id.card_todays_quiz);
        card_upcoming_quiz=view.findViewById(R.id.card_upcoming_quiz);
        card_active_users = view.findViewById(R.id.card_active_user);
        card_all_quiz= view.findViewById(R.id.card_all_quiz);
        card_view_result= view.findViewById(R.id.card_view_result);
        loadingBar=new ProgressDialog(getActivity());
        loadingBar.setMessage("Loading...");
        loadingBar.setCanceledOnTouchOutside(false);
        toastMsg=new ToastMsg(getActivity());
        activeUserList=new ArrayList<>();
        allQuizList=new ArrayList<>();
        todayQuizList=new ArrayList<>();
        upcomingQuizList=new ArrayList<>();
        floatingActionButton.setOnClickListener(this);
        card_todays_quiz.setOnClickListener(this);
        card_upcoming_quiz.setOnClickListener(this);
        card_active_users.setOnClickListener(this);
        card_all_quiz.setOnClickListener(this);
        card_view_result.setOnClickListener(this);
        module=new Module(getActivity());

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
//        if (id == R.id.floating)
//        {
//            Intent intent = new Intent(getActivity(), AddInfoPage.class);
//            startActivity(intent);
//        }
         if(id == R.id.card_todays_quiz)
        {
            Intent intent = new Intent(getActivity(), AdminAllQuizActivity.class);
            intent.putExtra("type","today");
            startActivity(intent);

        }
         else if(id == R.id.card_view_result)
        {
//            module.deleteNode("quiz_ques");
//
            Intent intent = new Intent(getActivity(), AdminJoinQuizActivity.class);
            startActivity(intent);
        }
         else  if (id == R.id.card_all_quiz)
         {
             Intent intent = new Intent(getActivity(), AdminAllQuizActivity.class);
             intent.putExtra("type","all");
             startActivity(intent);
         }
         else if (id == R.id.card_active_user)
         {
             Fragment fm = new AllUsersFragment();
             FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

             fragmentManager.beginTransaction()
                     .replace( R.id.container_admin,fm)
                     .addToBackStack(null)
                     .commit();

         }    }

    @Override
    public void onStart() {
        super.onStart();
        if (NetworkConnection.connectionChecking(getActivity())) {
            getAllData();
        }
        else
        {
            Intent intent = new Intent(getActivity(), NoInternetConnection.class);
            getActivity().startActivity(intent);
        }
    }

    private void getAllData() {
        loadingBar.show();
        activeUserList.clear();
        allQuizList.clear();
        todayQuizList.clear();
        upcomingQuizList.clear();
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference();
        ref.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0)
                {
                    loadingBar.dismiss();
                    // Active User Count
                  for(DataSnapshot user_snap:dataSnapshot.getChildren())
                  {
                      if (user_snap.hasChild("status")){
                          UserModel model=user_snap.getValue(UserModel.class);
                          if(model.getStatus().toString().equalsIgnoreCase("0"))
                          {
                              activeUserList.add(model);
                          }
                      }

                  }
                  txt_active_users.setText(""+activeUserList.size());

                  //All Quiz Count
//                    for(DataSnapshot quiz_snap:dataSnapshot.child("quiz").getChildren())
//                    {
//                        QuizModel modelQuiz=quiz_snap.getValue(QuizModel.class);
//                            allQuizList.add(modelQuiz);
//
//                            if(getCompareDate(modelQuiz.getQuiz_date().toString()))
//                            {
//                                todayQuizList.add(modelQuiz);
//                            }
//                            if(getAfterDate(modelQuiz.getQuiz_date().toString()))
//                            {
//                                upcomingQuizList.add(modelQuiz);
//                            }
//                    }
                    txt_all_quiz.setText(""+allQuizList.size());
                    txt_todays_quiz.setText(""+todayQuizList.size());
                    txt_upcoming_quiz.setText(""+upcomingQuizList.size());

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(),""+databaseError.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    public boolean getCompareDate(String date)
    {
        boolean flag=false;
        Date c_date=new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat formtTime=new SimpleDateFormat("hh:MM:ss");


//        String cdt=formtTime.format(c_date);
        String ch=dateFormat.format(c_date);
        Date date1=null;
        Date date2=null;
        try {
             date1 = dateFormat.parse(date);
             date2 = dateFormat.parse(ch);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        Log.e("dddadd",""+date2.toString()+" -- "+date1.toString());
        if(date2.compareTo(date1)==0)
        {
            flag=true;
        }

        return flag;
    }

    public boolean getAfterDate(String date)
    {
        boolean flag=false;
        Date c_date=new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat formtTime=new SimpleDateFormat("hh:MM:ss");


//        String cdt=formtTime.format(c_date);
        String ch=dateFormat.format(c_date);
        Date date1=null;
        Date date2=null;
        try {
            date1 = dateFormat.parse(date);
            date2 = dateFormat.parse(ch);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        Log.e("dddadd",""+date2.toString()+" -- "+date1.toString());
        if(date2.compareTo(date1)>0)
        {
            flag=true;
        }

        return flag;
    }

}
