package com.upsun.quizz.Model;

import androidx.annotation.NonNull;

public class RewardModel {
    String rank ;
    String rewards ;

    public RewardModel() {
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

    @NonNull
    @Override
    public String toString() {
        return "("+"\t rank="+rank+"\t rewards="+rewards+"\t)";
    }
}
