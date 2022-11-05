package com.upsun.quizz.Model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Comparator;

@Entity
public class QuizRankRewardModel {
    @PrimaryKey(autoGenerate = true)
    private int rrid;
    private String id;
    private String quiz_id;
    private String rank;
    private String  rewards;
    private String user_id;
    private String days;
    private String date;
    private String time;

    public QuizRankRewardModel() {
    }

    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getRewards() {
        return rewards;
    }

    public void setRewards(String rewards) {
        this.rewards = rewards;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public int getRrid() {
        return rrid;
    }

    public void setRrid(int rrid) {
        this.rrid = rrid;
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

    public static Comparator<QuizRankRewardModel> camp_rank_reward=new Comparator<QuizRankRewardModel>() {
        @Override
        public int compare(QuizRankRewardModel o1, QuizRankRewardModel o2) {
            int days1=Integer.parseInt(o1.getDays().toString());
            int days2=Integer.parseInt(o2.getDays().toString());
            return days1-days2;

        }
    };

    @Override
    public String toString() {
        return "QuizRankRewardModel{" +
                "rrid=" + rrid +
                ", id='" + id + '\'' +
                ", quiz_id='" + quiz_id + '\'' +
                ", rank='" + rank + '\'' +
                ", rewards='" + rewards + '\'' +
                ", user_id='" + user_id + '\'' +
                ", days='" + days + '\'' +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                '}';
    }
}
