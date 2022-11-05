package com.upsun.quizz.Model;

public class QuizQuesCategoryModel {
   String cat_id;
   String  id;
   String ques_no;

    public QuizQuesCategoryModel() {
    }

    public QuizQuesCategoryModel(String cat_id, String id, String ques_no) {
        this.cat_id = cat_id;
        this.id = id;
        this.ques_no = ques_no;
    }

    public String getCat_id() {
        return cat_id;
    }

    public void setCat_id(String cat_id) {
        this.cat_id = cat_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQues_no() {
        return ques_no;
    }

    public void setQues_no(String ques_no) {
        this.ques_no = ques_no;
    }
}
