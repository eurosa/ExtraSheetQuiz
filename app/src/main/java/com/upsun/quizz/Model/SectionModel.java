package com.upsun.quizz.Model;

import java.util.Comparator;

/**
 * Developed by Binplus Technologies pvt. ltd.  on 15,December,2020
 */
public class SectionModel {
   public String section_id,quiz_id,section_name;
   public String days;

    public SectionModel() {
    }

    public SectionModel(String section_id, String quiz_id, String section_name, String days) {
        this.section_id = section_id;
        this.quiz_id = quiz_id;
        this.section_name = section_name;
        this.days = days;
    }

    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }

    public String getQuiz_id() {
        return quiz_id;
    }

    public void setQuiz_id(String quiz_id) {
        this.quiz_id = quiz_id;
    }

    public String getSection_name() {
        return section_name;
    }

    public void setSection_name(String section_name) {
        this.section_name = section_name;
    }

    public String getSection_id() {
        return section_id;
    }

    public void setSection_id(String section_id) {
        this.section_id = section_id;
    }

    public static Comparator<SectionModel> orderCamp=new Comparator<SectionModel>() {
        @Override
        public int compare(SectionModel o1, SectionModel o2) {
            int day1=Integer.parseInt(o1.getDays().toString());
            int day2=Integer.parseInt(o2.getDays().toString());
            return day1-day2;
        }
    };
}
