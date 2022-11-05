package com.upsun.quizz.Model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Comparator;

@Entity
public class JoinedQuizModel {
    @PrimaryKey(autoGenerate = true)
    private int jid;
    private String join_id;
    private String quiz_id ;
    private String user_id ;
    private String description ;
    private String entry_fee ;
    private String participents;
    private String questions ;
    private String quiz_date ;
    private String quiz_end_time ;
    private String quiz_start_time;
    private String title ;
    private String instruction;
    private String language;
    private String join_date;
    private String days;
    private String date;
    private String time;

    public JoinedQuizModel() {
    }

    public JoinedQuizModel(String join_id, String quiz_id, String user_id, String description, String entry_fee, String participents, String questions, String quiz_date, String quiz_end_time, String quiz_start_time, String title, String instruction, String language,String join_date ,String days) {
        this.join_id = join_id;
        this.quiz_id = quiz_id;
        this.user_id = user_id;
        this.description = description;
        this.entry_fee = entry_fee;
        this.participents = participents;
        this.questions = questions;
        this.quiz_date = quiz_date;
        this.quiz_end_time = quiz_end_time;
        this.quiz_start_time = quiz_start_time;
        this.title = title;
        this.instruction = instruction;
        this.language = language;
        this.join_date = join_date;
        this.days = days;
    }

    public int getJid() {
        return jid;
    }

    public void setJid(int jid) {
        this.jid = jid;
    }

    public String getJoin_date() {
        return join_date;
    }

    public void setJoin_date(String join_date) {
        this.join_date = join_date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEntry_fee() {
        return entry_fee;
    }

    public void setEntry_fee(String entry_fee) {
        this.entry_fee = entry_fee;
    }

    public String getParticipents() {
        return participents;
    }

    public void setParticipents(String participents) {
        this.participents = participents;
    }

    public String getQuestions() {
        return questions;
    }

    public void setQuestions(String questions) {
        this.questions = questions;
    }

    public String getQuiz_date() {
        return quiz_date;
    }

    public void setQuiz_date(String quiz_date) {
        this.quiz_date = quiz_date;
    }

    public String getQuiz_end_time() {
        return quiz_end_time;
    }

    public void setQuiz_end_time(String quiz_end_time) {
        this.quiz_end_time = quiz_end_time;
    }

    public String getQuiz_start_time() {
        return quiz_start_time;
    }

    public void setQuiz_start_time(String quiz_start_time) {
        this.quiz_start_time = quiz_start_time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getInstruction() {
        return instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }

    public String getJoin_id() {
        return join_id;
    }

    public void setJoin_id(String join_id) {
        this.join_id = join_id;
    }

    public String getQuiz_id() {
        return quiz_id;
    }

    public void setQuiz_id(String quiz_id) {
        this.quiz_id = quiz_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public static Comparator<JoinedQuizModel> camp_jquiz=new Comparator<JoinedQuizModel>() {
        @Override
        public int compare(JoinedQuizModel o1, JoinedQuizModel o2) {
            int days1=Integer.parseInt(o1.getDays().toString());
            int days2=Integer.parseInt(o2.getDays().toString());
            return days1-days2;
        }
    };

    @Override
    public String toString() {
        return "JoinedQuizModel{" +
                "jid=" + jid +
                ", join_id='" + join_id + '\'' +
                ", quiz_id='" + quiz_id + '\'' +
                ", user_id='" + user_id + '\'' +
                ", description='" + description + '\'' +
                ", entry_fee='" + entry_fee + '\'' +
                ", participents='" + participents + '\'' +
                ", questions='" + questions + '\'' +
                ", quiz_date='" + quiz_date + '\'' +
                ", quiz_end_time='" + quiz_end_time + '\'' +
                ", quiz_start_time='" + quiz_start_time + '\'' +
                ", title='" + title + '\'' +
                ", instruction='" + instruction + '\'' +
                ", language='" + language + '\'' +
                ", join_date='" + join_date + '\'' +
                ", days='" + days + '\'' +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                '}';
    }
}
