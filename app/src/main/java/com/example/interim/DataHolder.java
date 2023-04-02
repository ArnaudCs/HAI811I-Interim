package com.example.interim;

public class DataHolder {
    private static final DataHolder instance = new DataHolder();
    private String planPrice;
    private String planName;

    private DataHolder() {}

    public static DataHolder getInstance() {
        return instance;
    }

    public String getPlanPrice() {
        return planPrice;
    }

    public void setPlanPrice(String planPrice) {
        this.planPrice = planPrice;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }
}