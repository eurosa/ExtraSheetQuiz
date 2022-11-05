package com.upsun.quizz.Model;

public class InfoPageModel {
    String title ;
    String content ;

    public InfoPageModel() {
    }

    public InfoPageModel(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
