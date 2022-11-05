package com.upsun.quizz.Model;

import java.util.Comparator;

/**
 * Developed by Binplus Technologies pvt. ltd.  on 23,April,2020
 */
public class CalculateRankModel {
    String result,user_id,username;

    public CalculateRankModel() {
    }

    public CalculateRankModel(String result, String user_id,String username) {
        this.result = result;
        this.username=username;
        this.user_id = user_id;
    }


    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public static Comparator<CalculateRankModel> rank=new Comparator<CalculateRankModel>() {
        @Override
        public int compare(CalculateRankModel o1, CalculateRankModel o2) {
            int s1=Integer.parseInt(o1.result);
            int s2=Integer.parseInt(o2.result);
            return  s2-s1;
        }
    };

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
