package com.upsun.quizz.Model;

/**
 * Developed by Binplus Technologies pvt. ltd.  on 03,May,2020
 */
public class ViewRankModel {
    String rank,rewards;

    public ViewRankModel() {
    }

    public ViewRankModel(String rank, String rewards) {
        this.rank = rank;
        this.rewards = rewards;
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
}
