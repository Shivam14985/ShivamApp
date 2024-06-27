package com.example.shivamsapp.Models;

public class UsersStory {
    private String storyImage;
    private long storyAt;

    public UsersStory(String storyImage, long storyAt) {
        this.storyImage = storyImage;
        this.storyAt = storyAt;
    }

    public UsersStory() {
    }

    public String getStoryImage() {
        return storyImage;
    }

    public void setStoryImage(String storyImage) {
        this.storyImage = storyImage;
    }

    public long getStoryAt() {
        return storyAt;
    }

    public void setStoryAt(long storyAt) {
        this.storyAt = storyAt;
    }
}
