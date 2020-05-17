package com.prady.codeheist.datamodels;

import com.google.firebase.Timestamp;

import java.util.HashMap;

public class Question {
    String title;
    HashMap<String,String> author;
    Timestamp time;
    HashMap<String, String> solution;

    public Question() {
    }

    public Question(String title, HashMap<String, String> author, Timestamp time, HashMap<String, String> solution) {
        this.title = title;
        this.author = author;
        this.time = time;
        this.solution = solution;
    }

    public String getTitle() {
        return title;
    }

    public HashMap<String, String> getAuthor() {
        return author;
    }

    public Timestamp getTime() {
        return time;
    }

    public HashMap<String, String> getSolution() {
        return solution;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(HashMap<String, String> author) {
        this.author = author;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    public void setSolution(HashMap<String, String> solution) {
        this.solution = solution;
    }
}
