package com.upsun.quizz.Intefaces;

import com.upsun.quizz.Model.JoinedQuizModel;

import java.util.ArrayList;

/**
 * Developed by Binplus Technologies pvt. ltd.  on 26,May,2020
 */
public interface OnJoinedQuizData {
    void getJoinedList(ArrayList<JoinedQuizModel> list);
    void getUserJoinedList(ArrayList<JoinedQuizModel> list);
}
