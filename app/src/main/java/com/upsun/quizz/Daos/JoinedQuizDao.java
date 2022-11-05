package com.upsun.quizz.Daos;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.upsun.quizz.Database.BaseDao;
import com.upsun.quizz.Model.JoinedQuizModel;

import java.util.List;

/**
 * Developed by Binplus Technologies pvt. ltd.  on 11,June,2021
 */
@Dao
public interface JoinedQuizDao extends BaseDao<JoinedQuizModel> {
    @Query("Select *  from JoinedQuizModel")
    List<JoinedQuizModel> getAllJoinedQuiz();

    @Query("Select Count(*) from JoinedQuizModel")
    long getJoinQuizCount();

    @Insert
    void insert(JoinedQuizModel model);

    @Update
    void update(JoinedQuizModel model);

    @Query("Delete  from JoinedQuizModel")
    void delete();

}
