package com.upsun.quizz.Model;

/**
 * Developed by Binplus Technologies pvt. ltd.  on 20,April,2020
 */
public class PointsModel {
    String id,points,amount;

    public PointsModel() {
    }

    public PointsModel(String id, String points, String amount) {
        this.id = id;
        this.points = points;
        this.amount = amount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
