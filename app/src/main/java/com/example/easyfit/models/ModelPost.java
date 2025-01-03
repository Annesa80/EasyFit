package com.example.easyfit.models;

public class ModelPost {
    private String pId;    // Post ID
    private String pDescr; // Post description
    private String pImage; // Post image URL
    private String uid;    // User ID (optional)
    private String uEmail; // User email (optional)
    private String uDp;    // User profile picture URL (optional)
    private String uName;  // User name (optional)

    // Default constructor (required for Firebase)
    public ModelPost() {}

    // Parameterized constructor


    public ModelPost(String pId, String pDescr, String pImage, String uid, String uEmail, String uDp, String uName) {
        this.pId = pId;
        this.pDescr = pDescr;
        this.pImage = pImage;
        this.uid = uid;
        this.uEmail = uEmail;
        this.uDp = uDp;
        this.uName = uName;
    }

    public String getpId() {
        return pId;
    }

    public void setpId(String pId) {
        this.pId = pId;
    }

    public String getpDescr() {
        return pDescr;
    }

    public void setpDescr(String pDescr) {
        this.pDescr = pDescr;
    }

    public String getpImage() {
        return pImage;
    }

    public void setpImage(String pImage) {
        this.pImage = pImage;
    }

    public String getuid() {
        return uid;
    }

    public void setuid(String uid) {
        this.uid = uid;
    }

    public String getuEmail() {
        return uEmail;
    }

    public void setuEmail(String uEmail) {
        this.uEmail = uEmail;
    }

    public String getuDp() {
        return uDp;
    }

    public void setuDp(String uDp) {
        this.uDp = uDp;
    }

    public String getuName() {
        return uName;
    }

    public void setuName(String uName) {
        this.uName = uName;
    }
}
