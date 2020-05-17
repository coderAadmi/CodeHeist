package com.prady.codeheist.datamodels;

import androidx.annotation.NonNull;

import com.google.firebase.Timestamp;

import java.util.Date;

public class Comment {

    String fromName;
    String fromId;
    String fromImg;
    Timestamp timestamp;
    Timestamp edited;
    long likes;
    long dislikes;
    String text;

    public Comment() {
    }

    public Comment(String fromName, String fromId, String fromImg, String text) {
        this.fromName = fromName;
        this.fromId = fromId;
        this.fromImg = fromImg;
        this.text = text;
        this.timestamp = new Timestamp(new Date());
        this.edited = new Timestamp(new Date());
        likes = 0;
        dislikes = 0;
    }

    public String getFromName() {
        return fromName;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

    public String getFromId() {
        return fromId;
    }

    public void setFromId(String fromId) {
        this.fromId = fromId;
    }

    public String getFromImg() {
        return fromImg;
    }

    public void setFromImg(String fromImg) {
        this.fromImg = fromImg;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public Timestamp getEdited() {
        return edited;
    }

    public void setEdited(Timestamp edited) {
        this.edited = edited;
    }

    public long getLikes() {
        return likes;
    }

    public void setLikes(long likes) {
        this.likes = likes;
    }

    public long getDislikes() {
        return dislikes;
    }

    public void setDislikes(long dislikes) {
        this.dislikes = dislikes;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @NonNull
    @Override
    public String toString() {
        return "By: "+fromName+" text : "+text;
    }
}
