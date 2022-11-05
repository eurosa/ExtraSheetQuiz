package com.upsun.quizz.utils;

import static com.upsun.quizz.Config.Constants.CAT_ID;
import static com.upsun.quizz.Config.Constants.EXPIRED_TIME;
import static com.upsun.quizz.Config.Constants.IS_APP_FIRST_TIME_LAUNCH;
import static com.upsun.quizz.Config.Constants.IS_LOGIN;
import static com.upsun.quizz.Config.Constants.KEY_DEVICE_ID;
import static com.upsun.quizz.Config.Constants.KEY_EMAIL;
import static com.upsun.quizz.Config.Constants.KEY_ID;
import static com.upsun.quizz.Config.Constants.KEY_MOBILE;
import static com.upsun.quizz.Config.Constants.KEY_NAME;
import static com.upsun.quizz.Config.Constants.KEY_PROFILE_IMG;
import static com.upsun.quizz.Config.Constants.KEY_QUIZ_STATUS;
import static com.upsun.quizz.Config.Constants.KEY_REWARDS;
import static com.upsun.quizz.Config.Constants.KEY_WALLET;
import static com.upsun.quizz.Config.Constants.PREF_NAME;
import static com.upsun.quizz.Config.Constants.referredBy;

import android.content.Context;
import android.content.SharedPreferences;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * Developed by Binplus Technologies pvt. ltd.  on 04,April,2020
 */
public class SessionManagment {
    public static SharedPreferences pref;
  public static   SharedPreferences.Editor editor;
    Context context;
    int PRIVATE_MODE=0;

    public SessionManagment(Context context) {
        this.context = context;
        pref=context.getSharedPreferences(PREF_NAME,PRIVATE_MODE);
        editor=pref.edit();
    }

    public void setAppOpenFirstTime(boolean isFirstTime){
        editor.putBoolean(IS_APP_FIRST_TIME_LAUNCH,isFirstTime);
        editor.commit();
    }

    public boolean getAppOpenFirstTime(){
        return pref.getBoolean(IS_APP_FIRST_TIME_LAUNCH,false);
    }

    public void setExpiryTime(long expiryTime){
        editor.putLong(EXPIRED_TIME, expiryTime);
        editor.commit();
    }
 public static void setLimit(String date){
        editor.putString("date", date);
        editor.commit();
    }
    public static long getDateDiff(SimpleDateFormat format, String oldDate, String newDate) {
        try {
            return TimeUnit.DAYS.convert(format.parse(newDate).getTime() - format.parse(oldDate).getTime(), TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
    public long getExpiryTime(){
        return pref.getLong(EXPIRED_TIME, 0);
    }
public static  String  getLimit(){
        return pref.getString("date", "");
    }


    public void createLoginSession(String id,String name,String email,String mobile,String img,String wallet,String rewards,String quiz_status,String device_id)
    {
        editor.putBoolean(IS_LOGIN,true);
        editor.putString(KEY_ID,id);
        editor.putString(KEY_EMAIL,email);
        editor.putString(KEY_MOBILE,mobile);
        editor.putString(KEY_NAME,name);
        editor.putString(KEY_PROFILE_IMG,img);
        editor.putString(KEY_WALLET,wallet);
        editor.putString(KEY_REWARDS,rewards);
        editor.putString(KEY_QUIZ_STATUS,quiz_status);
        editor.putString(KEY_DEVICE_ID,device_id);
        editor.commit();
    }

    public static HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        user.put(KEY_ID, pref.getString(KEY_ID, null));
        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));
        user.put(KEY_NAME, pref.getString(KEY_NAME, null));
        user.put(KEY_MOBILE, pref.getString(KEY_MOBILE, null));
        user.put(KEY_PROFILE_IMG, pref.getString(KEY_PROFILE_IMG, null));
        user.put(KEY_WALLET, pref.getString(KEY_WALLET, null));
        user.put(KEY_REWARDS, pref.getString(KEY_REWARDS, null));
        user.put(KEY_QUIZ_STATUS, pref.getString(KEY_QUIZ_STATUS, null));
        user.put(KEY_DEVICE_ID,pref.getString(KEY_DEVICE_ID,null));
        return user;
    }

    public String getName(){
        return pref.getString(KEY_NAME,null);
    }



    public String getNumber(){
        return pref.getString(KEY_MOBILE,null);
    }
    public String getId(){
        return pref.getString(KEY_ID,null);
    }

    public boolean isLogin()
    { return pref.getBoolean(IS_LOGIN,false);  }

    public void logoutSession(){
        editor.clear();
        editor.commit();
    }
    public void setCatId(String cat_id)
    {
        editor.putString(CAT_ID,cat_id);
        editor.apply();
    }
    public void updateProfile(String name,String email,String img)
    {
       editor.putString(KEY_NAME,name);
       editor.putString(KEY_EMAIL,email);
       editor.putString(KEY_PROFILE_IMG,img);
       editor.apply();
    }
    public String getCatId()
    {
        return pref.getString(CAT_ID,"");
    }

    public void updateWallet(String wallet)
    {
        editor.putString(KEY_WALLET,wallet);
        editor.apply();
    }
    public void updateQuizStatus(String quiz_status)
    {
        editor.putString(KEY_QUIZ_STATUS,quiz_status);
        editor.apply();
    }
    public void updateRewards(String rew)
    {
        editor.putString(KEY_REWARDS,rew);
        editor.apply();
    }

    public void setReferredBy(String referBy){
        editor.putString(referredBy,referBy);
        editor.apply();
    }
    public String getReferredBy(){
       return pref.getString(referredBy,null);
    }

    String referCode;

    public String getReferCode() {
        return pref.getString("referCode","");
    }

    public void setReferCode(String referCode) {
        editor.putString("referCode",referCode).apply();
        this.referCode = referCode;
    }

    String referLink;

    public String getReferLink() {
        return pref.getString("referLink","");
    }

    public void setReferLink(String referLink) {
        editor.putString("referLink",referLink).apply();
        this.referLink = referLink;
    }
}
