package com.upsun.quizz.Model;

import java.util.Comparator;

public class NewRankModel {
    String result,user_id,username,rank;

    public NewRankModel(String result, String user_id, String username, String rank) {
        this.result = result;
        this.user_id = user_id;
        this.username = username;
        this.rank = rank;
    }

    public NewRankModel() {
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public static Comparator<NewRankModel> position=new Comparator<NewRankModel>() {
        @Override
        public int compare(NewRankModel o1, NewRankModel o2) {
            int s1=Integer.parseInt(o1.rank);
            int s2=Integer.parseInt(o2.rank);
            return  s1-s2;
        }
    };

    @Override
    public String toString() {
        return "NewRankModel{" +
                "result='" + result + '\'' +
                ", user_id='" + user_id + '\'' +
                ", username='" + username + '\'' +
                ", rank='" + rank + '\'' +
                '}';
    }
}
