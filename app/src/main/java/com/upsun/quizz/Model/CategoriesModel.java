package com.upsun.quizz.Model;

/**
 * Developed by Binplus Technologies pvt. ltd.  on 08,April,2020
 */
public class CategoriesModel {
    String cat_id,cat_name;

    public CategoriesModel() {
    }

    public CategoriesModel(String cat_id, String cat_name) {
        this.cat_id = cat_id;
        this.cat_name = cat_name;
    }

    public String getCat_id() {
        return cat_id;
    }

    public void setCat_id(String cat_id) {
        this.cat_id = cat_id;
    }

    public String getCat_name() {
        return cat_name;
    }

    public void setCat_name(String cat_name) {
        this.cat_name = cat_name;
    }
}
