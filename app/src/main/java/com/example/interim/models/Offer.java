package com.example.interim.models;

import java.util.Date;

public class Offer {
    private String _id;
    private String jobTitle;
    private String companyName;
    private String location;
    private Date startDate;
    private Date endDate;
    private Date postDate;
    private Date expDate;
    private String keywords;
    private String category;
    private String label;
    private float salaryMin;
    private float salaryMax;
    private String description;
    private String url;
    private String details;

    public Offer(String jobTitle, String companyName, String location, Date startDate, Date endDate, Date postDate, Date expDate, String keywords, String category, String label, float salaryMin, float salaryMax, String description, String details, String url) {
        this.jobTitle = jobTitle;
        this.companyName = companyName;
        this.location = location;
        this.startDate = startDate;
        this.endDate = endDate;
        this.postDate = postDate;
        this.expDate = expDate;
        this.keywords = keywords;
        this.category = category;
        this.label = label;
        this.salaryMin = salaryMin;
        this.salaryMax = salaryMax;
        this.description = description;
        this.details = details;
        this.url = url;
    }

    public Offer() {
        // Required empty constructor for Firestore
    }

    public String getId() {
        return _id;
    }

    public void setId(String _id) {
        this._id = _id;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getPostDate() {
        return postDate;
    }

    public void setPostDate(Date postDate) {
        this.postDate = postDate;
    }

    public Date getExpDate() {
        return expDate;
    }

    public void setExpDate(Date expDate) {
        this.expDate = expDate;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public float getSalaryMin() {
        return salaryMin;
    }

    public void setSalaryMin(float salaryMin) {
        this.salaryMin = salaryMin;
    }

    public float getSalaryMax() {
        return salaryMax;
    }

    public void setSalaryMax(float salaryMax) {
        this.salaryMax = salaryMax;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}