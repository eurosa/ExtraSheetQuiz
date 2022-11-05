package com.upsun.quizz.Model;

import java.util.Comparator;

public class WithdrawModel {
   String request_amount;
   String status;
   String user_id;
   String id,acc_no,bank_name,h_name,pay_id,type,upi,ifsc;
   String days;

    public WithdrawModel() {
    }

    public WithdrawModel(String request_amount, String status, String user_id, String id, String acc_no, String bank_name, String h_name, String pay_id, String type, String upi, String ifsc, String days) {
        this.request_amount = request_amount;
        this.status = status;
        this.user_id = user_id;
        this.id = id;
        this.acc_no = acc_no;
        this.bank_name = bank_name;
        this.h_name = h_name;
        this.pay_id = pay_id;
        this.type = type;
        this.upi = upi;
        this.ifsc = ifsc;
        this.days = days;
    }

    public String getRequest_amount() {
        return request_amount;
    }

    public void setRequest_amount(String request_amount) {
        this.request_amount = request_amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAcc_no() {
        return acc_no;
    }

    public void setAcc_no(String acc_no) {
        this.acc_no = acc_no;
    }

    public String getBank_name() {
        return bank_name;
    }

    public void setBank_name(String bank_name) {
        this.bank_name = bank_name;
    }

    public String getH_name() {
        return h_name;
    }

    public void setH_name(String h_name) {
        this.h_name = h_name;
    }

    public String getPay_id() {
        return pay_id;
    }

    public void setPay_id(String pay_id) {
        this.pay_id = pay_id;
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

    public String getIfsc() {
        return ifsc;
    }

    public void setIfsc(String ifsc) {
        this.ifsc = ifsc;
    }

    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }

    public static Comparator<WithdrawModel> camp_withd =new Comparator<WithdrawModel>() {
        @Override
        public int compare(WithdrawModel o1, WithdrawModel o2) {
            int days1=Integer.parseInt(o1.getDays().toString());
            int days2=Integer.parseInt(o2.getDays().toString());
            return days1-days2;
        }
    };
}
