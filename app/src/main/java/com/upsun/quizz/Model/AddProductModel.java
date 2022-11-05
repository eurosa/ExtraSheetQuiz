package com.upsun.quizz.Model;

import java.util.Comparator;

public class AddProductModel {
    String p_id,p_reward,p_img,p_name,p_status,p_detail,days;
    public AddProductModel(){}

    public AddProductModel(String p_id, String p_img, String p_name,String p_reward, String p_status, String p_detail, String days) {
        this.p_id = p_id;
        this.p_img = p_img;
        this.p_name = p_name;
        this.p_reward = p_reward;
        this.p_status = p_status;
        this.p_detail = p_detail;
        this.days = days;
    }

    public String getP_id() {
        return p_id;
    }

    public void setP_id(String p_id) {
        this.p_id = p_id;
    }

    public String getP_img() {
        return p_img;
    }

    public void setP_img(String p_img) {
        this.p_img = p_img;
    }

    public String getP_name() {
        return p_name;
    }
    public String getP_reward() {
        return p_reward;
    }

    public void setP_name(String p_name) {
        this.p_name = p_name;
    }
    public void setP_reward(String p_reward) {
        this.p_reward = p_reward;
    }

    public String getP_status() {
        return p_status;
    }

    public void setP_status(String p_status) {
        this.p_status = p_status;
    }

    public String getP_detail() {
        return p_detail;
    }

    public void setP_detail(String p_detail) {
        this.p_detail = p_detail;
    }

    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }

    public static Comparator<AddProductModel> getComp_rew() {
        return comp_pro;
    }

    public static void setComp_rew(Comparator<AddProductModel> comp_rew) {
        AddProductModel.comp_pro = comp_rew;
    }

    public static Comparator<AddProductModel> comp_pro=new Comparator<AddProductModel>() {
        @Override
        public int compare(AddProductModel o1, AddProductModel o2) {
            int days1=Integer.parseInt(o1.getDays().toString());
            int days2=Integer.parseInt(o2.getDays().toString());
            return days1-days2;
        }
    };
}
