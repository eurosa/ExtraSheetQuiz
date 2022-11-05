package com.upsun.quizz.Daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.upsun.quizz.Database.BaseDao;
import com.upsun.quizz.Model.QuizResultModel;

import java.util.List;

/**
 * Developed by Binplus Technologies pvt. ltd.  on 11,June,2021
 */
@Dao
public interface QuizResultDao extends BaseDao<QuizResultModel> {
    @Query("Select *  from QuizResultModel")
    List<QuizResultModel> getAllQuizResult();

    @Query("Select Count(*) from QuizResultModel")
    long getQuizResultCount();

    @Insert
    void insert(QuizResultModel model);

    @Update
    void update(QuizResultModel model);

}
