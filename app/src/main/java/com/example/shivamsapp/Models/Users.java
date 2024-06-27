package com.example.shivamsapp.Models;

public class Users {

    private String name, profession, email, password;
    private String BackgroundProfile;
    private String userUid;
    private String Profile;
    private int followerCount;

    public Users(String name, String profession, String email, String password) {
        this.name = name;
        this.profession = profession;
        this.email = email;
        this.password = password;
    }

    public Users(String backgroundProfile) {
        BackgroundProfile = backgroundProfile;
    }

    public Users() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBackgroundProfile() {
        return BackgroundProfile;
    }

    public void setBackgroundProfile(String backgroundProfile) {
        BackgroundProfile = backgroundProfile;
    }

    public String getProfile() {
        return Profile;
    }

    public void setProfile(String profile) {
        Profile = profile;
    }

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }

    public int getFollowerCount() {
        return followerCount;
    }

    public void setFollowerCount(int followerCount) {
        this.followerCount = followerCount;
    }
}
