package com.upsun.quizz.admin.fragments;

import static com.upsun.quizz.Config.Constants.QUIZ_REF;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.upsun.quizz.Config.Module;
import com.upsun.quizz.R;
import com.upsun.quizz.admin.activities.AddQuestionsActivity;
import com.upsun.quizz.networkconnectivity.NetworkConnection;
import com.upsun.quizz.utils.ToastMsg;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddQuizFragment extends Fragment implements View.OnClickListener{

    ImageView iv_back;
    EditText et_title,et_desc,et_fee,et_no_ptcnt,et_no_ques;
    TextView tv_date,tv_start_time,tv_end_time;
    Button btn_add_quiz;
    String date;
    Module module;
    ToastMsg toastMsg;
    Calendar currentTime;
    ProgressDialog loadingBar;
    DatabaseReference quiz_ref;
    int hours,minutes;

    int y,m,d;

    public AddQuizFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_add_quiz, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        iv_back=(ImageView)view.findViewById(R.id.iv_back);
        et_title=(EditText)view.findViewById(R.id.et_title);
        et_desc=(EditText)view.findViewById(R.id.et_desc);
        et_fee=(EditText)view.findViewById(R.id.et_fee);
        et_no_ptcnt=(EditText)view.findViewById(R.id.et_no_ptcnt);
        et_no_ques=(EditText)view.findViewById(R.id.et_no_ques);
        tv_date=(TextView)view.findViewById(R.id.tv_date);
        tv_start_time=(TextView)view.findViewById(R.id.tv_start_time);
        tv_end_time=(TextView)view.findViewById(R.id.tv_end_time);
        btn_add_quiz=(Button)view.findViewById(R.id.btn_add_quiz);
        toastMsg=new ToastMsg(getActivity());
        module=new Module(getActivity());
        loadingBar=new ProgressDialog(getActivity());
        loadingBar.setMessage("Loading...");
        loadingBar.setCanceledOnTouchOutside(false);
        btn_add_quiz.setOnClickListener(this);
        tv_date.setOnClickListener(this);
        tv_start_time.setOnClickListener(this);
        tv_end_time.setOnClickListener(this);
        iv_back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id=v.getId();
        if(id == R.id.btn_add_quiz)
        {

            if(et_title.getText().toString().isEmpty()) {
                toastMsg.toastIconError(getActivity().getResources().getString(R.string.req_title));
                et_title.requestFocus();
            } else if(et_desc.getText().toString().isEmpty()){
                toastMsg.toastIconError(getActivity().getResources().getString(R.string.req_desc));
                et_desc.requestFocus();
            }
//            else if(tv_date.getText().toString().isEmpty()){
//                toastMsg.toastIconError(getActivity().getResources().getString(R.string.req_date));
//            }
//            else if(tv_start_time.getText().toString().isEmpty()){
//                toastMsg.toastIconError(getActivity().getResources().getString(R.string.req_start_time));
//            }else if(tv_end_time.getText().toString().isEmpty()){
//                toastMsg.toastIconError(getActivity().getResources().getString(R.string.req_end_time));
//            }
            else if(et_fee.getText().toString().isEmpty()){
                toastMsg.toastIconError(getActivity().getResources().getString(R.string.req_entry_fee));
                et_fee.requestFocus();
            }else if(et_no_ptcnt.getText().toString().isEmpty()){
                toastMsg.toastIconError(getActivity().getResources().getString(R.string.req_no_pcnt));
                et_no_ptcnt.requestFocus();
            }else if(et_no_ques.getText().toString().isEmpty()){
                toastMsg.toastIconError(getActivity().getResources().getString(R.string.req_no_ques));
                et_no_ques.requestFocus();
            }
            else
            {
                if(NetworkConnection.connectionChecking(getActivity()))
                {


                loadingBar.show();
                final String quiz_id=getUniqueId();
                quiz_ref= FirebaseDatabase.getInstance().getReference().child(QUIZ_REF);
                HashMap<String,Object> hashMap=new HashMap<>();
                hashMap.put("quiz_id",quiz_id);
                hashMap.put("title",et_title.getText().toString());
                hashMap.put("description",et_desc.getText().toString());
                hashMap.put("quiz_date","");
                hashMap.put("quiz_start_time","");
                hashMap.put("quiz_end_time","");
//                hashMap.put("quiz_date",tv_date.getText().toString());
//                hashMap.put("quiz_start_time",tv_start_time.getText().toString());
//                hashMap.put("quiz_end_time",tv_end_time.getText().toString());
                hashMap.put("entry_fee",et_fee.getText().toString());
                hashMap.put("participents",et_no_ptcnt.getText().toString());
                hashMap.put("questions",et_no_ques.getText().toString());
                quiz_ref.child(quiz_id).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            loadingBar.dismiss();
                            toastMsg.toastIconSuccess("Quiz Added Successfully");
                            Intent intent=new Intent(getActivity(), AddQuestionsActivity.class);
                            intent.putExtra("quiz_id",quiz_id);
                            intent.putExtra("no_of_ques",et_no_ques.getText().toString());
                            startActivity(intent);
                        }
                        else
                        {loadingBar.dismiss();
                            toastMsg.toastIconError(task.getException().getMessage());
                        }
                    }
                });
                }
                else
                {
                    module.noConnectionActivity();
                }
            }

        }
        else if(id == R.id.tv_start_time)
        {
            showTimePicker(tv_start_time);
        }else if(id == R.id.tv_end_time)
        {
            showTimePicker(tv_end_time);
        }
        else if(id == R.id.tv_date)
        {
            showDatePicker();
        }else if(id == R.id.iv_back)
        {
//            Intent intent=new Intent(getActivity(), AddQuestionsActivity.class);
//            intent.putExtra("quiz_id","123");
//            intent.putExtra("no_of_ques","2");
//            startActivity(intent);
            getActivity().getSupportFragmentManager().popBackStackImmediate();


        }

    }

    private void showTimePicker(final TextView txt) {
        currentTime=Calendar.getInstance();
        hours=currentTime.get(Calendar.HOUR_OF_DAY);
        minutes=currentTime.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog=new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                if(minute==0)
                {
                    txt.setText(hourOfDay+":00");
                }
                else if(minute<10)
                {
                    txt.setText(hourOfDay+":0"+minute);
                }
                else
                {
                    txt.setText(hourOfDay+":"+minute);
                }

                String date=txt.getText().toString().trim();
                SimpleDateFormat _24HourSDF = new SimpleDateFormat("HH:mm");
                SimpleDateFormat _12HourSDF = new SimpleDateFormat("hh:mm a");
                try {
                    Date _24HourDt = _24HourSDF.parse(date);

                    txt.setText(getTimeInFormat(_12HourSDF.format(_24HourDt)));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        },hours,minutes,false);
        timePickerDialog.show();

    }

    private void showDatePicker() {

        Calendar calendar=Calendar.getInstance();
        y=calendar.get(Calendar.YEAR);
        m=calendar.get(Calendar.MONTH);
        d=calendar.get(Calendar.DAY_OF_MONTH);

        final DatePickerDialog datePickerDialog=new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                if(dayOfMonth<10)
                {
                    if(month<10)
                    {
                        date="0"+dayOfMonth+"-0"+month+"-"+year;
                    }
                    else
                    {
                        date="0"+dayOfMonth+"-"+month+"-"+year;
                    }

                }
                else
                {
                    if(month<10)
                    {
                        date=dayOfMonth+"-0"+month+"-"+year;
                    }
                    else
                    {
                        date=dayOfMonth+"-"+month+"-"+year;
                    }
//                    date=dayOfMonth+"-"+month+"-"+year;
                }

                tv_date.setText(date);
            }
        },y,(m+1),d);

        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis()-1000);
        datePickerDialog.show();


    }
    public String getUniqueId()
    {
        String ss = "quiz";
        String unique_id = "";
        Date date=new Date();
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("ddMMyyyyhhmmss");
        unique_id = ss +simpleDateFormat.format(date).toString();
        return unique_id ;
    }

//    public String getUniqueId()
//    {
//        Date date=new Date();
//        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("ddMMyyyyhhmmss");
//        return simpleDateFormat.format(date).toString();
//    }
    public String getTimeInFormat(String str)
    {
        String fm="";
        String[] str_arr=str.split(" ");
        if(str_arr[1].equalsIgnoreCase("a.m."))
        {
            fm="AM";
        }
        else if(str_arr[1].equalsIgnoreCase("p.m."))
        {
            fm="PM";

        }
        else
        {
            fm=str_arr[1].toString();

        }

        String tm=str_arr[0].toString()+" "+fm;
        return tm;
    }
}
