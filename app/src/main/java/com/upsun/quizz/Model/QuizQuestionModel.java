package com.upsun.quizz.Model;

/**
 * Developed by Binplus Technologies pvt. ltd.  on 11,April,2020
 */
public class QuizQuestionModel {
    String id,cat_id,ques_no;

    public QuizQuestionModel() {
    }

    public QuizQuestionModel(String id, String cat_id, String ques_no) {
        this.id = id;
        this.cat_id = cat_id;
        this.ques_no = ques_no;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCat_id() {
        return cat_id;
    }

    public void setCat_id(String cat_id) {
        this.cat_id = cat_id;
    }

    public String getQues_no() {
        return ques_no;
    }

    public void setQues_no(String ques_no) {
        this.ques_no = ques_no;
    }
}
