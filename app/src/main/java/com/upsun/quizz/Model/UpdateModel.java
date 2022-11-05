package com.upsun.quizz.Model;

/**
 * Developed by Binplus Technologies pvt. ltd.  on 28,April,2020
 */
public class UpdateModel {
    String app_link,app_version,share_link,rewards_amt,wallet_amt,withdraw_limit;

    public UpdateModel() {
    }

    public UpdateModel(String app_link, String app_version, String share_link, String rewards_amt, String wallet_amt, String withdraw_limit) {
        this.app_link = app_link;
        this.app_version = app_version;
        this.share_link = share_link;
        this.rewards_amt = rewards_amt;
        this.wallet_amt = wallet_amt;
        this.withdraw_limit = withdraw_limit;
    }

    public String getApp_link() {
        return app_link;
    }

    public void setApp_link(String app_link) {
        this.app_link = app_link;
    }

    public String getApp_version() {
        return app_version;
    }

    public void setApp_version(String app_version) {
        this.app_version = app_version;
    }

    public String getShare_link() {
        return share_link;
    }

    public void setShare_link(String share_link) {
        this.share_link = share_link;
    }

    public String getRewards_amt() {
        return rewards_amt;
    }

    public void setRewards_amt(String rewards_amt) {
        this.rewards_amt = rewards_amt;
    }

    public String getWallet_amt() {
        return wallet_amt;
    }

    public void setWallet_amt(String wallet_amt) {
        this.wallet_amt = wallet_amt;
    }

    public String getWithdraw_limit() {
        return withdraw_limit;
    }

    public void setWithdraw_limit(String withdraw_limit) {
        this.withdraw_limit = withdraw_limit;
    }
}
