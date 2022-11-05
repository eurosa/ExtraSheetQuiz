package com.upsun.quizz.Model;

/**
 * Developed by Binplus Technologies pvt. ltd.  on 22,April,2020
 */
public class AnswerModel {
    String cat_id,ques_no,ans,id;

    public AnswerModel() {
    }

    public AnswerModel(String cat_id, String ques_no, String ans, String id) {
        this.cat_id = cat_id;
        this.ques_no = ques_no;
        this.ans = ans;
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

    public String getAns() {
        return ans;
    }

    public void setAns(String ans) {
        this.ans = ans;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
