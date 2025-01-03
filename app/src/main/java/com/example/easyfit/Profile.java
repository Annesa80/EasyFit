package com.example.easyfit;

public class Profile {

    private String name;
    private String username;
    private String email;
    private String gender;
    private String dob;
    private String phone;
    private String country;

    private String profileImageURL;  // Use exactly the same name as in Firebase

    // Default constructor required for Firebase
    public Profile() {
    }

    // Constructor with all fields
    public Profile(String name, String username, String email, String gender, String dob, String phone, String country, String profileImageURL) {
        this.name = name;
        this.username = username;
        this.email = email;
        this.gender = gender;
        this.dob = dob;
        this.phone = phone;
        this.country = country;
        this.profileImageURL = profileImageURL;  // Initialize profileImageURL
    }

    // Getter and setter methods for all fields, including profileImageURL
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getprofileImageURL() {
        return profileImageURL;
    }

    public void setprofileImageURL(String profileImageURL) {
        this.profileImageURL = profileImageURL;
    }
}
