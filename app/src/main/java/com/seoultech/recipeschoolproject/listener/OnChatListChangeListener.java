package com.seoultech.recipeschoolproject.listener;

import com.seoultech.recipeschoolproject.vo.ChatData;
import com.google.firebase.firestore.DocumentChange;

public interface OnChatListChangeListener {
    void onChatListChange(DocumentChange.Type changeType, ChatData chatData);
}