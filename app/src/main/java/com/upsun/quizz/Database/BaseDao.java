package com.upsun.quizz.Database;

import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Update;

import java.util.List;

/**
 * Developed by Binplus Technologies pvt. ltd.  on 11,June,2021
 */
public interface BaseDao<T> {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(T object);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<T> object);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void update(T Object);

    @Delete
    void delete(T object);
}
