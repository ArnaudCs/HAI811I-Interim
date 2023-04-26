package com.example.interim.offers;

public class filterDataHolder {
    private String category;
    private String jobTitle;
    private String cityText;
    private String startPriceText;
    private String endPriceText;
    private String startDateText;
    private String endDateText;

    public String getCityText() {
        return cityText;
    }

    public void setCityText(String cityText) {
        this.cityText = cityText;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getStartPriceText() {
        return startPriceText;
    }

    public void setStartPriceText(String startPriceText) {
        this.startPriceText = startPriceText;
    }

    public String getEndPriceText() {
        return endPriceText;
    }

    public void setEndPriceText(String endPriceText) {
        this.endPriceText = endPriceText;
    }

    public String getStartDateText() {
        return startDateText;
    }

    public void setStartDateText(String startDateText) {
        this.startDateText = startDateText;
    }

    public String getEndDateText() {
        return endDateText;
    }

    public void setEndDateText(String endDateText) {
        this.endDateText = endDateText;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return "filterDataHolder{" +
                "category='" + category + '\'' +
                ", jobTitle='" + jobTitle + '\'' +
                ", cityText='" + cityText + '\'' +
                ", startPriceText='" + startPriceText + '\'' +
                ", endPriceText='" + endPriceText + '\'' +
                ", startDateText='" + startDateText + '\'' +
                ", endDateText='" + endDateText + '\'' +
                '}';
    }

    public filterDataHolder(String category, String jobTitle, String cityText, String startPriceText, String endPriceText, String startDateText, String endDateText) {
        this.category = category;
        this.jobTitle = jobTitle;
        this.cityText = cityText;
        this.startPriceText = startPriceText;
        this.endPriceText = endPriceText;
        this.startDateText = startDateText;
        this.endDateText = endDateText;
    }
}
