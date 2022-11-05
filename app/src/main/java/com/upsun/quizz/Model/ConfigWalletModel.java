package com.upsun.quizz.Model;

/**
 * Developed by Binplus Technologies pvt. ltd.  on 18,April,2020
 */
public class ConfigWalletModel {
    String amount;

    public ConfigWalletModel() {
    }

    public ConfigWalletModel(String amount) {
        this.amount = amount;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
