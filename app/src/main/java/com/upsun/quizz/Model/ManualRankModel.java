package com.upsun.quizz.Model;

/**
 * Developed by Binplus Technologies pvt. ltd.  on 02,June,2020
 */
public class ManualRankModel {
    String user_id,username,rank,rewards;

    public ManualRankModel() {
    }

    public ManualRankModel(String user_id,String username, String rank, String rewards) {
        this.user_id = user_id;
        this.username=username;
        this.rank = rank;
        this.rewards = rewards;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
