package com.upsun.quizz.Model;

import java.util.Comparator;

public class AddQuizCategoryModel {
    String p_id,p_img,p_name,p_status,parent,days;
    public AddQuizCategoryModel(){}

    public AddQuizCategoryModel(String p_id, String p_img, String p_name, String p_status, String parent, String days) {
        this.p_id = p_id;
        this.p_img = p_img;
        this.p_name = p_name;
        this.p_status = p_status;
        this.parent = parent;
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

    public void setP_name(String p_name) {
        this.p_name = p_name;
    }

    public String getP_status() {
        return p_status;
    }

    public void setP_status(String p_status) {
        this.p_status = p_status;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }

    public static Comparator<AddQuizCategoryModel> getComp_cat() {
        return comp_cat;
    }

    public static void setComp_cat(Comparator<AddQuizCategoryModel> comp_cat) {
        AddQuizCategoryModel.comp_cat = comp_cat;
    }

    public static Comparator<AddQuizCategoryModel> comp_cat=new Comparator<AddQuizCategoryModel>() {
        @Override
        public int compare(AddQuizCategoryModel o1, AddQuizCategoryModel o2) {
            int days1=Integer.parseInt(o1.getDays().toString());
            int days2=Integer.parseInt(o2.getDays().toString());
            return days1-days2;
        }
    };
}
