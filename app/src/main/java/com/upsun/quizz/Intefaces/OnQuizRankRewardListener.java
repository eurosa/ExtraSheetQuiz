package com.upsun.quizz.Intefaces;

import com.upsun.quizz.Model.QuizRankRewardModel;

import java.util.ArrayList;

/**
 * Developed by Binplus Technologies pvt. ltd.  on 06,June,2020
 */
public interface OnQuizRankRewardListener {
    void getQuizRankRewards(ArrayList<QuizRankRewardModel> rewardList);
    void getUserQuizRank(ArrayList<String> rankList);
}
