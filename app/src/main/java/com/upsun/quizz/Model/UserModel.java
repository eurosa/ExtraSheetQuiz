package com.upsun.quizz.Model;

/**
 * Developed by Binplus Technologies pvt. ltd.  on 04,April,2020
 */
public class UserModel {
    String  id,name,email,mobile,password,status,img_url,wallet,rewards,quiz_status,device_id,referCode,referLink;


    public UserModel() {
    }

    public String getReferCode() {
        return referCode;
    }

    public String getReferLink() {
        return referLink;
    }

    public UserModel(String id, String name, String email, String mobile, String password, String status, String img_url, String wallet, String rewards, String quiz_status, String device_id) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.mobile = mobile;
        this.password = password;
        this.status = status;
        this.img_url = img_url;
        this.wallet = wallet;
        this.rewards = rewards;
        this.quiz_status = quiz_status;
        this.device_id = device_id;
    }

    public String getId() {
        return id;
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getWallet() {
        return wallet;
    }

    public void setWallet(String wallet) {
        this.wallet = wallet;
    }

    public String getRewards() {
        return rewards;
    }

    public void setRewards(String rewards) {
        this.rewards = rewards;
    }

    public String getQuiz_status() {
        return quiz_status;
    }

    public void setQuiz_status(String quiz_status) {
        this.quiz_status = quiz_status;
    }
}
