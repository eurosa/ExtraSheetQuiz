package com.upsun.quizz.Model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Comparator;

@Entity
public class QuizResultModel {
    @PrimaryKey(autoGenerate = true)
    private int resId;
    private String quiz_id ;
    private String user_id ;
    private String username;
    private String correct_ans ;
    private String wrong_ans ;
    private String percentage ;
    private String time_taken;
    private String status ;
    private String duration;
    private String result_id;
    private String tot_ques;
    private String section;
    private String date;
    private String time;

    public QuizResultModel() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public QuizResultModel(String quiz_id, String user_id, String username, String correct_ans, String wrong_ans, String percentage, String time_taken, String status, String duration, String result_id, String tot_ques, String section) {
        this.quiz_id = quiz_id;
        this.user_id = user_id;
        this.username = username;
        this.correct_ans = correct_ans;
        this.wrong_ans = wrong_ans;
        this.percentage = percentage;
        this.time_taken = time_taken;
        this.status = status;
        this.duration = duration;
        this.result_id = result_id;
        this.tot_ques = tot_ques;
        this.section = section;
    }

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getQuiz_id() {
        return quiz_id;
    }

    public void setQuiz_id(String quiz_id) {
        this.quiz_id = quiz_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getCorrect_ans() {
        return correct_ans;
    }

    public void setCorrect_ans(String correct_ans) {
        this.correct_ans = correct_ans;
    }

    public String getWrong_ans() {
        return wrong_ans;
    }

    public void setWrong_ans(String wrong_ans) {
        this.wrong_ans = wrong_ans;
    }

    public String getPercentage() {
        return percentage;
    }

    public void setPercentage(String percentage) {
        this.percentage = percentage;
    }

    public String getTime_taken() {
        return time_taken;
    }

    public void setTime_taken(String time_taken) {
        this.time_taken = time_taken;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getResult_id() {
        return result_id;
    }

    public void setResult_id(String result_id) {
        this.result_id = result_id;
    }

    public String getTot_ques() {
        return tot_ques;
    }

    public void setTot_ques(String tot_ques) {
        this.tot_ques = tot_ques;
    }

    //    public static Comparator<QuizResultModel> score= new Comparator<QuizResultModel>() {
//
//        public int compare(QuizResultModel q1, QuizResultModel q2) {
//
//
//          int s1 = Integer.parseInt(q1.getCorrect_ans());
//          int s2 = Integer.parseInt(q2.getCorrect_ans());
//
//
//
////     return ComparisonChain.start().compare(s1,s2).compare(w1,w2).compare(t1,t2).result();
////            /*For ascending order*/
////            return s1-s2;
//            /*For descending order*/
//           return s2-s1;
//        }};
//    @Override
//    public String toString() {
//        return "[ quiz_id =" + quiz_id + ", user_id =" +user_id + ",correct_ans =" +correct_ans +",wrong_ans ="+wrong_ans+",percentage ="+percentage+",time =" +time_taken+"]";
//    }
    public static Comparator<QuizResultModel> score=new Comparator<QuizResultModel>() {
        @Override
        public int compare(QuizResultModel o1, QuizResultModel o2) {
            int s1=Integer.parseInt(o1.correct_ans);
            int s2=Integer.parseInt(o2.correct_ans);
            return  s2-s1;
        }
    };

    @Override
    public String toString() {
        return "QuizResultModel{" +
                "resId=" + resId +
                ", quiz_id='" + quiz_id + '\'' +
                ", user_id='" + user_id + '\'' +
                ", username='" + username + '\'' +
                ", correct_ans='" + correct_ans + '\'' +
                ", wrong_ans='" + wrong_ans + '\'' +
                ", percentage='" + percentage + '\'' +
                ", time_taken='" + time_taken + '\'' +
                ", status='" + status + '\'' +
                ", duration='" + duration + '\'' +
                ", result_id='" + result_id + '\'' +
                ", tot_ques='" + tot_ques + '\'' +
                ", section='" + section + '\'' +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                '}';
    }
}
