package com.seoultech.recipeschoolproject.vo;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.Timestamp;

public class MessageData implements Parcelable, Comparable<MessageData> {
    private String userKey;
    private Timestamp timestamp;
    private String message;

    public String getUserKey() {
        return userKey;
    }

    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.userKey);
        dest.writeParcelable(this.timestamp, flags);
        dest.writeString(this.message);
    }

    public MessageData() {
    }

    protected MessageData(Parcel in) {
        this.userKey = in.readString();
        this.timestamp = in.readParcelable(Timestamp.class.getClassLoader());
        this.message = in.readString();
    }

    public static final Parcelable.Creator<MessageData> CREATOR = new Parcelable.Creator<MessageData>() {
        @Override
        public MessageData createFromParcel(Parcel source) {
            return new MessageData(source);
        }

        @Override
        public MessageData[] newArray(int size) {
            return new MessageData[size];
        }
    };

    @Override
    public int compareTo(MessageData o) {
        return Long.compare(getTimestamp().getSeconds(), o.getTimestamp().getSeconds());
    }
}
