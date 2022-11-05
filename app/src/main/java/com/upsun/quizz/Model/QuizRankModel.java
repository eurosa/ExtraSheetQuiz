package com.upsun.quizz.Model;

public class QuizRankModel {
    String quiz_id ;
    String rank ;
    String c_ans ;
    String w_ans ;
    String user_id;

    public QuizRankModel(String quiz_id, String rank, String c_ans, String w_ans, String user_id) {
        this.quiz_id = quiz_id;
        this.rank = rank;
        this.c_ans = c_ans;
        this.w_ans = w_ans;
        this.user_id = user_id;
    }

    public String getQuiz_id() {
        return quiz_id;
    }

    public void setQuiz_id(String quiz_id) {
        this.quiz_id = quiz_id;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getC_ans() {
        return c_ans;
    }

    public void setC_ans(String c_ans) {
        this.c_ans = c_ans;
    }

    public String getW_ans() {
        return w_ans;
    }

    public void setW_ans(String w_ans) {
        this.w_ans = w_ans;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    @Override
    public String toString() {
        return "[ quiz_id =" + quiz_id + ", user_id =" +user_id + ",correct_ans =" +c_ans +",wrong_ans ="+w_ans+",rank ="+rank+"]";
    }
}
