package com.upsun.quizz.Model;

/**
 * Developed by Binplus Technologies pvt. ltd.  on 12,April,2020
 */
public class RankModel {
    String rank,rewards,quiz_id;

    public RankModel() {
    }

    public RankModel(String rank, String rewards, String quiz_id) {
        this.rank = rank;
        this.rewards = rewards;
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

    public String getQuiz_id() {
        return quiz_id;
    }

    public void setQuiz_id(String quiz_id) {
        this.quiz_id = quiz_id;
    }
}
