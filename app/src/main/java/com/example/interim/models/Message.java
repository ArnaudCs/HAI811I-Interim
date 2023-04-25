package com.example.interim.models;

import java.util.Date;

public class Message {
//    private String _id;
    private String sender;
    private Date date;
    private String text;

    private int mId;

    public int getmId() {
        return mId;
    }

    public void setmId(int mId) {
        this.mId = mId;
    }

    public Message(String sender, Date date, String text) {
        this.sender = sender;
        this.date = date;
        this.text = text;
    }

    public Message() {
    }

//    public String get_id() {
//        return _id;
//    }
//
//    public void set_id(String _id) {
//        this._id = _id;
//    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
