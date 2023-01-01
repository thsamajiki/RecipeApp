package com.seoultech.recipeschoolproject.vo;


import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

import com.google.firebase.Timestamp;

public class RecipeData implements Parcelable {
    private String key;
    private String profileUrl;
    private String userName;
    private String userKey;
    private String content;
    private String photoUrl;
    private Timestamp postDate;
    private float rate;
    private int totalRatingCount;

    public String getUserKey() {
        return userKey;
    }

    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public Timestamp getPostDate() {
        return postDate;
    }

    public void setPostDate(Timestamp postDate) {
        this.postDate = postDate;
    }

    public float getRate() {
        return rate;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }

    public int getTotalRatingCount() {
        return totalRatingCount;
    }

    public void setTotalRatingCount(int totalRatingCount) {
        this.totalRatingCount = totalRatingCount;
    }


    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof RecipeData) {
            return ((RecipeData) obj).getKey().equals(getKey());
        }
        return false;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.key);
        dest.writeString(this.profileUrl);
        dest.writeString(this.userName);
        dest.writeString(this.userKey);
        dest.writeString(this.content);
        dest.writeString(this.photoUrl);
        dest.writeParcelable(this.postDate, flags);
        dest.writeFloat(this.rate);
        dest.writeInt(this.totalRatingCount);
    }

    public RecipeData() {
    }

    protected RecipeData(Parcel in) {
        this.key = in.readString();
        this.profileUrl = in.readString();
        this.userName = in.readString();
        this.userKey = in.readString();
        this.content = in.readString();
        this.photoUrl = in.readString();
        this.postDate = in.readParcelable(Timestamp.class.getClassLoader());
        this.rate = in.readFloat();
        this.totalRatingCount = in.readInt();
    }

    public static final Creator<RecipeData> CREATOR = new Creator<RecipeData>() {
        @Override
        public RecipeData createFromParcel(Parcel source) {
            return new RecipeData(source);
        }

        @Override
        public RecipeData[] newArray(int size) {
            return new RecipeData[size];
        }
    };
}