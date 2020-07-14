package com.prady.codeheist.datamodels;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.Timestamp;

import java.util.HashMap;
import java.util.TreeMap;

public class QuestionTitle implements Parcelable {

    String title;
    String id;//id of question
    HashMap<String,String> answerMap;
    String fromId;
    String fromImg;
    String fromName;
    String timestamp;
    String editedTime;

    public QuestionTitle() {
    }

    public QuestionTitle(Parcel in)
    {
        this.title = in.readString();
        this.id = in.readString();
        answerMap = (HashMap<String, String>) in.readSerializable();
        this.fromId = in.readString();
        this.fromImg = in.readString();
        this.fromName = in.readString();
        this.timestamp = in.readString();
        this.editedTime = in.readString();
    }

    public static final Creator<QuestionTitle> CREATOR = new Creator<QuestionTitle>() {
        @Override
        public QuestionTitle createFromParcel(Parcel source) {
            return new QuestionTitle(source);
        }

        @Override
        public QuestionTitle[] newArray(int size) {
            return new QuestionTitle[size];
        }
    };

    public void setAnswerMap(HashMap<String, String> answerMap) {
        this.answerMap = answerMap;
    }


    public TreeMap<String,String> getAnswerMap(){
        if(answerMap==null)
            return null;
        return new TreeMap<>(answerMap);
    }


    public String getTitle() {
        return title;
    }

    public String getId() {
        return id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getFromName() {
        return fromName;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getEditedTime() {
        return editedTime;
    }

    public void setEditedTime(String editedTime) {
        this.editedTime = editedTime;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(id);
        dest.writeSerializable(answerMap);
        dest.writeString(fromId);
        dest.writeString(fromImg);
        dest.writeString(fromName);
        dest.writeString(timestamp);
        dest.writeString(editedTime);
    }

}
