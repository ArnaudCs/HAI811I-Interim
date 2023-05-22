package com.example.interim.models;

import java.util.Date;

public class Blocked {
    private String blockId;
    private String blockedId;
    private String blockerId;
    private Date date;
    private String blockerMail;
    private String blockedMail;

    public String getBlockId() {
        return blockId;
    }

    public void setBlockId(String blockId) {
        this.blockId = blockId;
    }

    public Blocked(String blockedId, String blockerId, Date date, String blockedMail, String blockerMail) {
        this.blockedId = blockedId;
        this.blockerId = blockerId;
        this.date = date;
        this.blockerMail = blockerMail;
        this.blockedMail = blockedMail;
    }

    public Blocked() {
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
