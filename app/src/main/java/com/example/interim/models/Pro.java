package com.example.interim.models;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.util.Log;

import com.example.interim.MainActivity;
import com.example.interim.ProfileActivity;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Date;
import java.util.concurrent.Flow;

public class Pro {
    private String name;
    private String companyName;
    private String nationalNumber;
    private String email;
    private String phoneNumber;
    private String companyAddress;
    private String website;

    private String service;
    private String subService;
    private String contact2Name;
    private String contact2Email;
    private String contact2Phone;

    private boolean verified;

    public Pro(String name, String companyName, String nationalNumber, String email, String phoneNumber, String companyAddress, String website, String service, String subService, String contact2Name, String contact2Email, String contact2Phone) {
        this.name = name;
        this.companyName = companyName;
        this.nationalNumber = nationalNumber;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.companyAddress = companyAddress;
        this.website = website;
        this.service = service;
        this.subService = subService;
        this.contact2Name = contact2Name;
        this.contact2Email = contact2Email;
        this.contact2Phone = contact2Phone;
        this.verified = false;
    }

    public Pro(String name, String companyName, String nationalNumber, String email, String phoneNumber, String companyAddress, String website) {
        this.name = name;
        this.companyName = companyName;
        this.nationalNumber = nationalNumber;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.companyAddress = companyAddress;
        this.website = website;
        this.verified = false;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public Pro() {
        // Required empty constructor
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getNationalNumber() {
        return nationalNumber;
    }

    public void setNationalNumber(String nationalNumber) {
        this.nationalNumber = nationalNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCompanyAddress() {
        return companyAddress;
    }

    public void setCompanyAddress(String companyAddress) {
        this.companyAddress = companyAddress;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getSubService() {
        return subService;
    }

    public void setSubService(String subService) {
        this.subService = subService;
    }

    public String getContact2Name() {
        return contact2Name;
    }

    public void setContact2Name(String contact2Name) {
        this.contact2Name = contact2Name;
    }

    public String getContact2Email() {
        return contact2Email;
    }

    public void setContact2Email(String contact2Email) {
        this.contact2Email = contact2Email;
    }

    public String getContact2Phone() {
        return contact2Phone;
    }

    public void setContact2Phone(String contact2Phone) {
        this.contact2Phone = contact2Phone;
    }


// Getters and setters for all the fields
}
