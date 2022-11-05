package com.upsun.quizz.Intefaces;

import com.upsun.quizz.Model.NewRankModel;
import com.upsun.quizz.Model.RewardModel;

import java.util.ArrayList;

/**
 * Developed by Binplus Technologies pvt. ltd.  on 02,June,2020
 */
public interface OnRankListener {
    void getRankRewards(ArrayList<RewardModel> list);
    void getCalListRewards(ArrayList<NewRankModel> callist);
}
