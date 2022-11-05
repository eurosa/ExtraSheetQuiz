package com.upsun.quizz.Model;

import java.util.Comparator;

/**
 * Developed by Binplus Technologies pvt. ltd.  on 26,April,2020
 */
public class CreditTransactionModel {
    String amount;
    String points;
    String status;
    String txn_id;
    String user_id;
    String days;

    public String getReferredTo() {
        return referredTo;
    }

    public void setReferredTo(String referredTo) {
        this.referredTo = referredTo;
    }

    String referredTo;

    public CreditTransactionModel() {
    }

    public CreditTransactionModel(String amount, String points, String status, String txn_id, String user_id) {
        this.amount = amount;
        this.points = points;
        this.status = status;
        this.txn_id = txn_id;
        this.user_id = user_id;
    }

    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTxn_id() {
        return txn_id;
    }

    public void setTxn_id(String txn_id) {
        this.txn_id = txn_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public static Comparator<CreditTransactionModel> camp_trans=new Comparator<CreditTransactionModel>() {
        @Override
        public int compare(CreditTransactionModel o1, CreditTransactionModel o2) {
            int days1=Integer.parseInt(o1.getDays().toString());
            int days2=Integer.parseInt(o2.getDays().toString());
            return days1-days2;
        }
    };
}
