package com.example.shivamsapp.Models;

public class Comments {
    private String CommentedText, CommentedBy;
    private long CommentedAt;

    public Comments() {
    }


    public String getCommentedText() {
        return CommentedText;
    }

    public void setCommentedText(String commentedText) {
        CommentedText = commentedText;
    }

    public String getCommentedBy() {
        return CommentedBy;
    }

    public void setCommentedBy(String commentedBy) {
        CommentedBy = commentedBy;
    }

    public long getCommentedAt() {
        return CommentedAt;
    }

    public void setCommentedAt(long commentedAt) {
        CommentedAt = commentedAt;
    }
}
