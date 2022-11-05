package com.upsun.quizz.Model;

/**
 * Developed by Binplus Technologies pvt. ltd.  on 17,December,2020
 */
public class QuesModel {
    String que_id;
    AddQuestionModel model;

    public QuesModel() {
    }

    public QuesModel(String que_id, AddQuestionModel model) {
        this.que_id = que_id;
        this.model = model;
    }

    public String getQue_id() {
        return que_id;
    }

    public void setQue_id(String que_id) {
        this.que_id = que_id;
    }

    public AddQuestionModel getModel() {
        return model;
    }

    public void setModel(AddQuestionModel model) {
        this.model = model;
    }
}
