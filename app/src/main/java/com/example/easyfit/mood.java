package com.example.easyfit;

public class mood {
    private String mood;
    private String date;


    public mood() {
        // Default constructor required for Firebase
    }

    public mood(String mood, String date) {
        this.mood = mood;
        this.date = date;
    }

    public String getMood() {
        return mood;
    }

    public void setMood(String a) {
        this.mood = a;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String b) {
        this.date = b;
    }
}
