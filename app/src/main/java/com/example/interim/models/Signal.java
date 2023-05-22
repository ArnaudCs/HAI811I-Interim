package com.example.interim.models;

import java.util.Date;

public class Signal {
    private String signalId;
    private String signalerId;
    private String signaledId;
    private String signalerMail;
    private String signaledMail;

    private String reason;
    private Date signalDate;

    public Signal(String signalerId, String signaledId, String signalerMail, String signaledMail, String reason, Date date) {
        this.signalerId = signalerId;
        this.signaledId = signaledId;
        this.signalerMail = signalerMail;
        this.signaledMail = signaledMail;
        this.reason = reason;
        this.signalDate = date;
    }

    public Signal(String signalerId, String signaledId, String signalerMail, String reason, Date date) {
        this.signalerId = signalerId;
        this.signaledId = signaledId;
        this.signalerMail = signalerMail;
        this.reason = reason;
        this.signalDate = date;
    }

    public Signal(String signalerId, String signaledId, String reason, Date date) {
        this.signalerId = signalerId;
        this.signaledId = signaledId;
        this.reason = reason;
        this.signalDate = date;
    }

    public Signal() {
    }

    public String getSignalId() {
        return signalId;
    }

    public void setSignalId(String signalId) {
        this.signalId = signalId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getSignalerId() {
        return signalerId;
    }

    public void setSignalerId(String signalerId) {
        this.signalerId = signalerId;
    }

    public String getSignaledId() {
        return signaledId;
    }

    public void setSignaledId(String signaledId) {
        this.signaledId = signaledId;
    }

    public String getSignalerMail() {
        return signalerMail;
    }

    public void setSignalerMail(String signalerMail) {
        this.signalerMail = signalerMail;
    }

    public String getSignaledMail() {
        return signaledMail;
    }

    public void setSignaledMail(String signaledMail) {
        this.signaledMail = signaledMail;
    }

    public Date getSignalDate() {
        return signalDate;
    }

    public void setSignalDate(Date date) {
        this.signalDate = date;
    }
}
