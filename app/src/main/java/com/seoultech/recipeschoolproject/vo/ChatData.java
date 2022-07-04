package com.seoultech.recipeschoolproject.vo;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

import java.util.HashMap;

public class ChatData implements Parcelable, Comparable<ChatData> {
    private String key;
    private MessageData lastMessage;
    private HashMap<String, String> userProfiles;
    private HashMap<String, String> userNicknames;
    private HashMap<String, Boolean> userList;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public MessageData getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(MessageData lastMessage) {
        this.lastMessage = lastMessage;
    }

    public HashMap<String, String> getUserProfiles() {
        return userProfiles;
    }

    public void setUserProfiles(HashMap<String, String> userProfiles) {
        this.userProfiles = userProfiles;
    }

    public HashMap<String, String> getUserNicknames() {
        return userNicknames;
    }

    public void setUserNicknames(HashMap<String, String> userNicknames) {
        this.userNicknames = userNicknames;
    }

    public HashMap<String, Boolean> getUserList() {
        return userList;
    }

    public void setUserList(HashMap<String, Boolean> userList) {
        this.userList = userList;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj instanceof ChatData) {
            return ((ChatData) obj).getKey().equals(getKey());
        } else {
            return false;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.key);
        dest.writeParcelable(this.lastMessage, flags);
        dest.writeSerializable(this.userProfiles);
        dest.writeSerializable(this.userNicknames);
        dest.writeSerializable(this.userList);
    }

    public ChatData() {
    }

    protected ChatData(Parcel in) {
        this.key = in.readString();
        this.lastMessage = in.readParcelable(MessageData.class.getClassLoader());
        this.userProfiles = (HashMap<String, String>) in.readSerializable();
        this.userNicknames = (HashMap<String, String>) in.readSerializable();
        this.userList = (HashMap<String, Boolean>) in.readSerializable();
    }

    public static final Creator<ChatData> CREATOR = new Creator<ChatData>() {
        @Override
        public ChatData createFromParcel(Parcel source) {
            return new ChatData(source);
        }

        @Override
        public ChatData[] newArray(int size) {
            return new ChatData[size];
        }
    };

    @Override
    public int compareTo(ChatData o) {
        return Long.compare(o.lastMessage.getTimestamp().getSeconds(), lastMessage.getTimestamp().getSeconds());
    }
}