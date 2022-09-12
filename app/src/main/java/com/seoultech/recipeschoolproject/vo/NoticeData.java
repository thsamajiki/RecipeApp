package com.seoultech.recipeschoolproject.vo;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

import java.util.ArrayList;


public class NoticeData implements Parcelable {
    private String key;
    private String noticeTitle;
    private String noticeDesc;
    private String noticeDate;
    private ArrayList<String> noticeList;

    public NoticeData() {
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getNoticeTitle() {
        return noticeTitle;
    }

    public void setNoticeTitle(String noticeTitle) {
        this.noticeTitle = noticeTitle;
    }

    public String getNoticeDesc() {
        return noticeDesc;
    }

    public void setNoticeDesc(String noticeDesc) {
        this.noticeDesc = noticeDesc;
    }

    public ArrayList<String> getNoticeList() {
        return noticeList;
    }

    public void setNoticeList(ArrayList<String> noticeList) {
        this.noticeList = noticeList;
    }

    public String getNoticeDate() {
        return noticeDate;
    }

    public void setNoticeDate(String noticeDate) {
        this.noticeDate = noticeDate;
    }

    protected NoticeData(Parcel in) {
        key = in.readString();
    }

    public static final Creator<NoticeData> CREATOR = new Creator<NoticeData>() {
        @Override
        public NoticeData createFromParcel(Parcel in) {
            return new NoticeData(in);
        }

        @Override
        public NoticeData[] newArray(int size) {
            return new NoticeData[size];
        }
    };

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj instanceof NoticeData) {
            return ((NoticeData) obj).getKey().equals(getKey());
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
        dest.writeString(key);
        dest.writeString(noticeDate);
        dest.writeString(noticeTitle);
        dest.writeString(noticeDesc);
        dest.writeSerializable(this.noticeList);
    }


}
