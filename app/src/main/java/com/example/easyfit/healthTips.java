package com.example.easyfit;

public class healthTips {
    private String tip;

    // Default constructor required for Firestore and Realtime Database
    public healthTips() {
    }
    public healthTips(String tip) {
        this.tip = tip;
    }

    public String getTip() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip = tip;
    }
}