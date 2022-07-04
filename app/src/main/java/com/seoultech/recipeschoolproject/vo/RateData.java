package com.seoultech.recipeschoolproject.vo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.Timestamp;

public class RateData implements Parcelable {
    private String key;
    private String userKey;
    private String userNickname;
    private String profileUrl;
    private float rate;
    private Timestamp date;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getUserKey() {
        return userKey;
    }

    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }

    public String getUserNickname() {
        return userNickname;
    }

    public void setUserNickname(String userNickname) {
        this.userNickname = userNickname;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public float getRate() {
        return rate;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.key);
        dest.writeString(this.userKey);
        dest.writeString(this.userNickname);
        dest.writeString(this.profileUrl);
        dest.writeFloat(this.rate);
        dest.writeParcelable(this.date, flags);
    }

    public RateData() {
    }

    protected RateData(Parcel in) {
        this.key = in.readString();
        this.userKey = in.readString();
        this.userNickname = in.readString();
        this.profileUrl = in.readString();
        this.rate = in.readFloat();
        this.date = in.readParcelable(Timestamp.class.getClassLoader());
    }

    public static final Creator<RateData> CREATOR = new Creator<RateData>() {
        @Override
        public RateData createFromParcel(Parcel source) {
            return new RateData(source);
        }

        @Override
        public RateData[] newArray(int size) {
            return new RateData[size];
        }
    };
}