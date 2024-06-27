package com.example.shivamsapp.Models;

public class FriendsModel {

    private String followeBy;
    private long followedAt;

    public FriendsModel() {
    }

    public String getFolloweBy() {
        return followeBy;
    }

    public void setFolloweBy(String followeBy) {
        this.followeBy = followeBy;
    }

    public long getFollowedAt() {
        return followedAt;
    }

    public void setFollowedAt(long followedAt) {
        this.followedAt = followedAt;
    }
}
