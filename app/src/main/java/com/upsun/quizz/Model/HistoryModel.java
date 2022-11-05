package com.upsun.quizz.Model;

import java.util.Comparator;

/**
 * Developed by Binplus Technologies pvt. ltd.  on 14,June,2021
 */
public class HistoryModel {
    String quiz_id;
    String quiz_name;
    String start_time;
    String end_time;
    String quiz_date;
    String date;
    String time;
    String entry_fees;
    String id;
    String user_id;
    String answer;
    String rank;
    String reward;
    String section_id;
    private String days;

    public HistoryModel() {
    }

    public HistoryModel(String quiz_id, String quiz_name, String start_time, String end_time, String quiz_date, String date, String time, String entry_fees, String id, String user_id, String answer, String rank, String reward, String section_id) {
        this.quiz_id = quiz_id;
        this.quiz_name = quiz_name;
        this.start_time = start_time;
        this.end_time = end_time;
        this.quiz_date = quiz_date;
        this.date = date;
        this.time = time;
        this.entry_fees = entry_fees;
        this.id = id;
        this.user_id = user_id;
        this.answer = answer;
        this.rank = rank;
        this.reward = reward;
        this.section_id = section_id;
    }

    public String getQuiz_id() {
        return quiz_id;
    }

    public void setQuiz_id(String quiz_id) {
        this.quiz_id = quiz_id;
    }

    public String getQuiz_name() {
        return quiz_name;
    }

    public void setQuiz_name(String quiz_name) {
        this.quiz_name = quiz_name;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public String getQuiz_date() {
        return quiz_date;
    }

    public void setQuiz_date(String quiz_date) {
        this.quiz_date = quiz_date;
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

    public String getEntry_fees() {
        return entry_fees;
    }

    public void setEntry_fees(String entry_fees) {
        this.entry_fees = entry_fees;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getReward() {
        return reward;
    }

    public void setReward(String reward) {
        this.reward = reward;
    }

    public String getSection_id() {
        return section_id;
    }

    public void setSection_id(String section_id) {
        this.section_id = section_id;
    }

    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }

    public static Comparator<HistoryModel> camp_history=new Comparator<HistoryModel>() {
        @Override
        public int compare(HistoryModel o1, HistoryModel o2) {
            int days1=Integer.parseInt(o1.getDays().toString());
            int days2=Integer.parseInt(o2.getDays().toString());
            return days1-days2;
        }
    };

    @Override
    public String toString() {
        return "HistoryModel{" +
                "quiz_id='" + quiz_id + '\'' +
                ", quiz_name='" + quiz_name + '\'' +
                ", start_time='" + start_time + '\'' +
                ", end_time='" + end_time + '\'' +
                ", quiz_date='" + quiz_date + '\'' +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", entry_fees='" + entry_fees + '\'' +
                ", id='" + id + '\'' +
                ", user_id='" + user_id + '\'' +
                ", answer='" + answer + '\'' +
                ", rank='" + rank + '\'' +
                ", reward='" + reward + '\'' +
                ", section_id='" + section_id + '\'' +
                ", days='" + days + '\'' +
                '}';
    }
}
