package com.example.interim.models;

import java.util.Date;

public class SignaledOffer {
    private Date signalDate;
    private String userID;
    private String offerId;

    private String signalText;

    private String userMail;

    public String getUserMail() {
        return userMail;
    }

    public void setUserMail(String userMail) {
        this.userMail = userMail;
    }

    public String getOfferId() {
        return offerId;
    }

    public void setOfferId(String offerId) {
        this.offerId = offerId;
    }

    public SignaledOffer() {
    }

    public SignaledOffer(Date signalDate, String userID, String offerId, String signalText, String userMail) {
        this.signalDate = signalDate;
        this.userID = userID;
        this.offerId = offerId;
        this.signalText = signalText;
        this.userMail = userMail;
    }

    public Date getSignalDate() {
        return signalDate;
    }

    public void setSignalDate(Date signalDate) {
        this.signalDate = signalDate;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getApplicationId() {
        return offerId;
    }

    public void setApplicationId(String applicationId) {
        this.offerId = applicationId;
    }

    public String getSignalText() {
        return signalText;
    }

    public void setSignalText(String signalText) {
        this.signalText = signalText;
    }

    @Override
    public String toString() {
        return "SignaledOffer{" +
                "signalDate=" + signalDate +
                ", userID='" + userID + '\'' +
                ", offerId='" + offerId + '\'' +
                ", signalText='" + signalText + '\'' +
                ", userMail='" + userMail + '\'' +
                '}';
    }
}
