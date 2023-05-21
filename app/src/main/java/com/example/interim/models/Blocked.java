package com.example.interim.models;

import java.util.Date;

public class Blocked {
    private String blockedId;
    private String blockerId;
    private String blockerMail;
    private String blockedMail;
    private Date date;

    public Blocked(String blockedId, String blockerId, String blockerMail, String blockedMail, Date date) {
        this.blockedId = blockedId;
        this.blockerId = blockerId;
        this.blockerMail = blockerMail;
        this.blockedMail = blockedMail;
        this.date = date;
    }

    public Blocked(String blockedId, String blockerId, Date date) {
        this.blockedId = blockedId;
        this.blockerId = blockerId;
        this.date = date;
    }

    @Override
    public String toString() {
        return "Blocked{" +
                "blockedId='" + blockedId + '\'' +
                ", blockerId='" + blockerId + '\'' +
                ", blockerMail='" + blockerMail + '\'' +
                ", blockedMail='" + blockedMail + '\'' +
                ", date=" + date +
                '}';
    }

    public String getBlockedId() {
        return blockedId;
    }

    public void setBlockedId(String blockedId) {
        this.blockedId = blockedId;
    }

    public String getBlockerId() {
        return blockerId;
    }

    public void setBlockerId(String blockerId) {
        this.blockerId = blockerId;
    }

    public String getBlockerMail() {
        return blockerMail;
    }

    public void setBlockerMail(String blockerMail) {
        this.blockerMail = blockerMail;
    }

    public String getBlockedMail() {
        return blockedMail;
    }

    public void setBlockedMail(String blockedMail) {
        this.blockedMail = blockedMail;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
