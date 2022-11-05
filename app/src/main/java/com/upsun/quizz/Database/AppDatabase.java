package com.upsun.quizz.Database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.upsun.quizz.Daos.JoinedQuizDao;
import com.upsun.quizz.Daos.QuizResultDao;
import com.upsun.quizz.Daos.RankRewardsDao;
import com.upsun.quizz.Model.JoinedQuizModel;
import com.upsun.quizz.Model.QuizRankRewardModel;
import com.upsun.quizz.Model.QuizResultModel;

/**
 * Developed by Binplus Technologies pvt. ltd.  on 11,June,2021
 */
@Database(entities = {JoinedQuizModel.class, QuizRankRewardModel.class, QuizResultModel.class}, version =19,exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static final String DB_NAME="quizroom2_db";
    public abstract JoinedQuizDao getJoinedQuizDao();
    public abstract RankRewardsDao getRankRewardsDao();
    public abstract QuizResultDao getQuizResultDao();
    public static AppDatabase mAppDatabase;

    public static AppDatabase getInstance(Context context){
        if(null==mAppDatabase){
            mAppDatabase=buildAppDatabase(context);
        }
        return mAppDatabase;
    }

    public static AppDatabase buildAppDatabase(Context context){
        return Room.databaseBuilder(context,AppDatabase.class,DB_NAME)
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build();
    }



}
