package com.example.interim.models;

import java.util.Date;

public class Notification {
    private String notificationText;
    private String notificationTitle;
    private Date notificationDate;

    private String userId;

    public Notification() {
    }

    public Notification(String notificationText, String notificationTitle, Date notificationDate, String userId) {
        this.notificationText = notificationText;
        this.notificationTitle = notificationTitle;
        this.notificationDate = notificationDate;
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNotificationText() {
        return notificationText;
    }

    public void setNotificationText(String notificationText) {
        this.notificationText = notificationText;
    }

    public String getNotificationTitle() {
        return notificationTitle;
    }

    public void setNotificationTitle(String notificationTitle) {
        this.notificationTitle = notificationTitle;
    }

    public Date getNotificationDate() {
        return notificationDate;
    }

    public void setNotificationDate(Date notificationDate) {
        this.notificationDate = notificationDate;
    }

    @Override
    public String toString() {
        return "Notification{" +
                "notificationText='" + notificationText + '\'' +
                ", notificationTitle='" + notificationTitle + '\'' +
                ", notificationDate=" + notificationDate +
                ", userId='" + userId + '\'' +
                '}';
    }
}
