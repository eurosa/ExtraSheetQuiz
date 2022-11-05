package com.upsun.quizz.admin.activities;

import static com.upsun.quizz.Config.Constants.INSTRUCTION_REF;
import static com.upsun.quizz.Config.Constants.QUIZ_REF;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.upsun.quizz.Adapter.SpinnerAdapter;
import com.upsun.quizz.Config.Module;
import com.upsun.quizz.Model.AddQuizCategoryModel;
import com.upsun.quizz.Model.InstructionModel;
import com.upsun.quizz.Model.QuizSubCategroyModel;
import com.upsun.quizz.R;
import com.upsun.quizz.networkconnectivity.NetworkConnection;
import com.upsun.quizz.utils.ToastMsg;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class EditQuizActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    public final String TAG= EditQuizActivity.class.getSimpleName();
    ImageView iv_back;
    EditText et_title,et_desc,et_fee,et_no_ptcnt,et_no_ques,et_duration,reward_fee, et_ques_duration;
    RadioButton chk_english,chk_hindi,chkVisible,chkGone, byWallet, byReward, rbQuestion, rbQuiz, rbFive, rbTen;
    RadioGroup rgRank;
    CheckBox chk_ques_time;
    TextView tv_date,tv_start_time,tv_end_time;
    Button btn_add_quiz;
    LinearLayout layout_quiz_time, layout_ques_dur, layout_quiz_dur;
    String date;
    ToastMsg toastMsg;
    Calendar currentTime;
    ProgressDialog loadingBar;
    ArrayAdapter<String> arrayAdapter;
    DatabaseReference quiz_ref,ins_ref;
    ArrayList<InstructionModel> insList;
    ArrayList<String> strTitleList;
    Module module;
    Spinner spin_ins,spin_cate;
    int hours,minutes;
    int y,m,d;
    int validCat=0;
    String questionTime = "10";
    String noQuestion = "5";
    String quizType = "";
    String eCatId="",eCatName="",catId="",catName="";
    String rewardFee ="", qz_id="",title="",desc="",rank_visible="",fee="",ptcnt="",ques="",qDate="",qSTime="",qETime="",is_edit="",ins="",lang="", qDuration;
    Activity ctx= EditQuizActivity.this;


    private ArrayList<AddQuizCategoryModel> mList;
    private DatabaseReference demoRef;
    private List<String> sipnerCategoryArray;
    private List<QuizSubCategroyModel> subCateList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_quiz);
        initViews();
        if(is_edit.equalsIgnoreCase("true"))
        {
            et_title.setText(title);
            et_desc.setText(desc);
            et_fee.setText(fee);
            et_no_ptcnt.setText(ptcnt);
            et_no_ques.setText(ques);
            tv_date.setText(qDate);
            tv_end_time.setText(qETime);
            tv_start_time.setText(qSTime);
            reward_fee.setText(rewardFee);
            et_duration.setText(qDuration);
            btn_add_quiz.setText("Update Quiz");
            if(lang.equalsIgnoreCase("English"))
            {
                chk_english.setChecked(true);
                if(chk_hindi.isChecked())
                {
                    chk_hindi.setChecked(false);
                }
            }
            else if(lang.equalsIgnoreCase("Hindi"))
            {
                chk_hindi.setChecked(true);
                if(chk_english.isChecked())
                {
                    chk_english.setChecked(false);
                }
            }
            else
            {
                chk_english.setChecked(true);
                chk_hindi.setChecked(true);
            }

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
//        updateSpinnerData(is_edit,arrayAdapter);
    }

    private void updateSpinnerData(String is_edit, ArrayAdapter<String> arrayAdapter) {
        ArrayList<String> insStrList=new ArrayList<>();
//        insStrList=getInstructionList();
        arrayAdapter=new ArrayAdapter<String>(ctx,android.R.layout.simple_spinner_dropdown_item,insStrList);
        spin_ins.setAdapter(arrayAdapter);

        if(is_edit.equalsIgnoreCase("true"))
        {
           spin_ins.setSelection(getIndexIns(ins,insList));
        }
        if(ins.equalsIgnoreCase(""))
        {
            spin_ins.setSelection(0);
        }

    }

    private void initViews() {
        iv_back=(ImageView)findViewById(R.id.iv_back);
        et_title=(EditText)findViewById(R.id.et_title);
        et_desc=(EditText)findViewById(R.id.et_desc);
        et_fee=(EditText)findViewById(R.id.et_fee);
        et_duration=findViewById(R.id.et_duration);
        et_no_ptcnt=(EditText)findViewById(R.id.et_no_ptcnt);
        et_no_ques=(EditText)findViewById(R.id.et_no_ques);
        et_ques_duration=(EditText)findViewById(R.id.et_ques_duration);
        tv_date=(TextView)findViewById(R.id.tv_date);
        tv_start_time=(TextView)findViewById(R.id.tv_start_time);
        tv_end_time=(TextView)findViewById(R.id.tv_end_time);
        btn_add_quiz=(Button)findViewById(R.id.btn_add_quiz);
        chk_english=(RadioButton)findViewById(R.id.chk_english);
        chk_hindi=(RadioButton)findViewById(R.id.chk_hindi);
        reward_fee=(EditText)findViewById(R.id.et_reward_fee);
        toastMsg=new ToastMsg(ctx);
        chkGone= findViewById(R.id.rbGone);
        chkVisible = findViewById(R.id.rbVisibile);
        rgRank = findViewById(R.id.rg_rank);
        spin_ins=(Spinner)findViewById(R.id.spin_ins);
        byWallet=findViewById(R.id.rbWallet);
        byReward=findViewById(R.id.rbReward);
        rbQuestion=findViewById(R.id.rbQuestion);
        rbQuiz=findViewById(R.id.rbQuiz);
        rbFive=findViewById(R.id.rbFive);
        rbTen=findViewById(R.id.rbTen);
//        layout_quiz_time = findViewById(R.id.layout_quiz_time);
        layout_ques_dur = findViewById(R.id.layout_ques_dur);
        layout_quiz_dur = findViewById(R.id.layout_quiz_dur);
        spin_cate = (Spinner)findViewById(R.id.spin_cate);


        ins_ref= FirebaseDatabase.getInstance().getReference().child(INSTRUCTION_REF);
        insList=new ArrayList<>();
        strTitleList=new ArrayList<>();
        module=new Module(ctx);
        is_edit=getIntent().getStringExtra("is_edit");
        if(is_edit.equalsIgnoreCase("true"))
        {
            qz_id=getIntent().getStringExtra("quiz_id");
            title=getIntent().getStringExtra("title");
            desc=getIntent().getStringExtra("description");
            fee=getIntent().getStringExtra("entry_fee");
            rewardFee=getIntent().getStringExtra("reward_fee");
            ptcnt=getIntent().getStringExtra("participents");
            ques=getIntent().getStringExtra("questions");
            qDate=getIntent().getStringExtra("quiz_date");
            qETime=getIntent().getStringExtra("quiz_end_time");
            qSTime=getIntent().getStringExtra("quiz_start_time");
            qDuration=getIntent().getStringExtra("quiz_duration");
            quizType=getIntent().getStringExtra("quizType");
            questionTime=getIntent().getStringExtra("questions_time");
            lang=getIntent().getStringExtra("lang");
            ins=getIntent().getStringExtra("ins");
            eCatId=getIntent().getStringExtra("cat_id");
            eCatName=getIntent().getStringExtra("cat_name");

            Log.d(TAG, "initViews: "+questionTime);

            if(quizType != null && quizType.equalsIgnoreCase("Question")){
                rbQuestion.setChecked(true);
                layout_ques_dur.setVisibility(View.VISIBLE);
                et_ques_duration.setText(questionTime);
            }else rbQuiz.setChecked(true);

            if(noQuestion.equalsIgnoreCase("5")) rbFive.setChecked(true);
            else rbTen.setChecked(true);
        }

        loadingBar=new ProgressDialog(ctx);
        loadingBar.setMessage("Loading...");
        loadingBar.setCanceledOnTouchOutside(false);
        btn_add_quiz.setOnClickListener(this);
        tv_date.setOnClickListener(this);
        tv_start_time.setOnClickListener(this);
        tv_end_time.setOnClickListener(this);
        iv_back.setOnClickListener(this);

        rbFive.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked)
                noQuestion = "5";
        });

        rbTen.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked)
                noQuestion = "10";
        });

        rbQuestion.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                quizType = "Question";
                layout_ques_dur.setVisibility(View.VISIBLE);
//                questionTime = "10";
                et_ques_duration.setText(questionTime);
            }else layout_ques_dur.setVisibility(View.GONE);
        });

        rbQuiz.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                quizType = "Quiz";
                layout_quiz_dur.setVisibility(View.VISIBLE);
                questionTime = "0";
            }else layout_quiz_dur.setVisibility(View.GONE);
        });

        if(NetworkConnection.connectionChecking(this))
        {
            getInstructionList();
        }
        else
        {
            module.noConnectionActivity();
        }

        demoRef = FirebaseDatabase.getInstance().getReference();

        fetchingSpinnerQuizCate();
//        fetchingOfSubCate();

        spin_cate.setOnItemSelectedListener(this);




    }

    @Override
    public void onClick(View v) {
        int id=v.getId();
        if(id == R.id.btn_add_quiz)
        {
            if(et_title.getText().toString().isEmpty()) {
                toastMsg.toastIconError(getResources().getString(R.string.req_title));
                et_title.requestFocus();
            } else if(et_desc.getText().toString().isEmpty()){
                toastMsg.toastIconError(getResources().getString(R.string.req_desc));
                et_desc.requestFocus();
            }else if(tv_date.getText().toString().isEmpty()){
                toastMsg.toastIconError(getResources().getString(R.string.req_date));
            }
//            else if(tv_start_time.getText().toString().isEmpty()){
//                toastMsg.toastIconError(getResources().getString(R.string.req_start_time));
//            }else if(tv_end_time.getText().toString().isEmpty()){
//                toastMsg.toastIconError(getResources().getString(R.string.req_end_time));
//            }
            else if(et_fee.getText().toString().isEmpty()){
                toastMsg.toastIconError(getResources().getString(R.string.req_entry_fee));
                et_fee.requestFocus();
            }else if(et_no_ptcnt.getText().toString().isEmpty()){
                toastMsg.toastIconError(getResources().getString(R.string.req_no_pcnt));
                et_no_ptcnt.requestFocus();
//            }else if(et_no_ques.getText().toString().isEmpty()){
//                toastMsg.toastIconError(getResources().getString(R.string.req_no_ques));
//                et_no_ques.requestFocus();
            }else if(spin_ins.getSelectedItem().equals("Select"))
            {
                toastMsg.toastIconError(ctx.getResources().getString(R.string.req_ins));
            }else if(chk_english.isChecked() == false && chk_hindi.isChecked() ==false)
            {
                toastMsg.toastIconError(ctx.getResources().getString(R.string.req_lang));
            }
            else if(validCat==0){
                module.showToast("please select a category");
            }else if(validCat==3){
                module.showToast("please select valid category");
            }
            else
            {
                String language="";
                 String quiz_id="";
                if(is_edit.equalsIgnoreCase("true"))
                {
                    quiz_id=qz_id;
                    language=lang;
                }
                else
                {
                    quiz_id=getUniqueId();
                    language=module.getLanguage(chk_english,chk_hindi);
                }

                loadingBar.show();
                if (chkGone.isChecked())
                {
                    rank_visible = "0";
                }
                else
                {
                    chkVisible.setChecked(true);
                    rank_visible = "1";
                }

                quiz_ref= FirebaseDatabase.getInstance().getReference().child(QUIZ_REF);
                HashMap<String,Object> hashMap=new HashMap<>();
                hashMap.put("quiz_id",quiz_id);
                hashMap.put("title",et_title.getText().toString());
                hashMap.put("description",et_desc.getText().toString());
                hashMap.put("quiz_date",tv_date.getText().toString());

//                hashMap.put("quiz_start_time",tv_start_time.getText().toString());
//                hashMap.put("quiz_end_time",tv_end_time.getText().toString());
                hashMap.put("quiz_start_time","");
                hashMap.put("quiz_end_time","");

                hashMap.put("duration",et_duration.getText().toString());
                hashMap.put("quizType", quizType);
                hashMap.put("question_time",et_ques_duration.getText().toString());
                hashMap.put("entry_fee",et_fee.getText().toString());
                hashMap.put("participents",et_no_ptcnt.getText().toString());
                hashMap.put("questions",noQuestion);
                hashMap.put("language",language);
                hashMap.put("rank_visible",rank_visible);
                hashMap.put("reward_fee",reward_fee.getText().toString());
                hashMap.put("instruction",insList.get(getIndexIns(spin_ins.getSelectedItem().toString(),insList)).getId().toString());
                hashMap.put("cat_id",catId);
                hashMap.put("cat_name",catName);
                Log.e(TAG, "onClick: "+hashMap.toString());
                quiz_ref.child(quiz_id).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            loadingBar.dismiss();
                            if(is_edit.equalsIgnoreCase("true"))
                            {
                                toastMsg.toastIconSuccess("Quiz details update Successfully");

                            }
                            else
                            {
                                toastMsg.toastIconSuccess("Add Quiz Successfully");

                            }
                           finish();
                        }
                        else
                        {loadingBar.dismiss();
                            toastMsg.toastIconError(task.getException().getMessage());
                        }
                    }
                });

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
           finish();

        }

    }

    private void showTimePicker(final TextView txt) {
        currentTime=Calendar.getInstance();
        hours=currentTime.get(Calendar.HOUR_OF_DAY);
        minutes=currentTime.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog=new TimePickerDialog(ctx, new TimePickerDialog.OnTimeSetListener() {
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

        final DatePickerDialog datePickerDialog=new DatePickerDialog(ctx, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int mth, int dayOfMonth) {
             int month=mth+1;
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
                }

                tv_date.setText(date);
            }
        },y,m,d);

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

    public void getInstructionList()
    {
        final ArrayList<String> strList=new ArrayList<>();
        loadingBar.show();
        insList.clear();
        strList.clear();
        ins_ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                loadingBar.dismiss();
                strTitleList.add("Select");
                strList.add("Select");
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0)
                {
                    for(DataSnapshot snapshot:dataSnapshot.getChildren())
                    {
                        InstructionModel model=snapshot.getValue(InstructionModel.class);
                        insList.add(model);
                        strList.add(model.getTitle().toString());
                        strTitleList.add(model.getTitle().toString());

                    }
                    arrayAdapter=new ArrayAdapter<String>(ctx,android.R.layout.simple_spinner_dropdown_item,strTitleList);
                    spin_ins.setAdapter(arrayAdapter);
                }
                else {
                    toastMsg.toastIconError("First Add Some Instructions for Quiz");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            loadingBar.dismiss();
                Toast.makeText(ctx, ""+databaseError.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    public int getIndexIns(String title,ArrayList<InstructionModel> list)
    {
        int indx=-1;
        for(int i=0; i<list.size();i++)
        {
            if(list.get(i).getTitle().equalsIgnoreCase(title))
            {
                indx=i;
                break;
            }
        }
        return indx;
    }


    //fetching values of Spinner from firebase
    private void fetchingSpinnerQuizCate(){
        mList = new ArrayList<>();
        mList.clear();
        Query query=demoRef.child("parents").orderByChild("p_status").equalTo("0");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Log.e(TAG, "onDataChange: "+snapshot.toString() );
                    AddQuizCategoryModel model = snapshot.getValue(AddQuizCategoryModel.class);
                    mList.add(model);
                }
                SpinnerAdapter spinnerAdapter=new SpinnerAdapter(ctx,mList);
                spin_cate.setAdapter(spinnerAdapter);

                if(is_edit.equalsIgnoreCase("true")){
                    spin_cate.setSelection(getIndex(mList,eCatId));
//                        spin_cate.setSelection(1);

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                toastMsg.toastIconError("Something went wrong...");
            }
        });
    }

    //converting mList into spinner array
    private void spinnerArray(String str, List<AddQuizCategoryModel> mList){
        sipnerCategoryArray = new ArrayList<>();
        sipnerCategoryArray.add(str);

        for (AddQuizCategoryModel m : mList){
//            sipnerCategoryArray.add(m.getQuizCategory());

        }

    }

    //fetching of sub Categories
    private void fetchingOfSubCate(){
        subCateList = new ArrayList<>();

        demoRef.child("sub_quiz_cat").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    QuizSubCategroyModel model = snapshot.getValue(QuizSubCategroyModel.class);
                    subCateList.add(model);
                }

                subSpinnerArray("Select",subCateList);
                ArrayAdapter arrayAdapter = new ArrayAdapter(EditQuizActivity.this,android.R.layout.simple_spinner_item,sipnerCategoryArray);
                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spin_cate.setAdapter(arrayAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                toastMsg.toastIconError("Server Busy .... Not able to update post at this moment, please try again later...");
            }
        });
    }

    //converting mList into spinner array
    private void subSpinnerArray(String str, List<QuizSubCategroyModel> mList){
        sipnerCategoryArray = new ArrayList<>();
        sipnerCategoryArray.add(str);

        for (QuizSubCategroyModel m : mList){
            sipnerCategoryArray.add(m.getSubCategoryName());
        }

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if((!getSubCategory(mList,mList.get(position).getP_id().toString()) && (mList.get(position).getParent().equals("0")))){
            validCat=1;
            catId=mList.get(position).getP_id().toString();
            catName=mList.get(position).getP_name().toString();
        }else if(!mList.get(position).getParent().equals("0"))
        {
            validCat=2;
            catId=mList.get(position).getP_id().toString();
            catName=mList.get(position).getP_name().toString();
        }else{
            validCat=3;
        }
        Log.e(TAG, "onItemSelected: "+validCat );
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public int getIndex(ArrayList<AddQuizCategoryModel> list,String str){
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
    public boolean getSubCategory(List<AddQuizCategoryModel> list,String pnt){
        boolean flag=false;
        for(int i=0; i<list.size();i++){
            if(list.get(i).getParent().equalsIgnoreCase(pnt)){
                flag=true;
                break;
            }
        }
        return flag;
    }
}
