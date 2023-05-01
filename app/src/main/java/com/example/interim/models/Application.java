package com.example.interim.models;

public class Application {
    private String id;
    private String applicantAdress;
    private String applicantBirth;
    private String applicantFirstName;
    private String applicantId;
    private String applicantMail;
    private String applicantName;
    private String applicantPhone;
    private String offerId;
    private int status;
    private Offer offer;

    public Application() {
    }

    public Application(String applicantAdress, String applicantBirth, String applicantFirstName, String applicantId, String applicantMail, String applicantName, String applicantPhone, String offerId, int status) {
        this.applicantAdress = applicantAdress;
        this.applicantBirth = applicantBirth;
        this.applicantFirstName = applicantFirstName;
        this.applicantId = applicantId;
        this.applicantMail = applicantMail;
        this.applicantName = applicantName;
        this.applicantPhone = applicantPhone;
        this.offerId = offerId;
        this.status = status;
    }

    public Application(String applicantAdress, String applicantBirth, String applicantFirstName, String applicantId, String applicantMail, String applicantName, String applicantPhone, String offerId, int status, Offer offer) {
        this.applicantAdress = applicantAdress;
        this.applicantBirth = applicantBirth;
        this.applicantFirstName = applicantFirstName;
        this.applicantId = applicantId;
        this.applicantMail = applicantMail;
        this.applicantName = applicantName;
        this.applicantPhone = applicantPhone;
        this.offerId = offerId;
        this.status = status;
        this.offer = offer;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Offer getOffer() {
        return offer;
    }

    public void setOffer(Offer offer) {
        this.offer = offer;
    }

    public String getApplicantAdress() {
        return applicantAdress;
    }

    public void setApplicantAdress(String applicantAdress) {
        this.applicantAdress = applicantAdress;
    }

    public String getApplicantBirth() {
        return applicantBirth;
    }

    public void setApplicantBirth(String applicantBirth) {
        this.applicantBirth = applicantBirth;
    }

    public String getApplicantFirstName() {
        return applicantFirstName;
    }

    public void setApplicantFirstName(String applicantFirstName) {
        this.applicantFirstName = applicantFirstName;
    }

    public String getApplicantId() {
        return applicantId;
    }

    public void setApplicantId(String applicantId) {
        this.applicantId = applicantId;
    }

    public String getApplicantMail() {
        return applicantMail;
    }

    public void setApplicantMail(String applicantMail) {
        this.applicantMail = applicantMail;
    }

    public String getApplicantName() {
        return applicantName;
    }

    public void setApplicantName(String applicantName) {
        this.applicantName = applicantName;
    }

    public String getApplicantPhone() {
        return applicantPhone;
    }

    public void setApplicantPhone(String applicantPhone) {
        this.applicantPhone = applicantPhone;
    }

    public String getOfferId() {
        return offerId;
    }

    public void setOfferId(String offerId) {
        this.offerId = offerId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
