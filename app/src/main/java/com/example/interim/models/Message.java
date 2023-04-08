package com.example.interim.models;

import java.util.Date;

public class Message {
    private String _id;
    private String senderId;
    private Date date;
    private String text;

    public Message(String senderId, Date date, String text) {
        this.senderId = senderId;
        this.date = date;
        this.text = text;
    }

    public Message() {
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
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
