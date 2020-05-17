package com.prady.codeheist.datamodels;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.TreeMap;

public class QuestionTitle implements Parcelable {

    String title;
    String id;
    HashMap<String,String> answerMap;

    public QuestionTitle() {
    }

    public QuestionTitle(Parcel in)
    {
        this.title = in.readString();
        this.id = in.readString();
        answerMap = (HashMap<String, String>) in.readSerializable();
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(id);
        dest.writeSerializable(answerMap);
    }

}
