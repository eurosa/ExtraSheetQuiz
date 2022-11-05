package com.upsun.quizz.Model;

import java.util.Comparator;

public class ContactUsModel {
    String name ;
    String email ;
    String subject ;
    String message ;
    String id;

    public ContactUsModel() {
    }

    public ContactUsModel(String name, String email, String subject, String message, String id) {
        this.name = name;
        this.email = email;
        this.subject = subject;
        this.message = message;
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

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public static Comparator<ContactUsModel> max=new Comparator<ContactUsModel>() {
        @Override
        public int compare(ContactUsModel o1, ContactUsModel o2) {

            int sum1=sumAll(o1.id);
            int sum2=sumAll(o2.id);
            return sum1-sum2;
        }
    };

    public static int sumAll(String str)
    {
        int sum=0;
        for(int i=0; i<str.length();i++)
        {
            sum=sum+Integer.parseInt(String.valueOf(str.charAt(i)));
        }
        return sum;
    }


}
