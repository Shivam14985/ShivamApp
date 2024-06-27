package com.example.shivamsapp.Models;

public class HomeModel {

    private String postID, postImg, posteBy, postDescription;
    private int postLike, CommentsNumber;
    private long postedAt;


    public HomeModel() {
    }

    public HomeModel(int postLike) {
        this.postLike = postLike;
    }

    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public String getPostImg() {
        return postImg;
    }

    public void setPostImg(String postImg) {
        this.postImg = postImg;
    }

    public String getPosteBy() {
        return posteBy;
    }

    public void setPosteBy(String posteBy) {
        this.posteBy = posteBy;
    }

    public String getPostDescription() {
        return postDescription;
    }

    public void setPostDescription(String postDescription) {
        this.postDescription = postDescription;
    }

    public long getPostedAt() {
        return postedAt;
    }

    public void setPostedAt(long postedAt) {
        this.postedAt = postedAt;
    }

    public int getPostLike() {
        return postLike;
    }

    public void setPostLike(int postLike) {
        this.postLike = postLike;
    }

    public int getCommentsNumber() {
        return CommentsNumber;
    }

    public void setCommentsNumber(int commentsNumber) {
        CommentsNumber = commentsNumber;
    }
}
