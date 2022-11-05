package com.upsun.quizz.Model;

import java.util.ArrayList;

/**
 * Developed by Binplus Technologies pvt. ltd.  on 17,December,2020
 */
public class CatQuesModel {
    String cat_id;
    ArrayList<QuesModel> list;

    public CatQuesModel() {
    }

    public CatQuesModel(String cat_id, ArrayList<QuesModel> list) {
        this.cat_id = cat_id;
        this.list = list;
    }

    public String getCat_id() {
        return cat_id;
    }

    public void setCat_id(String cat_id) {
        this.cat_id = cat_id;
    }

    public ArrayList<QuesModel> getList() {
        return list;
    }

    public void setList(ArrayList<QuesModel> list) {
        this.list = list;
    }
}
