package com.prady.codeheist.datamodels;

import com.google.firebase.Timestamp;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

public class Answer {

    String id;//id of answer
    String questionId;//id of question
    String qTitle;
    String fromName;
    String fromId;
    String fromImg;
    Timestamp timestamp;
    Timestamp edited;
    long likes;
    long dislikes;
    TreeMap<String,String> map;
    List<Comment> comments;

    public List<Comment> getComments() {
        return comments;
    }

    public Answer() {
    }

    public Answer(String qTitle, String fromName, String fromId, String fromImg, TreeMap<String, String> map) {
        this.qTitle = qTitle;
        this.fromName = fromName;
        this.fromId = fromId;
        this.fromImg = fromImg;
        this.map = map;
        this.timestamp = new Timestamp(new Date());
        this.edited = new Timestamp(new Date());
        likes = 0;
        dislikes = 0;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String getqTitle() {
        return qTitle;
    }

    public void setqTitle(String qTitle) {
        this.qTitle = qTitle;
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

    public TreeMap<String, String> getMap() {
        return map;
    }

    public void setMap(HashMap<String, String> map) {
        this.map = new TreeMap<>(map);
    }
}
