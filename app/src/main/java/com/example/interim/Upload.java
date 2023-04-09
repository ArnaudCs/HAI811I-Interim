package com.example.interim;

public class Upload {
    private String mName;
    private String mImageUrl;

    public Upload(){

    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmImageUrl() {
        return mImageUrl;
    }

    public void setmImageUrl(String mImageUrl) {
        this.mImageUrl = mImageUrl;
    }

    public Upload(String name, String imageUrl){
        mName = "ProfilePic";
        mImageUrl = imageUrl;
    }
}
