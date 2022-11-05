package com.upsun.quizz.Model;

import java.util.Comparator;

public class QuizModel {

    String description ;
    String entry_fee ;
    String participents;
    String questions ;
    String quiz_date ;
    String quiz_end_time ;
    String quiz_id;
    String quiz_start_time;
    String title ;
    String instruction;
    String language;
    String cat_id;
    String cat_name;
    String question_time = "";
    String quizType;

    public String getFee_type() {
        return fee_type;
    }

    public void setFee_type(String fee_type) {
        this.fee_type = fee_type;
    }

    String days;
    String duration;
    String rank_visible;
    String fee_type;
    String reward_fee;

    public String getReward_fee() {
        return reward_fee;
    }

    public void setReward_fee(String reward_fee) {
        this.reward_fee = reward_fee;
    }

    public QuizModel() {
    }

    public QuizModel(String description, String entry_fee, String reward_fee, String participents, String questions, String quiz_date, String quiz_end_time, String quiz_id, String quiz_start_time, String title, String instruction, String language, String cat_id, String cat_name, String days, String duration , String rank_visible, String quizType, String question_time) {
        this.description = description;
        this.entry_fee = entry_fee;
        this.reward_fee = reward_fee;
        this.participents = participents;
        this.questions = questions;
        this.quiz_date = quiz_date;
        this.quiz_end_time = quiz_end_time;
        this.quiz_id = quiz_id;
        this.quiz_start_time = quiz_start_time;
        this.title = title;
        this.instruction = instruction;
        this.language = language;
        this.cat_id = cat_id;
        this.cat_name = cat_name;
        this.days = days;
        this.duration = duration;
        this.rank_visible = rank_visible;
        this.question_time = question_time;
        this.quizType = quizType;
    }

    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }

    public String getRank_visible() {
        return rank_visible;
    }

    public void setRank_visible(String rank_visible) {
        this.rank_visible = rank_visible;
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

    public String getQuiz_id() {
        return quiz_id;
    }

    public void setQuiz_id(String quiz_id) {
        this.quiz_id = quiz_id;
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

    public String getCat_id() {
        return cat_id;
    }

    public void setCat_id(String cat_id) {
        this.cat_id = cat_id;
    }

    public String getCat_name() {
        return cat_name;
    }

    public void setCat_name(String cat_name) {
        this.cat_name = cat_name;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getQuestion_time() {
        return question_time;
    }

    public void setQuestion_time(String question_time) {
        this.question_time = question_time;
    }

    public String getQuizType() {
        return quizType;
    }

    public void setQuizType(String quizType) {
        this.quizType = quizType;
    }

    public static Comparator<QuizModel> camp_quiz=new Comparator<QuizModel>() {
        @Override
        public int compare(QuizModel o1, QuizModel o2) {
            int days1=Integer.parseInt(o1.getDays().toString());
            int days2=Integer.parseInt(o2.getDays().toString());
            return days1-days2;
        }
    };

}
