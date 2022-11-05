package com.upsun.quizz.Daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.upsun.quizz.Database.BaseDao;
import com.upsun.quizz.Model.QuizRankRewardModel;

import java.util.List;

/**
 * Developed by Binplus Technologies pvt. ltd.  on 11,June,2021
 */
@Dao
public interface RankRewardsDao extends BaseDao<QuizRankRewardModel> {
    @Query("Select *  from QuizRankRewardModel")
    List<QuizRankRewardModel> getAllQuizRnkRewards();

    @Query("Select Count(*) from QuizRankRewardModel")
    long getRankRewardsCount();

    @Insert
    void insert(QuizRankRewardModel model);

    @Update
    void update(QuizRankRewardModel model);

}
