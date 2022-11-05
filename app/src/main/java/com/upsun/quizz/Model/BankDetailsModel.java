package com.upsun.quizz.Model;

/**
 * Developed by Binplus Technologies pvt. ltd.  on 25,April,2020
 */
public class BankDetailsModel {
    String pay_id,user_id,type,upi,bank_name,ifsc,acc_no,h_name;

    public BankDetailsModel() {
    }

    public BankDetailsModel(String pay_id, String user_id, String type, String upi, String bank_name, String ifsc, String acc_no, String h_name) {
        this.pay_id = pay_id;
        this.user_id = user_id;
        this.type = type;
        this.upi = upi;
        this.bank_name = bank_name;
        this.ifsc = ifsc;
        this.acc_no = acc_no;
        this.h_name = h_name;
    }

    public String getPay_id() {
        return pay_id;
    }

    public void setPay_id(String pay_id) {
        this.pay_id = pay_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUpi() {
        return upi;
    }

    public void setUpi(String upi) {
        this.upi = upi;
    }

    public String getBank_name() {
        return bank_name;
    }

    public void setBank_name(String bank_name) {
        this.bank_name = bank_name;
    }

    public String getIfsc() {
        return ifsc;
    }

    public void setIfsc(String ifsc) {
        this.ifsc = ifsc;
    }

    public String getAcc_no() {
        return acc_no;
    }

    public void setAcc_no(String acc_no) {
        this.acc_no = acc_no;
    }

    public String getH_name() {
        return h_name;
    }

    public void setH_name(String h_name) {
        this.h_name = h_name;
    }
}
