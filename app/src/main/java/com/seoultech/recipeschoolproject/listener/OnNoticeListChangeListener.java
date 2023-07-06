package com.seoultech.recipeschoolproject.listener;

import com.google.firebase.firestore.DocumentChange;
import com.seoultech.recipeschoolproject.vo.NoticeData;

public interface OnNoticeListChangeListener {
    void onNoticeListChange(DocumentChange.Type changeType, NoticeData noticeData);
}