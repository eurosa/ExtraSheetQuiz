package com.upsun.quizz.Model;

/**
 * Developed by Binplus Technologies pvt. ltd.  on 07,April,2020
 */
public class AddQuestionModel {
    String ques_no,cat_id,ques,option_a,option_b,option_c,option_d,ans,language,hindi_ques,hindi_option_a,hindi_option_b,hindi_option_c,hindi_option_d,hindi_ans;

    public AddQuestionModel() {
    }

    public AddQuestionModel(String ques_no, String cat_id, String ques, String option_a, String option_b, String option_c, String option_d, String ans, String language, String hindi_ques, String hindi_option_a, String hindi_option_b, String hindi_option_c, String hindi_option_d, String hindi_ans) {
        this.ques_no = ques_no;
        this.cat_id = cat_id;
        this.ques = ques;
        this.option_a = option_a;
        this.option_b = option_b;
        this.option_c = option_c;
        this.option_d = option_d;
        this.ans = ans;
        this.language = language;
        this.hindi_ques = hindi_ques;
        this.hindi_option_a = hindi_option_a;
        this.hindi_option_b = hindi_option_b;
        this.hindi_option_c = hindi_option_c;
        this.hindi_option_d = hindi_option_d;
        this.hindi_ans = hindi_ans;
    }

    public String getQues_no() {
        return ques_no;
    }

    public void setQues_no(String ques_no) {
        this.ques_no = ques_no;
    }

    public String getCat_id() {
        return cat_id;
    }

    public void setCat_id(String cat_id) {
        this.cat_id = cat_id;
    }

    public String getQues() {
        return ques;
    }

    public void setQues(String ques) {
        this.ques = ques;
    }

    public String getOption_a() {
        return option_a;
    }

    public void setOption_a(String option_a) {
        this.option_a = option_a;
    }

    public String getOption_b() {
        return option_b;
    }

    public void setOption_b(String option_b) {
        this.option_b = option_b;
    }

    public String getOption_c() {
        return option_c;
    }

    public void setOption_c(String option_c) {
        this.option_c = option_c;
    }

    public String getOption_d() {
        return option_d;
    }

    public void setOption_d(String option_d) {
        this.option_d = option_d;
    }

    public String getAns() {
        return ans;
    }

    public void setAns(String ans) {
        this.ans = ans;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getHindi_ques() {
        return hindi_ques;
    }

    public void setHindi_ques(String hindi_ques) {
        this.hindi_ques = hindi_ques;
    }

    public String getHindi_option_a() {
        return hindi_option_a;
    }

    public void setHindi_option_a(String hindi_option_a) {
        this.hindi_option_a = hindi_option_a;
    }

    public String getHindi_option_b() {
        return hindi_option_b;
    }

    public void setHindi_option_b(String hindi_option_b) {
        this.hindi_option_b = hindi_option_b;
    }

    public String getHindi_option_c() {
        return hindi_option_c;
    }

    public void setHindi_option_c(String hindi_option_c) {
        this.hindi_option_c = hindi_option_c;
    }

    public String getHindi_option_d() {
        return hindi_option_d;
    }

    public void setHindi_option_d(String hindi_option_d) {
        this.hindi_option_d = hindi_option_d;
    }

    public String getHindi_ans() {
        return hindi_ans;
    }

    public void setHindi_ans(String hindi_ans) {
        this.hindi_ans = hindi_ans;
    }
}
