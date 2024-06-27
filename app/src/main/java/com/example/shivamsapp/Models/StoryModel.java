package com.example.shivamsapp.Models;

import java.util.ArrayList;

public class StoryModel {
    private String storyBy;
    private long stotoryAt;
    ArrayList<UsersStory>stories;

    public StoryModel() {
    }

    public String getStoryBy() {
        return storyBy;
    }

    public void setStoryBy(String storyBy) {
        this.storyBy = storyBy;
    }

    public long getStotoryAt() {
        return stotoryAt;
    }

    public void setStotoryAt(long stotoryAt) {
        this.stotoryAt = stotoryAt;
    }

    public ArrayList<UsersStory> getStories() {
        return stories;
    }

    public void setStories(ArrayList<UsersStory> stories) {
        this.stories = stories;
    }
}